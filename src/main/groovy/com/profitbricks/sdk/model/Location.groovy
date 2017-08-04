package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Readable
import groovy.transform.*

/**
 * a location POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#locations
 *
 * Created by fudge on 31/01/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
class Location extends ModelBase {
    @Readable
    String name
    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    List<String> features

    final String resource = 'locations'

    /**
     * locations cannot be created via the API, this just throws
     * @see NoSuchMethodException
     */
    @Override
    final Location create() {
        throw new NoSuchMethodException('create not implemented for Locations')
    }

    /**
     * locations cannot be updated via the API, this just throws
     * @see NoSuchMethodException
     */
    @Override
    final boolean update() {
        throw new NoSuchMethodException('update not implemented for Locations')
    }
}
