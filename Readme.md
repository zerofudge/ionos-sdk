# The Profitbricks Cloud API Groovy SDK

![Logo](src/main/resources/grooy-sdk-logo.png)


## Basic Objective

There is basically just one pattern: Create an entity and then invoke one of the CRUD methods on it:

- `create()` - to effectively send a resource creation REST request over the wire
- `read()` - to fetch an existing REST resource by its UUID
- `update()` - to send an update to an existing REST resource
- `delete()` - to delete an existing resource
- `all` - this property implements a *list* operation to fetch UUIDs of existing REST resources

An entity must be sufficiently filled for the associated REST request to succeed. No extra validation
will take place. In case of error you would see an exception with the proper cause.
Take note, that for resources that exist within the context of another resource, that other resource
must also be properly instanciated and injected.

For example: to successfully operate on a network
interface, the entity (`NIC`) must also contain proper `Server` and `LAN` instances.

No extra modeling was made (e.g. no *parent* links or the like) - the entities just enclose the transported
JSON representations.

On a successfully delivered REST `POST`, `PUT` or `DELETE` request the Profitbricks API might send a
`Location` header instead of the final resulting response to the requested action. This SDK will block
on such a response and then keeps polling for the final result. If a successful result doesn't come
in time, an exception will be thrown.
This decision was made to allow for easy and agile scripting, as the result matters most of the time and
such a behaviour would be needed anyways. See also below (*open issues*)

For API resources not fitting into the CRUD scheme, an extra command facade is provided. This provides
functionality like attaching and detaching storage volumes to virtual servers or associating network
interfaces with load balancers. (see `com.profitbricks.sdk.Commands` facade)

This implementation is based on Groovy 2, so it technically runs on any JVM with at least 1.7


## How to install it

a) build yourself: `./gradlew install`

b) just add the proper maven dependency:
  - either in your pom.xml:

        <dependency>
          <groupId>com.profitbricks.sdk/groupId>
          <artifactId>groovy</artifactId>
          <version>1.4.0</version>
        </dependency>`

  - or in your build.gradle:

        dependencies {
          compile 'com.profitbricks.sdk:groovy:1.4.0'
        }


## How to configure it

The current implementation relies solely on system properties (and proper defaults). The following
table lists all relevant system properties.

| name                         |      default                             |  notes                                                                                           |
-------------------------------|:-----------------------------------------|:-------------------------------------------------------------------------------------------------|
| `api.URL`                    | https://api.profitbricks.com/cloudapi/v3 | the base API URL                                                                                 |
| `api.verifySSL`              | `true`                                   | ignores all SSL certificate issues if `false`                                                    |
| `api.user`                   |                                          | the API user name for basic auth                                                                 |
| `api.password`               |                                          | the API password for basic auth                                                                  |
| `api.wait.init.milliseconds` | 100                                      | if waiting for success, this is the inital time period between two checks                        |
| `api.wait.timeout.seconds`   | 120                                      | if waiting for success, this is the timeout                                                      |
| `api.wait.max.milliseconds`  | 1500                                     | if waiting for success, this is the max time period between two checks                           |
| `api.wait.factor`            | 1.87                                     | if waiting for success, this is the factor by which the current time period value is multiplied  |


## How to work with it

To just try it all out, this is how you'd do it

a) insert your API credentials in the `build.gradle` (search for `#credentials`)

b) run `./gradlew run` (which will run `SDKTester`), this'll take some minutes..


This is also hinting at what to look at next: `src/main/groovy/SDKTester.groovy` fully tests
the SDK. So if you're looking for code examples, this is where to find them.


Just for completeness: Of course you can use this SDK in a plain old Java fashion. Some of the
groovyness will be gone but there won't be any technical issue doing that.

## Shortcomings and open issues

- Not all CRUD functionality needs to be instance methods on the entities. Especially `all` (a.k.a. `list`) and `read()` should be
static members. This'll be done in an upcoming version.

- The current rather simplistic configuration approach using system properties should actually be replaced by a properly
provided externalized configuration within the class path. There is already a draft for this in `src/main/resources/config.yaml`
but needs implementation and will also be done with an upcoming version.

- In the long run, having proper lifecycle control over your entities proves to be more than helpful. The current (brutely blocking) 
approach to query for `/request` resources will be replaces by a future/promise based mechanism.
