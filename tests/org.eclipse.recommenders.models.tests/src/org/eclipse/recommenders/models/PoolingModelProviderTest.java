package org.eclipse.recommenders.models;

import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.models.ModelArchiveCoordinate.UNKNOWN;
import static org.eclipse.recommenders.utils.names.VmTypeName.OBJECT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.utils.Zips;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

public class PoolingModelProviderTest {

    private static File zip;
    BasedTypeName someName = new BasedTypeName(ProjectCoordinate.UNKNOWN, OBJECT);

    @BeforeClass
    public static void beforeClass() throws IOException {
        zip = new File(Zips.NULL().getName());
        zip.deleteOnExit();
    }

    PoolingModelProvider<BasedTypeName, String> sut = create();

    @Test
    public void testAquireRelease() {
        Optional<String> last = null;
        for (int i = 0; i < 200; i++) {
            last = sut.acquireModel(someName);
            sut.releaseModel(last.get());
        }
        assertTrue("pool exhausted but returned all models properly", last.isPresent());
    }

    @Test
    public void testNoReleaseExhaustsPool() {

        Optional<String> last = null;
        for (int i = 0; i < 10; i++) {
            last = sut.acquireModel(someName);
        }
        assertFalse("pool did not get exhausted", last.isPresent());
    }

    @Test
    public void testRepeatedCallsReturnSameModel() {

        ;
        String model1 = sut.acquireModel(someName).orNull();
        String model2 = sut.acquireModel(someName).orNull();
        // two different models because model1 wasn't release yet
        assertNotSame(model1, model2);
        sut.releaseModel(model1);
        String model3 = sut.acquireModel(someName).orNull();
        // model1 was release and should be reused by the pool, thus should be the same as model1
        assertSame(model1, model3);

    }

    private PoolingModelProvider<BasedTypeName, String> create() {
        IModelRepository repository = mock(IModelRepository.class);
        when(repository.getLocation(any(ModelArchiveCoordinate.class))).thenReturn(of(zip));

        IModelArchiveCoordinateResolver models = mock(IModelArchiveCoordinateResolver.class);
        when(models.suggest(any(ProjectCoordinate.class), anyString())).thenReturn(of(UNKNOWN));

        return new PoolingModelProviderStub(repository, models, "calls");
    }

    private final class PoolingModelProviderStub extends PoolingModelProvider<BasedTypeName, String> {
        private PoolingModelProviderStub(IModelRepository repository, IModelArchiveCoordinateResolver index,
                String modelType) {
            super(repository, index, modelType);
        }

        @Override
        protected Optional<String> loadModel(ZipFile zip, BasedTypeName key) throws Exception {
            // return a "simple" model
            return of(new String(""));
        }
    }
}
