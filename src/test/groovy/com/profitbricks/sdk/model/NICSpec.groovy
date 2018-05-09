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
@Subject(NIC)
@Title("unit tests NIC CRUD")
@Stepwise
class NICSpec extends Specification {
    @Subject
    NIC nic

    @Shared DataCenter dataCenter
    @Shared Server server
    @Shared LAN lan
    @Shared String nicID

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'de/fkb').create() as DataCenter
        assert dataCenter.id
        lan = new LAN(dataCenter: dataCenter, name: 'LAN fixture', _public: false).create()
        assert lan.id
        server = new Server(dataCenter: dataCenter, name: 'Server fixture', ram: 256, cores: 1).create()
        assert server.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'NICs can be created'() {
        given: 'a NIC POGO'
        nic = new NIC(
            server: server,
            lan: lan,
            name: 'Groovy SDK TestNIC',
            firewallActive: false,
            dhcp: false,
            nat: false,
            ips: ['1.2.3.4']
        )

        when: 'its create() method is called'
        final _nic = nic.create()
        nicID = _nic.id

        then: 'the return value of that is a NIC'
        _nic instanceof NIC

        and: 'it has a valid uuid'
        UUID.fromString nicID
    }

    final 'NICs can be retrieved'() {
        when: 'a NIC is retrieved'
        nic = testNIC

        then: 'the returned object is a properly populated NIC'
        nic.id
        nic.name == 'Groovy SDK TestNIC'
        !nic.firewallActive
        !nic.dhcp
        !nic.nat
        nic.ips.size() == 1
        nic.ips.first() == '1.2.3.4'
        nic.mac =~ /(?i)[a-z0-9:]+/
        nic.lan.id == lan.id
    }

    final 'NICs can be listed'() {
        given:
        nic = testNIC

        when: 'the NICs all property is called'
        final ids = nic.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'NICs can be updated'() {
        given:
        nic = testNIC

        when: 'its properties are updated'
        nic.name = 'foo'
        nic.ips = []
        nic.dhcp = true
        nic.firewallActive = true
        nic.update()

        and: 'it is read again'
        nic = testNIC

        then: 'the changes should be reflected'
        nic.name == 'foo'
        nic.ips.size() == 1
        nic.ips.first() != '1.2.3.4'
        nic.dhcp
        nic.firewallActive
    }

    final 'NICs can be deleted'() {
        when:
        testNIC.delete()

        then:
        !testNIC
    }

    private final NIC getTestNIC() {
        new NIC(server: server, id: nicID).read()
    }
}
