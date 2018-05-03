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
@Subject(Location)
@Title("unit tests Location CRUD")
class LocationSpec extends Specification {

    @Subject
    Location location

    final 'locations can be listed'() {
        given: 'an empty Location POGO'
        location = new Location()

        when: 'its all property is called'
        final ids = location.all

        then: 'the result is non-empty and contains valid location ids'
        !ids.empty
        ids.each {
            assert it =~ /[a-z]+\/[a-z]+/
        }
    }

    @Unroll
    final 'locations can be retrieved'() {
        when: 'a location POGO is retrieved with a valid id'
        final _location = new Location(id: id).read() as Location
        println _location.properties

        then: 'the result is a valid location'
        _location.name
        _location.id
        !_location.features?.empty

        where:
        id << new Location().all
    }
}
