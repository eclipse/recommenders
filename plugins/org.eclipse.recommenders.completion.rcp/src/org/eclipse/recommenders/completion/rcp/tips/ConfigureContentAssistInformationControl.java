/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon Laffoy - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.tips;

import static java.util.Objects.requireNonNull;
import static org.eclipse.jdt.internal.ui.JavaPlugin.getActiveWorkbenchShell;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.internal.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.rcp.utils.BrowserUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.ImmutableList;

public class ConfigureContentAssistInformationControl extends AbstractInformationControl {

    private static final List<SessionProcessorDescriptor> NONE = Collections.<SessionProcessorDescriptor>emptyList();

    private static final String HTTP = "http:";
    private static final String HTTPS = "https:";

    public static final String X_PREFERENCES = "X-preferences:";
    public static final String X_SESSION_PROCESSOR = "X-sessionProcessor:";

    public static final char SWITCH_ON = '+';
    public static final char SWITCH_OFF = '-';

    private final String info;
    private final CompletionRcpPreferences preferences;

    public ConfigureContentAssistInformationControl(Shell parent, String statusLineText, String info,
            CompletionRcpPreferences preferences) {
        super(parent, statusLineText);
        this.info = requireNonNull(info);
        this.preferences = preferences;
        create();
    }

    @Override
    public boolean hasContents() {
        return true;
    }

    @Override
    protected void createContent(Composite parent) {
        Link link = new Link(parent, SWT.NONE);
        Dialog.applyDialogFont(link);
        link.setForeground(parent.getForeground());
        link.setBackground(parent.getBackground());
        link.setText(info);
        link.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("restriction")
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispose();
                if (StringUtils.startsWith(e.text, HTTP) || StringUtils.startsWith(e.text, HTTPS)) {
                    BrowserUtils.openInExternalBrowser(e.text);
                } else if (StringUtils.startsWith(e.text, X_PREFERENCES)) {
                    createPreferenceDialogOn(getActiveWorkbenchShell(),
                            StringUtils.substringAfter(e.text, X_PREFERENCES), null, null).open();
                } else if (preferences != null && StringUtils.startsWith(e.text, X_SESSION_PROCESSOR)) {
                    String sessionProcessorIDWithSwitch = StringUtils.substringAfter(e.text, X_SESSION_PROCESSOR);
                    char processorSwitch = sessionProcessorIDWithSwitch.charAt(0);
                    String sessionProcessor = StringUtils.substring(sessionProcessorIDWithSwitch, 1);
                    SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(sessionProcessor);
                    if (descriptor != null) {
                        if (processorSwitch == SWITCH_ON) {
                            preferences.setSessionProcessorEnabled(ImmutableList.of(descriptor), NONE);
                        } else if (processorSwitch == SWITCH_OFF) {
                            preferences.setSessionProcessorEnabled(NONE, ImmutableList.of(descriptor));
                        }
                    }
                }
            }
        });
    }

}
