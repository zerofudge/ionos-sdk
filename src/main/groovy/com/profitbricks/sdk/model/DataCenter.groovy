package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a data center POGO
 * see: https://devops.profitbricks.com/api/cloud/v3/#data-centers
 *
 * Created by fudge on 31/01/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
class DataCenter extends ModelBase {
    @Creatable @Updatable
    String name, description
    @Creatable
    String location

    //@Override
    final String resource = 'datacenters'
}
