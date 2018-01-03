package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a network interface POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#network-interfaces
 *
 * Created by fudge on 03/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'server', 'lan'])
@EqualsAndHashCode(callSuper = true)
final class NIC extends ModelBase {
    Server server
    LAN lan
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name
    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    String mac
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    List<String> ips
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean dhcp = true, nat = false, firewallActive = false

    @Override
    final from(Object data) {
        lan = lan ?: new LAN(dataCenter: server?.dataCenter, id: data?.properties?.lan).read()
        super.from(data)
    }

    @Override
    final NIC create() { _from super.create() }

    @Override
    final NIC read(final id = id) { _from super.read(id) }

    @Override
    final String getResource() { "${server.resource}/${server.id}/nics" }

    @Override
    protected final Map getCreateBody() {
        def body = super.createBody
        body.properties.lan = lan.id
        return body
    }

    @Override
    protected final Map getUpdateBody() {
        def body = createBody
        body.properties.dhcp = String.valueOf(dhcp)
        body.properties.nat = String.valueOf(nat)
        body.properties.firewallActive = String.valueOf(firewallActive)
        return body
    }

    private final NIC with(final Server s) { server = s; this }
    private final NIC with(final LAN l) { lan = l; this }

    private final _from(final nic) {
        final NIC n = (nic as NIC)?.with(server)
        if (lan) n?.with(lan)
        return n
    }
}
