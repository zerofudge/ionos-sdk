package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Creatable
import com.profitbricks.sdk.annotation.Readable
import com.profitbricks.sdk.annotation.Updatable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * a user POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#user-management
 *
 * Created by nurfet-becirevic on 14/09/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class User extends ModelBase {

    @Creatable @Updatable
    String firstname, lastname, email
    @Creatable
    String password
    @Creatable @Updatable
    boolean administrator, forceSecAuth
    @Readable
    boolean secAuthActive

    final String resource = 'um/users'
}

