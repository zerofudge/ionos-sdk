package com.profitbricks.sdk.model

import com.profitbricks.sdk.annotation.*
import groovy.transform.*

/**
 * a storage volume POGO
 * see: https://devops.profitbricks.com/api/cloud/v4/#volumes
 *
 * Created by fudge on 03/02/17.
 */
@ToString(includeNames = true, ignoreNulls = true, includeSuperProperties = true, includePackage = false, excludes = ['resource', 'dataCenter'])
@EqualsAndHashCode(callSuper = true)
final class Volume extends ModelBase {
    DataCenter dataCenter

    @Creatable
    String type = 'HDD',
           availabilityZone = 'AUTO',
           image,
           licenceType = 'UNKNOWN',
           imageAlias,
           imagePassword
    @Creatable @Updatable
    String name,  bus = 'VIRTIO'
    @Creatable
    List<String> sshKeys
    @Creatable
    int size
    @Readable
    int deviceNumber

    @Override
    final Volume create() {
        (super.create() as Volume)?.with dataCenter
    }

    @Override
    final Volume read(final id = id) {
        (super.read(id) as Volume)?.with dataCenter
    }

    @Override
    final String getResource() {
        "${dataCenter.resource}/${dataCenter.id}/volumes"
    }

    private final Volume with(final DataCenter dc) {
        dataCenter = dc; this
    }
}
