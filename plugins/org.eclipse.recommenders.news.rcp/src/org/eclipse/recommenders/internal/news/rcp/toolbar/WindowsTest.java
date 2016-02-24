/**
 * Copyright (c) 2016 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.toolbar;

import javax.annotation.PostConstruct;

import org.eclipse.recommenders.internal.news.rcp.CommonImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class WindowsTest {

    @PostConstruct
    public void createGui(Composite composite) {
        Button button = new Button(composite, SWT.FLAT);
        button.setImage(CommonImages.RSS_ACTIVE.createImage());
    }
}
