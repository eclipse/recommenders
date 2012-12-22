package examples;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.models.Gav;
import org.eclipse.recommenders.models.repo.Coordinate;
import org.eclipse.recommenders.models.repo.LocalModelRepository;
import org.eclipse.recommenders.models.repo.LocalModelRepositoryEvents;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.eventbus.Subscribe;

@SuppressWarnings("unused")
public class UsingLocalRepository {

    void downloadModelArchive(Coordinate model, LocalModelRepository repository) {
        File archive = repository.resolve(model, newMonitor());
    }

    void findLocalModelArchive(Coordinate model, LocalModelRepository repository) {
        if (repository.isCached(model)) {
            File archive = repository.resolve(model, newMonitor());
        }
    }

    void deleteCachedModelArchive(Coordinate model, LocalModelRepository repository) {
        repository.delete(model, newMonitor());
    }

    void deleteIndex(LocalModelRepository repository) {
        repository.delete(LocalModelRepository.INDEX, newMonitor());
    }

    void findAllModelArtifacts(Gav[] gavs, String[] knownModelClassifiers, LocalModelRepository repository) {

        Table<Gav, String, Pair<Coordinate, Boolean>> mappings = HashBasedTable.create();
        for (Gav gav : gavs) {
            for (String classifier : knownModelClassifiers) {
                Coordinate coord = repository.searchModel(gav, classifier).or(Coordinate.UNKNOWN);
                Boolean downloaded = false;
                if (coord != Coordinate.UNKNOWN) {
                    downloaded = repository.get(coord).exists();
                }
                mappings.put(gav, classifier, Pair.of(coord, downloaded));
            }
        }
        // update ui...
    }

    @Subscribe
    void listenRepositoryStarted(LocalModelRepositoryEvents.RepositoryCreatedEvent e) {
        // TODO check if a new index is available and download it

    }

    @Subscribe
    void listenRepositoryClosed(LocalModelRepositoryEvents.RepositoryShutdownEvent e) {
        // TODO persists what needs to be persisted
    }

    @Subscribe
    void listenRepositoryClosed(LocalModelRepositoryEvents.ModelUpdatedEvent e) {
        // TODO delete old cached keys, and reload the models currently required
    }

    private IProgressMonitor newMonitor() {
        // TODO Auto-generated method stub
        return null;
    }
}
