package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Creatable
import com.profitbricks.sdk.annotation.Readable
import com.profitbricks.sdk.annotation.Updatable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * a resource POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#user-management
 *
 * Created by nurfet-becirevic on 15/09/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Resource extends ModelBase {
    String type

    @Override
    final Resource read(final id = id) { (super.read(id) as Resource)?.of type }

    @Override
    final String getResource() { "um/resources/${type}" }

    @Override
    final Resource create() {
        throw new NoSuchMethodException('create not implemented for Resource')
    }

    @Override
    final boolean update() {
        throw new NoSuchMethodException('update not implemented for Resource')
    }

    private final Resource of(final String t) { type = t; this }
}

