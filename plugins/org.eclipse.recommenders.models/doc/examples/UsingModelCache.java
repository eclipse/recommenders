package examples;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.models.Gav;
import org.eclipse.recommenders.models.repo.Coordinate;
import org.eclipse.recommenders.models.repo.ModelCache;
import org.eclipse.recommenders.models.repo.ModelCacheEvents;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.eventbus.Subscribe;

@SuppressWarnings("unused")
public class UsingModelCache {

    void downloadModelArchive(Coordinate model, ModelCache repository) {
        File archive = repository.resolve(model, newMonitor());
    }

    void findLocalModelArchive(Coordinate model, ModelCache repository) {
        if (!repository.get(model).isPresent()) {
            File archive = repository.resolve(model, newMonitor());
        }
    }

    void deleteCachedModelArchive(Coordinate model, ModelCache repository) {
        repository.delete(model, newMonitor());
    }

    void deleteIndex(ModelCache repository) {
        repository.delete(ModelCache.INDEX, newMonitor());
    }

    void findAllModelArtifacts(Gav[] gavs, String[] knownModelTypes, ModelCache repository) {

        Table<Gav, String, Pair<Coordinate, Boolean>> mappings = HashBasedTable.create();
        for (Gav gav : gavs) {
            for (String modelType : knownModelTypes) {
                Coordinate coord = repository.searchModel(gav, modelType).or(Coordinate.UNKNOWN);
                Boolean downloaded = false;
                if (coord != Coordinate.UNKNOWN) {
                    downloaded = repository.get(coord).isPresent();
                }
                mappings.put(gav, modelType, Pair.of(coord, downloaded));
            }
        }
        // update ui...
    }

    @Subscribe
    void onEvent(ModelCacheEvents.ModelCacheStartedEvent e) {
        // TODO check if a new index is available and download it

    }

    @Subscribe
    void onEvent(ModelCacheEvents.ModelCacheClosedEvent e) {
        // TODO persists what needs to be persisted
    }

    @Subscribe
    void onEvent(ModelCacheEvents.ModelArchiveUpdatedEvent e) {
        // TODO delete old cached keys, and reload the models currently required
    }

    private IProgressMonitor newMonitor() {
        // TODO Auto-generated method stub
        return null;
    }
}
