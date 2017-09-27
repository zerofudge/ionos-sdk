package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a Request POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#requests
 *
 * Created by ali bazlamit on 30/03/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Request extends ModelBase {
    String status
    String message
    String etag
    Targets targets

    static class Targets {
        String status
        Target target
    }
    
    static class Target {
        String id
        String type
        String href
    }
    
    final String resource = 'requests'
    @Override
    final String getResource() { "requests" }
}

