package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.Readable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * a contract resources POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#contract-resources
 *
 * Created by nurfet-becirevic on 18/09/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class Contract extends ModelBase {
    @Readable
    long contractNumber
    @Readable
    String owner, status
    @Readable
    Limits resourceLimits

    final String resource = 'contracts'

    @Override
    final Contract create() {
        throw new NoSuchMethodException('create not implemented for Contracts')
    }

    @Override
    final boolean update() {
        throw new NoSuchMethodException('update not implemented for Contracts')
    }

    @ToString(includeNames = true, ignoreNulls = true, includePackage = false)
    private final class Limits {
        @SuppressWarnings("GroovyUnusedDeclaration")
        int coresPerServer, coresPerContract, coresProvisioned, reservableIps, reservedIpsOnContract, reservedIpsInUse
        @SuppressWarnings("GroovyUnusedDeclaration")
        long ramPerServer, ramPerContract, ramProvisioned, hddLimitPerVolume, hddLimitPerContract, hddVolumeProvisioned, ssdLimitPerVolume, ssdLimitPerContract, ssdVolumeProvisioned
    }
}
