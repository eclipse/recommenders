package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jface.internal.provisional.action.ToolBarManager2;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.model.SnipmatchRcpModelFactory;
import org.eclipse.recommenders.rcp.model.SnippetRepositoryConfigurations;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IViewSite;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

public class SnippetsViewTest {

    @Test
    public void testWay1() throws InterruptedException, IOException {
        final Shell shell = new Shell(Display.getDefault());
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                shell.setSize(500, 200);
                try {
                    SnippetsView sv = createSUT();
                    sv.createPartControl(shell);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                shell.open();
            }
        });
        SWTBot bot = new SWTBot(shell);
        assertThat(bot.text(), is(not(nullValue())));
        assertThat(bot.tree(), is(not(nullValue())));
    }

    @Test
    public void testWay2() {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell showViewShell = bot.shell("Show View").activate();
        showViewShell.bot().tree().expandNode("Code Recommenders").getNode("Snippets").doubleClick();

        SWTBotView snippetsView = bot.activeView();
        assertThat(snippetsView.getTitle(), equalTo("Snippets"));

    }

    private static SnippetsView createSUT() throws IOException {
        Repositories repos = createRepositoriesMock();
        SharedImages images = new SharedImages();
        SnippetRepositoryConfigurations configs = SnipmatchRcpModelFactory.eINSTANCE
                .createSnippetRepositoryConfigurations();
        EventBus bus = new EventBus();
        File repositoryConfigurationFile = new File("test.config");
        SnippetsView sv = spy(new SnippetsView(repos, images, configs, bus, repositoryConfigurationFile));

        IViewSite viewSite = mock(IViewSite.class, RETURNS_DEEP_STUBS);
        when(viewSite.getActionBars().getToolBarManager()).thenReturn(new ToolBarManager2());
        when(sv.getViewSite()).thenReturn(viewSite);
        return sv;
    }

    private static Repositories createRepositoriesMock() {
        Repositories mock = mock(Repositories.class);
        Set<ISnippetRepository> repositories = Sets.newHashSet();

        when(mock.getRepositories()).thenReturn(repositories);
        return mock;
    }

}
