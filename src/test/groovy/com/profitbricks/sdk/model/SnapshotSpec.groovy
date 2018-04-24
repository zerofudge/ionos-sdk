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

import static java.util.UUID.fromString


/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(Snapshot)
@Title("unit tests snapshot CRUD and commands")
@Stepwise
class SnapshotSpec extends Specification {

    @Subject Snapshot snapshot

    @Shared DataCenter dataCenter
    @Shared Volume volume
    @Shared String snapshotID

    final setupSpec() {
        dataCenter = new DataCenter(name: 'Groovy SDK Test Fixture', location: 'us/las').create() as DataCenter
        assert dataCenter.id
        volume = new Volume(dataCenter: dataCenter, name: 'Volume Fixture', size: 1).create()
        assert volume.id
    }

    final cleanupSpec() {
        assert dataCenter.delete()
    }

    final 'snapshots can be created'() {
        when:
        snapshot = Commands.snapshot volume
        snapshotID = snapshot.id

        then:
        fromString "${snapshot.id}"
    }

    final 'snapshots can be listed'() {
        when:
        final ids = testSnapshot.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {fromString(it)}.size()
    }

    final 'snapshots can be read'() {
        when:
        snapshot = testSnapshot

        then: 'the returned object is a properly populated snapshot'
        snapshot.size == volume.size
        snapshot.location == dataCenter.location
        snapshot.name =~ volume.name
        snapshot.description =~ volume.id
        snapshot.licenceType == 'UNKNOWN'
        !snapshot.cpuHotPlug
        !snapshot.cpuHotUnplug
        !snapshot.ramHotPlug
        !snapshot.ramHotUnplug
        !snapshot.nicHotPlug
        !snapshot.nicHotUnplug
        !snapshot.discVirtioHotPlug
        !snapshot.discVirtioHotUnplug
        !snapshot.discScsiHotPlug
        !snapshot.discScsiHotUnplug
    }

    final 'snapshots can be updated'() {
        given:
        snapshot = testSnapshot

        when: 'its properties are updated'
        snapshot.name = 'foo'
        snapshot.cpuHotPlug = true
        snapshot.ramHotPlug = true
        snapshot.nicHotPlug = true
        snapshot.update()

        and: 'it is read again'
        snapshot = testSnapshot

        then: 'the changes should be reflected'
        snapshot.name == 'foo'
        snapshot.cpuHotPlug
        snapshot.ramHotPlug
        snapshot.nicHotPlug
    }

    final 'snapshots can be restored'() {
        expect:
        Commands.restore volume, testSnapshot
    }

    final 'snapshots can be deleted'() {
        when:
        testSnapshot.delete()

        then: 'that result is empty'
        !testSnapshot
    }

    private Snapshot getTestSnapshot() {
        new Snapshot(id: snapshotID).read() as Snapshot
    }
}
