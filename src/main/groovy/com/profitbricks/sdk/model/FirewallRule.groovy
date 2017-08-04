package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a firewall rule POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#firewall-rules
 *
 * Created by fudge on 03/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'nic'])
@EqualsAndHashCode(callSuper = true)
final class FirewallRule extends ModelBase {
    NIC nic
    @SuppressWarnings("GroovyUnusedDeclaration")
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
