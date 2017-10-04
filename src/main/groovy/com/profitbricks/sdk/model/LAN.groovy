package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a LAN POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#lans
 *
 * Created by fudge on 03/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class LAN extends ModelBase {
    DataCenter dataCenter
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean _public = true
    @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    List<IPFailover> ipFailover

    @ToString(includeNames = true, ignoreNulls = true, includePackage = false)
    static class IPFailover {
        @SuppressWarnings("GroovyUnusedDeclaration")
        String ip, nicUuid
    }

    @Override
    final getId() { super.id as Integer }

    @Override
    final LAN create() { (super.create() as LAN)?.with(dataCenter) }

    @Override
    final LAN read(final id = id) { (super.read(id) as LAN)?.with(dataCenter) }

    @Override
    final String getResource() { "${dataCenter.resource}/${dataCenter.id}/lans" }

    private final LAN with(final DataCenter dc) { dataCenter = dc; this }
}
