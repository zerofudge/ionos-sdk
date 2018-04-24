/*
   Copyright 2018 Profitbricks GmbH

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.profitbricks.sdk.model

import spock.lang.*

/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(ContractStats)
@Title("unit tests contract resources CRUD")
@Stepwise
class ContractStatsSpec extends Specification {

    @Subject ContractStats stats

    final 'contract resources can be read'() {
        when:
        stats = new ContractStats().read() as ContractStats

        then:
        stats.contractNumber > 0
        stats.owner == System.properties['api.user']
        stats.status == 'BILLABLE'
        stats.resourceLimits.coresPerServer > 0
        stats.resourceLimits.coresPerContract > 0
        stats.resourceLimits.coresProvisioned >= 0
        stats.resourceLimits.reservableIps > 0
        stats.resourceLimits.reservedIpsOnContract >= 0
        stats.resourceLimits.reservedIpsInUse >= 0
        stats.resourceLimits.ramPerServer > 0
        stats.resourceLimits.ramPerContract > 0
        stats.resourceLimits.ramProvisioned >= 0
        stats.resourceLimits.hddLimitPerVolume > 0
        stats.resourceLimits.hddLimitPerContract > 0
        stats.resourceLimits.hddVolumeProvisioned >= 0
        stats.resourceLimits.ssdLimitPerVolume > 0
        stats.resourceLimits.ssdLimitPerContract > 0
        stats.resourceLimits.ssdVolumeProvisioned >= 0
    }
}
