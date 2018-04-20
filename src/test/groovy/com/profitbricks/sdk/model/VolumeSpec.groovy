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
@Subject(Volume)
@Title("unit tests volume CRUD and commands")
@Stepwise
class VolumeSpec extends Specification {

    @Subject
    Volume volume

    @Shared DataCenter dataCenter
    @Shared Image image
    @Shared Server server
    @Shared String volumeID

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK TestDatacenter', location: 'us/las').create() as DataCenter
        assert dataCenter.id
        image = new Image().all.collect{new Image(id:it).read() as Image}.find{it.name == 'us_las-3GiB'}
        assert image.id
        server = new Server(dataCenter: dataCenter, name: 'Groovy SDK TestServer', ram: 256, cores: 1).create()
        assert server.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'volumes can be created'() {
        given: 'a volume POGO'
        volume = new Volume(
                dataCenter: dataCenter,
                name: "Groovy SDK TestVolume",
                size: 3,
                type: "HDD",
                bus: "VIRTIO",
                availabilityZone: "ZONE_3",
                image: image.id,
                licenceType: null,
                imagePassword: 's3cr3tpw',
                sshKeys: ['ssh-rsa dummy'])

        when: 'its create() method is called'
        final _volume = volume.create()
        volumeID = _volume.id

        then: 'the return value of that is a volume'
        _volume instanceof Volume

        and: 'it has a valid uuid'
        fromString volumeID
    }

    final 'volumes can be read'() {
        when: 'a volume is retrieved'
        volume = testVolume

        then: 'the returned object is a properly populated volume'
        fromString "${volume.id}"
        volume.name == 'Groovy SDK TestVolume'
        volume.size == 3
        volume.type == 'HDD'
        volume.bus == 'VIRTIO'
        volume.availabilityZone == 'ZONE_3'
        volume.licenceType == 'LINUX'
        fromString volume.image
        !volume.imagePassword
        !volume.sshKeys
        !volume.deviceNumber
    }

    final 'volumes can be listed'() {
        given:
        volume = testVolume

        when: 'the volumes all property is called'
        final ids = volume.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {fromString(it)}.size()
    }

    final 'volumes can be updated'() {
        given:
        volume = testVolume

        when: 'its properties are updated'
        volume.name = 'foo'
        volume.bus = 'IDE'
        volume.size *= 2
        volume.update()

        and: 'it is read again'
        volume = volume.read() as Volume

        then: 'the changes should be reflected'
        volume.name == 'foo'
        volume.size == 6
    }

    final 'volumes can be attached to servers'() {
        given:
        volume = testVolume

        when: 'the volume is attached to a server'
        attach server, volume
        volume = testVolume

        then: 'this should be reflected'
        attached(server, Volume).contains volume.id
        volume.deviceNumber
    }

    final 'volumes can be detached from servers'() {
        given:
        volume = testVolume

        when: 'the volume is attached to a server'
        detach server, volume
        volume = testVolume

        then: 'this should be reflected'
        !attached(server, Volume).contains(volume.id)
        !volume.deviceNumber
    }

    final 'volumes can be deleted'() {
        given:
        volume = testVolume

        when: 'it is deleted'
        volume.delete()

        and: 'retrieved again'
        volume = testVolume

        then: 'that result is empty'
        !volume
    }

    private final Volume getTestVolume() {
        new Volume(dataCenter: dataCenter, id: volumeID).read() as Volume
    }
}
