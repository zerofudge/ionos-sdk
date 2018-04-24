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
@Subject(DataCenter)
@Title("unit tests DataCenter CRUD")
@Stepwise
class DataCenterSpec extends Specification {

    @Subject
    DataCenter dataCenter

    @Shared String datacenterID

    final 'datacenters can be created'() {
        given: 'a DataCenter POGO'
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'de/fkb', description: 'Groovy SDK test datacenter')

        when: 'its create() method is called'
        final _dc = dataCenter.create()

        then: 'the return value of that is a DataCenter POGO'
        _dc instanceof DataCenter

        and: 'it has a valid uuid'
        _dc.id
        UUID.fromString(_dc.id as String)

        cleanup:
        datacenterID = _dc.id
    }

    final 'datacenters can be listed'() {
        given: 'an empty DataCenter POGO'
        dataCenter = new DataCenter()

        when: 'its all property is called'
        final ids = dataCenter.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'datacenters can be retrieved'() {
        when: 'a DataCenter is retrieved'
        final _dc = testDataCenter

        then: 'the returned object is a properly populated datacenter'
        _dc instanceof DataCenter
        _dc.id
        _dc.name
        _dc.location
    }

    final 'datacenters can be updated'() {
        given: 'a valid datacenter POGO'
        dataCenter = testDataCenter
        final change = 'foo'

        when: 'its properties are updated'
        dataCenter.name = change
        dataCenter.description = change
        dataCenter.update()

        and: 'it is read again'
        dataCenter = testDataCenter

        then: 'the changes should be reflected'
        dataCenter.name == change
        dataCenter.description == change
    }

    final 'datacenters can be deleted'() {
        when: 'it is deleted'
        testDataCenter.delete()

        then: 'it is gone'
        !testDataCenter
    }

    private final DataCenter getTestDataCenter() {
        new DataCenter(id: datacenterID).read() as DataCenter
    }
}
