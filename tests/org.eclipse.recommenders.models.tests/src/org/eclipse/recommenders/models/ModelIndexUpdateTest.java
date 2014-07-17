package org.eclipse.recommenders.models;

import static org.eclipse.recommenders.tests.models.utils.ModelIndexTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Test;

public class ModelIndexUpdateTest {

    private static ProjectCoordinate PC_1 = new ProjectCoordinate("org.example", "project", "1.0.0");
    private static ProjectCoordinate PC_2 = new ProjectCoordinate("org.example", "project", "2.0.0");
    private static ProjectCoordinate PC_3 = new ProjectCoordinate("org.example", "example", "1.0.0");
    private static ProjectCoordinate PC_4 = new ProjectCoordinate("com.example", "project", "1.0.0");

    @Test
    public void testUpdatingIndex() throws Exception {
        Directory oldIndex = createIndexDirectory(PC_1, PC_3, PC_4);
        Directory newIndex = createIndexDirectory(PC_2, PC_4);

        IModelIndex sut = new ModelIndex(oldIndex);
        sut.open();

        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_1.toString()).get(), is(equalTo(PC_1)));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_2.toString()).isPresent(), is(false));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_3.toString()).get(), is(equalTo(PC_3)));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_4.toString()).get(), is(equalTo(PC_4)));

        sut.updateIndex(newIndex);

        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_1.toString()).isPresent(), is(false));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_2.toString()).get(), is(equalTo(PC_2)));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_3.toString()).isPresent(), is(false));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_4.toString()).get(), is(equalTo(PC_4)));

        sut.close();
    }

    @Test
    public void testUpdatingEmptyIndex() throws Exception {
        Directory oldIndex = createIndexDirectory();
        Directory newIndex = createIndexDirectory(PC_2, PC_4);

        IModelIndex sut = new ModelIndex(oldIndex);
        sut.open();

        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_2.toString()).isPresent(), is(false));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_4.toString()).isPresent(), is(false));

        sut.updateIndex(newIndex);

        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_2.toString()).get(), is(equalTo(PC_2)));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_4.toString()).get(), is(equalTo(PC_4)));

        sut.close();
    }

    @Test
    public void testUpdatingIndexWithEmptyDirectory() throws Exception {
        Directory oldIndex = createIndexDirectory(PC_2, PC_4);
        Directory newIndex = createIndexDirectory();

        IModelIndex sut = new ModelIndex(oldIndex);
        sut.open();

        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_2.toString()).get(), is(equalTo(PC_2)));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_4.toString()).get(), is(equalTo(PC_4)));

        sut.updateIndex(newIndex);

        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_2.toString()).isPresent(), is(false));
        assertThat(sut.suggestProjectCoordinateByArtifactId(PC_4.toString()).isPresent(), is(false));

        sut.close();
    }

    private Directory createIndexDirectory(ProjectCoordinate... projectCoordinates) throws Exception {
        Document[] documents = new Document[projectCoordinates.length];

        for (int i = 0; i < projectCoordinates.length; i++) {
            ProjectCoordinate pc = projectCoordinates[i];
            documents[i] = coordinateWithSymbolicName(new DefaultArtifact(pc.toString()), pc.toString());
        }

        return inMemoryIndex(documents);
    }

}
