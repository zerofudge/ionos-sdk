package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Updatable
import groovy.transform.*

/**
 * an image POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#images
 *
 * Created by fudge on 03/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Image extends ModelBase {
    @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name, description, location, licenceType, imageType
    @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    int size
    @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean cpuHotPlug, cpuHotUnplug, ramHotPlug, ramHotUnplug, nicHotPlug, nicHotUnplug, discVirtioHotPlug, discVirtioHotUnplug, discScsiHotPlug, discScsiHotUnplug, _public

    /**
     * images cannot be created via the API, this just throws
     * @see NoSuchMethodException
     */
    @Override
    final Image create() {
        throw new NoSuchMethodException('create not implemented for Images')
    }

    final String resource = 'images'
}
