package org.eclipse.recommenders.models;

import static com.google.common.base.Charsets.UTF_8;
import static org.eclipse.recommenders.utils.Fingerprints.sha1;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.GetTask;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
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

        Transporter metadataDownload = mockedDownload(ImmutableMap.of(
                "org/example/example/1.0.0-SNAPSHOT/maven-metadata.xml", metadata,
                "org/example/example/1.0.0-SNAPSHOT/maven-metadata.xml.sha1", sha1(metadata)));
        Transporter modelDownload = mockedDownload(ImmutableMap.of(
                "org/example/example/1.0.0-SNAPSHOT/example-1.0.0-20140625.000000-1-classifier.zip", model,
                "org/example/example/1.0.0-SNAPSHOT/example-1.0.0-20140625.000000-1-classifier.zip.sha1", sha1(model)));

        RepositorySystem system = createRepositorySystem(metadataDownload, modelDownload);
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

    private Transporter mockedDownload(Map<String, String> resources) throws Exception {
        Transporter transporter = mock(Transporter.class);
        for (Entry<String, String> resource : resources.entrySet()) {
            doAnswer(new Get(resource.getValue())).when(transporter).get(
                    (GetTask) argThat(hasProperty("location", equalTo(new URI(resource.getKey())))));

        }
        return transporter;
    }

    private static RepositorySystem createRepositorySystem(Transporter... transporters) throws Exception {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.setServices(TransporterFactory.class, new MockTransporterFactory(transporters));

        return locator.getService(RepositorySystem.class);
    }

    private final class Get implements Answer<Void> {

        private final String contents;

        public Get(String contents) {
            this.contents = contents;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            GetTask task = (GetTask) invocation.getArguments()[0];
            OutputStream out = task.newOutputStream();
            byte[] bytes = contents.getBytes(UTF_8);
            task.getListener().transportStarted(0, bytes.length);
            out.write(bytes);
            task.getListener().transportProgressed(ByteBuffer.wrap(bytes));
            out.close();
            return null;
        }
    }

    private static class MockTransporterFactory implements TransporterFactory {

        private final Transporter[] transporters;

        private int transportersUsed = 0;

        public MockTransporterFactory(Transporter... transporters) {
            this.transporters = transporters;
        }

        @Override
        public Transporter newInstance(RepositorySystemSession session, RemoteRepository repository) {
            return transporters[transportersUsed++];
        }

        @Override
        public float getPriority() {
            return 0;
        }
    }
}
