package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a network interface POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#network-interfaces
 *
 * Created by fudge on 03/02/17.
 * (c)2017 Profitbricks.com
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
    boolean dhcp = true, nat = false
    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean firewallActive

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
    protected final Map getUpdateBody() { createBody }

    private final NIC with(final Server s) { server = s; this }
    private final NIC with(final LAN l) { lan = l; this }

    private final _from(final nic) {
        final NIC n = (nic as NIC)?.with(server)
        if (lan) n?.with(lan)
        return n

    }
}
