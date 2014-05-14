package org.eclipse.recommenders.models;

import static com.google.common.base.Charsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.AbstractTransporter;
import org.eclipse.aether.spi.connector.transport.GetTask;
import org.eclipse.aether.spi.connector.transport.PeekTask;
import org.eclipse.aether.spi.connector.transport.PutTask;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.recommenders.utils.Fingerprints;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class ModelRepositoryTest {

    private static ModelCoordinate EXAMPLE_MC = new ModelCoordinate("org.example", "example", "classifier", "zip",
            "1.0.0");

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void test() throws Exception {
        final String metadata = mavenMetadata(EXAMPLE_MC);
        final String model = "model data";

        MockTransporterFactory transporterFactory = new MockTransporterFactory(ImmutableMap.of(
                "org/example/example/1.0.0-SNAPSHOT/maven-metadata.xml", metadata,
                "org/example/example/1.0.0-SNAPSHOT/maven-metadata.xml.sha1", Fingerprints.sha1(metadata),
                "org/example/example/1.0.0-SNAPSHOT/example-1.0.0-20140625.000000-1-classifier.zip", model,
                "org/example/example/1.0.0-SNAPSHOT/example-1.0.0-20140625.000000-1-classifier.zip.sha1",
                Fingerprints.sha1(model)));
        RepositorySystem system = createRepositorySystem(transporterFactory);
        IModelRepository sut = new ModelRepository(system, tmp.getRoot(), "http://www.example.org/repo");

        Optional<File> resolvedModel = sut.resolve(EXAMPLE_MC, false);
        assertThat(Files.toString(resolvedModel.get(), UTF_8), is(equalTo("model data")));
    }

    private String mavenMetadata(ModelCoordinate mc) {
        /* @formatter:off */
        final String metadata = String.format("<?xml version='1.0' encoding='UTF-8'?>"
                + "<metadata modelVersion='1.1.0'>"
                + "<groupId>%1$s</groupId>"
                + "<artifactId>%2$s</artifactId>"
                + "<version>%3$s-SNAPSHOT</version>"
                + "<versioning>"
                + "<snapshot>"
                + "<timestamp>20140625.000000</timestamp>"
                + "<buildNumber>1</buildNumber>"
                + "</snapshot>"
                + "<lastUpdated>20140625000000</lastUpdated>"
                + "<snapshotVersions>"
                + "<snapshotVersion>"
                + "<classifier>%4$s</classifier>"
                + "<extension>%5$s</extension>"
                + "<value>%3$s-20140625.000000-1</value>"
                + "<updated>20140625000000</updated>"
                + "</snapshotVersion>"
                + "</snapshotVersions>"
                + "</versioning>"
                + "</metadata>",
                mc.getGroupId(), mc.getArtifactId(), mc.getVersion(), mc.getClassifier(), mc.getExtension());
        /* @formatter:on */
        return metadata;
    }

    private static RepositorySystem createRepositorySystem(TransporterFactory transporterFactory) {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.setServices(TransporterFactory.class, transporterFactory);

        return locator.getService(RepositorySystem.class);
    }

    private static class MockTransporterFactory implements TransporterFactory {

        private final Map<String, String> repoContents;

        public MockTransporterFactory(Map<String, String> repoContents) {
            this.repoContents = repoContents;
        }

        @Override
        public Transporter newInstance(RepositorySystemSession session, RemoteRepository repository) {
            return new MockTransporter(repoContents);
        }

        @Override
        public float getPriority() {
            return 0;
        }
    }

    private static class MockTransporter extends AbstractTransporter {

        private Map<String, String> contents = Maps.newHashMap();

        public MockTransporter(Map<String, String> repoContents) {
            contents = repoContents;
        }

        @Override
        public int classify(Throwable error) {
            error.printStackTrace();
            if (error instanceof FileNotFoundException) {
                return ERROR_NOT_FOUND;
            } else {
                return ERROR_OTHER;
            }
        }

        @Override
        protected void implPeek(PeekTask task) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void implGet(GetTask task) throws Exception {
            String data = contents.get(task.getLocation().toString());
            if (data == null) {
                throw new FileNotFoundException();
            }
            byte[] bytes = data.getBytes(UTF_8);
            utilGet(task, new ByteArrayInputStream(bytes), true, bytes.length, false);
        }

        @Override
        protected void implPut(PutTask task) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void implClose() {
            // No-op
        }
    }
}
