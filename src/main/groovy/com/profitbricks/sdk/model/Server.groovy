package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a server POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#servers
 *
 * Created by fudge on 31/01/17.
 * (c)2017 Profitbricks.com
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class Server extends ModelBase {
    DataCenter dataCenter
    @Creatable @Updatable
    int cores, ram
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name, availabilityZone = 'AUTO', cpuFamily = 'AMD_OPTERON'
    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    String vmState, licenseType = 'AUTO'

    @Override
    final String getResource() { "${dataCenter.resource}/${dataCenter.id}/servers" }

    @Override
    final Server create() { (super.create() as Server)?.with dataCenter }

    @Override
    final Server read(final id = id) { (super.read(id) as Server)?.with dataCenter }

    private final Server with(final DataCenter dc) { dataCenter = dc; this }
}
