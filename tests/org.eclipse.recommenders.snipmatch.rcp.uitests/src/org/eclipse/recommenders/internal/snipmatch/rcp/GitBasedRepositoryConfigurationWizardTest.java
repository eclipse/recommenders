package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.internal.snipmatch.rcp.GitBasedRepositoryConfigurationWizard.*;
import static org.eclipse.recommenders.utils.Checks.cast;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collections;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.rcp.model.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.rcp.model.impl.EclipseGitSnippetRepositoryConfigurationImpl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

public class GitBasedRepositoryConfigurationWizardTest {

    @Test
    public void testDefaultPushBranchPrefix() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testGitPushBranchPrefix() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");
        setGitPushBranchPrefixCombo(bot);

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testGerritPushBranchPrefix() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");
        setGerritPushBranchPrefixCombo(bot);

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/for")));
    }

    @Test
    public void testCustomPushBranchPrefix() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");
        setCustomPushBranchPrefixCombo(bot);

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(true));

        setPushBranchPrefixText(bot, "custom");

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("custom")));
    }

    @Test
    public void testInvalidCustomPushBranchPrefix() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");
        setCustomPushBranchPrefixCombo(bot);

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(true));

        setPushBranchPrefixText(bot, "with some spaces");

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        clickCancelButton(bot);
    }

    @Test
    public void testEmptyName() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        clickCancelButton(bot);
    }

    @Test
    public void testFetchUrl() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "");
        setPushUrl(bot, "http://push/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "udp://fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "git+ssh://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("git+ssh://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testPushUrl() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "udp://push/");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "git+ssh://push/");
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("git+ssh://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testNewRepositoryUsingExistingFetchUriAsNewPushUri() {
        ISnippetRepository snippetRepository = Mockito.mock(ISnippetRepository.class);
        Mockito.when(snippetRepository.getRepositoryLocation()).thenReturn("http://push/");

        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.singleton(snippetRepository));

        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testNewRepositoryUsingExistingFetchUriAsNewFetchUri() {
        ISnippetRepository snippetRepository = Mockito.mock(ISnippetRepository.class);
        Mockito.when(snippetRepository.getRepositoryLocation()).thenReturn("http://fetch/");

        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.singleton(snippetRepository));

        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");
        setPushUrl(bot, "http://push/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        clickCancelButton(bot);
    }

    @Test
    public void testEditRepository() {
        EclipseGitSnippetRepositoryConfiguration configurationToEdit = Mockito
                .mock(EclipseGitSnippetRepositoryConfiguration.class);
        Mockito.when(configurationToEdit.getName()).thenReturn("name");
        Mockito.when(configurationToEdit.getUrl()).thenReturn("http://old-fetch/");
        Mockito.when(configurationToEdit.getPushUrl()).thenReturn("http://push/");
        Mockito.when(configurationToEdit.getPushBranchPrefix()).thenReturn("refs/heads");

        ISnippetRepository snippetRepositoryToEdit = Mockito.mock(ISnippetRepository.class);
        Mockito.when(snippetRepositoryToEdit.getRepositoryLocation()).thenReturn("http://old-fetch/");

        ISnippetRepository anotherSnippetRepository = Mockito.mock(ISnippetRepository.class);
        Mockito.when(anotherSnippetRepository.getRepositoryLocation()).thenReturn("http://another-fetch/");

        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories())
                .thenReturn(Sets.newHashSet(snippetRepositoryToEdit, anotherSnippetRepository));

        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        sut.setConfiguration(configurationToEdit);
        SWTBot bot = createBot(sut);

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(true));

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        setFetchUrl(bot, "http://another-fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "http://old-fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "http://new-fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://new-fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    private void setName(SWTBot bot, String text) {
        bot.textWithLabel(Messages.WIZARD_GIT_REPOSITORY_LABEL_NAME).setText(text);
    }

    private void setFetchUrl(SWTBot bot, String text) {
        bot.textWithLabel(Messages.WIZARD_GIT_REPOSITORY_LABEL_FETCH_URL).setText(text);
    }

    private void setPushUrl(SWTBot bot, String text) {
        bot.textWithLabel(Messages.WIZARD_GIT_REPOSITORY_LABEL_PUSH_URL).setText(text);
    }

    private void setPushBranchPrefixText(SWTBot bot, String text) {
        bot.textWithId(PUSH_BRANCH_PREFIX_TEXT_KEY, PUSH_BRANCH_PREFIX_TEXT_VALUE).setText(text);
    }

    private void setGitPushBranchPrefixCombo(SWTBot bot) {
        bot.comboBoxWithLabel(Messages.WIZARD_GIT_REPOSITORY_LABEL_PUSH_BRANCH_PREFIX)
                .setSelection(Messages.WIZARD_GIT_REPOSITORY_OPTION_GIT_PUSH_BRANCH_PREFIX);
    }

    private void setGerritPushBranchPrefixCombo(SWTBot bot) {
        bot.comboBoxWithLabel(Messages.WIZARD_GIT_REPOSITORY_LABEL_PUSH_BRANCH_PREFIX)
                .setSelection(Messages.WIZARD_GIT_REPOSITORY_OPTION_GERRIT_PUSH_BRANCH_PREFIX);
    }

    private void setCustomPushBranchPrefixCombo(SWTBot bot) {
        bot.comboBoxWithLabel(Messages.WIZARD_GIT_REPOSITORY_LABEL_PUSH_BRANCH_PREFIX)
                .setSelection(Messages.WIZARD_GIT_REPOSITORY_OPTION_OTHER_PUSH_BRANCH_PREFIX);
    }

    private SWTBotButton getFinishButton(SWTBot bot) {
        return bot.button("Finish");
    }

    private void clickCancelButton(SWTBot bot) {
        bot.button("Cancel").click();
    }

    private SWTBotText getPushBranchPrefixText(SWTBot bot) {
        return bot.textWithId(PUSH_BRANCH_PREFIX_TEXT_KEY, PUSH_BRANCH_PREFIX_TEXT_VALUE);
    }

    private SWTBot createBot(GitBasedRepositoryConfigurationWizard sut) {
        final WizardDialog dialog = new WizardDialog(null, sut);
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                dialog.setBlockOnOpen(false);
                dialog.open();
            }
        });
        SWTBot bot = new SWTBot();
        return bot.shell(Messages.WIZARD_GIT_REPOSITORY_WINDOW_TITLE).bot();
    }
}
