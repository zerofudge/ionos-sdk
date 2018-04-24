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

import com.profitbricks.sdk.Commands
import spock.lang.*


/**
 * @author fudge <frank.geusch@profitbricks.com>
 */
@Subject(UserGroup)
@Title('tests user group CRUD and commands')
@Stepwise
class UserGroupSpec extends Specification {

    @Subject UserGroup group

    @Shared String groupID
    @Shared User user

    final 'user groups can be created'() {
        given:
        group = new UserGroup(
            name: 'Groovy SDK Testgroup',
            createDataCenter: true,
            createSnapshot: true,
            reserveIp: true,
            accessActivityLog: true
        )

        when:
        final _group = group.create()
        groupID = _group.id

        then:
        _group instanceof UserGroup
        _group.id
    }

    final 'user groups can be listed'() {
        when:
        final ids = testGroup.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'user groups can be retrieved'() {
        when:
        group = testGroup

        then:
        group.name == 'Groovy SDK Testgroup'
        group.createDataCenter
        group.createSnapshot
        group.reserveIp
        group.accessActivityLog
    }

    final 'user groups can be updated'() {
        given:
        group = testGroup

        when:
        group.name = 'foo'
        group.createDataCenter = false
        group.createSnapshot = false
        group.reserveIp = false
        group.accessActivityLog = false
        group.update()

        and: 'it is read again'
        group = testGroup

        then: 'the changes should be reflected'
        group.name == 'foo'
        !group.createDataCenter
        !group.createSnapshot
        !group.reserveIp
        !group.accessActivityLog
    }

    final 'users can be assigned to a user group'() {
        given:
        group = testGroup
        user = new User(firstname: 'John', lastname: 'Doe', email: UserSpec.randomEmail, password: 's3cr3tpw').create() as User

        when:
        Commands.assign group, user

        then:
        Commands.userIDs(group).contains(user.id)
    }

    final 'users can be unassigned from a user group'() {
        when:
        group = testGroup
        Commands.unassign group, user

        then:
        !Commands.userIDs(group).contains(user.id)

        cleanup:
        user.delete()
    }

    final 'user groups can be deleted'() {
        when:
        testGroup.delete()

        then:
        !testGroup
    }

    private final UserGroup getTestGroup() {
        new UserGroup(id: groupID).read() as UserGroup
    }
}
