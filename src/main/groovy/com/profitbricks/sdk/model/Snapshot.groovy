package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a snapshot POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#snapshots
 *
 * Created by fudge on 06/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Snapshot extends ModelBase {

    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name, description, location, licenceType
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean cpuHotPlug, cpuHotUnplug, ramHotPlug, ramHotUnplug, nicHotPlug, nicHotUnplug, discVirtioHotPlug, discVirtioHotUnplug, discScsiHotPlug, discScsiHotUnplug
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    int size

    final String resource = 'snapshots'
}
