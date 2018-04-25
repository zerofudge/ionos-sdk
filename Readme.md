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
    * [Groups](#groups)
      * [List Groups](#list-groups)
      * [Retrieve a Group](#retrieve-a-group)
      * [Create a Group](#create-a-group)
      * [Update a Group](#update-a-group)
      * [Delete a Group](#delete-a-group)
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
| `api.verifySSL` | `true` | set to `false` to ignore SSL certificate verification issues |
| `api.user`| - | the API user name for basic authentication. **required** |
| `api.password` | - | the API password for basic authentication. **required**|
| `api.wait.init.milliseconds` | 100 | if waiting for success, this is the initial time period between two checks. |
| `api.wait.timeout.seconds` | 120 | if waiting for success, this is the timeout. |
| `api.wait.max.milliseconds` | 1500 | if waiting for success, this is the maximum time period between two checks. |
| `api.wait.factor`| 1.87 | if waiting for success, this is the factor by which the current time period value is multiplied. |


## SDK Reference

This section provides details on all the available operations and the parameters they accept. Also included are brief code snippets illustrating its usage.


### datacenters

Virtual data centers (VDCs) are the foundation of the ProfitBricks platform. VDCs act as logical containers for all other objects you will be creating, e.g., servers. You can provision as many VDCs as you want. VDCs have their own private network and are logically isolated from each other.

#### list datacenters

Lists the ids of all currently provisioned datacenters that are accessible for the current user.

```
List<String> datacenterIDs = new DataCenter().all
```

#### retrieve a datacenter

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```
DataCenter datacenter = new DataCenter(id: datacenterId).read()
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

```
def dc = new DataCenter(
	name: "groovy name",
	location: 'us/ewr',
	description: 'groovy description'
).create()

assert dc.id
```

#### update a datacenter

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `DataCenter::name` | no |
| `DataCenter::description` | no |

```
DataCenter datacenter = ...
dc.name = "updated name from ${dc.name}"
dc.description = "groovy new datacenter! (old: ${dc.description})"
assert dc.update()
```

#### delete a datacenter

Removes all objects within the virtual data center AND removes the virtual data center object itself.

**NOTE**: This is a highly destructive operation which should be used with extreme caution.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```
DataCenter dc = ...
dc.delete()
```


### Locations

Locations are the physical ProfitBricks computing centers where you can provision your resources.

#### List locations

Retrieves the list of IDs of currently available locations.

```
List<String> locationIDs = new Location().all
```

#### retrieve a location

| Argument | Required |
|---|---|
| `Location::id` | **yes** |

```
Location location = new Location(id: 'us/ewr').read()
```


### Servers

A valid `DataCenter` instance is needed:

```
DataCenter dc = ...
Server s = new Server(dataCenter: dc)
```

#### List Servers

Retrieves the list of IDs of created servers for a given datacenter.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```
DataCenter dc = ...
List<String> serverIDs = new Server(dataCenter: dc).all
```

#### Retrieve a Server

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
DataCenter dc = ...
Server server = new Server(id: serverId, dataCenter: dc).read()
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

```
DataCenter dc = ...
Server server = new Server(
	dataCenter: dc,
	name: "server name",
	cores: 1,
	ram: 1024,
	availabilityZone: "ZONE_1",
	cpuFamily: "INTEL_XEON"
).create()
assert server.id
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

```
Server server = ...
server.name = "updated"
server.cores += 2
server.ram += 512
server.update()
```

#### Delete a Server

Removes a server from a data center. **NOTE**: This will not automatically remove the storage volume(s) attached to a server. A separate operation is required to delete a storage volume.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server server = ...
server.delete()
```

#### list attached volumes

Retrieves a list of ids of all volumes currently attached to a given server.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server server = ...
List<String> volumeIDs = Commands.attached(server, Volume)
```

#### attach a volume

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Volume::id` | **yes** |

```
Server server = ...
Volume volume = ...
Commands.attach(server, volume)
```

#### detach a volume

Depending on the volume `hot_unplug` settings, this may result in the server being rebooted.

This will NOT delete the volume from your virtual data center. You will need to make a separate request to delete a volume.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Volume::id` | **yes** |

```
Server server = ...
Volume volume = ...
Commands.detach(server, volume)
```

#### list attached images

Retrieves a list of ids of all images (CDROMs) currently attached to a given server.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server server = ...
List<String> imageIDs = Commands.attached(server, Image)
```

#### attach an image

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Image::id` | **yes** |

```
Server server = ...
Image image = ...
Commands.attach(server, image)
```

#### detach an image

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `Image::id` | **yes** |

```
Server server = ...
Image image = ...
Commands.detach(server, image)
```

#### reboot a server

Forces a hard reboot of the server. Do not use this method if you want to gracefully reboot the machine. This is the equivalent of powering off the machine and turning it back on.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server server = ...
Commands.reboot(server)
```

#### start a server

Starts a server. If the server's public IP address was deallocated then a new IP address will be assigned.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server server = ...
Commands.start(s)
```

#### stop a server

Stops a server. The machine will be forcefully powered off, billing will stop, and the public IP address, if one is allocated, will be deallocated.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server server = ...
Commands.stop(s)
```


### Images

#### list images

Retrieves a list of ids of all available images (CDROMs).

```
List<String> imageIDs = new Image().all
```

#### retrieve an image

| Argument | Required |
|---|---|
| `Image::id` | **yes** |

```
Image image = new Image(id: imageID).read()
```


### Volumes

#### list volumes

Retrieves a list of ids of volumes in a given datacenter.
A valid `DataCenter` instance is needed for this.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```
DataCenter dc = ...
List<String> volumeIDs = new Volume(dataCenter: dc).all
```

#### retrieve a volume

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |

```
DataCenter dc = ...
Volume volume = new Volume(dataCenter: dc, id: volumeId).read()
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

```
DataCenter dc = ...
Volume v = new Volume(dataCenter: dc, name: "name", size: 1).create()
assert v.id
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

```
Volume v = ...
v.name = "updated name from ${vname}"
v.size = 2
v.update()
```

#### delete a volume

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |

```
Volume v = ...
v.delete()
```

#### create a volume snapshot

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |
| `Snapshot::name` | no |
| `Snapshot::description` | no |

```
Volume v = ...
Snapshot snapshot = Commands.snapshot(v, 'snapshot_4711', 'a fancy snapshot')
```

#### restore a volume snapshot

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Volume::id` | **yes** |
| `Snapshot::id` | **yes** |

```
Snapshot sn = ...
Volume v = ...
assert Commands.restore(v, sn)
```

### Snapshots

#### list snapshots

Retrieves a list of ids of all available snapshots.

```
List<String> snapshotIDs = new Snapshot().all
```

#### retrieve a snapshot

| Argument | Required |
|---|---|
| `Snapshot::id` | **yes** |

```
Snapshot snap = new Snapshot(id: snapshotId).read()
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

```
Snapshot snap = ...
snap.name="updated from ${snap.name}"
snap.ramHotPlug = true
snap.update()
```

#### delete a snapshot

| Argument | Required |
|---|---|
| `Snapshot::id` | **yes** |

```
Snapshot snap = ...
sn.delete()
```


### IP Blocks

#### list IP blocks

Retrieves a list of ids of previously reserved (created) IP blocks.

```
List<String> ipBlockIDs = new IPBlock().all
```

#### retrieve an IP block

| Argument | Required |
|---|---|
| `IPBlock::id` | **yes** |

```
IPBlock block = new IPBlock(id: ipBlockId).read()
```

#### create an IP block

| Argument | Required | Description |
|---|---|---|
| `IPBlock::location` | **yes** | a valid location ID, see table above |
| `IPBlock::size` | **yes** | the number of IP addresses to reserve |
| `IPBlock::name` | no ||

```
IPBlock block = new IPBlock(location: 'us/ewr', size: 2).create()
assert block.id
```

#### delete an IP block

| Argument | Required | 
|---|---|
| `IPBlock::id` | **yes** |

```
IPBlock block = ...
block.delete()
```


### LANs

#### list LANs

Retrieves a list of LAN ids within the virtual data center. This needs a valid `DataCenter` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```
List<String> lanIDs = new LAN(dataCenter: datacenterId).all
```

#### create a LAN

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** ||
| `LAN::_public` | **yes** | publicly reachable LAN? |
| `LAN::name` | no ||

```
DataCenter dc = ...
LAN lan = new LAN(dataCenter: dc, name: 'primary LAN', _public: true).create()
assert lan.id
```

#### retrieve a LAN

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** | 
| `LAN::id` | **yes** |

```
DataCenter dc = ...
LAN lan = new LAN(dataCenter: dc, id: lanId).read()
assert lan.id
```

#### update a LAN

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** ||
| `LAN::id` | **yes** ||
| `LAN::name` | no ||
| `LAN::_public` | no ||
| `LAN::ipFailover` | no | a collection of `IPFailover` instances |

```
LAN lan = ...
l.name = "name"
l._public = false
l.ipFailover = [
    new LAN.IPFailover(ip: '158.222.103.175', nicUuid: '43ec1562-042f-40ae-8162-44c97466ab52'),
    new LAN.IPFailover(ip: '158.222.103.175', nicUuid: '7240dbbc-de87-4fec-8e50-5a5ce77690e0')
]
assert l.update()
```

#### delete a LAN

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LAN::id` | **yes** |

```
LAN lan = ...
lan.delete()
```

### Network Interfaces

#### list NICs

Retrieves a list of NIC ids for a given server. This needs a valid `Server` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |

```
Server s = ...
List<String> nicIDs = new NIC(server: s).all
```

#### retrieve a NIC

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |

```
Server s = ...
NIC nic = new NIC(server: s, id: nicID).read()
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

```
Server s = ...
LAN lan = ...
NIC nic = new NIC(server: s, lan: lan, nat: true).create()
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

```
NIC nic = ...
nic.name = "name"
nic.ips = ['192.168.0.2']
nic.dhcp = false
nic.nat = false
assert nic.update()
```

#### delete a NIC

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |

```
NIC nic = ...
assert nic.delete()
```


### Firewall Rules

#### list firewall rules

Retrieves a list of firewall rules associated with a particular NIC. Needs a valid `NIC` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |

```
NIC nic = ...
List<String> fwRuleIDs = new FirewallRule(nic: nic).all
```

#### Retrieve a firewall rule

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `FirewallRule::id` | **yes** |

```
NIC nic = ...
FirewallRule rule = new FirewallRule(nic: nic, id: ruleID).read()
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

```
NIC nic = ...
FirewallRule rule = new FirewallRule(nic: nic, protocol: 'UDP').create()
assert rule.id
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

```
FirewallRule rule = ...
fw.name = "name"
fw.sourceMac = 'aa:bb:cc:dd:ee:ff'
fw.sourceIp = '23.23.23.23'
fw.targetIp = n.ips.first()
fw.portRangeStart = '1234'
fw.portRangeEnd = '4711'
fw.update()
```

#### Delete a Firewall Rule

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `Server::id` | **yes** |
| `NIC::id` | **yes** |
| `FirewallRule::id` | **yes** |

```
FirewallRule rule = ...
rule.delete()
```


### Load Balancers

#### list load balancers

Retrieves a list of load balancer ids within the data center. This needs a valid `DataCenter` instance.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |

```
DataCenter dc = ...
List<String> loadbalancerIDs = new LoadBalancer(dataCenter: dc).all
```

#### retrieve a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |

```
DataCenter dc = ...
LoadBalancer lb = new LoadBalancer(dataCenter: dc, id: loadbalancerId).read()
```

#### create a load balancer

| Argument | Required | Description |
|---|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::name` | **yes** ||
| `LoadBalancer::ip` | no | IPv4 address of the load balancer. All attached NICs will inherit this address |
| `LoadBalancer::dhcp` | no | if DHCP shall be used |

```
DataCenter dc = ...
LoadBalancer lb = new LoadBalancer(dataCenter: dc, name: "frontend").create()
assert lb.id
```

#### update a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |
| `LoadBalancer::name` | no |
| `LoadBalancer::ip` | no |
| `LoadBalancer::dhcp` | no |

```
LoadBalancer lb = ...
lb.name = "name"
lb.dhcp = false
lb.ip = '192.168.0.11'
lb.update()
```

#### delete a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |

```
LoadBalancer lb = ...
lb.delete()
```

#### list load balanced NICs

Retrieves a list of ids of NICs associated with the load balancer. Needs valid `DataCenter` and `LoadBalancer` instances.

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |

```
LoadBalancer lb = ...
List<String> nicIDs = Commands.associatedNICs(lb)
```

#### associate a NIC with a load balancer

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |
| `NIC::id` | **yes** |

```
LoadBalancer lb = ...
NIC nic = ...
assert Commands.associate(lb, nic);
```

#### remove a NIC association

| Argument | Required |
|---|---|
| `DataCenter::id` | **yes** |
| `LoadBalancer::id` | **yes** |
| `NIC::id` | **yes** |

```
LoadBalancer lb = ...
NIC nic = ...
assert Commands.dissociate(lb, nic);
```


### Users

#### list users

Retrieves a list of ids of all users for the current contract.

```
List<String> userIDs = new User().all
```

#### retrieve a user

| Argument | Required |
|---|---|
| `User::id` | **yes** |

```
User user = new User(id: userId).read()
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

```
User user = new User(
    firstname: "John",
    lastname: "Doe",
    email: "groovy.user@dev.org",
    password: "HJhbjhjhbhhhgjhhhg6567fsdf234",
    administrator: true,
    forceSecAuth: true
).create()
assert user.id
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

```
User user = ...
user.administrator = false
assert user.update()
```

#### delete a user

| Argument | Required |
|---|---|
| `User::id` | **yes** |

```
User user = ...
user.delete()
```


### Groups

#### list groups

```
List<String> groupIDs = new Group().all
```

---

#### retrieve a group

**Request Arguments**

| Argument | Required |
|---|---|
| `Group::id` | **yes** |

```
Group group = new Group(id: groupId).read()
```

#### create a group

| Argument | Required | Description |
|---|---|---|
| `Group::name` | **yes** ||
| `Group::createDataCenter` | no | permission to create data centers |
| `Group::createSnapshot` | no | permission to create snapshots |
| `Group::reserveIp` | no | permission to create IP blocks |
| `Group::accessActivityLog` | no | permission to access the activity log |

```
Group group = new Group(
    name: "GroovyUserGroup",
    createDataCenter: true,
    createSnapshot: true,
    reserveIp: true,
    accessActivityLog: false
).create()
assert group.id
```

#### update a group

| Argument | Required |
|---|---|
| `Group::id` | **yes** |
| `Group::name` | no |
| `Group::createDataCenter` | no |
| `Group::createSnapshot` | no |
| `Group::reserveIp` | no |
| `Group::accessActivityLog` | no |

```
Group group = ...
group.createDatacenter = false
group.update()
```

#### delete a group

| Argument | Required |
|---|---|
| `Group::id` | **yes** |

```
Group group = ...
group.delete()
```

#### list group users

Retrieves a list of ids of all users that are members of a group.

| Argument | Required |
|---|---|
| `Group::id` | **yes** |

```
Group group = ...
List<String> userIDs = Commands.userIDs(group)
```

#### add a user to a group

| Argument | Required |
|---|---|
| `Group::id` | **yes** |
| `User::id` | **yes** |

```
Group group = ...
User user = ...
assert Commands.assign(group, user)
```

#### remove a user from a group

| Argument | Required |
|---|---|
| `Group::id` | **yes** |
| `User::id` | **yes** |

```
Group group = ...
User user = ...
assert Commands.unassign(group, user)
```


### Contract Resources

Retrieve the current resource allocation statistics for this contract.

```
ContractStats stats = new ContractStats().read()
```

### A Concise Example

```
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

Not all CRUD functionality needs to be instance methods on the entities. Especially `all` (a.k.a. `list`) and `read()` should be static members. This will be implemented in a future version.

The current configuration approach using system properties should be replaced by a properly provided externalized configuration within the class path. A draft for this can be found `src/main/resources/config.yaml`. This will be implemented in a future version.

Having proper life cycle control over your entities is helpful in the long run. The current approach to query for `/request` resources will be replaced by a future/promise based mechanism.


## Support

You can engage with us in the ProfitBricks [DevOps Central community](https://devops.profitbricks.com/community) and we will be happy to answer any questions you might have about using this SDK.

Please report any issues or bugs your encounter using the [GitHub Issue Tracker](https://github.com/profitbricks/profitbricks-sdk-groovy/issues).


## Testing

You can find a full test suite in `src/test/groovy`. You can run all tests issuing 
```
./gradlew test -Dapi.user=YOUR_USERNAME -Dapi.password=YOUR_PASSWORD
```

