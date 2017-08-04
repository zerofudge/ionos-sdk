package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a load balancer POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#load-balancers
 *
 * Created by fudge on 03/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class LoadBalancer extends ModelBase {
    DataCenter dataCenter
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name, ip
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean dhcp = true

    @Override
    final LoadBalancer create() { (super.create() as LoadBalancer)?.with dataCenter }

    @Override
    final LoadBalancer read(final id = id) { (super.read(id) as LoadBalancer)?.with dataCenter }

    @Override
    final String getResource() { "${dataCenter.resource}/${dataCenter.id}/loadbalancers" }

    private final LoadBalancer with(final DataCenter dc) { dataCenter = dc; this }
}
