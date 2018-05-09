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
 * a network interface POGO
 * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#network-interfaces">Cloud API reference</a>
 *
 * @author fudge <frank.geusch@profitbricks.com>
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'server', 'lan'])
@EqualsAndHashCode(callSuper = true)
final class NIC extends ModelBase {
    Server server
    LAN lan
    @Creatable @Updatable
    String name
    @Readable
    String mac
    @Creatable @Updatable
    List<String> ips
    @Creatable @Updatable
    boolean dhcp = true, nat = false, firewallActive = false

    @Override
    final from(Object data) {
        lan = lan ?: new LAN(dataCenter: server?.dataCenter, id: data?.properties?.lan).read()
        super.from(data)
    }

    @Override
    final NIC create(final Map options = [:]) { _from super.create(options) }

    @Override
    final NIC read(final id = id, final Map options = [:]) { _from super.read(id, options) }

    @Override
    final String getResource() { "${server.resource}/${server.id}/nics" }

    @Override
    protected final Map getCreateBody() {
        def body = super.createBody
        body.properties += [lan: lan.id]
        return body
    }

    @Override
    protected final Map getUpdateBody() {
        def body = createBody
        body.properties += [dhcp: dhcp, nat: nat, firewallActive: firewallActive]
        return body
    }

    private final NIC with(final Server s) { server = s; this }
    private final NIC with(final LAN l) { lan = l; this }

    private final _from(final nic) {
        final NIC n = (nic as NIC)?.with(server)
        if (!lan && rawProperties.lan) {
            lan = new LAN(id: rawProperties.lan, dataCenter: server.dataCenter)
        }

        n?.with(lan)
    }
}
