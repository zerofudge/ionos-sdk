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
    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    long contractNumber

    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    String owner, status

    @Readable @SuppressWarnings("GroovyUnusedDeclaration")
    Limits resourceLimits

    final String resource = 'contracts'

    @Override
    final Contract read() {
        throw new NoSuchMethodException('read not implemented for Contracts. Use `contract` command.')
    }

    @Override
    final Contract create() {
        throw new NoSuchMethodException('create not implemented for Contracts')
    }

    @Override
    final boolean update() {
        throw new NoSuchMethodException('update not implemented for Contracts')
    }

    @ToString(includeNames = true, ignoreNulls = true, includePackage = false)
    private static class Limits {
        @SuppressWarnings("GroovyUnusedDeclaration")
        int coresPerServer, coresPerContract, coresProvisioned, reservableIps, reservedIpsOnContract, reservedIpsInUse
        @SuppressWarnings("GroovyUnusedDeclaration")
        long ramPerServer, ramPerContract, ramProvisioned, hddLimitPerVolume, hddLimitPerContract, hddVolumeProvisioned, ssdLimitPerVolume, ssdLimitPerContract, ssdVolumeProvisioned
    }
}
