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

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a storage volume POGO
 * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#volumes">Cloud API reference</a>
 *
 * Created by fudge on 03/02/17.
 * @author fudge <frank.geusch@profitbricks.com>
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class Volume extends ModelBase {
    DataCenter dataCenter

    @Creatable
    String type = 'HDD',
           availabilityZone = 'AUTO',
           image,
           licenceType = 'UNKNOWN',
           imageAlias,
           imagePassword
    @Creatable @Updatable
    String name,  bus = 'VIRTIO'
    @Creatable
    List<String> sshKeys
    @Creatable @Updatable
    int size
    @Readable
    int deviceNumber

    @Override
    final Volume create(final Map options = [:]) {
        (super.create(options) as Volume)?.with dataCenter
    }

    @Override
    final Volume read(final id = id, final Map options = [:]) {
        (super.read(id, options) as Volume)?.with dataCenter
    }

    @Override
    final String getResource() {
        "${dataCenter.resource}/${dataCenter.id}/volumes"
    }

    private final Volume with(final DataCenter dc) {
        dataCenter = dc; this
    }
}
