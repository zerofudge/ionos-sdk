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
import static java.util.UUID.fromString


/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(Image)
@Title("unit tests image CRUD and commands")
@Stepwise
class ImageSpec extends Specification {

    @Shared @Subject
    Image image

    @Shared DataCenter dataCenter
    @Shared Server server

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'de/fkb').create() as DataCenter
        assert dataCenter.id
        server = new Server(dataCenter: dataCenter, name: 'Server Fixture', ram: 256, cores: 1).create()
        assert server.id
        image = new Image().all.collect {new Image(id: it).read() as Image}.find {
            it.location == dataCenter.location
        }
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'images can be listed'() {
        when:
        final ids = image.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {fromString(it)}.size()
    }

    final 'images can be read'() {
        when: 'an image is retrieved'

        then: 'the returned object is a properly populated image'
        fromString "${image.id}"
        image.name
        image.description ?: 'might be null'
        image.size >= 0
        image.location =~ /[a-z]{2}\/[a-z]+/
        image.licenceType
        image.imageType
        image.cpuHotPlug != null
        image.cpuHotUnplug != null
        image.ramHotPlug != null
        image.ramHotUnplug != null
        image.nicHotPlug != null
        image.nicHotUnplug != null
        image.discVirtioHotPlug != null
        image.discVirtioHotUnplug != null
        image.discScsiHotPlug != null
        image.discScsiHotUnplug != null
        image._public != null
    }

    final 'images can be attached to servers'() {
        when: 'the image is attached to a server'
        attach server, image

        then: 'this should be reflected'
        attached(server, Image).contains image.id
    }

    final 'images can be detached from servers'() {
        when: 'the image is detached from the server'
        detach server, image

        then: 'this should be reflected'
        !attached(server, Image).contains(image.id)
    }
}
