import com.profitbricks.sdk.model.*
import groovy.time.TimeCategory
import org.apache.http.client.HttpResponseException

import static com.profitbricks.sdk.Commands.*

// default run target: just `gradle run`
// see also JVM args in build.gradle
class SDKTester {
    private final static rand = new Random(System.currentTimeMillis())
    private final rnd = {rand.ints().findFirst().asInt}

    final static void main(final String[] args) {
        def _start = new Date()
        DataCenter dc

        new SDKTester().with { try {

            // -------- location list & read ----------

            Location loc = new Location()
            loc.all.collect{loc.read(it)}.each {
                println "found $it"
            }

            // -------- datacenter create ----------

            dc = new DataCenter(
                name: "datacenter_${rnd()}",
                location: 'de/fkb',
                description: 'deprecated'
            ).create() as DataCenter
            println "created $dc"
            assert dc == dc.read()

            // -------- datacenter list & read ----------

            dc.all.collect { dc.read(it) }.each {
                println "found $it"
            }

            // -------- datacenter update ---------

            dc.name = "shmuzz_${rnd()}"
            dc.description = 'groovy new datacenter!'
            assert dc.update()
            println "updated $dc"
            sleep(3000)
            assert dc == dc.read()

            // -------- server create ---------

            Server s = new Server(
                dataCenter: dc,
                name: "server_${rnd()}",
                cores: 1,
                ram: 1024
            ).create()
            s = s.read()
            assert s.vmState =~ /(?i)running/
            println "created $s"

            // -------- server list & read ----------

            s.all.collect { s.read(it) }.each {
                println "found $it"
            }

            // -------- server update ---------
            s.name = "snoopy_${rnd()}"
            s.cores *= 2
            s.ram *= 2
            assert s.update()
            println "updated $s"

            // -------- server delete ---------
            s.all.each { id ->
                def _s = s.read(id)
                assert _s.delete()
                println "deleted ${_s}"
            }

            s = s.create()
            println "recreated $s"

            // -------- volume create ---------

            Volume v = new Volume(
                dataCenter: dc,
                name: "volume_${rnd()}",
                size: 1
            ).create()
            v = v.read()
            println "created $v"

            // -------- volume update ---------

            v.name = "spliffy_${rnd()}"
            v.size = 2
            assert v.update()
            println "updated $v"

            // -------- volume list & read ----------

            v.all.collect { v.read(it) }.each {
                println "found $it"
            }

            // -------- volume delete ---------

            v.all.each { id ->
                def _v = v.read(id)
                assert _v.delete()
                println "deleted $_v"
            }

            v = v.create()
            println "recreated $v"

            // -------- lan create ---------

            LAN l = new LAN(
                dataCenter: dc,
                name: "lan_${rnd()}",
                _public: true
            ).create()
            l = l.read()
            println "created $l"

            // -------- lan update ---------

            l.name = "snuffles_${rnd()}"
            l._public = false
            assert l.update()
            println "updated $l"

            // -------- lan list & read ----------

            l.all.collect { l.read(it) }.each {
                println "found $it"
            }

            // -------- lan delete ---------

            l.all.each { id ->
                def _l = l.read(id)
                assert _l.delete()
                println "deleted $_l"
            }

            l = l.create()
            println "recreated $l"

            // -------- nic create ---------

            NIC n = new NIC(
                server: s,
                lan: l,
                name: "nic_${rnd()}"
            ).create()
            n = n.read()
            println "created $n"
            assert n.lan

            // -------- nic update ---------

            n.name = "snoozles_${rnd()}"
            n.ips = ['192.168.0.2']
            n.dhcp = false
            n.nat = false
            assert n.update()
            println "updated $n"

            // -------- nic list & read ----------

            n.all.collect { n.read(it) }.each {
                println "found $it"
            }

            // -------- nic delete ---------

            n.all.each { id ->
                def _n = n.read(id)
                assert _n.delete()
                println "deleted $_n"
            }

            n = n.create()
            println "recreated $n"

            // -------- loadbalancer create ---------
            //sleep was added because the API fails to create a loadbalancer immediately after the last operation
            sleep(5000)
            LoadBalancer lb = new LoadBalancer(
                dataCenter: dc,
                name: "loadbalancer_${rnd()}"
            ).create()
            lb = lb.read()
            println "created $lb"

            // -------- loadbalancer update ---------

            lb.name = "smacky_${rnd()}"
            lb.dhcp = false
            lb.ip = '192.168.0.11'
            assert lb.update()
            println "updated $lb"

            // -------- loadbalancer list & read ----------

            lb.all.collect { lb.read(it) }.each {
                println "found $it"
            }

            // -------- loadbalancer delete ---------

            lb.all.each { id ->
                def _lb = lb.read(id)
                assert _lb.delete()
                println "deleted $_lb"
            }

            lb = lb.create()
            println "recreated $lb"

            // -------- crip delete and create ---------
            IPBlock ip = new IPBlock(
                location: dc.location,
                name: "ipblock_${rnd()}",
                size: 2
            ).create() as IPBlock
            ip = ip.read() as IPBlock
            println "created $ip"

            ip.all.each { id ->
                def _ip = ip.read(id) as IPBlock
                assert _ip.delete()
                println "deleted $_ip"
            }

            //
            // -------- firewall rule create ---------

            FirewallRule fw = new FirewallRule(
                nic: n,
                protocol: 'UDP'
            ).create()
            fw = fw.read()
            println "created $fw"

            // -------- firewall rule update ---------

            fw.name = "rule_${rnd()}"
            fw.sourceMac = 'aa:bb:cc:dd:ee:ff'
            fw.sourceIp = '23.23.23.23'
            fw.targetIp = n.ips.first()
            fw.portRangeStart = '1234'
            fw.portRangeEnd = '4711'

            assert fw.update()
            println "updated $fw"

            // -------- firewall list & read ----------

            fw.all.collect { fw.read(it) }.each {
                println "found $it"
            }

            // -------- firewall rule delete ---------

            fw.all.each { id ->
                def _fw = fw.read(id)
                assert _fw.delete()
                println "deleted $_fw"
            }

            fw = fw.create()
            println "recreated $fw"

            // -------- image list and read ---------

            Image i = new Image()
            i = i.all.collect{i.read(it) as Image}.findAll{
                it._public &&
                it.location == dc.location &&
                it.licenceType =~ /(?i)linux/ &&
                it.imageType =~ /(?i)cdrom/
            }.first()
            assert i
            println "selected image: $i"

            i.name = 'my ubuntu'
            i._public = false
            try {
                i.update()
            } catch (HttpResponseException e) {
                assert e.statusCode == 422
            } finally {
                i = i.read() as Image
            }

            // -------- snapshot list, delete & read ---------

            Snapshot sn = new Snapshot()
            sn.all.collect { sn.read(it) as Snapshot }.each {
                println "found $it"
                it.name = "${it.name} updated"
                it.description = "${it.description} updated"
                try {
                    assert it.update()
                } catch (HttpResponseException e) {
                    assert e.statusCode == 422
                }
                println "updated $it"
                assert it.delete()
                println "deleted $it"
            }

            // -------- commands: server ---------

            assert attach(s, v)
            println "attached $v to $s"
            assert attachedVolumes(s).collect { it.id }.contains(v.id)
            assert attachedVolume(s, v).id.equals(v.id)

            assert detach(s, v)
            println "detached $v from $s"

            assert attach(s, i)
            println "attached $i to $s"
            assert attachedCDROMs(s).collect { it.id }.contains(i.id)
            assert attachedCDROM(s, i).id.equals(i.id)

            assert detach(s, i)
            println "detached $i from $s"

            assert stop(s)
            assert (s = s.read()).vmState =~ /(?i)shutoff/
            println "stopped $s"
            assert start(s)
            assert (s = s.read()).vmState =~ /(?i)running/
            println "started $s"
            assert reboot(s)
            assert (s = s.read()).vmState =~ /(?i)running/
            println "rebooted $s"

            // -------- commands: volume ---------

            sn = snapshot(v)
            assert sn.id
            assert sn.location == dc.location
            println "created $sn"

            assert restore(v, sn)
            println "restored from $sn"

            // -------- commands: loadbalancer ---------

            println "associated ${associate(lb, n)} with ${lb.read()}"
            def loadBalancer = lb.read()

            associatedNics(loadBalancer).collect { it }.each {
                println "found associated ${n.read(it)}"
            }

            println "found associated ${associatedNic(lb, n)}"
            assert dissociate(lb, n)
            println "removed $n from ${lb.read()}"

            // -------- datacenter delete ---------

            def _dc = dc.read(dc.id) as DataCenter
            assert _dc.delete()
            println "deleted $_dc"

            // -------- request list & get status---------

            Request r = new Request()
            assert r.all.size > 0
            println "status found ${requestStatus(r.all[0])}"

            } finally {
                println "that took me ${TimeCategory.minus(new Date(), _start)}"
            }
        }
    }
}
