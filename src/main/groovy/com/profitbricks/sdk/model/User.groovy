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

import com.profitbricks.sdk.annotation.Creatable
import com.profitbricks.sdk.annotation.Readable
import com.profitbricks.sdk.annotation.Updatable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * a user POGO
 * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#user-management">Cloud API reference</a>
 *
 * @author nurfet-becirevic
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource'])
@EqualsAndHashCode(callSuper = true)
final class User extends ModelBase {

    @Creatable @Updatable
    String firstname, lastname, email
    @Creatable
    String password
    @Creatable @Updatable
    boolean administrator, forceSecAuth
    @Readable
    boolean secAuthActive

    final String resource = 'um/users'
}

