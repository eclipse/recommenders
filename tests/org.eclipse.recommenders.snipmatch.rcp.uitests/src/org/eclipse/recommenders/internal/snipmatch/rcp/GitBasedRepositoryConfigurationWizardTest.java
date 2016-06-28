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
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(SWTBotJunit4ClassRunner.class)
public class GitBasedRepositoryConfigurationWizardTest {

    @Test
    public void testDefaultPushBranchPrefix() throws InterruptedException {
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
    public void testGitPushBranchPrefix() throws InterruptedException {
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
    public void testGerritPushBranchPrefix() throws InterruptedException {
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
    public void testCustomPushBranchPrefix() throws InterruptedException {
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
