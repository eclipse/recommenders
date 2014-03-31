package org.eclipse.recommenders.internal.models.rcp.listener;

import static org.eclipse.recommenders.internal.models.rcp.ModelsPackage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor;
import org.eclipse.recommenders.internal.models.rcp.ModelRepository;
import org.eclipse.recommenders.internal.models.rcp.State;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelIndex;
import org.eclipse.recommenders.utils.Zips;

public class RepositorySynchronizers {

    public static class ModelArchiveRequestScheduler extends EContentAdapter {
        @Override
        public void notifyChanged(Notification msg) {
            switch (msg.getFeatureID(ModelRepository.class)) {
            case MODEL_REPOSITORY__REQUESTS:
                if (msg.getEventType() == Notification.ADD) {
                    ModelArchiveDescriptor d = (ModelArchiveDescriptor) msg.getNewValue();
                    Notifier target2 = getTarget();
                    // sut.getRepository().getLocation(d.getCoordinate(), true);
                }
            }
        }
    }

    public static class ModelRepositorySetUrlHandler extends AdapterImpl {

        @Override
        public void notifyChanged(Notification msg) {
            switch (msg.getFeatureID(ModelRepository.class)) {
            case MODEL_REPOSITORY__URL:
                ModelRepository repo = (ModelRepository) getTarget();
                File basedir = repo.getBasedir();
                File cache = new File(basedir, "repository");
                URL url = repo.getUrl();
                try {
                    IModelRepository impl = new org.eclipse.recommenders.models.ModelRepository(cache,
                            url.toExternalForm());
                    repo.setRepository(impl);
                    ModelArchiveDescriptor index = repo.find(ModelIndex.INDEX);
                    new IndexAvailableRepositoryHandler(index);
                    repo.download(index, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

        }

        public static class IndexAvailableRepositoryHandler extends AdapterImpl {

            private ModelArchiveDescriptor index;
            private ModelRepository repo;
            private File indexdir;

            public IndexAvailableRepositoryHandler(ModelArchiveDescriptor index) {
                this.index = index;
                repo = index.getOrigin();
                index.eAdapters().add(this);
            }

            @Override
            public void notifyChanged(Notification msg) {
                switch (msg.getFeatureID(ModelArchiveDescriptor.class)) {
                case MODEL_ARCHIVE_DESCRIPTOR__STATE:
                    if (index.getState() == State.CACHED) {
                        indexdir = new File(repo.getBasedir(), "index");
                        unzipIndex();
                        createAndInitializeModelIndex();
                        unref();
                    }
                }
            }

            private void createAndInitializeModelIndex() {
                ModelIndex index = new ModelIndex(indexdir);
                repo.setIndex(index);
                try {
                    index.open();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            private void unzipIndex() {
                try {
                    Zips.unzip(index.getLocation(), indexdir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void unref() {
                index.eAdapters().remove(this);
                index = null;
            }
        }
    }
}
