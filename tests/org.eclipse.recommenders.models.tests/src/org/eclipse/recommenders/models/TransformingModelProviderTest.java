package org.eclipse.recommenders.models;

import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.utils.Throws.throwUnhandledException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.testing.JarFileMockBuilder;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public class TransformingModelProviderTest {

    private static final String TRANSFORM_EXTENSION = "transform";
    private static final Map<String, IInputStreamTransformer> NO_TRANSFORMERS = Collections.EMPTY_MAP;

    private static final UniqueTypeName ORG_EXAMPLE_FOO = new UniqueTypeName(ProjectCoordinate.UNKNOWN,
            VmTypeName.get("Lorg/example/Foo"));
    private static final UniqueTypeName ORG_EXAMPLE_BAR = new UniqueTypeName(ProjectCoordinate.UNKNOWN,
            VmTypeName.get("Lorg/example/Bar"));
    private static final UniqueTypeName ORG_EXAMPLE_QUZ = new UniqueTypeName(ProjectCoordinate.UNKNOWN,
            VmTypeName.get("Lorg/example/Quz"));

    private static final String ORG_EXAMPLE_FOO_JSON_TRANSFORM = "org/example/Foo.json.transform";
    private static final String ORG_EXAMPLE_BAR_JSON = "org/example/Bar.json";

    private static final String TRANSFORMED_DATA = "Transformed Data";
    private static final String UNTRANSFORMED_DATA = "Untransformed Data";

    @Test
    public void testNoModel() throws Exception {
        SimpleModelProvider<UniqueTypeName, String> sut = create(NO_TRANSFORMERS);
        ZipFile zip = mockZipFile();

        Optional<String> transformedModel = sut.doAcquireModel(ORG_EXAMPLE_QUZ, zip);
        assertThat(transformedModel.isPresent(), is(false));
    }

    @Test
    public void testNoTransformers() throws Exception {
        SimpleModelProvider<UniqueTypeName, String> sut = create(NO_TRANSFORMERS);
        ZipFile zip = mockZipFile();

        Optional<String> transformedModel = sut.doAcquireModel(ORG_EXAMPLE_FOO, zip);
        assertThat(transformedModel.isPresent(), is(false));

        String untransformedModel = sut.doAcquireModel(ORG_EXAMPLE_BAR, zip).get();
        assertThat(untransformedModel, is(equalTo(UNTRANSFORMED_DATA)));
    }

    @Test
    public void testTransform() throws Exception {
        Map<String, IInputStreamTransformer> transformers = ImmutableMap.<String, IInputStreamTransformer>of(
                TRANSFORM_EXTENSION, new DummyTransformer());
        SimpleModelProvider<UniqueTypeName, String> sut = create(transformers);
        ZipFile zip = mockZipFile();

        String transformedModel = sut.doAcquireModel(ORG_EXAMPLE_FOO, zip).get();
        Assert.assertThat(transformedModel, Matchers.is(Matchers.equalTo(TRANSFORMED_DATA)));

        String untransformedModel = sut.doAcquireModel(ORG_EXAMPLE_BAR, zip).get();
        Assert.assertThat(untransformedModel, Matchers.is(Matchers.equalTo(UNTRANSFORMED_DATA)));
    }

    private ZipFile mockZipFile() {
        JarFileMockBuilder builder = new JarFileMockBuilder();
        builder.addEntry(ORG_EXAMPLE_FOO_JSON_TRANSFORM, createInputStream(UNTRANSFORMED_DATA));
        builder.addEntry(ORG_EXAMPLE_BAR_JSON, createInputStream(UNTRANSFORMED_DATA));
        return builder.build();
    }

    private SimpleModelProvider<UniqueTypeName, String> create(Map<String, IInputStreamTransformer> transformers) {
        IModelRepository repository = mock(IModelRepository.class);
        IModelArchiveCoordinateAdvisor models = mock(IModelArchiveCoordinateAdvisor.class);

        return new PoolingModelProviderStub(repository, models, "type", transformers);
    }

    private ByteArrayInputStream createInputStream(String data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            throwUnhandledException(e);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private final class PoolingModelProviderStub extends PoolingModelProvider<UniqueTypeName, String> {
        private PoolingModelProviderStub(IModelRepository repository, IModelArchiveCoordinateAdvisor index,
                String modelType, Map<String, IInputStreamTransformer> transformers) {
            super(repository, index, modelType, transformers);
        }

        @Override
        protected Optional<String> loadModel(InputStream stream, UniqueTypeName key) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return of(sb.toString());
        }

        @Override
        protected String getPath(UniqueTypeName key) {
            return Zips.path(key.getName(), Constants.DOT_JSON);
        }
    }

    private final class DummyTransformer implements IInputStreamTransformer {

        @Override
        public InputStream transform(InputStream stream) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(TRANSFORMED_DATA.getBytes());
            } catch (IOException e) {
                throwUnhandledException(e);
            }
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }
}
