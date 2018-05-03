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

import com.profitbricks.sdk.Commands
import spock.lang.*


/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(LoadBalancer)
@Title('tests load balancer CRUD and commands')
@Stepwise
class LoadBalancerSpec extends Specification {

    @Subject
    LoadBalancer loadBalancer

    @Shared DataCenter dataCenter
    @Shared NIC nic
    @Shared String lbID

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'de/fkb').create() as DataCenter
        assert dataCenter.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'load balancers can be created'() {
        given: 'a load balancer POGO'
        loadBalancer = new LoadBalancer(
            dataCenter: dataCenter,
            name: 'Groovy SDK TestBalancer',
            dhcp: false,
            ip: '1.2.3.4')

        when: 'its create() method is called'
        final _lb = loadBalancer.create()
        lbID = _lb.id

        then: 'the return value of that is a load balancer'
        _lb instanceof LoadBalancer

        and: 'it has a valid uuid'
        UUID.fromString lbID
    }

    final 'load balancers can be retrieved'() {
        when: 'a load balancer is retrieved'
        loadBalancer = testBalancer

        then: 'the returned object is a properly populated load balancer'
        loadBalancer.id
        loadBalancer.name == 'Groovy SDK TestBalancer'
        !loadBalancer.dhcp
        loadBalancer.ip == '1.2.3.4'
    }

    final 'load balancers can be listed'() {
        when: 'the load balancer\'s all property is called'
        final ids = testBalancer.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'load balancers can be updated'() {
        given:
        loadBalancer = testBalancer

        when: 'its properties are updated'
        loadBalancer.name = 'foo'
        loadBalancer.ip = '2.3.4.5'
        loadBalancer.dhcp = true
        loadBalancer.update()

        and: 'it is read again'
        loadBalancer = testBalancer

        then: 'the changes should be reflected'
        loadBalancer.name == 'foo'
        loadBalancer.dhcp
        loadBalancer.ip == '2.3.4.5'
    }

    final 'NICs can be associated with load balancers'() {
        given:
        loadBalancer = testBalancer
        nic = new NIC(
            server: new Server(dataCenter: dataCenter, name: 'Server Fixture', cores: 1, ram: 256).create(),
            lan: new LAN(id: 1), name: 'NIC Fixture').create()

        when:
        Commands.associate loadBalancer, nic

        then:
        Commands.associatedNICs(loadBalancer).contains nic.id
    }

    final 'NICs can be dissociated from load balancers'() {
        given:
        loadBalancer = testBalancer

        when:
        Commands.dissociate loadBalancer, nic

        then:
        !Commands.associatedNICs(loadBalancer).contains(nic.id)
    }

    final 'load balancers can be deleted'() {
        when: 'it is deleted'
        testBalancer.delete()

        then: 'it is gone'
        !testBalancer
    }

    private final LoadBalancer getTestBalancer() {
        new LoadBalancer(dataCenter: dataCenter, id: lbID).read() as LoadBalancer
    }
}
