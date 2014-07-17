package org.eclipse.recommenders.models;

import static org.eclipse.recommenders.tests.models.utils.ModelIndexTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ModelIndexUpdateTestParam {

    private static ProjectCoordinate PC_1 = new ProjectCoordinate("org.example", "project", "1.0.0");
    private static ProjectCoordinate PC_2 = new ProjectCoordinate("org.example", "project", "2.0.0");
    private static ProjectCoordinate PC_3 = new ProjectCoordinate("org.example", "example", "1.0.0");
    private static ProjectCoordinate PC_4 = new ProjectCoordinate("com.example", "project", "1.0.0");

    private List<ProjectCoordinate> oldIndex;
    private List<ProjectCoordinate> newIndex;

    public ModelIndexUpdateTestParam(List<ProjectCoordinate> oldIndex, List<ProjectCoordinate> newIndex)
            throws Exception {
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        List<ProjectCoordinate> empty = Lists.newArrayList();

        scenarios.add(scenario(Lists.newArrayList(PC_1, PC_3, PC_4), Lists.newArrayList(PC_2, PC_4)));
        scenarios.add(scenario(Lists.newArrayList(PC_1, PC_3, PC_4), Lists.newArrayList(PC_2, PC_4)));
        scenarios.add(scenario(Lists.newArrayList(PC_2, PC_4), empty));
        scenarios.add(scenario(empty, Lists.newArrayList(PC_2, PC_4)));

        return scenarios;
    }

    private static Object[] scenario(List<ProjectCoordinate> oldIndex, List<ProjectCoordinate> newIndex) {
        return new Object[] { oldIndex, newIndex };
    }

    @Test
    public void test() throws Exception {
        Directory oldIndexDirectory = createIndexDirectory(oldIndex);
        Directory newIndexDirectory = createIndexDirectory(newIndex);

        IModelIndex sut = new ModelIndex(oldIndexDirectory);
        sut.open();

        for (ProjectCoordinate expected : oldIndex) {
            ProjectCoordinate actual = sut.suggestProjectCoordinateByArtifactId(expected.toString()).get();
            assertThat(actual, is(equalTo(expected)));
        }

        for (ProjectCoordinate expected : newIndex) {
            if (!oldIndex.contains(expected)) {
                assertThat(sut.suggestProjectCoordinateByArtifactId(expected.toString()).isPresent(), is(false));
            }
        }

        sut.updateIndex(newIndexDirectory);

        for (ProjectCoordinate expected : newIndex) {
            ProjectCoordinate actual = sut.suggestProjectCoordinateByArtifactId(expected.toString()).get();
            assertThat(actual, is(equalTo(expected)));
        }

        for (ProjectCoordinate expected : oldIndex) {
            if (!newIndex.contains(expected)) {
                assertThat(sut.suggestProjectCoordinateByArtifactId(expected.toString()).isPresent(), is(false));
            }
        }

        sut.close();
    }

    private Directory createIndexDirectory(Collection<ProjectCoordinate> projectCoordinates) throws Exception {
        Document[] documents = new Document[projectCoordinates.size()];

        int index = 0;
        for (ProjectCoordinate pc : projectCoordinates) {
            documents[index] = coordinateWithSymbolicName(new DefaultArtifact(pc.toString()), pc.toString());
            index++;
        }

        return inMemoryIndex(documents);
    }

}
