package com.profitbricks.sdk

import com.profitbricks.sdk.model.*
import org.apache.http.client.HttpResponseException

import static com.profitbricks.sdk.Common.*
import static groovyx.net.http.ContentType.URLENC

/**
 * provides commands for provisioned resources
 *
 * Created by fudge on 06/02/17.
 * (c)2015 Profitbricks.com
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
     * @return a list of volume ids
     */
    static final List<String> attachedVolumes(final Server server) {
        API.get(requestFor("${server.resource}/${server.id}/volumes"))?.data?.items?.collect{it.id}
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
     * @return a list of image ids
     */
    static final List<String> attachedCDROMs(final Server server) {
        API.get(requestFor("${server.resource}/${server.id}/cdroms"))?.data?.items?.collect{it.id}
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
            return new NIC().from(resp?.data) as NIC
        }
        throw new HttpResponseException(resp?.status as int, "NIC not associated")
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
}
