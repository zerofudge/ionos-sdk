import com.profitbricks.sdk.model.*
import static com.profitbricks.sdk.Commands.*

class SDKExample {
    final static void main(final String[] args) {

        // Creating a Data Center
        println "Creating a Data Center"

        DataCenter dc = new DataCenter(
            name: "Example DC",
            location: 'de/fkb',
            description: 'desc'
        ).create() as DataCenter

        println "Data Center Ready"

        // Listing Data Centers
        println "Listing Data Centers"
        println dc.all


        // Add a LAN
        println "Creating a public LAN"
        LAN lan = new LAN(
            dataCenter: dc,
            name: "public lan",
            _public: true
        ).create()

        println "Public LAN Ready"

        // Add a server
        println "Creating a server"
        Server server = new Server(
            dataCenter: dc,
            name: "Example server",
            cores: 1,
            ram: 1024
        ).create()

        println "Server Ready"

        // Reading server
        println "Read server"
        println server.read()

        // Adding NIC to the example server
        println "Adding NIC to server"
        NIC nic = new NIC(
            server: server,
            lan: lan,
            name: "example nic"
        ).create()
        println "NIC Ready"

        // Find a Linux image to attach to volume
        println "Searching for a Linux image"
        Image image = new Image()
        image = image.all.collect{image.read(it) as Image}.findAll{
            it._public &&
            it.location == dc.location &&
            it.licenceType =~ /(?i)linux/ &&
            it.imageType =~ /(?i)hdd/
        }.first()

        println "Linux Image found: $image"

        // Create a volume
        println "Creating a Volume"
        Volume volume = new Volume(
            dataCenter: dc,
            name: "OS volume",
            size: 4,
            image: image.id,
            type: "HDD",
            imagePassword: "test1234"

        ).create()
        println "Volume Ready"


        // Attach the OS volume to the example server
        println "Attaching the OS volume to the example server"
        attach(server, volume)
        println "Volume Attached"

        // List attached volumes
        println "Listing attached volumes"
        println attachedVolumes(server)

        // Example cleaning
        println "Started cleaning"
        def _dc = dc.read(dc.id) as DataCenter
        _dc.delete()
        println "Cleaning Complete"
    }
}
