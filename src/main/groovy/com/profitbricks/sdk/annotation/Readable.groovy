package com.profitbricks.sdk.annotation

import java.lang.annotation.*

/**
 * marks a field as transmittable in READ requests
 *
 * Created by fudge on 21/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface Readable {}
