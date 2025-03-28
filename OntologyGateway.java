import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import py4j.GatewayServer;

import java.io.File;
import java.util.Map;

public class OntologyGateway {
    private final OWLOntologyManager manager;
    private OWLOntology ontology;

    public OntologyGateway() {
        this.manager = OWLManager.createOWLOntologyManager();
    }

    // Load an ontology from a Turtle (.ttl) or RDF/XML (.rdf) file
    public void loadOntology(String filePath) throws OWLOntologyCreationException {
        File file = new File(filePath);
        this.ontology = manager.loadOntologyFromOntologyDocument(file);
        System.out.println("Ontology loaded successfully: " + filePath);
    }

    // Save the ontology in Turtle format while retaining existing prefixes
    public void saveOntology(String outputPath) throws Exception {
        if (ontology == null) {
            System.out.println("No ontology is loaded!");
            return;
        }

        // Get the ontology format (this could be RDF/XML, Turtle, etc.)
        OWLDocumentFormat format = manager.getOntologyFormat(ontology);

        // Check if the format is PrefixOWLDocumentFormat to get prefixes
        if (format.isPrefixOWLDocumentFormat()) {
            // Get the map of prefixes from the ontology format
            Map<String, String> prefixMap = format.asPrefixOWLDocumentFormat().getPrefixName2PrefixMap();

            // Create the Turtle format object
            TurtleDocumentFormat turtleFormat = new TurtleDocumentFormat();

            // Copy the prefixes to the Turtle format
            for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
                turtleFormat.setPrefix(entry.getKey(), entry.getValue());
            }

            // Save the ontology with the same prefixes
            File outputFile = new File(outputPath);
            manager.saveOntology(ontology, turtleFormat, IRI.create(outputFile.toURI()));

            System.out.println("Ontology saved successfully: " + outputPath);
        } else {
            System.out.println("The ontology format does not support prefixes.");
        }
    }

    // Start the Py4J Gateway Server
    public static void main(String[] args) {
        OntologyGateway app = new OntologyGateway();
        GatewayServer server = new GatewayServer(app, 25333);
        server.start();
        System.out.println("Py4J Gateway Server Started...");
    }
}
