package examples;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.recommenders.models.Coordinate;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelRepositoryEvents;

import com.google.common.eventbus.Subscribe;

public class RepositoryScenarios {

    void downloadModelArchive(Coordinate model, IModelRepository repository) {
        File archive = repository.resolve(model, newMonitor());
    }

    void findLocalModelArchive(Coordinate model, IModelRepository repository) {
        if (repository.isCached(model)) {
            File archive = repository.resolve(model, newMonitor());
        }
    }

    void deleteCachedModelArchive(Coordinate model, IModelRepository repository) {
        repository.delete(model, newMonitor());
    }

    void deleteIndex(IModelRepository repository) {
        repository.delete(IModelRepository.INDEX, newMonitor());
    }

    private NullProgressMonitor newMonitor() {
        return new NullProgressMonitor();
    }

    @Subscribe
    void listenRepositoryStarted(ModelRepositoryEvents.RepositoryCreatedEvent e) {
        // TODO check if a new index is available and download it

    }

    @Subscribe
    void listenRepositoryClosed(ModelRepositoryEvents.RepositoryShutdownEvent e) {
        // TODO persists what needs to be persisted
    }

    @Subscribe
    void listenRepositoryClosed(ModelRepositoryEvents.ModelUpdatedEvent e) {
        // TODO delete old cached keys, and reload the models currently required
    }

}
