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
 * a server POGO
 * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#servers">Cloud API reference</a>
 *
 * @author fudge <frank.geusch@profitbricks.com>
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class Server extends ModelBase {
    DataCenter dataCenter
    @Creatable @Updatable
    int cores, ram
    @Creatable @Updatable
    String name, availabilityZone = 'AUTO', cpuFamily = 'AMD_OPTERON'
    @Readable
    String vmState, licenseType = 'AUTO'
    @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    final allowReboot = true

    @Override
    final String getResource() { "${dataCenter.resource}/${dataCenter.id}/servers" }

    @Override
    final Server create() { (super.create() as Server)?.with dataCenter }

    @Override
    final Server read(final id = id) { (super.read(id) as Server)?.with dataCenter }

    private final Server with(final DataCenter dc) { dataCenter = dc; this }
}
