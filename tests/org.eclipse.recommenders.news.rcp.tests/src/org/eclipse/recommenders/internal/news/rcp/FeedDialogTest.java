/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FeedDialogTest {
    private static final String EMPTY_STRING = "";

    private final Shell shell = Display.getDefault().getActiveShell();
    private final NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
    private final FeedDescriptor feed = mock(FeedDescriptor.class);
    private final FeedDialog feedDialog = spy(new FeedDialog(shell, preferences));

    @Test
    public void testNameInputIsEmpty() {
        // TODO Move to new test suite and do it properly
        // if (feedDialog.open() == Window.OK) {
        // feedDialog.getNameValue().setText(EMPTY_STRING);
        // assertThat(feedDialog.getErrorMessage(), is(equalTo(Messages.FEED_DIALOG_ERROR_EMPTY_NAME)));
        // feedDialog.closeDialog();
        // }
    }
}
