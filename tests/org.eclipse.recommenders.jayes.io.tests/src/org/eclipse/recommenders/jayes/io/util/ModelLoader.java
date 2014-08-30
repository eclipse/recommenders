package org.eclipse.recommenders.jayes.io.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.io.jbif.JayesBifReader;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class ModelLoader {

    private static final String REMOTE_MODEL_REPO = "http://download.eclipse.org/recommenders/models/luna/";

    private final List<BayesNet> networks;
    private static final Logger logger = LoggerFactory.getLogger(ModelLoader.class);

    public ModelLoader(String modelArtifact) throws ClassNotFoundException, IOException {
        networks = loadModels(modelArtifact);
        logger.info("successfully loaded " + networks.size() + " models");
    }

    private List<BayesNet> loadModels(String modelArtifact) throws ClassNotFoundException, IOException {

        ModelRepository repo = new ModelRepository(new File("target/local-repo"),REMOTE_MODEL_REPO);

        Optional<File> models = repo.resolve(ModelCoordinate.valueOf("jre:jre:call:zip:1.0.0"), true);

        return readModelFromZip(models.get());
    }

    private List<BayesNet> readModelFromZip(File zip) throws IOException, ClassNotFoundException {
        ZipInputStream stream = new ZipInputStream(new FileInputStream(zip));
        List<BayesNet> models = new ArrayList<BayesNet>();
        ZipEntry entry;
        while ((entry = stream.getNextEntry()) != null) {
            if (entry.isDirectory() || entry.getName().startsWith("META-INF")) {
                continue;
            }
            logger.debug(entry.getName());
            @SuppressWarnings("resource")
            // everything should get closed by the ZipInputStream
            JayesBifReader rdr = new JayesBifReader(stream);
            models.add(rdr.read());
            stream.closeEntry();
        }
        stream.close();
        return models;
    }

    public List<BayesNet> getNetworks() {
        return this.networks;
    }

}