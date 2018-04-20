/*
   Copyright 2018 Profitbricks GmbH

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.profitbricks.sdk.model

import spock.lang.*

import static com.profitbricks.sdk.Commands.*


/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(Server)
@Title("unit tests server CRUD and commands")
@Stepwise
class ServerSpec extends Specification {

    @Subject
    Server server

    @Shared DataCenter dataCenter
    @Shared String serverID

    final setupSpec() {
        dataCenter = new DataCenter(name: "Groovy SDK Test", location: 'de/fkb').create() as DataCenter
        assert dataCenter.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'servers can be created'() {
        given: 'a server POGO'
        server = new Server(
            dataCenter: dataCenter,
            name: 'Groovy SDK TestServer',
            cores: 1,
            ram: 1024,
            cpuFamily: 'INTEL_XEON',
            availabilityZone: 'ZONE_1')

        when: 'its create() method is called'
        final _server = server.create()
        serverID = _server.id

        then: 'the return value of that is a server'
        _server instanceof Server

        and: 'it has a valid uuid'
        UUID.fromString serverID
    }

    final 'servers can be retrieved'() {
        when: 'a server is retrieved'
        server = testServer

        then: 'the returned object is a properly populated server'
        server.id
        server.name == 'Groovy SDK TestServer'
        server.cores == 1
        server.ram == 1024
        server.cpuFamily == 'INTEL_XEON'
        server.availabilityZone == 'ZONE_1'
        server.licenseType == 'AUTO'
    }

    final 'servers can be listed'() {
        given:
        server = testServer

        when: 'the servers all property is called'
        final ids = server.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'servers can be updated'() {
        given:
        server = testServer

        when: 'its properties are updated'
        server.name = 'foo'
        server.ram += 1024
        server.cores *= 2
        server.update()

        and: 'it is read again'
        server = server.read() as Server

        then: 'the changes should be reflected'
        server.name == 'foo'
        server.ram == 2048
        server.cores == 2
    }

    final 'servers can be stopped'() {
        given: 'a running server'
        server = testServer
        assert server.vmState =~ /(?i)running/

        when: 'the stop command is invoked'
        stop server
        server = testServer

        then: 'this should be reflected in the running state'
        server.vmState =~ /(?i)shutoff/
    }

    final 'servers can be started'() {
        given: 'a stopped server'
        server = testServer
        assert server.vmState =~ /(?i)shutoff/

        when: 'the start command is invoked'
        start server
        server = testServer

        then: 'this should be reflected in the running state'
        server.vmState =~ /(?i)running/
    }

    final 'servers can be rebooted'() {
        given: 'a running server'
        server = testServer
        assert server.vmState =~ /(?i)running/

        when: 'the reboot command is invoked'
        reboot server
        server = testServer

        then: 'this should be reflected in the running state'
        server.vmState =~ /(?i)running/
    }

    final 'servers can be deleted'() {
        given: 'a server'
        server = testServer

        when: 'it is deleted'
        server.delete()

        and: 'retrieved again'
        server = testServer

        then: 'that result is empty'
        !server
    }

    private final Server getTestServer() {
        new Server(dataCenter: dataCenter, id: serverID).read() as Server
    }
}
