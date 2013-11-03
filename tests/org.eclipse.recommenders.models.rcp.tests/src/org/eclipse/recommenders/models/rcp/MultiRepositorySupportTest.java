/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.models.rcp;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.utils.Checks.cast;
import static org.eclipse.recommenders.utils.Constants.EXT_ZIP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.recommenders.internal.models.rcp.EclipseModelIndex;
import org.eclipse.recommenders.internal.models.rcp.ModelsRcpPreferences;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.Urls;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

public class MultiRepositorySupportTest {

    public static final ProjectCoordinate PC1 = new ProjectCoordinate("org.example", "one", "1.0.0");
    public static final ProjectCoordinate PC2 = new ProjectCoordinate("org.example", "two", "2.0.0");
    public static final ProjectCoordinate PC3 = new ProjectCoordinate("org.example", "three", "3.0.0");
    public static final ProjectCoordinate PC4 = new ProjectCoordinate("org.example", "four", "4.0.0");

    public static final ModelCoordinate MC1 = new ModelCoordinate("org.example", "one", "call", EXT_ZIP, "1.0.0");
    public static final ModelCoordinate MC2 = new ModelCoordinate("org.example", "two", "call", EXT_ZIP, "2.0.0");
    public static final ModelCoordinate MC3 = new ModelCoordinate("org.example", "three", "call", EXT_ZIP, "3.0.0");
    public static final ModelCoordinate MC4 = new ModelCoordinate("org.example", "four", "call", EXT_ZIP, "4.0.0");

    public static final Map<ProjectCoordinate, ModelCoordinate> CALL_MODEL_MAPPING = ImmutableMap.of(PC1, MC1, PC2, MC2, PC3, MC3, PC4, MC4);

    public static final Pair<String, ModelCoordinate[]> REPO_1 = Pair.newPair("http://www.example.com/repo1",
            new ModelCoordinate[] { MC1 });
    public static final Pair<String, ModelCoordinate[]> REPO_2 = Pair.newPair("http://www.example.org/repo2",
            new ModelCoordinate[] { MC2 });
    public static final Pair<String, ModelCoordinate[]> REPO_3 = Pair.newPair("http://www.example.com/repo3",
            new ModelCoordinate[] { MC1, MC3 });

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private IModelIndex createMockedModelIndex(final ModelCoordinate... models) {
        IModelIndex mock = mock(IModelIndex.class);
        when(mock.getKnownModels("call")).thenReturn(ImmutableSet.copyOf(models));
        when(mock.suggest(any(ProjectCoordinate.class), any(String.class))).thenAnswer(
                new Answer<Optional<ModelCoordinate>>() {

                    @Override
                    public Optional<ModelCoordinate> answer(InvocationOnMock invocation) throws Throwable {
                        Object[] arguments = invocation.getArguments();
                        String classifier = cast(arguments[1]);
                        ProjectCoordinate pc = cast(arguments[0]);
                        if (classifier != "call") {
                            return absent();
                        }
                        ModelCoordinate modelCoordinate = CALL_MODEL_MAPPING.get(pc);
                        if (modelCoordinate != null) {
                            if (Lists.newArrayList(models).contains(modelCoordinate)) {
                                return of(modelCoordinate);
                            }
                        }
                        return absent();
                    }
                });
        return mock;
    }

    private EclipseModelIndex createSUT(Pair<String, ModelCoordinate[]>... configuration) throws IOException {
        File basedir = temporaryFolder.newFolder();
        ModelsRcpPreferences prefs = new ModelsRcpPreferences();

        final Map<String, IModelIndex> map = Maps.newHashMap();
        String[] remotes = new String[configuration.length];
        for (int i = 0; i < configuration.length; i++) {
            Pair<String, ModelCoordinate[]> pair = configuration[i];
            map.put(Urls.mangle(pair.getFirst()), createMockedModelIndex(pair.getSecond()));
            remotes[i] = configuration[i].getFirst();
        }

        prefs.remotes = remotes;
        IModelRepository repository = mock(IModelRepository.class);
        EventBus bus = mock(EventBus.class);
        EclipseModelIndex sut = spy(new EclipseModelIndex(basedir, prefs, repository, bus));
        when(sut.createModelIndex(any(File.class))).thenAnswer(new Answer<IModelIndex>() {

            @Override
            public IModelIndex answer(InvocationOnMock invocation) throws Throwable {
                File file = cast(invocation.getArguments()[0]);
                String mangledUrl = file.getName();
                return map.get(mangledUrl);
            }
        });
        sut.open();
        return sut;
    }

    @Test
    public void testSingleRepositoryContainsSearchedModel() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_1);
        ModelCoordinate mc = sut.suggest(PC1, "call").orNull();
        assertEquals(REPO_1.getFirst(), mc.getHint(ModelCoordinate.HINT_REPOSITORY_URL).orNull());
    }

    @Test
    public void testSingleRepositoryDoesNotContainSearchedModel() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_1);
        Optional<ModelCoordinate> omc = sut.suggest(PC2, "call");
        assertFalse(omc.isPresent());
    }

    @Test
    public void testFirstRepositoryContainsSearchedModel() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_2, REPO_1);
        ModelCoordinate mc = sut.suggest(PC2, "call").orNull();
        assertEquals(REPO_2.getFirst(), mc.getHint(ModelCoordinate.HINT_REPOSITORY_URL).orNull());
    }

    @Test
    public void testSecondRepositoryContainsSearchedModel() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_1, REPO_2);
        ModelCoordinate mc = sut.suggest(PC2, "call").orNull();
        assertEquals(REPO_2.getFirst(), mc.getHint(ModelCoordinate.HINT_REPOSITORY_URL).orNull());
    }

    @Test
    public void testFirstAndSecondRepositoryContainsSearchedModel1() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_1, REPO_3);
        ModelCoordinate mc = sut.suggest(PC1, "call").orNull();
        assertEquals(REPO_1.getFirst(), mc.getHint(ModelCoordinate.HINT_REPOSITORY_URL).orNull());
    }

    @Test
    public void testFirstAndSecondRepositoryContainsSearchedModel2() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_3, REPO_1);
        ModelCoordinate mc = sut.suggest(PC1, "call").orNull();
        assertEquals(REPO_3.getFirst(), mc.getHint(ModelCoordinate.HINT_REPOSITORY_URL).orNull());
    }

    @Test
    public void testAllRepositoriesDoNotContainSearchedModel() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_1, REPO_2, REPO_3);
        Optional<ModelCoordinate> omc = sut.suggest(PC4, "call");
        assertFalse(omc.isPresent());
    }

    @Test
    public void testThirdRepositoryContainsSearchedModel() throws IOException {
        EclipseModelIndex sut = createSUT(REPO_1, REPO_3, REPO_2);
        ModelCoordinate mc = sut.suggest(PC2, "call").orNull();
        assertEquals(REPO_2.getFirst(), mc.getHint(ModelCoordinate.HINT_REPOSITORY_URL).orNull());
    }
}
