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

package com.profitbricks.sdk

import com.profitbricks.sdk.model.*
import org.apache.http.client.HttpResponseException

import static com.profitbricks.sdk.Common.*
import static groovyx.net.http.ContentType.URLENC


/**
 * provides commands for provisioned resources
 *
 * @author fudge <frank.geusch@profitbricks.com>
 */
class Commands {

    // --------------------------------- A T T A C H E M E N T   C O M M A N D S ---------------------------------

    /**
     * attaches a given volume or image to a given server
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#attach-a-volume">attach a volume</a>
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#attach-a-cd-rom">attach a cd-rom</a>
     *
     * @param server an existing server
     * @param thing an existing volume or image
     * @return true if attaching worked, false otherwise
     * @throws IllegalArgumentException in case the given thing is neither volume or image
     */
    final static boolean attach(final Server server, final ModelBase thing) {
        waitFor(
            API.post(requestFor("${server.resource}/${server.id}/${pathFor(thing.class)}") + [body: [id: thing.id]])
        )?.status == 202
    }

    /**
     * lists the ids of all attached volumes or images for a given server
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#list-attached-volumes">list attached volumes</a>
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#list-attached-cd-roms">list attached cd-roms</a>
     *
     * @param server a valid server instance
     * @param type one of #Volume (default) or #Image
     * @return a list of volume or image ids
     * @throws IllegalArgumentException in case the given thing is neither volume or image
     */
    static final List<String> attached(final Server server, final Class<? extends ModelBase> attachedType = Volume) {
        API.get(requestFor("${server.resource}/${server.id}/${pathFor(attachedType)}"))?.data?.items?.collect {it.id}
    }
    
    /**
     * detaches a given attached volume from a given server
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#detach-a-volume">detach a volume</a>
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#detach-a-cd-rom">detach a cd-rom</a>
     *
     * @param server an existing server
     * @param thing an existing attached volume or image
     * @return true if detaching worked, false otherwise
     */
    static final boolean detach(final Server server, final ModelBase thing) {
        waitFor(API.delete(requestFor("${server.resource}/${server.id}/${pathFor(thing.class)}/${thing.id}")))?.status == 202
    }

    private final static String pathFor(final Class<? extends ModelBase> clz) {
        switch (clz) {
            case Volume: return 'volumes'
            case Image: return 'cdroms'
            default:
                throw new IllegalArgumentException("unable to access attached resources of class ${clz?.simpleName}")
        }
    }


    // --------------------------------- S E R V E R   C O M M A N D S ---------------------------------

    /**
     * starts a given server
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#start-a-server">start a server</a>
     *
     * @param server
     * @param server an existing server
     * @return true if the start worked, false otherwise
     */
    static final boolean start(final Server server) {
        waitFor(API.post(requestFor("${server.resource}/${server.id}/start")))?.status == 202
    }

    /**
     * stops a given server
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#stop-a-server">stop a server</a>
     *
     * @param server
     * @param server an existing server
     * @return true if the stop worked, false otherwise
     */
    static final boolean stop(final Server server) {
        waitFor(API.post(requestFor("${server.resource}/${server.id}/stop")))?.status == 202
    }

    /**
     * reboots a given server
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#reboot-a-server">reboot a server</a>
     *
     * @param server
     * @param server an existing server
     * @return true if the reboot worked, false otherwise
     */
    static final boolean reboot(final Server server) {
        waitFor(API.post(requestFor("${server.resource}/${server.id}/reboot")))?.status == 202
    }


    // --------------------------------- S N A P S H O T   C O M M A N D S ---------------------------------

    /**
     * creates a snapshot of a given volume
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#create-volume-snapshot">create volume snapshot</a>
     *
     * @param volume an existing volume
     * @param name (optional) a name for the newly created snapshot
     * @param description (optional) the description for the newly created snapshot
     * @return the newly created snapshot
     */
    static final Snapshot snapshot(final Volume volume, final String name = '', final String description = '') {
        def req = requestFor("${volume.resource}/${volume.id}/create-snapshot") + [body: [
            name: name ?: "snapshot_of_${volume.name}",
            description: description ?: "source volume: ${volume.name} (${volume.id}), created: ${new Date().format('dd.MM.yyyy HH:mm')}"
        ]]
        req.requestContentType = URLENC
        def resp = waitFor(API.post(req))
        if (resp?.status == 202) {
            return new Snapshot().from(resp?.data) as Snapshot
        }
        throw new HttpResponseException(resp?.status as int, "snapshot not created")
    }

