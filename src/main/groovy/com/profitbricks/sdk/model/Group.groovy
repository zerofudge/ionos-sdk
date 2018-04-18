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

    @Creatable @Updatable
    String name
    @Creatable @Updatable
    boolean createDataCenter, createSnapshot, reserveIp, accessActivityLog

    final String resource = 'um/groups'
}

