package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.internal.snipmatch.rcp.GitBasedRepositoryConfigurationWizard.*;
import static org.eclipse.recommenders.utils.Checks.cast;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collections;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.rcp.model.impl.EclipseGitSnippetRepositoryConfigurationImpl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;
import org.mockito.Mockito;

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
    public void testEmptyFetchUrl() {
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

        clickCancelButton(bot);
    }

    @Test
    public void testVariousFetchUrlPrefixes() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setPushUrl(bot, "http://push/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "amazon-s3://user@fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "cvs://fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "bundle:///");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "jar://fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "file:///");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "pop://fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "ftp://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "telnet://fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "git://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "udp://fetch/");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "git+ssh://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "///");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "http://user@fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "***");
        assertThat(finishButton.isEnabled(), is(false));

        setFetchUrl(bot, "http://user:password@fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "https://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "sftp://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "ssh://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        setFetchUrl(bot, "ssh+git://fetch/");
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("ssh+git://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("http://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testVariousPushUrlPrefixes() {
        Repositories repositories = Mockito.mock(Repositories.class);
        Mockito.when(repositories.getRepositories()).thenReturn(Collections.<ISnippetRepository>emptySet());
        GitBasedRepositoryConfigurationWizard sut = new GitBasedRepositoryConfigurationWizard(repositories);
        SWTBot bot = createBot(sut);

        setName(bot, "name");
        setFetchUrl(bot, "http://fetch/");

        SWTBotText pushBranchPrefixText = getPushBranchPrefixText(bot);
        assertThat(pushBranchPrefixText.isEnabled(), is(false));

        SWTBotButton finishButton = getFinishButton(bot);
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "amazon-s3://user@push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "cvs://push/");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "bundle:///");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "jar://push/");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "file:///");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "pop://push/");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "ftp://push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "telnet://push/");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "git://push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "udp://push/");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "git+ssh://push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "///");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "http://user@push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "***");
        assertThat(finishButton.isEnabled(), is(false));

        setPushUrl(bot, "http://user:password@push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "https://push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "sftp://push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "ssh://push/");
        assertThat(finishButton.isEnabled(), is(true));

        setPushUrl(bot, "ssh+git://push/");
        assertThat(finishButton.isEnabled(), is(true));

        finishButton.click();
        EclipseGitSnippetRepositoryConfigurationImpl configuration = cast(sut.getConfiguration());

        assertThat(configuration.getName(), is(equalTo("name")));
        assertThat(configuration.getUrl(), is(equalTo("http://fetch/")));
        assertThat(configuration.getPushUrl(), is(equalTo("ssh+git://push/")));
        assertThat(configuration.getPushBranchPrefix(), is(equalTo("refs/heads")));
    }

    @Test
    public void testUseOfExistingFetchUriAsNewPushUriAllowed() {
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
    public void testUseOfExistingFetchUriAsNewFetchUriNotAllowed() {
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
