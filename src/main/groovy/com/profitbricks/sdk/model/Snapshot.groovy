package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a snapshot POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#snapshots
 *
 * Created by fudge on 06/02/17.
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Snapshot extends ModelBase {

    @Creatable @Updatable
    String name, description, location, licenceType
    @Creatable @Updatable
    boolean cpuHotPlug, cpuHotUnplug, ramHotPlug, ramHotUnplug, nicHotPlug, nicHotUnplug, discVirtioHotPlug, discVirtioHotUnplug, discScsiHotPlug, discScsiHotUnplug
    @Creatable @Updatable
    int size

    final String resource = 'snapshots'
}
