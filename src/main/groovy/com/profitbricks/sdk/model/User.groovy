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

    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    String firstname, lastname, email
    @Creatable @SuppressWarnings("GroovyUnusedDeclaration")
    String password
    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean administrator, forceSecAuth
    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean secAuthActive

    @Override
    protected final Map getUpdateBody() {
        def body = super.updateBody
        body.properties.administrator = String.valueOf(administrator)
        body.properties.forceSecAuth = String.valueOf(forceSecAuth)
        return body
    }

    final String resource = 'um/users'
}

