package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a group POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#user-management
 *
 * Created by nurfet-becirevic on 14/09/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Group extends ModelBase {

    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String name
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean createDataCenter, createSnapshot, reserveIp, accessActivityLog

    @Override
    protected final Map getUpdateBody() {
        def body = super.updateBody
        body.properties.createDataCenter = String.valueOf(createDataCenter)
        body.properties.createSnapshot = String.valueOf(createSnapshot)
        body.properties.reserveIp = String.valueOf(reserveIp)
        body.properties.accessActivityLog = String.valueOf(accessActivityLog)
        return body
    }

    final String resource = 'um/groups'
}

