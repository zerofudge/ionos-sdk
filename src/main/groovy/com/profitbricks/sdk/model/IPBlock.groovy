package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Creatable
import groovy.transform.*

/**
 * an (reserved) IP block POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#ip-block
 *
 * Created by fudge on 03/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class IPBlock extends ModelBase {
    @Creatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name, location
    @Creatable @SuppressWarnings("GroovyUnusedDeclaration")
    int size = 1
    @SuppressWarnings("GroovyUnusedDeclaration")
    List<String> ips

    final String resource = 'ipblocks'

    /**
     * IP blocks cannot be updated via the API, this just throws
     * @see NoSuchMethodException
     */
    @Override
    final boolean update() {
        throw new NoSuchMethodException('update not implemented for IPBlocks')
    }
}
