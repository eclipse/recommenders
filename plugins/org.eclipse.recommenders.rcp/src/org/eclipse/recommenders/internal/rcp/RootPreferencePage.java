/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Olav Lenz - externalize Strings.
 */
package org.eclipse.recommenders.internal.rcp;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RootPreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

    @Override
    public void init(final IWorkbench workbench) {
        setDescription(Messages.PREFPAGE_DESCRIPTION_EMPTY);
    }

    @Override
    protected Control createContents(final Composite parent) {
        noDefaultAndApplyButton();
        Composite content = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(content);

        createLink(content, Messages.PREFPAGE_LABEL_HOMEPAGE, "homepage.png", //$NON-NLS-1$
                Messages.PREFPAGE_LINK_HOMEPAGE, "http://www.eclipse.org/recommenders/"); //$NON-NLS-1$

        createLink(content, Messages.PREFPAGE_LABEL_MANUAL, "container_obj.gif", Messages.PREFPAGE_LINK_MANUAL, //$NON-NLS-1$
                "http://www.eclipse.org/recommenders/manual/"); //$NON-NLS-1$

        createLink(content, Messages.PREFPAGE_LABEL_FAVORITE, "favorite_star.png", Messages.PREFPAGE_LINK_FAVORITE, //$NON-NLS-1$
                "http://marketplace.eclipse.org/content/eclipse-code-recommenders"); //$NON-NLS-1$

        createLink(content, Messages.PREFPAGE_LABEL_TWITTER, "bird_blue_16.png", Messages.PREFPAGE_LINK_TWITTER, //$NON-NLS-1$
                "http://twitter.com/recommenders"); //$NON-NLS-1$

        return new Composite(parent, SWT.NONE);
    }

    private void createLink(Composite content, String description, String icon, String urlLabel, String url) {
        CLabel label = new CLabel(content, SWT.NONE);
        label.setText(description);
        Image image = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.recommenders.rcp", //$NON-NLS-1$
                "icons/obj16/" + icon).createImage();//$NON-NLS-1$
        label.setImage(image);

        Link link = new Link(content, SWT.NONE);
        link.setText(MessageFormat.format(urlLabel, url));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
                            .createBrowser("recommenders-bugzilla"); //$NON-NLS-1$
                    browser.openURL(new URL(event.text));
                } catch (Exception e) {
                }
            }
        });
    }
}
