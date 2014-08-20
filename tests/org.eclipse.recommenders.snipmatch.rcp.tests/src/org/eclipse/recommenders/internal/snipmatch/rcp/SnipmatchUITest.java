package org.eclipse.recommenders.internal.snipmatch.rcp;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.internal.snipmatch.rcp.editors.SnippetMetadataPage;
import org.eclipse.recommenders.rcp.model.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.rcp.model.SnipmatchRcpModelFactory;
import org.eclipse.recommenders.rcp.model.SnippetRepositoryConfigurations;
import org.eclipse.recommenders.snipmatch.GitSnippetRepository;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.snipmatch.model.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SnipmatchUITest {

    private static SWTBotView snippetsView;
    private static SWTWorkbenchBot bot;

    private static SnippetRepositoryConfiguration configuration1;
    private static SnippetRepositoryConfiguration configuration2;

    private static GitSnippetRepository mockedRepository1;
    private static GitSnippetRepository mockedRepository2;

    private static Map<SnippetRepositoryConfiguration, Integer> numberOfSnippetsGroupedByConfig;

    private static final Snippet snippet1 = createSnippet("Snippet1", "Test Snippet1 Description");
    private static final Snippet snippet2 = createSnippet("Snippet2", "Test Snippet2 Description");
    private static final Snippet snippet3 = createSnippet("Snippet3", "Test Snippet3 Description");
    private static final Snippet snippet4 = createSnippet("Template4", "Test Snippet4 Description");
    private static final Snippet snippet5 = createSnippet("Snippet5", "Test Snippet5 Description");
    private static final Snippet snippet6 = createSnippet("Template6", "Test Snippet6 Description");

    private static SWTWorkbenchBot prepareTest() {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();

        configureConfigurations();
        configureRepositories();

        bot.viewByTitle("Welcome").close();
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell showViewShell = bot.shell("Show View").activate();
        showViewShell.bot().tree().expandNode("Code Recommenders").getNode("Snippets").doubleClick();

        SWTBotView snippetsView = bot.activeView();

        assertThat(snippetsView.getTitle(), is(equalTo("Snippets")));

        snippetsView.toolbarButton(Messages.TOOLBAR_TOOLTIP_EXPAND_ALL).click();

        return bot;
    }

    private static void configureConfigurations() {
        SnippetRepositoryConfigurations configs = InjectionService.getInstance().getInjector()
                .getInstance(SnippetRepositoryConfigurations.class);

        configs.getRepos().clear();

        configuration1 = createConfiguration(0, "Repo1", "http://www.example.org/repo");
        configuration2 = createConfiguration(1, "Repo2", "http://www.example.com/repo");

        configs.getRepos().add(configuration1);
        configs.getRepos().add(configuration2);
    }

    private static void configureRepositories() {
        Repositories repos = InjectionService.getInstance().getInjector().getInstance(Repositories.class);

        numberOfSnippetsGroupedByConfig = Maps.newHashMap();

        mockedRepository1 = createRepositoryMock(configuration1, snippet1, snippet2, snippet3, snippet4);
        mockedRepository2 = createRepositoryMock(configuration2, snippet5, snippet6);

        repos.getRepositories().clear();

        repos.getRepositories().add(mockedRepository1);
        repos.getRepositories().add(mockedRepository2);
    }

    private static Snippet createSnippet(String name, String description) {
        return new Snippet(UUID.randomUUID(), name, description, Lists.<String>newArrayList(),
                Lists.<String>newArrayList(), "");
    }

    private static GitSnippetRepository createRepositoryMock(SnippetRepositoryConfiguration configuration,
            ISnippet... snippets) {

        numberOfSnippetsGroupedByConfig.put(configuration, snippets.length);

        GitSnippetRepository mockedRepository = mock(GitSnippetRepository.class);
        when(mockedRepository.getId()).thenReturn(configuration.getId());
        when(mockedRepository.isImportSupported()).thenReturn(true);
        when(mockedRepository.isDeleteSupported()).thenReturn(true);

        List<Recommendation<ISnippet>> proposals = Lists.newArrayList();
        when(mockedRepository.search("")).thenReturn(proposals);
        for (ISnippet snippet : snippets) {
            final Recommendation<ISnippet> recommendation = Recommendation.newRecommendation(snippet, 0.);
            proposals.add(recommendation);
            when(mockedRepository.search(snippet.getName())).thenReturn(Lists.newArrayList(recommendation));
            when(mockedRepository.hasSnippet(snippet.getUuid())).thenReturn(true);
        }

        return mockedRepository;
    }

    private static EclipseGitSnippetRepositoryConfiguration createConfiguration(int id, String name, String url) {
        EclipseGitSnippetRepositoryConfiguration configuration = SnipmatchRcpModelFactory.eINSTANCE
                .createEclipseGitSnippetRepositoryConfiguration();
        configuration.setId(id);
        configuration.setName(name);
        configuration.setDescription("Description");
        configuration.setUrl(url);
        configuration.setPushUrl("http://example.com/");
        configuration.setPushBranchPrefix("refs/for");
        return configuration;
    }

    public static String createStringForRepository(SnippetRepositoryConfiguration config) {
        return config.getName() + " "
                + format(Messages.TABLE_CELL_SUFFIX_SNIPPETS, numberOfSnippetsGroupedByConfig.get(config));
    }

    public static String createStringForSnippet(ISnippet snippet) {
        return SnippetProposal.createDisplayString(snippet);
    }

    private void resetTestEnvironment() {
        configureRepositories();

        snippetsView.bot().text().setText("");
        snippetsView.toolbarButton(Messages.TOOLBAR_TOOLTIP_COLLAPSE_ALL).click();
    }

    private SWTBotTree findSnippetsTree() {
        return snippetsView.bot().treeWithId(SnippetsView.TREE);
    }

    private SWTBotText findSearchField() {
        return snippetsView.bot().textWithId(SnippetsView.SEARCH_FIELD);
    }

    @BeforeClass
    public static void setup() {
        System.out.println("Start SnipmatchUITest: " + System.currentTimeMillis());
        bot = prepareTest();
        snippetsView = bot.activeView();
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("Finish SnipmatchUITest: " + System.currentTimeMillis());
    }

    @Test
    public void testAllSnippetsAreDisplayed() throws Exception {
        resetTestEnvironment();

        final SWTBotTree tree = findSnippetsTree();

        assertThat(tree.hasItems(), is(true));
        assertThat(tree.rowCount(), is(2));

        Pair<SnippetRepositoryConfiguration, List<Snippet>> node1 = Pair.newPair(configuration1,
                Lists.newArrayList(snippet1, snippet2, snippet3, snippet4));
        Pair<SnippetRepositoryConfiguration, List<Snippet>> node2 = Pair.newPair(configuration2,
                Lists.newArrayList(snippet5, snippet6));

        assertThat(containsElementsInCorrectOrder(tree, node1, node2), is(true));
    }

    private static boolean containsElementsInCorrectOrder(SWTBotTree tree,
            Pair<SnippetRepositoryConfiguration, List<Snippet>>... expectedValues) {
        if (tree.getAllItems().length != expectedValues.length) {
            return false;
        }

        for (int i = 0; i < expectedValues.length; i++) {
            Pair<SnippetRepositoryConfiguration, List<Snippet>> expectedValue = expectedValues[i];

            SWTBotTreeItem actualNode = tree.getAllItems()[i];
            SnippetRepositoryConfiguration expectedNode = expectedValue.getFirst();

            if (!actualNode.getText().equals(createStringForRepository(expectedNode))) {
                return false;
            }

            if (actualNode.getItems().length != expectedValue.getSecond().size()) {
                return false;
            }

            for (int j = 0; j < actualNode.getItems().length; j++) {
                SWTBotTreeItem actualLeaf = actualNode.getItems()[j];
                Snippet expectedLeaf = expectedValue.getSecond().get(j);

                if (!actualLeaf.getText().equals(createStringForSnippet(expectedLeaf))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    public void testSearchFunctionality() throws Exception {
        resetTestEnvironment();

        findSearchField().setText("Snippet1");

        final SWTBotTree tree = findSnippetsTree();

        Pair<SnippetRepositoryConfiguration, List<Snippet>> node1 = Pair.newPair(configuration1,
                Lists.newArrayList(snippet1));
        Pair<SnippetRepositoryConfiguration, List<Snippet>> node2 = Pair.newPair(configuration2,
                Lists.<Snippet>newArrayList());

        assertThat(containsElementsInCorrectOrder(tree, node1, node2), is(true));
    }

    @Test
    public void testAllSnippetsAreShownAfterSearchStringIsRemoved() throws Exception {
        resetTestEnvironment();

        findSearchField().setText("Snippet1");

        final SWTBotTree tree = findSnippetsTree();

        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));
        assertThat(treeItem.rowCount(), is(1));

        SWTBotTreeItem treeItem2 = tree.getTreeItem(createStringForRepository(configuration2));
        assertThat(treeItem2.rowCount(), is(0));

        findSearchField().setText("");

        Pair<SnippetRepositoryConfiguration, List<Snippet>> node1 = Pair.newPair(configuration1,
                Lists.newArrayList(snippet1, snippet2, snippet3, snippet4));
        Pair<SnippetRepositoryConfiguration, List<Snippet>> node2 = Pair.newPair(configuration2,
                Lists.newArrayList(snippet5, snippet6));

        assertThat(containsElementsInCorrectOrder(tree, node1, node2), is(true));
    }

    @Test
    public void testExpandCollapseTest() throws Exception {
        resetTestEnvironment();

        snippetsView.toolbarButton(Messages.TOOLBAR_TOOLTIP_COLLAPSE_ALL).click();

        for (SWTBotTreeItem item : findSnippetsTree().getAllItems()) {
            assertThat(item.isExpanded(), is(false));
        }

        snippetsView.toolbarButton(Messages.TOOLBAR_TOOLTIP_EXPAND_ALL).click();

        for (SWTBotTreeItem item : findSnippetsTree().getAllItems()) {
            assertThat(item.isExpanded(), is(true));
        }
    }

    @Test
    public void testEditSnippetUsingTooltipMenu() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.doubleClick();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText txtSnippetName = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        txtSnippetName.setText("new snippet name");
        assertThat(snippetEditor.isDirty(), is(true));

        bot.menu("File").menu("Save").click();

        verify(mockedRepository1, times(1)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testEditSnippetUsingContextMenu() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_SNIPPET).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText txtSnippetName = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        txtSnippetName.setText("new snippet name");
        assertThat(snippetEditor.isDirty(), is(true));

        bot.menu("File").menu("Save").click();

        verify(mockedRepository1, times(1)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testDeleteSnippet() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).click();

        bot.waitUntil(Conditions.shellIsActive(Messages.CONFIRM_DIALOG_DELETE_SNIPPET_TITLE));
        bot.button("OK").click();
        verify(mockedRepository1, times(1)).delete(Matchers.<UUID>any());
    }

    @Test
    public void testDeleteSnippetCancelOperation() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).click();

        bot.waitUntil(Conditions.shellIsActive(Messages.CONFIRM_DIALOG_DELETE_SNIPPET_TITLE));
        bot.button("Cancel").click();
        verify(mockedRepository1, never()).delete(Matchers.<UUID>any());
    }

    @Test
    public void testCreateNewSnippetForSpecificRepoUsingContextMenu() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));
        treeItem.contextMenu(
                MessageFormat.format(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET_TO_REPOSITORY,
                        configuration1.getName())).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();

        SWTBotText snippetNameTextField = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        snippetNameTextField.setText("New Snippet");
        assertThat(snippetEditor.isDirty(), is(true));

        bot.menu("File").menu("Save").click();

        verify(mockedRepository1, times(1)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testCreateNewSnippetWithEmptyNameFailed() throws Exception {
        resetTestEnvironment();

        snippetsView.toolbarButton(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText snippetNameTextField = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        snippetNameTextField.setText("foo");
        snippetNameTextField.setText("");
        assertThat(snippetEditor.isDirty(), is(true));

        bot.menu("File").menu("Save").click();

        bot.waitUntil(Conditions.shellIsActive(Messages.DIALOG_TITLE_INAVLID_SNIPPET_NAME));
        bot.button("OK").click();

        verify(mockedRepository1, never()).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testCreateNewSnippetSelectingRepositoryToStore() throws Exception {
        resetTestEnvironment();

        snippetsView.toolbarButton(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText snippetNameTextField = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        snippetNameTextField.setText("foo");
        assertThat(snippetEditor.isDirty(), is(true));

        bot.menu("File").menu("Save").click();

        bot.waitUntil(Conditions.shellIsActive(Messages.SELECT_REPOSITORY_DIALOG_TITLE));
        bot.table().select(configuration2.getName());
        bot.button("OK").click();

        verify(mockedRepository1, never()).importSnippet(Matchers.<ISnippet>any());
        verify(mockedRepository2, times(1)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testContextMenuItemStatusWhenOneSnippetSelected() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();

        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1)).getItems()[0];

        assertThat(
                treeItem.contextMenu(
                        format(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET_TO_REPOSITORY, configuration1.getName()))
                        .isEnabled(), is(true));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_SNIPPET).isEnabled(), is(true));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).isEnabled(), is(true));

        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_ADD_REPOSITORY).isEnabled(), is(true));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_REPOSITORY).isEnabled(), is(false));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_REPOSITORY).isEnabled(), is(false));
    }

    @Test
    public void testContextMenuItemStatusWhenSeletedTwoSnippetFromDifferentRepos() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem1 = tree.getTreeItem(createStringForRepository(configuration1)).getItems()[0];
        SWTBotTreeItem treeItem2 = tree.getTreeItem(createStringForRepository(configuration2)).getItems()[0];

        tree.select(treeItem1, treeItem2);

        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET).isEnabled(), is(true));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_SNIPPET).isEnabled(), is(true));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).isEnabled(), is(true));

        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_ADD_REPOSITORY).isEnabled(), is(true));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_REPOSITORY).isEnabled(), is(false));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_REPOSITORY).isEnabled(), is(false));
    }

    @Test
    public void testContextMenuItemStatusWhenSeletedOneRepository() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1));

        assertThat(
                treeItem.contextMenu(
                        format(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET_TO_REPOSITORY, configuration1.getName()))
                        .isEnabled(), is(true));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_SNIPPET).isEnabled(), is(false));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).isEnabled(), is(false));

        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_ADD_REPOSITORY).isEnabled(), is(true));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_REPOSITORY).isEnabled(), is(true));
        assertThat(treeItem.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_REPOSITORY).isEnabled(), is(true));
    }

    @Test
    public void testContextMenuItemStatusWhenSelectRepoAndSnippet() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem1 = tree.getTreeItem(createStringForRepository(configuration1));
        SWTBotTreeItem treeItem2 = tree.getTreeItem(createStringForRepository(configuration1)).getItems()[0];
        tree.select(treeItem1, treeItem2);

        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET).isEnabled(), is(true));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_SNIPPET).isEnabled(), is(false));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).isEnabled(), is(false));

        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_ADD_REPOSITORY).isEnabled(), is(true));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_REPOSITORY).isEnabled(), is(false));
        assertThat(tree.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_REPOSITORY).isEnabled(), is(false));
    }

}
