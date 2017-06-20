package com.profitbricks.sdk.annotation

import java.lang.annotation.*

/**
 * marks a field as transmittable in CREATE requests
 *
 * Created by fudge on 20/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface Creatable {}
