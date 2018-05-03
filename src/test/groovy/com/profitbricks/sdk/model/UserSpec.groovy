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
@Subject(User)
@Title('tests user CRUD')
@Stepwise
class UserSpec extends Specification {

    private static final rnd = new Random(System.currentTimeMillis())

    @Subject User user

    @Shared String userID, email

    final 'users can be created'() {
        given:
        email = randomEmail
        user = new User(
            firstname: 'John',
            lastname: 'Doe',
            email: email,
            password: 's3cr3tpw',
            administrator: false,
            forceSecAuth: false
        )

        when:
        final _user = user.create()
        userID = _user.id

        then:
        _user instanceof User
        _user.id
    }

    final 'users can be listed'() {
        when:
        final ids = testUser.all

        then: 'the result is non-empty and contains valid UUIDs'
        !ids.empty
        ids.size() == ids.collect {UUID.fromString(it)}.size()
    }

    final 'users can be retrieved'() {
        when:
        user = testUser

        then:
        user.firstname == 'John'
        user.lastname == 'Doe'
        user.email == email
        !user.administrator
        !user.forceSecAuth
        !user.secAuthActive
    }

    final 'users can be updated'() {
        given:
        user = testUser
        email = randomEmail

        when:
        user.firstname = 'Jane'
        user.lastname = 'Doh'
        user.email = email
        user.administrator = true
        user.forceSecAuth = true
        user.update()

        and: 'it is read again'
        user = testUser

        then: 'the changes should be reflected'
        user.firstname == 'Jane'
        user.lastname == 'Doh'
        user.email == email
        user.administrator
        user.forceSecAuth
    }

    final 'users can be deleted'() {
        when:
        testUser.delete()

        then:
        !testUser
    }

    private final User getTestUser() {
        new User(id: userID).read() as User
    }

    final static getRandomEmail() {
        byte[] rbytes = new byte[256]; rnd.nextBytes(rbytes)
        "${new String(rbytes).replaceAll(/[^a-z]+/, '')[0..10]}@random.org"
    }
}
