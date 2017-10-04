package com.profitbricks.sdk

import com.profitbricks.sdk.model.*
import org.apache.http.client.HttpResponseException

import static com.profitbricks.sdk.Common.*
import static groovyx.net.http.ContentType.URLENC

/**
 * provides commands for provisioned resources
 *
 * Created by fudge on 06/02/17.
 * Copyright (c) 2017, ProfitBricks GmbH
 */
class Commands {

    // --------------------------------- S E R V E R   C O M M A N D S ---------------------------------

    /**
     * attaches a given volume to a given server
     *
     * @param server an existing server
     * @param volume an existing volume
     * @return true if attaching worked, false otherwise
     */
    final static boolean attach(final Server server, final Volume volume) {
        waitFor(API.post(requestFor("${server.resource}/${server.id}/volumes") + [body: [id: volume.id]]))?.status == 202
    }

    /**
     * gets the attached volumes for a given server
     *
     * @param server the server with the attached volumes
     * @return a list of attached volumes
     */
    static final List<Volume> attachedVolumes(final Server server) {
        API.get(requestFor("${server.resource}/${server.id}/volumes"))?.data?.items?.collect{new Volume().from(it) as Volume}
    }
    
    /**
     * This will retrieve the properties of an attached volume.
     *
     * @param server the server with the attached volume
     * @param volume an existing volume
     * @returns the volume attached
     */
    static final Volume attachedVolume(final Server server,final String volumeId) {
        return new Volume().from(API.get(requestFor("${server.resource}/${server.id}/volumes/${volumeId}"))?.data) as Volume
    }

    /**
     * detaches a given attached volume from a given server
     *
     * @param server an existing server
     * @param volume an existing attached volume
     * @return true if detaching worked, false otherwise
     */
    static final boolean detach(final Server server, final Volume volume) {
        waitFor(API.delete(requestFor("${server.resource}/${server.id}/volumes/${volume.id}")))?.status == 202
    }

    /**
     * attaches a given CD-ROM image to a given server
     *
     * @param server an existing server
     * @param cdROM an existing, accessible CD-ROM image
     * @return true if attaching worked, false otherwise
     */
    static final boolean attach(final Server server, final Image cdROM) {
        waitFor(API.post(requestFor("${server.resource}/${server.id}/cdroms") + [body: [id: cdROM.id]]))?.status == 202
    }

    /**
     * gets the attached CD-ROM images for a given server
     *
     * @param server the server with the attached volumes
     * @return a list of images
     */
    static final List<Image> attachedCDROMs(final Server server) {
        API.get(requestFor("${server.resource}/${server.id}/cdroms"))?.data?.items?.collect{new Image().from(it) as Image}
    }
    
    /**
     * gets the attached CD-ROM image for a given server
     * @param cdROM an existing, accessible CD-ROM image
     * @param server the server with the attached volumes
     * @return the image attached
     */
    static final Image attachedCDROM(final Server server, final String cdROMId) {
        return new Image().from(API.get(requestFor("${server.resource}/${server.id}/cdroms/${cdROMId}"))?.data) as Image
    }

    /**
     * detaches a given attached CD-ROM image from a given server
     *
     * @param server an existing server
     * @param volume an existing attached CD-ROM image
     * @return true if detaching worked, false otherwise
     */
    static final boolean detach(final Server server, final Image cdROM) {
        waitFor(API.delete(requestFor("${server.resource}/${server.id}/cdroms/${cdROM.id}")))?.status == 202
    }

    /**
     * starts a given server
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
     *
     * @param server
     * @param server an existing server
     * @return true if the reboot worked, false otherwise
     */
    static final boolean reboot(final Server server) {
        waitFor(API.post(requestFor("${server.resource}/${server.id}/reboot")))?.status == 202
    }


    // --------------------------------- V O L U M E   C O M M A N D S ---------------------------------

    /**
     * creates a snapshot of a given volume
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
     *
     * @param an existing load balancer
     * @param nic an existing NIC
     * @return true if the association worked, false otherwise
     */
    static final NIC associate(final LoadBalancer loadBalancer, final NIC nic) {
        def resp = waitFor(API.post(requestFor("${loadBalancer.resource}/${loadBalancer.id}/balancednics") + [body: [id: nic.id]]))
        if (resp?.status == 202) {
            return new NIC(server: nic.server, lan: nic.lan).from(resp?.data) as NIC
        }
        throw new HttpResponseException(resp?.status as int, "NIC not associated")
    }
    
