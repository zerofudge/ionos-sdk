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
 * a load balancer POGO
 * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#load-balancers">Cloud API reference</a>
 *
 * @author fudge <frank.geusch@profitbricks.com>
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class LoadBalancer extends ModelBase {
    DataCenter dataCenter
    @Creatable @Updatable
    String name, ip
    @Creatable @Updatable
    boolean dhcp = true

    @Override
    final LoadBalancer create(final Map options = [:]) { (super.create(options) as LoadBalancer)?.with dataCenter }

    @Override
    final LoadBalancer read(final id = id, final Map options = [:]) { (super.read(id, options) as LoadBalancer)?.with dataCenter }

    @Override
    final String getResource() { "${dataCenter.resource}/${dataCenter.id}/loadbalancers" }

    private final LoadBalancer with(final DataCenter dc) { dataCenter = dc; this }
}
