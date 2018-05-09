### release 3.1.0 (05/??/2018)

- change: fetched `NIC`s now always contain a valid (yet shallow) `LAN`


### release 3.0.0 (05/03/2018)

- change: the artifact was renamed to `groovy-sdk`, the group name changed to `com.profitbricks`
- change: the artifact is no fat jar anymore, streamlined set of compile dependencies
- fix: timings for status requests now actually work reliably
- fix: removed some unnecessary code parts and refactored others
- change: the artifacts are now available from official maven central
- change: refactored test code to actual tests (Spock Specs)
- change: removed `Share` and `Resource` entities until the user management API is fixed
- fix: streamlined, corrected and improved the documentation
- change: all API calls (all CRUD methods + list and commands) can be called with an optional map argument
- change: based on the previous change, most configuration options can now be passed per API call
- fix: `Volume.update()`, `LAN.update()` and `Snapshot.update()` works now reliably
- change: `Server.update()` now always include `allowReboot: true`
- change: regarding configuration options: the system property prefix has been renamed from `api.` to `com.profitbricks.sdk.`


### release 2.0.0 (09/25/2017)

- add image aliases
- add IP failover
- add user management
- add ssh keys to volume create
- enable firewall activation/deactivation
- fix NIC update for boolean parameters set to false
- fix load balancer update for boolean parameters set to false
- fix IP block read for `ips` property


### release 1.4.1 (06/14/2017)

- update Readme and provided an example app
- complete test coverage for all endpoints


### release 1.4.0 (03/08/2017)

- fixed an issue with handling failed requests


### release 1.3.0 (03/08/2017)

- fixed an issue with handling request timeouts


### release 1.2.0 (03/08/2017)

- fixed an issue with toString()'ing an initial model instance
- when reading NICs, the associated LAN is also read implicitly
- slightly improved (debug) logging


### release 1.1.0 (03/02/2017)

- multithreading issue fixed
- minor documentation enhancements


### release 1.0.0 (02/15/2017)

- initial feature complete (API wise) version
