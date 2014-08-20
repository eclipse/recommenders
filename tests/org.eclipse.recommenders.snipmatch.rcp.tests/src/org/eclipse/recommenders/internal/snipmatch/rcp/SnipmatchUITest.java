package org.eclipse.recommenders.internal.snipmatch.rcp;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.internal.snipmatch.rcp.editors.SnippetMetadataPage;
import org.eclipse.recommenders.rcp.model.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.rcp.model.SnipmatchRcpModelFactory;
import org.eclipse.recommenders.rcp.model.SnippetRepositoryConfigurations;
import org.eclipse.recommenders.snipmatch.GitSnippetRepository;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.Snippet;
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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import com.google.common.collect.Lists;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SnipmatchUITest {

    private static SWTBotView snippetsView;
    private static SWTWorkbenchBot bot;

    private static EclipseGitSnippetRepositoryConfiguration configuration1;
    private static EclipseGitSnippetRepositoryConfiguration configuration2;

    private static GitSnippetRepository mockedRepository1;
    private static GitSnippetRepository mockedRepository2;

    private int importCallCount = 0;

    private static SWTWorkbenchBot prepareTest() {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();

        configureRepository();

        bot.viewByTitle("Welcome").close();
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell showViewShell = bot.shell("Show View").activate();
        showViewShell.bot().tree().expandNode("Code Recommenders").getNode("Snippets").doubleClick();

        SWTBotView snippetsView = bot.activeView();

        assertThat(snippetsView.getTitle(), is(equalTo("Snippets")));

        snippetsView.toolbarButton(Messages.TOOLBAR_TOOLTIP_EXPAND_ALL).click();

        return bot;
    }

    private static void configureRepository() {
        Repositories repos = InjectionService.getInstance().getInjector().getInstance(Repositories.class);
        SnippetRepositoryConfigurations configs = InjectionService.getInstance().getInjector()
                .getInstance(SnippetRepositoryConfigurations.class);

        configuration1 = createConfiguration(0, "Repo1", "http://www.example.org/repo");
        configuration2 = createConfiguration(1, "Repo2", "http://www.example.com/repo");

        Snippet snippet1 = createSnippet("Snippet1", "Test Snippet1 Description");
        Snippet snippet2 = createSnippet("Snippet2", "Test Snippet2 Description");
        Snippet snippet3 = createSnippet("Snippet3", "Test Snippet3 Description");
        Snippet snippet4 = createSnippet("Template4", "Test Snippet4 Description");

        Snippet snippet5 = createSnippet("Snippet5", "Test Snippet5 Description");
        Snippet snippet6 = createSnippet("Template6", "Test Snippet6 Description");

        mockedRepository1 = createRepositoryMock(configuration1, snippet1, snippet2, snippet3, snippet4);
        mockedRepository2 = createRepositoryMock(configuration2, snippet5, snippet6);

        repos.getRepositories().clear();
        configs.getRepos().clear();

        repos.getRepositories().add(mockedRepository1);
        repos.getRepositories().add(mockedRepository2);
        configs.getRepos().add(configuration1);
        configs.getRepos().add(configuration2);
    }

    private static Snippet createSnippet(String name, String description) {
        return new Snippet(UUID.randomUUID(), name, description, Lists.<String>newArrayList(),
                Lists.<String>newArrayList(), "");
    }

    private static GitSnippetRepository createRepositoryMock(EclipseGitSnippetRepositoryConfiguration configuration,
            ISnippet... snippets) {
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

    public static String createStringForRepository(String name, int count) {
        return name + " " + format(Messages.TABLE_CELL_SUFFIX_SNIPPETS, count);
    }

    private void resetTestEnvironment() {
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
        bot = prepareTest();
        snippetsView = bot.activeView();
    }

    @Test
    public void testAllSnippetsAreDisplayed() throws Exception {
        resetTestEnvironment();

        final SWTBotTree tree = findSnippetsTree();

        assertThat(tree.hasItems(), is(true));
        assertThat(tree.rowCount(), is(2));

        final SWTBotTreeItem treeItemRepo1 = tree.getTreeItem(createStringForRepository(configuration1.getName(), 4));
        assertThat(treeItemRepo1.rowCount(), is(4));

        final SWTBotTreeItem treeItemRepo2 = tree.getTreeItem(createStringForRepository(configuration2.getName(), 2));
        assertThat(treeItemRepo2.rowCount(), is(2));
    }

    @Test
    public void testSearchFunctionality() throws Exception {
        resetTestEnvironment();

        findSearchField().setText("Snippet1");

        final SWTBotTree tree = findSnippetsTree();

        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1.getName(), 4));
        assertThat(treeItem.rowCount(), is(1));

        SWTBotTreeItem treeItem2 = tree.getTreeItem(createStringForRepository(configuration2.getName(), 2));
        assertThat(treeItem2.rowCount(), is(0));
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
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1.getName(), 4));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.doubleClick();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText txtSnippetName = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        txtSnippetName.setText("new snippet name");
        assertThat(snippetEditor.isDirty(), is(true));

        snippetEditor.saveAndClose();
        verify(mockedRepository1, times(++importCallCount)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testEditSnippetUsingContextMenu() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1.getName(), 4));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_EDIT_SNIPPET).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText txtSnippetName = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        txtSnippetName.setText("new snippet name");
        assertThat(snippetEditor.isDirty(), is(true));

        snippetEditor.saveAndClose();
        int count = importCallCount + 3;
        // I will have a look at this to find out why import is called three times.
        verify(mockedRepository1, times(count)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    public void testDeleteSnippet() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1.getName(), 4));
        SWTBotTreeItem selectedSnippet = treeItem.getItems()[0];
        selectedSnippet.contextMenu(Messages.SNIPPETS_VIEW_MENUITEM_REMOVE_SNIPPET).click();

        bot.waitUntil(Conditions.shellIsActive(Messages.CONFIRM_DIALOG_DELETE_SNIPPET_TITLE));
        bot.button("OK").click();
        verify(mockedRepository1, times(++importCallCount)).delete(Matchers.<UUID>any());
    }

    @Test
    public void testCreateNewSnippetForSpecificRepoUsingContextMenu() throws Exception {
        resetTestEnvironment();

        SWTBotTree tree = findSnippetsTree();
        SWTBotTreeItem treeItem = tree.getTreeItem(createStringForRepository(configuration1.getName(), 4));
        treeItem.contextMenu(
                MessageFormat.format(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET_TO_REPOSITORY,
                        configuration1.getName())).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();

        SWTBotText snippetNameTextField = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        snippetNameTextField.setText("New Snippet");
        assertThat(snippetEditor.isDirty(), is(true));

        snippetEditor.saveAndClose();

        final int count = importCallCount + 2;
        // I will have a look at this to find out why import is called two times.
        verify(mockedRepository1, times(count)).importSnippet(Matchers.<ISnippet>any());
    }

    @Test
    @Ignore
    public void testCreateNewSnippet() throws Exception {
        resetTestEnvironment();

        snippetsView.toolbarButton(Messages.SNIPPETS_VIEW_MENUITEM_ADD_SNIPPET).click();

        final SWTBotEditor snippetEditor = bot.activeEditor();
        SWTBotText snippetNameTextField = snippetEditor.bot().textWithId(SnippetMetadataPage.TEXT_SNIPPETNAME);
        snippetNameTextField.setText("");

        assertThat(snippetEditor.isDirty(), is(true));
        snippetEditor.saveAndClose();
        // Test blocks here.

        bot.waitUntil(Conditions.shellIsActive(Messages.DIALOG_MESSAGE_INVALID_SNIPPET_NAME));
        bot.button("OK").click();
        verify(mockedRepository1, times(importCallCount)).importSnippet(Matchers.<ISnippet>any());
    }

}
