package org.eclipse.recommenders.internal.models.rcp.tests;

import static org.eclipse.recommenders.internal.models.rcp.ModelsPackage.MODEL_REPOSITORY__REPOSITORY;
import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor;
import org.eclipse.recommenders.internal.models.rcp.ModelRepository;
import org.eclipse.recommenders.internal.models.rcp.ModelsFactory;
import org.eclipse.recommenders.internal.models.rcp.State;
import org.eclipse.recommenders.internal.models.rcp.listener.RepositorySynchronizers;
import org.eclipse.recommenders.internal.models.rcp.listener.RepositorySynchronizers.ModelArchiveRequestScheduler;
import org.eclipse.recommenders.models.ModelIndex;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class ModelRepositoryTest {

    private static ModelRepository sut;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        File basedir = Files.createTempDir();
        sut = ModelsFactory.eINSTANCE.createModelRepository();
        sut.eAdapters().add(new RepositorySynchronizers.ModelRepositorySetUrlHandler());
        sut.setBasedir(basedir);
        sut.setUrl(new URL("http://download.eclipse.org/recommenders/models/luna-m3"));
        sut.eAdapters().add(new AdapterImpl() {

            ModelArchiveRequestScheduler s = new RepositorySynchronizers.ModelArchiveRequestScheduler();

            @Override
            public void notifyChanged(Notification msg) {
                switch (msg.getFeatureID(ModelRepository.class)) {
                case MODEL_REPOSITORY__REPOSITORY:
                    if (msg.getNewValue() != null) {
                        getTarget().eAdapters().add(s);
                    } else {
                        getTarget().eAdapters().remove(s);
                    }
                }
            }
        });
    }

    @Test
    public void testRequestDownload() throws MalformedURLException {
        ModelArchiveDescriptor index = sut.find(ModelIndex.INDEX);
        sut.getRequests().add(index);
    }

    @Test
    public void testSetUrlTriggersModelDownload() throws MalformedURLException {
        File basedir = Files.createTempDir();
        sut = ModelsFactory.eINSTANCE.createModelRepository();
        sut.eAdapters().add(new RepositorySynchronizers.ModelRepositorySetUrlHandler());
        sut.setBasedir(basedir);
        sut.setUrl(new URL("http://download.eclipse.org/recommenders/models/luna-m3"));
        System.out.println(sut);
    }

    @Test
    public void testCachingSame() {
        ModelArchiveDescriptor t1 = sut.find(ModelIndex.INDEX);
        ModelArchiveDescriptor t2 = sut.find(ModelIndex.INDEX);
        assertSame(t1, t2);
    }

    @Test
    public void testCachingNotSameAfterGCWhenNoRefsKept() {
        int t1 = System.identityHashCode(sut.find(ModelIndex.INDEX));
        System.gc();
        int t2 = System.identityHashCode(sut.find(ModelIndex.INDEX));
        assertNotEquals(t1, t2);
    }

    @Test
    public void testCachingSameAfterGCWhenRefsKept() {
        ModelArchiveDescriptor t1 = sut.find(ModelIndex.INDEX);
        System.gc();
        ModelArchiveDescriptor t2 = sut.find(ModelIndex.INDEX);
        assertSame(t1, t2);
    }

    @Test
    public void testFindGetCoordinate() {
        ModelArchiveDescriptor actual = sut.find(ModelIndex.INDEX);
        assertEquals(ModelIndex.INDEX, actual.getCoordinate());
    }

    @Test(expected = RuntimeException.class)
    public void testStateChangedEventSend() {
        ModelArchiveDescriptor actual = sut.find(ModelIndex.INDEX);
        actual.eAdapters().add(new AdapterImpl() {
            @Override
            public void notifyChanged(Notification msg) {
                throw new RuntimeException("all good if this happens!");
            }
        });
        actual.setState(State.DOWNLOADING);
    }

    @Test
    public void testDownloadCoordinate() {
        ModelArchiveDescriptor actual = sut.find(ModelIndex.INDEX);
        sut.download(actual, null);
        assertNotNull(actual.getLocation());
    }
}
