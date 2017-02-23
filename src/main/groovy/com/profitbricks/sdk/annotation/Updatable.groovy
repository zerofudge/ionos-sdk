package com.profitbricks.sdk.annotation

import java.lang.annotation.*

/**
 * marks a field as transmittable in UPDATE requests
 *
 * Created by fudge on 20/02/17.
 * (c)2015 Profitbricks.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface  Updatable {}
