/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.eclipse.recommenders.snipmatch.LocationConstraint.*;
import static org.eclipse.recommenders.utils.Constants.DOT_JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.recommenders.snipmatch.FileSnippetRepository;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.LocationConstraint;
import org.eclipse.recommenders.snipmatch.SnipmatchContext;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.gson.GsonUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class FileSnippetRepositoryTest {

    private static final List<String> NO_EXTRA_SEARCH_TERMS = Collections.emptyList();
    private static final List<String> NO_TAGS = Collections.emptyList();

    private static final UUID FIRST_UUID = UUID.randomUUID();
    private static final UUID SECOND_UUID = UUID.randomUUID();
    private static final UUID THIRD_UUID = UUID.randomUUID();

    private static final String SNIPPET_NAME = "snippet";

    private FileSnippetRepository sut;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private File snippetsDir;

    @Before
    public void setUp() throws IOException {
        File baseDir = tempFolder.newFolder();
        snippetsDir = new File(baseDir, "snippets");
        snippetsDir.mkdirs();
        sut = new FileSnippetRepository(baseDir);
    }

    @Test
    public void testDeleteSnippet() throws Exception {
        Snippet snippet = new Snippet(FIRST_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        File snippetFile = storeSnippet(snippet);

        sut.open();
        boolean wasDeleted = sut.delete(snippet.getUuid());
        List<Recommendation<ISnippet>> search = sut.search(new SnipmatchContext(SNIPPET_NAME, FILE));
        sut.close();

        assertThat(wasDeleted, is(true));
        assertThat(snippetFile.exists(), is(false));
        assertThat(search.isEmpty(), is(true));
    }

    @Test
    public void testDontDeleteSnippet() throws Exception {
        Snippet snippet = new Snippet(FIRST_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        File snippetFile = storeSnippet(snippet);

        sut.open();
        boolean wasDeleted = sut.delete(SECOND_UUID);
        List<Recommendation<ISnippet>> search = sut.search(new SnipmatchContext(SNIPPET_NAME, FILE));
        sut.close();

        assertThat(wasDeleted, is(false));
        assertThat(snippetFile.exists(), is(true));
        assertThat(search.get(0).getProposal(), is((ISnippet) snippet));
        assertThat(search.size(), is(1));
    }

    @Test
    public void testDeleteOneKeepOneSnippet() throws Exception {
        Snippet snippetToDelete = new Snippet(FIRST_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        Snippet snippetToKeep = new Snippet(SECOND_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        File snippetFileToDelete = storeSnippet(snippetToDelete);
        File snippetFileToKeep = storeSnippet(snippetToKeep);

        sut.open();
        boolean wasDeleted = sut.delete(snippetToDelete.getUuid());
        List<Recommendation<ISnippet>> search = sut.search(new SnipmatchContext("snippet", FILE));
        sut.close();

        assertThat(wasDeleted, is(true));
        assertThat(snippetFileToDelete.exists(), is(false));
        assertThat(snippetFileToKeep.exists(), is(true));
        assertThat(search.get(0).getProposal(), is((ISnippet) snippetToKeep));
        assertThat(search.size(), is(1));
    }

    @Test
    public void testHasSnippetCallForExistingUUID() throws Exception {
        createAndStoreSnippet(FIRST_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        sut.open();

        assertThat(sut.hasSnippet(FIRST_UUID), is(true));
    }

    @Test
    public void testHasSnippetCallForNonExistingUUID() throws Exception {
        sut.open();

        assertThat(sut.hasSnippet(UUID.randomUUID()), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testCallHasSnippetOnClosedRepo() throws Exception {
        sut.hasSnippet(UUID.randomUUID());
    }

    @Test(expected = IllegalStateException.class)
    public void testCallDeleteOnClosedRepo() throws Exception {
        sut.delete(UUID.randomUUID());
    }

    @Test(expected = IllegalStateException.class)
    public void testCallSearchOnClosedRepo() throws Exception {
        sut.search(new SnipmatchContext(" ", FILE));
    }

    @Test
    public void testRepoIsClosedWhenNumberOfCloseCallsIsEqualsToNumberOfOpenCalls() throws Exception {
        ISnippetRepository thread1 = sut;
        ISnippetRepository thread2 = sut;

        thread1.open();
        assertThat(sut.isOpen(), is(true));

        thread2.open();
        assertThat(sut.isOpen(), is(true));

        thread1.close();
        assertThat(sut.isOpen(), is(true));

        thread2.close();
        assertThat(sut.isOpen(), is(false));
    }

    @Test
    public void testMultipleCallsOfOpenAreLegal() throws Exception {
        sut.open();
        sut.open();
        assertThat(sut.isOpen(), is(true));
    }

    @Test
    public void testMultipleCallsOfCloseAreLegal() throws Exception {
        sut.open();
        assertThat(sut.isOpen(), is(true));
        sut.close();
        assertThat(sut.isOpen(), is(false));
        sut.close();
        assertThat(sut.isOpen(), is(false));
    }

    @Test
    public void testRepoCanBeReopened() throws Exception {
        sut.open();
        sut.close();
        assertThat(sut.isOpen(), is(false));
        sut.open();
        assertThat(sut.isOpen(), is(true));
    }

    @Test
    public void testImportOfNewSnippet() throws Exception {
        sut.open();

        String snippetName = "New Snippet";
        ISnippet snippet = new Snippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", NONE);
        sut.importSnippet(snippet);

        assertThat(getOnlyElement(sut.search(new SnipmatchContext("", NONE))).getProposal(), is(snippet));
    }

    @Test
    public void testImportOfNewSnippetIfSnippetWithSameNameAlreadyExists() throws Exception {
        String snippetName = "New Snippet";
        Snippet originalSnippet = new Snippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", NONE);
        storeSnippet(originalSnippet);
        sut.open();

        Snippet otherSnippet = new Snippet(SECOND_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", NONE);

        sut.importSnippet(otherSnippet);

        assertThat(sut.search(new SnipmatchContext("", NONE)).size(), is(2));
    }

    @Test
    public void testImportSnippetWithModifiedMetaData() throws Exception {
        String snippetName = "New Snippet";
        Snippet originalSnippet1 = new Snippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        storeSnippet(originalSnippet1);
        Snippet originalSnippet = originalSnippet1;

        sut.open();

        Snippet copiedSnippet = Snippet.copy(originalSnippet);
        copiedSnippet.setExtraSearchTerms(Lists.newArrayList("term1", "term2"));

        sut.importSnippet(copiedSnippet);

        assertThat(getOnlyElement(sut.search(new SnipmatchContext("", FILE))).getProposal(),
                is((ISnippet) copiedSnippet));
    }

    @Test
    public void testImportSnippetWithModifiedCode() throws Exception {
        String snippetName = "New Snippet";
        Snippet originalSnippet1 = new Snippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        storeSnippet(originalSnippet1);
        Snippet originalSnippet = originalSnippet1;

        sut.open();

        Snippet copiedSnippet = Snippet.copy(originalSnippet);
        copiedSnippet.setCode("Modified Code");
        copiedSnippet.setUUID(SECOND_UUID);

        sut.importSnippet(copiedSnippet);

        assertThat(sut.search(new SnipmatchContext("", FILE)).size(), is(2));
    }

    @Test
    public void testSearchByName() throws Exception {
        String snippetName = "The snippet";
        ISnippet snippet = createAndStoreSnippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        sut.open();

        assertThat(getOnlyElement(sut.search(new SnipmatchContext("name:" + "s", FILE))).getProposal(), is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("name:" + "sn", FILE))).getProposal(), is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("name:" + "snippet", FILE))).getProposal(),
                is(snippet));
        assertThat(sut.search(new SnipmatchContext("name:" + "a", FILE)).isEmpty(), is(true));
    }

    @Test
    public void testSearchByDescription() throws Exception {
        String snippetName = "New Snippet";
        String snippetDescription = "description";
        ISnippet snippet = createAndStoreSnippet(FIRST_UUID, snippetName, "description", NO_EXTRA_SEARCH_TERMS,
                NO_TAGS, "", FILE);

        sut.open();

        assertThat(getOnlyElement(sut.search(new SnipmatchContext("description:" + snippetDescription, FILE)))
                .getProposal(), is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("description:" + "d", FILE))).getProposal(),
                is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("description:" + "de", FILE))).getProposal(),
                is(snippet));
        assertThat(sut.search(new SnipmatchContext("name:" + snippetDescription, FILE)).isEmpty(), is(true));
    }

    @Test
    public void testSearchByExtraSearchTerm() throws Exception {
        String snippetName = "New Snippet";
        List<String> extraSearchTerms = ImmutableList.of("foo", "bar");
        ISnippet snippet = createAndStoreSnippet(FIRST_UUID, snippetName, "", extraSearchTerms, NO_TAGS, "", FILE);

        sut.open();

        assertThat(getOnlyElement(sut.search(new SnipmatchContext("extra:" + "f", FILE))).getProposal(), is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("extra:" + "foo", FILE))).getProposal(), is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("extra:" + "bar", FILE))).getProposal(), is(snippet));
        assertThat(sut.search(new SnipmatchContext("extra:" + "quz", FILE)).isEmpty(), is(true));
    }

    @Test
    public void testSearchByTag() throws Exception {
        String snippetName = "New Snippet";
        List<String> tags = ImmutableList.of("foo", "bar");
        ISnippet snippet = createAndStoreSnippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, tags, "", FILE);

        sut.open();

        assertThat(sut.search(new SnipmatchContext("tag:" + "f", FILE)).isEmpty(), is(true));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("tag:" + "foo", FILE))).getProposal(), is(snippet));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("tag:" + "bar", FILE))).getProposal(), is(snippet));
        assertThat(sut.search(new SnipmatchContext("tag:" + "quz", FILE)).isEmpty(), is(true));
    }

    @Test
    public void testSearchByLocationConstraint() throws Exception {
        String snippetName = "New Snippet";
        ISnippet snippet = createAndStoreSnippet(FIRST_UUID, snippetName, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "",
                JAVADOC);

        sut.open();

        assertThat(sut.search(new SnipmatchContext("", JAVA)).isEmpty(), is(true));
        assertThat(sut.search(new SnipmatchContext("", JAVA_STATEMENTS)).isEmpty(), is(true));
        assertThat(sut.search(new SnipmatchContext("", JAVA_TYPE_MEMBERS)).isEmpty(), is(true));
        assertThat(getOnlyElement(sut.search(new SnipmatchContext("", JAVADOC))).getProposal(), is(snippet));
    }

    @Test
    public void testPreferNameMatchesOverDescription() throws Exception {
        createAndStoreSnippet(FIRST_UUID, "first", "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        createAndStoreSnippet(SECOND_UUID, "second", "first", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("first", FILE));

        Recommendation<ISnippet> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID)).get();
        Recommendation<ISnippet> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID)).get();
        assertThat(forFirst.getRelevance(), is(greaterThan(forSecond.getRelevance())));
    }

    @Test
    public void testNoPreferenceBetweenDescriptionAndExtraSearchTerms() throws Exception {
        createAndStoreSnippet(FIRST_UUID, "first", "searchword", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        createAndStoreSnippet(SECOND_UUID, "second", "", ImmutableList.of("searchword"), NO_TAGS, "", FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("searchword", FILE));

        Recommendation<ISnippet> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID)).get();
        Recommendation<ISnippet> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID)).get();
        assertThat(forFirst.getRelevance(), is(equalTo(forSecond.getRelevance())));
    }

    @Test
    public void testPreferDescriptionMatchesOverTags() throws Exception {
        createAndStoreSnippet(FIRST_UUID, "addlistener", "add a listener to a Widget", NO_EXTRA_SEARCH_TERMS,
                ImmutableList.of("eclipse", "swt", "ui"), "", FILE);
        createAndStoreSnippet(SECOND_UUID, "Browser", "new Browser", NO_EXTRA_SEARCH_TERMS,
                ImmutableList.of("eclipse", "swt", "widget"), "", FILE);
        createAndStoreSnippet(THIRD_UUID, "Third", "something", NO_EXTRA_SEARCH_TERMS,
                ImmutableList.of("eclipse", "swt", "widget"), "", FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("widget", FILE));

        Recommendation<ISnippet> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID)).get();
        Recommendation<ISnippet> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID)).get();
        Recommendation<ISnippet> forThird = Iterables.tryFind(result, new UuidPredicate(THIRD_UUID)).get();
        assertThat(forFirst.getRelevance(), is(greaterThan(forSecond.getRelevance())));
        assertThat(forFirst.getRelevance(), is(greaterThan(forThird.getRelevance())));
    }

    @Test
    public void testRelevanceDoesntExceedOne() throws Exception {
        createAndStoreSnippet(FIRST_UUID, "searchword", "searchword", ImmutableList.of("searchword"),
                ImmutableList.of("searchword"), "", FILE);
        createAndStoreSnippet(SECOND_UUID, "searchword", "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("searchword", FILE));
        Recommendation<ISnippet> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID)).get();
        Recommendation<ISnippet> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID)).get();
        assertThat(forFirst.getRelevance(), is(greaterThan(forSecond.getRelevance())));
    }

    @Test
    public void testEmptyQueryReturnsAllSnippetsOnOneParameterSearch() throws Exception {
        createAndStoreSnippet(FIRST_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        createAndStoreSnippet(SECOND_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("", FILE));
        Optional<Recommendation<ISnippet>> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID));
        Optional<Recommendation<ISnippet>> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID));

        assertThat(forFirst.isPresent(), is(true));
        assertThat(forSecond.isPresent(), is(true));
    }

    @Test
    public void testEmptyQueryReturnsAllSnippetsOnTwoParametersSearch() throws Exception {
        createAndStoreSnippet(FIRST_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);
        createAndStoreSnippet(SECOND_UUID, SNIPPET_NAME, "", NO_EXTRA_SEARCH_TERMS, NO_TAGS, "", FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("", FILE), 2);
        Optional<Recommendation<ISnippet>> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID));
        Optional<Recommendation<ISnippet>> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID));

        assertThat(forFirst.isPresent(), is(false));
        assertThat(forSecond.isPresent(), is(false));
    }

    @Test
    public void testNumberOfTagsDoesntAffectRelevance() throws Exception {
        createAndStoreSnippet(FIRST_UUID, "first", "", NO_EXTRA_SEARCH_TERMS, ImmutableList.of("tag1"), "", FILE);
        createAndStoreSnippet(SECOND_UUID, "second", "", NO_EXTRA_SEARCH_TERMS, ImmutableList.of("tag1", "tag2"), "",
                FILE);

        sut.open();
        List<Recommendation<ISnippet>> result = sut.search(new SnipmatchContext("tag:tag1", FILE));

        Recommendation<ISnippet> forFirst = Iterables.tryFind(result, new UuidPredicate(FIRST_UUID)).get();
        Recommendation<ISnippet> forSecond = Iterables.tryFind(result, new UuidPredicate(SECOND_UUID)).get();
        assertThat(forFirst.getRelevance(), is(closeTo(forSecond.getRelevance(), 0.01)));
    }

    private ISnippet createAndStoreSnippet(UUID uuid, String name, String description, List<String> extraSearchTerms,
            List<String> tags, String code, LocationConstraint locationConstraint) throws Exception {
        Snippet snippet = new Snippet(uuid, name, description, extraSearchTerms, tags, code, locationConstraint);
        storeSnippet(snippet);
        return snippet;
    }

    private File storeSnippet(Snippet snippet) throws Exception {
        File jsonFile = new File(snippetsDir, snippet.getUuid() + DOT_JSON);
        GsonUtil.serialize(snippet, jsonFile);
        return jsonFile;
    }

    private static final class UuidPredicate implements Predicate<Recommendation<? extends ISnippet>> {

        private final UUID uuid;

        public UuidPredicate(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public boolean apply(Recommendation<? extends ISnippet> snippet) {
            return uuid.equals(snippet.getProposal().getUuid());
        }
    }
}
