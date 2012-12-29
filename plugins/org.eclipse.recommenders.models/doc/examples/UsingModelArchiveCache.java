package examples;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.archives.ModelArchiveCache;
import org.eclipse.recommenders.models.archives.ModelArchiveCacheEvents;
import org.eclipse.recommenders.models.archives.ModelArchiveCoordinate;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.eventbus.Subscribe;

@SuppressWarnings("unused")
public class UsingModelArchiveCache {

    void downloadModelArchive(ModelArchiveCoordinate model, ModelArchiveCache repository) throws IOException {
        repository.resolve(model, newMonitor());
    }

    void findLocalModelArchive(ModelArchiveCoordinate model, ModelArchiveCache repository) throws IOException {
        if (!repository.location(model).isPresent()) {
            repository.resolve(model, newMonitor());
        }
    }

    void deleteCachedModelArchive(ModelArchiveCoordinate model, ModelArchiveCache repository) throws IOException {
        repository.delete(model, newMonitor());
    }

    void deleteIndex(ModelArchiveCache repository) throws IOException {
        repository.delete(ModelArchiveCache.INDEX, newMonitor());
    }

    void findAllModelArtifacts(ProjectCoordinate[] gavs, String[] knownModelTypes, ModelArchiveCache repository) {

        Table<ProjectCoordinate, String, Pair<ModelArchiveCoordinate, Boolean>> mappings = HashBasedTable.create();
        for (ProjectCoordinate gav : gavs) {
            for (String modelType : knownModelTypes) {
                ModelArchiveCoordinate coord =
                        repository.searchModelArchive(gav, modelType).or(ModelArchiveCoordinate.UNKNOWN);
                Boolean downloaded = false;
                if (coord != ModelArchiveCoordinate.UNKNOWN) {
                    downloaded = repository.location(coord).isPresent();
                }
                mappings.put(gav, modelType, Pair.of(coord, downloaded));
            }
        }
        // update ui...
    }

    @Subscribe
    void onEvent(ModelArchiveCacheEvents.ModelArchiveCacheOpenedEvent e) {
        // TODO check if a new index is available and download it

    }

    @Subscribe
    void onEvent(ModelArchiveCacheEvents.ModelArchiveCacheClosedEvent e) {
        // TODO persists what needs to be persisted
    }

    @Subscribe
    void onEvent(ModelArchiveCacheEvents.ModelArchiveInstalledEvent e) {
        // TODO delete old cached keys, and reload the models currently required
    }

    private IProgressMonitor newMonitor() {
        // TODO Auto-generated method stub
        return null;
    }
}
