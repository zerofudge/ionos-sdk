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
@Subject(IPBlock)
@Title('tests IP block CRUD')
@Stepwise
class IPBlockSpec extends Specification {

    @Subject
    IPBlock ipBlock

    @Shared String ipID

    final 'IP blocks can be created'() {
        given: 'a IP block POGO'
        ipBlock = new IPBlock(
                name: 'Groovy SDK TestIP',
                location: 'de/fkb',
                size: 2)

        when: 'its create() method is called'
        final _ip = ipBlock.create()
        ipID = _ip.id

        then: 'the return value of that is an IP block'
        _ip instanceof IPBlock

        and: 'it has a valid uuid'
        UUID.fromString ipID
    }

    final 'IP blocks can be retrieved'() {
        when: 'an IP block is retrieved'
        ipBlock = testBlock

        then: 'the returned object is a properly populated load balancer'
        ipBlock.id
        ipBlock.name == 'Groovy SDK TestIP'
        ipBlock.size == 2
        ipBlock.size == ipBlock.ips.findAll {it =~ /[0-9.]+/}.size()
        ipBlock.location == 'de/fkb'
    }

    final 'IP blocks can be listed'() {
        when: 'the ip block\'s all property is called'
        final ids = testBlock.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'IP blocks can be deleted'() {
        when:
        testBlock.delete()

        then:
        !testBlock
    }

    private final IPBlock getTestBlock() {
        new IPBlock(id: ipID).read() as IPBlock
    }
}