    /**
     * gets the associated NICS of a loadbalancer
     *
     * @param LoadBalancer the LoadBalancer with the associated NICS
     * @return a list of associated NICS
     */
    static final List<String> associatedNics(final LoadBalancer loadBalancer) {
        API.get(requestFor("${loadBalancer.resource}/${loadBalancer.id}/balancednics"))?.data?.items?.collect{it.id}
    }
    
    /**
     * removes the association of a given NIC and a given load balancer
     *
     * @param an existing load balancer
     * @param nic an existing NIC
     * @return true if the association was successfully removed, false otherwise
     */
    static final boolean dissociate(final LoadBalancer loadBalancer, final NIC nic) {
        waitFor(API.delete(requestFor("${loadBalancer.resource}/${loadBalancer.id}/balancednics/${nic.id}")))?.status == 202
    }
    
    // --------------------------------- R E Q U E S T  C O M M A N D S ---------------------------------
    
    /**
     * Returns a request status
     *
     * @param requestId id of the request you need the status for
     * @return Request with status details
     */
    static final Request requestStatus(final String requestId) {
        def data=API.get(requestFor("requests/${requestId}/status"))?.data
        Request request=data.metadata as Request
        request.id=data.id.split('/')[0]
        return request
    }

    // --------------------------------- G R O U P   C O M M A N D S ---------------------------------

    /**
     * gets a list of all the users that are members of a particular group
     *
     * @param group the group with the associated users
     * @return a list of user IDs
     */
    static final List<String> listGroupUsers(final Group group) {
        API.get(requestFor("${group.resource}/${group.id}/users"))?.data?.items?.collect{it.id}
    }

    /**
     * adds a user to a group
     *
     * @param group the group to add a user to
     * @param user the user to be added
     * @return true if the user was successfully added, false otherwise
     */
    final static boolean addGroupUser(final Group group, final User user) {
        waitFor(API.post(requestFor("${group.resource}/${group.id}/users") + [body: [id: user.id]]))?.status == 202
    }

    /**
     * removes a user from a group
     *
     * @param group the group to remove a user from
     * @param user the user to be removed
     * @return true if the user was successfully removed, false otherwise
     */
    final static boolean removeGroupUser(final Group group, final User user) {
        waitFor(API.delete(requestFor("${group.resource}/${group.id}/users/${user.id}")))?.status == 202
    }

    // --------------------------------- S H A R E   C O M M A N D S ---------------------------------

    /**
     * adds a share resource to a given group
     *
     * @param group an existing group
     * @param resourceId ID of an existing resource to share
     * @param editPrivilege the group has permission to edit privileges on the resource
     * @param sharePrivilege the  group has permission to share the resource
     * @return the share added
     */
    static final Share share(final Group group, final String resourceId, final boolean editPrivilege = false, final boolean sharePrivilege = false) {
        Share share = new Share(group: group, editPrivilege: editPrivilege, sharePrivilege: sharePrivilege)
        def resp = waitFor(API.post(requestFor("${group.resource}/${group.id}/shares/${resourceId}") + [body: share.getCreateBody()]))
        if (resp?.status == 202) {
            share = share.from(resp?.data) as Share
            share.group = group
            return share
        }
        throw new HttpResponseException(resp?.status as int, "Share not added")
    }

    // --------------------------------- R E S O U R C E   C O M M A N D S ---------------------------------

    /**
     * gets all available resources of all types
     *
     * @return a list of resources
     */
    static final List<Resource> resources() {
        API.get(requestFor("um/resources"))?.data?.items?.collect{
            Resource r = new Resource().from(it) as Resource; r.type = it.type; r
        }
    }

    // --------------------------------- C O N T R A C T   C O M M A N D S ---------------------------------

    /**
     * gets information about the resource limits for a particular contract and the current resource usage
     *
     * @return a contract
     */
    static final Contract contract() {
        return new Contract().from(API.get(requestFor("contracts"))?.data) as Contract
    }
}
