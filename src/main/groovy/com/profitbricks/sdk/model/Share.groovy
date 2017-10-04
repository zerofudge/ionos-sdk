package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Creatable
import com.profitbricks.sdk.annotation.Updatable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * a share POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#user-management
 *
 * Created by nurfet-becirevic on 15/09/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'group'])
@EqualsAndHashCode(callSuper = true)
final class Share extends ModelBase {
    Group group

    @Creatable @Updatable @SuppressWarnings("GroovyUnusedDeclaration")
    boolean editPrivilege, sharePrivilege

    @Override
    final Map getCreateBody() {
        return super.createBody
    }

    @Override
    protected final Map getUpdateBody() {
        def body = super.updateBody
        body.properties.editPrivilege = String.valueOf(editPrivilege)
        body.properties.sharePrivilege = String.valueOf(sharePrivilege)
        return body
    }

    @Override
    final Resource create() {
        throw new NoSuchMethodException('create not implemented for Share. Use `share` command.')
    }

    @Override
    final Share read(final id = id) { (super.read(id) as Share)?.with group }

    @Override
    final String getResource() { "${group.resource}/${group.id}/shares" }

    private final Share with(final Group g) { group = g; this }
}

