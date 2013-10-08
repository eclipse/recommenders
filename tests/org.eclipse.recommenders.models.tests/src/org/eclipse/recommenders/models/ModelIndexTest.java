package org.eclipse.recommenders.models;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.recommenders.utils.Artifacts;
import org.eclipse.recommenders.utils.Constants;
import org.junit.Test;
import org.sonatype.aether.artifact.Artifact;

public class ModelIndexTest {

    private static final Artifact PROJECT_1_0_0 = Artifacts.newArtifact("org.example:project:1.0.0");
    private static final Artifact PROJECT_1_0 = Artifacts.newArtifact("org.example:project:1.0");
    private static final Artifact PROJECT_1_0_0_RC1 = Artifacts.newArtifact("org.example:project:1.0.0.rc1");

    private static final String SYMBOLIC_NAME = "org.example.project";

    private static final ProjectCoordinate EXPECTED = new ProjectCoordinate("org.example", "project", "1.0.0");

    @Test
    public void testValidVersion() throws Exception {
        Directory index = inMemoryIndex(coordinateWithSymbolicName(PROJECT_1_0_0, SYMBOLIC_NAME));

        IModelIndex sut = new ModelIndex(index);
        sut.open();
        ProjectCoordinate pc = sut.suggestProjectCoordinateByArtifactId(SYMBOLIC_NAME).get();
        sut.close();

        assertThat(pc, is(equalTo(EXPECTED)));
    }

    @Test
    public void testNonCanonicalVersion() throws Exception {
        Directory index = inMemoryIndex(coordinateWithSymbolicName(PROJECT_1_0, SYMBOLIC_NAME));

        IModelIndex sut = new ModelIndex(index);
        sut.open();
        ProjectCoordinate pc = sut.suggestProjectCoordinateByArtifactId(SYMBOLIC_NAME).get();
        sut.close();

        assertThat(pc, is(equalTo(EXPECTED)));
    }

    @Test
    public void testInvalidVersion() throws Exception {
        Directory index = inMemoryIndex(coordinateWithSymbolicName(PROJECT_1_0_0_RC1, SYMBOLIC_NAME));

        IModelIndex sut = new ModelIndex(index);
        sut.open();
        ProjectCoordinate pc = sut.suggestProjectCoordinateByArtifactId(SYMBOLIC_NAME).get();
        sut.close();

        assertThat(pc, is(equalTo(EXPECTED)));
    }

    private Directory inMemoryIndex(Document... documents) throws Exception {
        RAMDirectory directory = new RAMDirectory();
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35, new KeywordAnalyzer());
        IndexWriter writer = new IndexWriter(directory, conf);
        for (Document document : documents) {
            writer.addDocument(document);
        }
        writer.close();
        return directory;
    }

    private Document coordinateWithSymbolicName(Artifact coordinate, String symbolicName) {
        Document doc = new Document();
        doc.add(newStored(Constants.F_COORDINATE, coordinate.toString()));
        doc.add(newStored(Constants.F_SYMBOLIC_NAMES, symbolicName));
        return doc;
    }

    private Field newStored(String key, String value) {
        return new Field(key, value, Store.YES, Index.NOT_ANALYZED);
    }
}
