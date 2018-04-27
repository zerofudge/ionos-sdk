# Groovy SDK

Version: **3.0.0**

![Profitbricks Groovy/Java SDK](grooy-sdk-logo.png)

## Table of Contents

* [Abstract](#abstract)
* [Design Objective](#design-objective)
* [Getting Started](#getting-started)
    * [Installation](#installation)
    * [Configuration](#configuration)
* [SDK Reference](#sdk-reference)
    * [Data Centers](#datacenters)
      * [List Data Centers](#list-datacenters)
      * [Retrieve a Data Center](#retrieve-a-datacenter)
      * [Create a Data Center](#create-a-datacenter)
      * [Update a Data Center](#update-a-datacenter)
      * [Delete a Data Center](#delete-a-datacenter)
    * [Locations](#locations)
      * [List Locations](#list-locations)
      * [Retrieve a Location](#retrieve-a-location)
    * [Servers](#servers)
      * [List Servers](#list-servers)
      * [Retrieve a Server](#retrieve-a-server)
      * [Create a Server](#create-a-server)
      * [Update a Server](#update-a-server)
      * [Delete a Server](#delete-a-server)
      * [List Attached Volumes](#list-attached-volumes)
      * [Attach a Volume](#attach-a-volume)
      * [Detach a Volume](#detach-a-volume)
      * [List Attached ](#list-attached-images)
      * [Attach an Image](#attach-an-image)
      * [Detach an Image](#detach-an-image)
      * [Reboot a Server](#reboot-a-server)
      * [Start a Server](#start-a-server)
      * [Stop a Server](#stop-a-server)
    * [Images](#images)
      * [List Images](#list-images)
      * [Retrieve an Image](#retrieve-an-image)
    * [Volumes](#volumes)
      * [List Volumes](#list-volumes)
      * [Retrieve a Volume](#retrieve-a-volume)
      * [Create a Volume](#create-a-volume)
      * [Update a Volume](#update-a-volume)
      * [Delete a Volume](#delete-a-volume)
      * [Create a Volume Snapshot](#create-a-volume-snapshot)
      * [Restore a Volume Snapshot](#restore-a-volume-snapshot)
    * [Snapshots](#snapshots)
      * [List Snapshots](#list-snapshots)
      * [Retrieve a Snapshot](#retrieve-a-snapshot)
      * [Update a Snapshot](#update-a-snapshot)
      * [Delete a Snapshot](#delete-a-snapshot)
    * [IP Blocks](#ip-blocks)
      * [List IP Blocks](#list-ip-blocks)
      * [Retrieve an IP Block](#retrieve-an-ip-block)
      * [Create an IP Block](#create-an-ip-block)
      * [Delete an IP Block](#delete-an-ip-block)
    * [LANs](#lans)
      * [List LANs](#list-lans)
      * [Retrieve a LAN](#retrieve-a-lan)
      * [Create a LAN](#create-a-lan)
      * [Update a LAN](#update-a-lan)
      * [Delete a LAN](#delete-a-lan)
    * [Network Interfaces (NICs)](#network-interfaces)
      * [List NICs](#list-nics)
      * [Retrieve a NIC](#retrieve-a-nic)
      * [Create a NIC](#create-a-nic)
      * [Update a NIC](#update-a-nic)
      * [Delete a NIC](#delete-a-nic)
    * [Firewall Rules](#firewall-rules)
      * [List Firewall Rules](#list-firewall-rules)
      * [Retrieve a Firewall Rule](#retrieve-a-firewall-rule)
      * [Create a Firewall Rule](#create-a-firewall-rule)
      * [Update a Firewall Rule](#update-a-firewall-rule)
      * [Delete a Firewall Rule](#delete-a-firewall-rule)
    * [Load Balancers](#load-balancers)
      * [List Load Balancers](#list-load-balancers)
      * [Retrieve a Load Balancer](#retrieve-a-load-balancer)
      * [Create a Load Balancer](#create-a-load-balancer)
      * [Update a Load Balancer](#update-a-load-balancer)
      * [List Load Balanced NICs](#list-load-balanced-nics)
      * [Associate NIC with a Load Balancer](#associate-a-nic-with-a-load-balancer)
      * [Remove a NIC-Load-Balancer Association](#remove-a-nic-association)
    * [Users](#users)
      * [List Users](#list-users)
      * [Retrieve a User](#retrieve-a-user)
      * [Create a User](#create-a-user)
      * [Update a User](#update-a-user)
      * [Delete a User](#delete-a-user)
    * [User Groups](#user-groups)
      * [List User Groups](#list-user-groups)
      * [Retrieve a User Group](#retrieve-a-user-group)
      * [Create a User Group](#create-a-user-group)
      * [Update a User Group](#update-a-user-group)
      * [Delete a User Group](#delete-a-user-group)
      * [List Group Users](#list-group-users)
      * [Add a User to a Group](#add-a-user-to-a-group)
      * [Remove a User from a Group](#remove-a-user-from-a-group)
    * [Retrieve Contract Resource Statistics](#contract-resources)
* [Example](#a-concise-example)
* [TODO](#todo)
* [Support](#support)
* [Testing](#testing)


## Abstract

This Groovy SDK provides a convenient way to use the ProfitBricks Cloud API from JVM based applications.
This guide will show you how to programmatically perform infrastructure management operations with it.


## Design Objective

There is essentially only one usage pattern: Create an entity, then invoke one of the CRUD methods on it:

- `create()`: Effectively send a resource creation REST request (`HTTP CREATE`).
- `read()`: Fetch an existing REST resource by its UUID (`HTTP GET`).
- `update()`: Send an update to an existing REST resource (`HTTP PUT`).
- `delete()`: Delete an existing resource (`HTTP DELETE`).
- `all`: This property implements a *list* operation to fetch UUIDs of existing REST resources.

An entity must be sufficiently populated in order for the associated REST request to succeed. No extra validation will take place. If there is an error, you will see an exception with the cause. 

For resources that exist within the context of another resource, that other resource must also be properly instantiated and injected.

_For example_: to successfully operate on a network interface, the entity (`NIC`) must also contain proper `Server` and `LAN` instances.

No extra modeling was made (e.g. no *parent* links). The entities just enclose the transported JSON representations.

On a successfully-delivered `POST`, `PUT` or `DELETE` request, the Profitbricks API might send a `Location` header instead of the final resulting response to the requested action. This SDK will block on such a response, then continue polling for the final result. If a successful result does not come
in time, an exception will be thrown.

This was made to allow for easy and agile API scripting, as the result matters most of the time, and such a behavior would be needed anyway.

For API resources which do not fit into the CRUD scheme, an extra command facade is provided. This provides functionality like attaching and detaching storage volumes to virtual servers or associating network interfaces with load balancers.

This implementation is based on Groovy 2, so it technically runs on any JVM version 7 or later.

Before you begin you will need to have [signed up](https://www.profitbricks.com/signup) for a ProfitBricks account. The credentials you set up during the sign-up process will be used to authenticate against the Cloud API.


## Getting Started

### Installation

This SDK is available from the [ProfitBricks GitHub Page](https://github.com/profitbricks/profitbricks-sdk-groovy) as well as [Maven Central](https://oss.sonatype.org/#TBD)

- **optional**: build and deploy it to your local maven repository:

```
./gradlew assemble publishToMavenLocal -x test` 
```

- add the dependency to your project:
  - example: gradle

        dependencies {
            compile 'com.profitbricks:groovy-sdk:3.0.0'
        }

  - example: maven

        <dependency>
		    <groupId>com.profitbricks</groupId>
		    <artifactId>groovy-sdk</artifactId>
		    <version>3.0.0</version>
        </dependency>

### Configuration

The most convenient way to configure the API client is to use system properties. The following table lists all those properties along with their default value (if any).

**System Properties**

| name | default | notes |
|---|---|---|
| `com.profitbricks.sdk.verifySSL` | `true` | set to `false` to ignore SSL certificate verification issues |
| `com.profitbricks.sdk.user`| - | the API user name for basic authentication. **required** |
| `com.profitbricks.sdk.password` | - | the API password for basic authentication. **required**|
| `com.profitbricks.sdk.wait.init.milliseconds` | 100 | if waiting for success, this is the initial time period between two checks. |
| `com.profitbricks.sdk.wait.timeout.seconds` | 120 | if waiting for success, this is the timeout. |
| `com.profitbricks.sdk.wait.max.milliseconds` | 1500 | if waiting for success, this is the maximum time period between two checks. |
| `com.profitbricks.sdk.wait.factor`| 1.87 | if waiting for success, this is the factor by which the current time period value is multiplied. |


Individual configuration values can also be overridden with each individual request.

All CRUD (and list) methods as well as all commands can be invoked with an optional map as the last parameter. The keys in this map are expected to be named like the corresponding system property **minus** the prefix `com.profitbricks.sdk.`.

**Note:** `verifySSL` can only be configured via system property and not be overridden.

```groovy
// for example
datacenter.create(user: 'otheruser', password: 'otherpassword', 'wait.factor': Math.PI)
```


## SDK Reference

This section provides details on all the available operations and the parameters they accept. Also included are brief code snippets illustrating its usage.


### datacenters

Virtual data centers (VDCs) are the foundation of the ProfitBricks platform. VDCs act as logical containers for all other objects you will be creating, e.g., servers. You can provision as many VDCs as you want. VDCs have their own private network and are logically isolated from each other.

#### list datacenters

Lists the ids of all currently provisioned datacenters that are accessible for the current user.

```groovy
List<String> datacenterIDs = new DataCenter().all
assert datacenterIDs : 'no datacenters found!'
```

#### retrieve a datacenter

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```groovy
assert datacenterId : 'datacenter id missing!'
DataCenter datacenter = new DataCenter(id: datacenterId).read()
assert datacenter : 'no such datacenter!'
```

#### create a datacenter

| Argument | Required |
|---|---|
| `DataCenter::name` | **yes** |
| `DataCenter::location` | **yes** | 
| `DataCenter::description` | no |

**Supported Locations**

| value | geographical location 
|---|---|
| `us/las` | US / Las Vegas |
| `us/ewr` | US / Newark |
| `de/fra` | Germany / Frankfurt |
| `de/fkb` | Germany / Karlsruhe |

```groovy
def dc = new DataCenter(
	name: "groovy name",
	location: 'us/ewr',
	description: 'groovy description'
).create()
assert dc.id : 'datacenter creation failed!'
```

#### update a datacenter

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `DataCenter::name` | no |
| `DataCenter::description` | no |

```groovy
assert datacenter : 'datacenter missing!'
dc.name = "updated name from ${dc.name}"
dc.description = "groovy new datacenter! (old: ${dc.description})"
assert dc.update() : 'datacenter update failed!'
```

#### delete a datacenter

Removes all objects within the virtual data center AND removes the virtual data center object itself.

**NOTE**: This is a highly destructive operation which should be used with extreme caution.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
assert dc.delete() : 'datacenter deletion failed!'
```


### Locations

Locations are the physical ProfitBricks computing centers where you can provision your resources.

#### List locations

Retrieves the list of IDs of currently available locations.

```groovy
List<String> locationIDs = new Location().all
assert locationIDs : 'no locations found!'
```

#### retrieve a location

| Argument | Required |
|---|---|
| `Location::id` | **yes** |

```groovy
Location location = new Location(id: 'us/ewr').read()
assert location : 'no such location!'
```


### Servers

A valid `DataCenter` instance is needed to manage servers.

#### List Servers

Retrieves the list of IDs of created servers for a given datacenter.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
List<String> serverIDs = new Server(dataCenter: datacenter).all
assert serverIDs : 'no servers found!'
```

#### Retrieve a Server

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
assert serverId : 'server id missing!'
Server server = new Server(id: serverId, dataCenter: datacenter).read()
assert server : 'no such server!'
```

#### Create a Server

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** ||
| `Server::name` | **yes** ||
| `Server::cores` | **yes** | number of cores |
| `Server::ram` | **yes** | amount of memory in MB, size must be specified in multiples of 256 |
| `Server::availabilityZone` | no | fire zone (see table below) |
| `Server::cpuFamily` | no | CPU family: `AMD_OPTERON` or `INTEL_XEON` default: `AMD_OPTERON` |

**Supported Fire Zones**

| firezone | comment |
|---|---|
| `AUTO` | automatically selected zone, default |
| `ZONE_1` | firezone 1 |
| `ZONE_2` | firezone 2 |

```groovy
assert datacenter : 'datacenter missing!'
Server server = new Server(
	dataCenter: datacenter,
	name: "server name",
	cores: 1,
	ram: 1024,
	availabilityZone: "ZONE_1",
	cpuFamily: "INTEL_XEON"
).create()
assert server.id : 'server creation failed!'
```

#### Update a Server

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Server::name` | no |
| `Server::cores` | no |
| `Server::ram` | no |
| `Server::availabilityZone` | no |
| `Server::cpuFamily` | no |

```groovy
assert server : 'server missing!'
server.name = "updated"
server.cores += 2
server.ram += 512
assert server.update() : 'server update failed!'
```

#### Delete a Server

Removes a server from a data center. **NOTE**: This will not automatically remove the storage volume(s) attached to a server. A separate operation is required to delete a storage volume.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
assert server.delete() : 'server deletion failed!'
```

#### list attached volumes

Retrieves a list of ids of all volumes currently attached to a given server.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
List<String> volumeIDs = Commands.attached(server, Volume)
assert volumeIDs : 'no volumes found!'
```

#### attach a volume

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Volume::id` | **yes** |

```groovy
assert server : 'server missing!'
assert volume : 'volume missing!'
assert Commands.attach(server, volume) : 'volume attachment failed!'
```

#### detach a volume

Depending on the volume `hot_unplug` settings, this may result in the server being rebooted.

This will NOT delete the volume from your virtual data center. You will need to make a separate request to delete a volume.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Volume::id` | **yes** |

```groovy
assert server : 'server missing!'
assert volume : 'volume missing!'
assert Commands.detach(server, volume) : 'volume detachment failed!'
```

#### list attached images

Retrieves a list of ids of all images (CDROMs) currently attached to a given server.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
List<String> imageIDs = Commands.attached(server, Image)
assert imageIDs : 'no attached images!'
```

#### attach an image

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Image::id` | **yes** |

```groovy
assert server : 'server missing!'
assert image : 'image missing!'
assert Commands.attach(server, image) : 'image attachment failed!'
```

#### detach an image

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Image::id` | **yes** |

```groovy
assert server : 'server missing!'
assert image : 'image missing!'
assert Commands.detach(server, image) : 'image detachment failed!'
```

#### reboot a server

Forces a hard reboot of the server. Do not use this method if you want to gracefully reboot the machine. This is the equivalent of powering off the machine and turning it back on.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
assert Commands.reboot(server) : 'server reboot failed!'
```

#### start a server

Starts a server. If the server's public IP address was deallocated then a new IP address will be assigned.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
assert Commands.start(s) : 'server start failed!'
```

#### stop a server

Stops a server. The machine will be forcefully powered off, billing will stop, and the public IP address, if one is allocated, will be deallocated.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
assert Commands.stop(s) : 'server stop failed!'
```


### Images

#### list images

Retrieves a list of ids of all available images (CDROMs).

```groovy
List<String> imageIDs = new Image().all
assert imageIDs : 'no images found!'
```

#### retrieve an image

| Argument | Required |
|---|---|
| `Image::id` | **yes** |

```groovy
Image image = new Image(id: imageID).read()
assert image._public : 'image is not public!'
```


### Volumes

#### list volumes

Retrieves a list of ids of volumes in a given datacenter.
A valid `DataCenter` instance is needed for this.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```groovy
assert dc : 'datacenter missing!'
List<String> volumeIDs = new Volume(dataCenter: dc).all
assert volumeIDs : 'no volumes found!'
```

#### retrieve a volume

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |

```groovy
assert dc : 'datacenter missing!'
Volume volume = new Volume(dataCenter: dc, id: volumeId).read()
assert volume : 'no such volume!'
```

#### create a volume

Creates a volume within the virtual data center. This will **not** attach the volume to a server. Please see the [Attach a Volume](#attach-a-volume) entry in the Server section for details on how to attach storage volumes.

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** | The ID of the VDC. |
| `Volume::name` | **yes** | The name of the volume. |
| `Volume::size` | **yes** | The size of the volume in GB. |
| `Volume::bus` | no | The bus type of the volume (VIRTIO or IDE). Default: VIRTIO. |
| `Volume::image` | no | The image or snapshot ID. |
| `Volume::imageAlias` | no | The alias of the image. |
| `Volume::type` | no | The volume type, HDD or SSD. |
| `Volume::licenceType` | no | The licence type of the volume. see table below |
| `Volume::imagePassword` | no | password for the root or administrative account, must consist of 8-50 alphanumeric characters|
| `Volume::sshKeys` | no | collection of SSH public keys  |
| `Volume::availabilityZone` | no | availability zone the volume should reside in, see table below |

| Licence Type | Comment |
|---|---|
| `WINDOWS2016` | for Microsoft Windows Server 2016 |
| `WINDOWS` | for Microsoft Windows Server 2008 and 2012 |
| `LINUX` | for Linux |
| `OTHER` | for any volumes that do not match one of the other licence types |
| `UNKNOWN` | default value when you've uploaded an image and haven't set the license type |

| Availability Zone | Comment |
|---|---|
| `AUTO` | Automatically Selected Zone |
| `ZONE_1` | Fire Zone 1 |
| `ZONE_2` | Fire Zone 2 |
| `ZONE_3` | Fire Zone 3 |

```groovy
assert datacenter : 'datacenter missing!'
Volume v = new Volume(dataCenter: datacenter, name: "name", size: 1).create()
assert v.id : 'volume creation failed!'
```

#### update a volume

* The size of an existing storage volume can only be increased.
* If no according hot plug settings are configured, this operation may cause a server reboot.

| Arguments | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |
| `Volume::name` | no |
| `Volume::size` | no |
| `Volume::bus` | no |
| `Volume::image` | no |
| `Volume::type` | no |
| `Volume::licenceType` | no |
| `Volume::availabilityZone` | no |

```groovy
assert volume : 'volume missing!'
volume.name = "updated name from ${vname}"
volume.size = 2
assert v.update() : 'volume update failed!'
```

#### delete a volume

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |

```groovy
assert volume : 'volume missing!'
assert volume.delete() : 'volume deletion failed!'
```

#### create a volume snapshot

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |
| `Snapshot::name` | no |
| `Snapshot::description` | no |

```groovy
assert volume : 'volume missing!'
Snapshot snapshot = Commands.snapshot(v, 'snapshot_4711', 'a fancy snapshot')
assert snapshot : 'snapshot operation failed!'
```

#### restore a volume snapshot

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |
| `Snapshot::id` | **yes** |

```groovy
assert volume : 'volume missing!'
assert snapshot : 'snapshot missing!'
assert Commands.restore(v, sn) : 'restoration from snapshot failed!'
```

### Snapshots

#### list snapshots

Retrieves a list of ids of all available snapshots.

```groovy
List<String> snapshotIDs = new Snapshot().all
assert snapshotIDs : 'no snapshots found!'
```

#### retrieve a snapshot

| Argument | Required |
|---|---|
| `Snapshot::id` | **yes** |

```groovy
Snapshot snap = new Snapshot(id: snapshotId).read()
assert snap : 'no such snapshot!'
```

#### update a snapshot

| Argument | Required | Description |
|---|---|---|
| `Snapshot::id` | **yes** |
| `Snapshot::name` | no |
| `Snapshot::description` | no |
| `Snapshot::licenceType` | no | licence type: `LINUX`, `WINDOWS`, `WINDOWS2016` or `OTHER` |
| `Snapshot::cpuHotPlug` | no | CPU hot plug capability |
| `Snapshot::cpuHotUnplug` | no | CPU hot unplug capability |
| `Snapshot::ramHotPlug` | no | memory hot plug capability |
| `Snapshot::ramHotUnplug` | no | memory hot unplug capability |
| `Snapshot::nicHotPlug` | no | NIC hot plug capability |
| `Snapshot::nicHotUnplug` | no | NIC hot unplug capability |
| `Snapshot::siscVirtioHotPlug` | no | VirtIO drive hot plug capability |
| `Snapshot::discVirtioHotUnplug` | no | VirtIO drive hot unplug capability |
| `Snapshot::discScsiHotPlug` | no | SCSI drive hot plug capability |
| `Snapshot::discScsiHotUnplug` | no | SCSI drive hot unplug capability |

```groovy
assert snapshot : 'snapshot missing!'
snapshot.name = "updated from ${snapshot.name}"
snapshot.ramHotPlug = true
assert snapshot.update() : 'snapshot update failed!'
```

#### delete a snapshot

| Argument | Required |
|---|---|
| `Snapshot::id` | **yes** |

```groovy
assert snapshot : 'snapshot missing!'
assert snapshot.delete() : 'snapshot deletion failed!'
```


### IP Blocks

#### list IP blocks

Retrieves a list of ids of previously reserved (created) IP blocks.

```groovy
List<String> ipBlockIDs = new IPBlock().all
assert ipBlockIDs : 'no reserverd IP blocks!'
```

#### retrieve an IP block

| Argument | Required |
|---|---|
| `IPBlock::id` | **yes** |

```groovy
assert ipBlockId : 'IP block id missing!'
IPBlock block = new IPBlock(id: ipBlockId).read()
assert block : 'no such IP block!'
```

#### create an IP block

| Argument | Required | Description |
|---|---|---|
| `IPBlock::location` | **yes** | a valid location ID, see table above |
| `IPBlock::size` | **yes** | the number of IP addresses to reserve |
| `IPBlock::name` | no ||

```groovy
IPBlock block = new IPBlock(location: 'us/ewr', size: 2).create()
assert block.id : 'IP block creation failed!'
```

#### delete an IP block

| Argument | Required | 
|---|---|
| `IPBlock::id` | **yes** |

```groovy
assert block : 'IP block missing!'
assert block.delete() : 'IP block deletion failed!'
```


### LANs

#### list LANs

Retrieves a list of LAN ids within the virtual data center. This needs a valid `DataCenter` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
List<String> lanIDs = new LAN(dataCenter: datacenter).all
assert lanIDs : 'no LANs in this datacenter!'
```

#### create a LAN

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** ||
| `LAN::_public` | **yes** | publicly reachable LAN? |
| `LAN::name` | no ||

```groovy
assert datacenter : 'datacenter missing!'
LAN lan = new LAN(dataCenter: datacenter, name: 'primary LAN', _public: true).create()
assert lan.id : 'LAN creation failed!'
```

#### retrieve a LAN

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** | 
| `LAN::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
assert lanId : 'LAN id missing!'
LAN lan = new LAN(dataCenter: dc, id: lanId).read()
assert lan : 'no such LAN!'
```

#### update a LAN

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** ||
| `LAN::id` | **yes** ||
| `LAN::name` | no ||
| `LAN::_public` | no ||
| `LAN::ipFailover` | no | a collection of `IPFailover` instances |

```groovy
assert lan : 'LAN missing!'
lan.name = "name"
lan._public = false
lan.ipFailover = [
    new LAN.IPFailover(ip: '158.222.103.175', nicUuid: '43ec1562-042f-40ae-8162-44c97466ab52'),
    new LAN.IPFailover(ip: '158.222.103.175', nicUuid: '7240dbbc-de87-4fec-8e50-5a5ce77690e0')
]
assert lan.update() : 'LAN update failed!'
```

#### delete a LAN

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LAN::id` | **yes** |

```groovy
assert lan : 'LAN missing!'
assert lan.delete() : 'LAN deletion failed!'
```

### Network Interfaces

#### list NICs

Retrieves a list of NIC ids for a given server. This needs a valid `Server` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```groovy
assert server : 'server missing!'
List<String> nicIDs = new NIC(server: server).all
assert nicIDs : 'no NICs for this server!'
```

#### retrieve a NIC

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |

```groovy
assert server : 'server missing!'
assert nicID : 'NIC id missing!'
NIC nic = new NIC(server: s, id: nicID).read()
assert nic : 'no such NIC for this server!'
```

#### create a NIC

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::lan` | **yes** | id of the LAN the NIC will be attached on  (if that LAN does not exist it will be created) |
| `NIC::name` | no ||
| `NIC::ips` | no | all IPs assigned to the NIC |
| `NIC::dhcp` | no | DHCP enabled? default: true |
| `NIC::nat` | no | if address translation should be performed |
| `NIC::firewallActive` | no | if there are active firewall rules for this NIC |

```groovy
assert server : 'server missing!'
assert lan : 'LAN missing!'
NIC nic = new NIC(server: server, lan: lan, nat: true).create()
assert nic.id : 'NIC creation failed!'
```

#### update a NIC

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `NIC::lan` | no |
| `NIC::name` | no |
| `NIC::ips` | no |
| `NIC::dhcp` | no |
| `NIC::nat` | no |
| `NIC::firewallActive` | no |

```groovy
assert nic : 'NIC missing!'
nic.name = "name"
nic.ips = ['192.168.0.2']
nic.dhcp = false
nic.nat = false
assert nic.update() : 'NIC update failed!'
```

#### delete a NIC

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |

```groovy
assert nic : 'NIC missing!'
assert nic.delete() : 'NIC deletion failed!'
```


### Firewall Rules

#### list firewall rules

Retrieves a list of firewall rules associated with a particular NIC. Needs a valid `NIC` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |

```groovy
assert nic : 'NIC missing!'
List<String> fwRuleIDs = new FirewallRule(nic: nic).all
assert fwRuleIDs : 'no firewall rules for the given NIC!'
```

#### Retrieve a firewall rule

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `FirewallRule::id` | **yes** |

```groovy
assert nic : 'NIC missing!'
assert ruleID : 'firewall rule id missing!'
FirewallRule rule = new FirewallRule(nic: nic, id: ruleID).read()
assert rule : 'no such firewall rule for this NIC!'
```

#### Create a firewall rule

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `FirewallRule::protocol` | **yes** | one of `TCP`, `UDP`, `ICMP` or `ANY` |
| `FirewallRule::name` | no |
| `FirewallRule::sourceMac` | no | only allow connections from this MAC |
| `FirewallRule::sourceIp` | no | only allow connections from this IPv4 address |
| `FirewallRule::targetIp` | no | only allow connections to this IPv4 address |
| `FirewallRule::portRangeStart` | no | only allow connections for ports in the given range, this defines the range start  |
| `FirewallRule::portRangeEnd` | no | only allow connections for ports in the given range, this defines the range end |
| `FirewallRule::icmpType` | no | only allow packets with the given ICMP type |
| `FirewallRule::icmpCode` | no | only allow packets with the given ICMP code |

```groovy
assert nic : 'NIC missing!'
FirewallRule rule = new FirewallRule(nic: nic, protocol: 'UDP').create()
assert rule.id : 'firewall rule creation failed!'
```

#### Update a Firewall Rule

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `FirewallRule::id` | **yes** |
| `FirewallRule::protocol` | no |
| `FirewallRule::name` | no |
| `FirewallRule::sourceMac` | no |
| `FirewallRule::sourceIp` | no |
| `FirewallRule::targetIp` | no |
| `FirewallRule::portRangeStart` | no |
| `FirewallRule::portRangeEnd` | no |
| `FirewallRule::icmpType` | no |
| `FirewallRule::icmpCode` | no |

```groovy
assert rule : 'firewall rule missing!'
rule.name = "name"
rule.sourceMac = 'aa:bb:cc:dd:ee:ff'
rule.sourceIp = '23.23.23.23'
rule.targetIp = n.ips.first()
rule.portRangeStart = '1234'
rule.portRangeEnd = '4711'
assert rule.update() : 'firewall rule update failed!'
```

#### Delete a Firewall Rule

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `FirewallRule::id` | **yes** |

```groovy
assert rule : 'firewall rule missing!'
assert rule.delete() : 'firewall rule deletion failed!'
```


### Load Balancers

#### list load balancers

Retrieves a list of load balancer ids within the data center. This needs a valid `DataCenter` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
List<String> loadbalancerIDs = new LoadBalancer(dataCenter: datacenter).all
assert loadbalancerIDs : 'no load balancers for this datacenter!'
```

#### retrieve a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |

```groovy
assert datacenter : 'datacenter missing!'
assert loadbalancerId : 'load balancer id missing!'
LoadBalancer lb = new LoadBalancer(dataCenter: datacenter, id: loadbalancerId).read()
assert lb : 'no such load balancer!'
```

#### create a load balancer

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::name` | **yes** ||
| `LoadBalancer::ip` | no | IPv4 address of the load balancer. All attached NICs will inherit this address |
| `LoadBalancer::dhcp` | no | if DHCP shall be used |

```groovy
assert datacenter : 'datacenter missing!'
LoadBalancer lb = new LoadBalancer(dataCenter: datacenter, name: "frontend").create()
assert lb.id : 'load balancer creation failed!'
```

#### update a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |
| `LoadBalancer::name` | no |
| `LoadBalancer::ip` | no |
| `LoadBalancer::dhcp` | no |

```groovy
assert loadbalancer : 'load balancer missing!'
loadbalancer.name = "name"
loadbalancer.dhcp = false
loadbalancer.ip = '192.168.0.11'
assert loadbalancer.update() : 'load balancer update failed!'
```

#### delete a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |

```groovy
assert loadbalancer : 'load balancer missing!'
assert loadbalancer.delete() : 'load balancer deletion failed!'
```

#### list load balanced NICs

Retrieves a list of ids of NICs associated with the load balancer. Needs valid `DataCenter` and `LoadBalancer` instances.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |

```groovy
assert loadbalancer : 'load balancer missing!'
List<String> nicIDs = Commands.associatedNICs(loadbalancer)
assert nicIDs : 'no NICs associated with this load balancer!'
```

#### associate a NIC with a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |
| `NIC::id` | **yes** |

```groovy
assert loadbalancer : 'load balancer missing!'
assert nic : 'NIC missing!'
assert Commands.associate(loadbalancer, nic) : 'nic to load balancer association failed!'
```

#### remove a NIC association

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |
| `NIC::id` | **yes** |

```groovy
assert loadbalancer : 'load balancer missing!'
assert nic : 'NIC missing!'
assert Commands.dissociate(loadbalancer, nic) : 'nic from load balancer dissociation failed!'
```


### Users

#### list users

Retrieves a list of ids of all users for the current contract.

```groovy
List<String> userIDs = new User().all
assert userIDs : 'no users found!'
```

#### retrieve a user

| Argument | Required |
|---|---|
| `User::id` | **yes** |

```groovy
assert userId : 'user id missing!'
User user = new User(id: userId).read()
assert user : 'no such user!'
```

#### create a User

| Argument | Required | Description |
|---|---|---|
| `User::firstname` | **yes** | user first name |
| `User::lastname` | **yes** | user last name |
| `User::email` | **yes** | user email address |
| `User::password` | **yes** | user basic auth password |
| `User::administrator` | no |  assigns administrative rights |
| `User::forceSecAuth` | no | if two-factor auth should be forced |

```groovy
User user = new User(
    firstname: "John",
    lastname: "Doe",
    email: "groovy.user@dev.org",
    password: "HJhbjhjhbhhhgjhhhg6567fsdf234",
    administrator: true,
    forceSecAuth: true
).create()
assert user.id : 'user creation failed!'
```

#### update a user

| Argument | Required |
|---|---|
| `User::id` | **yes** |
| `User::firstname` | no |
| `User::lastname` | no |
| `User::email` | no |
| `User::administrator` | no |
| `User::forceSecAuth` | no |

```groovy
assert user : 'user missing!'
user.administrator = false
assert user.update() : 'user update failed!'
```

#### delete a user

| Argument | Required |
|---|---|
| `User::id` | **yes** |

```groovy
assert user : 'user missing!'
assert user.delete() : 'user deletion failed!'
```


### User Groups

#### list user groups

```groovy
List<String> groupIDs = new UserGroup().all
assert groupIDs : 'no user groups exist!'
```

---

#### retrieve a user group

**Request Arguments**

| Argument | Required |
|---|---|
| `UserGroup::id` | **yes** |

```groovy
assert groupId : 'user group id missing!'
UserGroup group = new UserGroup(id: groupId).read()
assert group : 'no such user group!'
```

#### create a user group

| Argument | Required | Description |
|---|---|---|
| `UserGroup::name` | **yes** ||
| `UserGroup::createDataCenter` | no | permission to create data centers |
| `UserGroup::createSnapshot` | no | permission to create snapshots |
| `UserGroup::reserveIp` | no | permission to create IP blocks |
| `UserGroup::accessActivityLog` | no | permission to access the activity log |

```groovy
UserGroup group = new UserGroup(
    name: 'Admin Group',
    createDataCenter: true,
    createSnapshot: true,
    reserveIp: true,
    accessActivityLog: false
).create()
assert group.id : 'user group creation failed!'
```

#### update a user group

| Argument | Required |
|---|---|
| `UserGroup::id` | **yes** |
| `UserGroup::name` | no |
| `UserGroup::createDataCenter` | no |
| `UserGroup::createSnapshot` | no |
| `UserGroup::reserveIp` | no |
| `UserGroup::accessActivityLog` | no |

```groovy
assert userGroup : 'user group missing!'
userGroup.createDatacenter = false
assert userGroup.update() : 'user group update failed!'
```

#### delete a user group

| Argument | Required |
|---|---|
| `UserGroup::id` | **yes** |

```groovy
assert userGroup : 'user group missing!'
assert group.delete() : 'user group deletion failed!'
```

#### list group users

Retrieves a list of ids of all users that are members of a group.

| Argument | Required |
|---|---|
| `UserGroup::id` | **yes** |

```groovy
assert userGroup : 'user group missing!'
List<String> userIDs = Commands.userIDs(group)
assert userIDs : 'no users in this group!'
```

#### add a user to a group

| Argument | Required |
|---|---|
| `UserGroup::id` | **yes** |
| `User::id` | **yes** |

```groovy
assert userGroup : 'user group missing!'
assert user : 'user missing!'
assert Commands.assign(userGroup, user) : 'assigning user to user group failed!'
```

#### remove a user from a group

| Argument | Required |
|---|---|
| `UserGroup::id` | **yes** |
| `User::id` | **yes** |

```groovy
assert userGroup : 'user group missing!'
assert user : 'user missing!'
assert Commands.unassign(group, user) : 'assigning user to user group failed!'
```


### Contract Resources

Retrieve the current resource allocation statistics for this contract.

```groovy
ContractStats stats = new ContractStats().read()
assert stats : 'retrieval of contract stats failed!'
```

### A Concise Example

```groovy
import com.profitbricks.sdk.model.*
import static com.profitbricks.sdk.Commands.*

// create a datacenter
DataCenter dc = new DataCenter(name: "Example DC", location: 'de/fkb', description: 'desc').create()

// create a LAN
LAN lan = new LAN(dataCenter: dc, name: "public lan", _public: true).create()

// create a server
Server server = new Server(dataCenter: dc, name: "Example server", cores: 1, ram: 1024).create()

// add a NIC to server
NIC nic = new NIC(server: server, lan: lan, name: "example nic").create()
assert nic.ips

// find a linux image to attach to server
Image image = new Image().all.collect{image.read(it) as Image}.findAll {
    it._public &&
    it.location == dc.location &&
    it.licenceType =~ /(?i)linux/ &&
    it.imageType =~ /(?i)hdd/
}.first()

// create a volume from that image
Volume volume = new Volume(dataCenter: dc, name: "OS volume", image: image.id, imagePassword: 'test1234').create()

// attach volume to server
attach server, volume

// delete datacenter
dc.delete()
```


## TODO

Having proper life cycle control over your entities is helpful in the long run. The current approach to query for `/request` resources should be replaced by a future/promise based mechanism.


## Support

You can engage with us in the ProfitBricks [DevOps Central community](https://devops.profitbricks.com/community), there we'll gladly answer any questions you might have about this SDK.

Please report any issues or bugs your encounter using the [GitHub Issue Tracker](https://github.com/profitbricks/profitbricks-sdk-groovy/issues).


## Testing

You can find a full test suite in `src/test/groovy`. Run all tests by issuing 
```bash
./gradlew test \
-Dcom.profitbricks.sdk.user=YOUR_USERNAME \
-Dcom.profitbricks.sdk.password=YOUR_PASSWORD
```