    /**
     * restores a given volume from a given snapshot
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#restore-volume-snapshot">restore volume snapshot</a>
     *
     * @param volume an existing volume
     * @param snapshot an existing snapshot
     * @return true if the restore worked, false otherwise
     */
    static final boolean restore(final Volume volume, final Snapshot snapshot) {
        final req = requestFor("${volume.resource}/${volume.id}/restore-snapshot") + [body: [snapshotId: snapshot.id]]
        req.requestContentType = URLENC
        waitFor(API.post(req))?.status == 202
    }


    // --------------------------------- L O A D   B A L A N C E R   C O M M A N D S ---------------------------------

    /**
     * associates a given NIC with a given load balancer
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#associate-nic-to-load-balancer">associate nic to load balancer</a>
     *
     * @param an existing load balancer
     * @param nic an existing NIC
     * @return the given (updated) NIC
     */
    static final NIC associate(final LoadBalancer loadBalancer, final NIC nic) {
        def resp = waitFor(API.post(requestFor("${loadBalancer.resource}/${loadBalancer.id}/balancednics") + [body: [id: nic.id]]))
        if (resp?.status == 202) {
            return new NIC(server: nic.server, lan: nic.lan).from(resp?.data) as NIC
        }
        throw new HttpResponseException(resp?.status as int, "NIC not associated")
    }
    
    /**
     * fetches the ids of the associated NICs of a given load balancer
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#list-load-balanced-nics">list load balanced nics</a>
     *
     * @param loadBalancer a valid load balancer instance
     * @return a list of NIC ids
     */
    static final List<String> associatedNICs(final LoadBalancer loadBalancer) {
        API.get(requestFor("${loadBalancer.resource}/${loadBalancer.id}/balancednics"))?.data?.items?.collect {it.id}
    }
    
    /**
     * removes the association of a given NIC and a given load balancer
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#remove-a-nic-association">remove a NIC association</a>
     *
     * @param an existing load balancer
     * @param nic an existing NIC
     * @return true if the association was successfully removed, false otherwise
     */
    static final boolean dissociate(final LoadBalancer loadBalancer, final NIC nic) {
        waitFor(API.delete(requestFor("${loadBalancer.resource}/${loadBalancer.id}/balancednics/${nic.id}")))?.status == 202
    }
    

    // --------------------------------- U S E R   G R O U P   C O M M A N D S ---------------------------------

    /**
     * lists the ids of all users that are assigned to the given group
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#list-users-in-a-group">list users in a group</a>
     *
     * @param group the group with the associated users
     * @return a list of user IDs
     */
    static final List<String> userIDs(final UserGroup group) {
        API.get(requestFor("${group.resource}/${group.id}/users"))?.data?.items?.collect{it.id}
    }

    /**
     * assigns a user to a group
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#add-user-to-group">add user to group</a>
     *
     * @param group the group to assign to
     * @param user the user to be assigned
     * @return true if the user was successfully assigned, false otherwise
     */
    final static boolean assign(final UserGroup group, final User user) {
        waitFor(API.post(requestFor("${group.resource}/${group.id}/users") + [body: [id: user.id]]))?.status == 202
    }

    /**
     * unassigns a user from a group
     * @see <a href="https://devops.profitbricks.com/api/cloud/v4/#remove-user-from-a-group">remove user from a group</a>
     *
     * @param group the group to unassign from
     * @param user the user to be unassigned
     * @return true if the user was successfully unassigned, false otherwise
     */
    final static boolean unassign(final UserGroup group, final User user) {
        waitFor(API.delete(requestFor("${group.resource}/${group.id}/users/${user.id}")))?.status == 202
    }
}
