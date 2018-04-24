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
@Subject(FirewallRule)
@Title('tests firewall rule CRUD')
@Stepwise
class FirewallRuleSpec extends Specification {

    @Subject
    FirewallRule firewallRule

    @Shared DataCenter dataCenter
    @Shared NIC nic
    @Shared String fwrID

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'de/fkb').create() as DataCenter
        assert dataCenter.id

        nic = new NIC(
            server: new Server(dataCenter: dataCenter, name: 'Server Fixture', cores: 1, ram: 256).create(),
            lan: new LAN(id: 1), name: 'NIC Fixture', dhcp: true).create()
        assert nic.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'firewall rules can be created'() {
        given: 'a firewall rule POGO'
        firewallRule = new FirewallRule(
            nic: nic,
            name: 'Groovy SDK Test Firewall Rule',
            sourceMac: MAC,
            sourceIp: '1.2.3.4',
            targetIp: '5.6.7.8',
            portRangeStart: '1234',
            portRangeEnd: '2345',
            protocol: 'TCP'
        )

        when: 'its create() method is called'
        final _fwr = firewallRule.create()
        fwrID = _fwr.id

        then: 'the return value of that is a firewall rule'
        _fwr instanceof FirewallRule

        and: 'it has a valid uuid'
        UUID.fromString fwrID
    }

    final 'firewall rules can be retrieved'() {
        when: 'a NIC is retrieved'
        firewallRule = testRule

        then: 'the returned object is a properly populated NIC'
        firewallRule.name == 'Groovy SDK Test Firewall Rule'
        firewallRule.sourceMac =~ /(?i)[a-z0-9:]+/
        firewallRule.sourceIp == '1.2.3.4'
        firewallRule.targetIp == '5.6.7.8'
        firewallRule.portRangeStart == '1234'
        firewallRule.portRangeEnd == '2345'
        !firewallRule.icmpType
        !firewallRule.icmpCode
        firewallRule.protocol == 'TCP'
    }

    final 'firewall rules can be listed'() {
        when:
        final ids = testRule.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'firewall rules can be updated'() {
        given:
        firewallRule = testRule
        final mac = MAC

        when: 'its properties are updated'
        firewallRule.name = 'foo'
        firewallRule.sourceMac = mac
        firewallRule.sourceIp = '4.3.2.1'
        firewallRule.targetIp = '8.7.6.5'
        firewallRule.portRangeStart = null
        firewallRule.portRangeEnd = null
        firewallRule.update()

        and: 'it is read again'
        firewallRule = testRule

        then: 'the changes should be reflected'
        firewallRule.name == 'foo'
        firewallRule.sourceMac == mac
        firewallRule.sourceIp == '4.3.2.1'
        firewallRule.targetIp == '8.7.6.5'
    }

    final 'firewall rules can be deleted'() {
        when:
        testRule.delete()

        then:
        !testRule
    }

    private final FirewallRule getTestRule() {
        new FirewallRule(nic: nic, id: fwrID).read()
    }

    private static final getMAC() {
        NetworkInterface.networkInterfaces*.hardwareAddress*.collect{ String.format('%02x', it) }*.join(':').find{ it }
    }
}
