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


/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(LAN)
@Title("unit tests LAN CRUD")
@Stepwise
class LANSpec extends Specification {

    @Subject
    LAN lan

    @Shared DataCenter dataCenter
    @Shared NIC nic
    @Shared int lanID

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'de/fkb').create() as DataCenter
        assert dataCenter.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'LANs can be created'() {
        given: 'a LAN POGO'
        lan = new LAN(
                dataCenter: dataCenter,
                name: 'Groovy SDK TestLAN',
                _public: false)

        when: 'its create() method is called'
        final _lan = lan.create()
        lanID = _lan.id

        then: 'the return value of that is a LAN'
        _lan instanceof LAN

        and: 'it has a valid id'
        lanID > 0
    }

    final 'LANs can be listed'() {
        given:
        lan = testLAN

        when: 'the servers all property is called'
        final ids = lan.all

        then: 'the result is non-empty and contains valid LAN ids'
        !ids.empty
        ids.size() == ids.collect { Integer.parseInt(it)}.size()
    }

    final 'LANs can be retrieved'() {
        when: 'a LAN is retrieved'
        lan = testLAN

        then: 'the returned object is a properly populated LAN'
        Integer.parseInt "${lan.id}"
        lan.name == 'Groovy SDK TestLAN'
        !lan._public
    }

    final 'LANs can be updated'() {
        given:
        lan = testLAN
        final crip = new IPBlock(name: 'IP Fixture', location: dataCenter.location, size: 1).create() as IPBlock
        nic = new NIC(server:
                new Server(dataCenter: dataCenter, name: 'Server Fixture', cores: 1, ram: 256).create(),
                lan: lan, name: 'NIC Fixture', ips: crip.ips).create()

        when: 'its properties are updated'
        lan.name = 'foo'
        lan._public = true
        lan.ipFailover = [new LAN.IPFailover(ip: crip.ips.first(), nicUuid: nic.id)]
        lan.update()

        and: 'it is read again'
        lan = testLAN

        then: 'the changes should be reflected'
        lan.name == 'foo'
        lan._public
        lan.ipFailover.size() == 1
    }

    final 'LANs can be deleted'() {
        given:
        lan = testLAN
        lan.ipFailover = []
        lan.update()
        nic.delete()

        when:
        lan.delete()

        then:
        !testLAN
    }

    private final LAN getTestLAN() {
        new LAN(dataCenter: dataCenter, id: lanID).read() as LAN
    }
}
