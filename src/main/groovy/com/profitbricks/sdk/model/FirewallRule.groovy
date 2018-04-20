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
 * a firewall rule POGO
 * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#firewall-rules">Cloud API reference</a>
 *
 * @author fudge <frank.geusch@profitbricks.com>
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'nic'])
@EqualsAndHashCode(callSuper = true)
final class FirewallRule extends ModelBase {
    NIC nic
    @Creatable @Updatable
    String name, sourceMac, sourceIp, targetIp, portRangeStart, portRangeEnd, icmpType, icmpCode
    @Creatable
    String protocol

    @Override
    final FirewallRule create() { (super.create() as FirewallRule)?.with(nic) }

    @Override
    final FirewallRule read(final id = id) { (super.read(id) as FirewallRule)?.with(nic) }

    @Override
    final String getResource() { "${nic.resource}/${nic.id}/firewallrules" }

    private final FirewallRule with(NIC n) {nic = n; this}
}
