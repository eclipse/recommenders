/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.utils.rcp.preferences;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PreferencesPagesTest {

    private static final String PREFPAGE_ID_NON_EXISTING = "invalid";
    private static final String PREFPAGE_ID_JAVA_BASE = "org.eclipse.jdt.ui.preferences.JavaBasePreferencePage";
    private static final String PREFPAGE_ID_EDITORS = "org.eclipse.ui.preferencePages.Editors";
    private static final String PREFPAGE_ID_SPELLING = "org.eclipse.ui.editors.preferencePages.Spelling";

    @Test
    public void emptyLinkStringForEmptyPrefPageID() {
        assertThat(PreferencePages.createLinkLabelToPreferencePage(""), isEmptyString());
    }

    @Test
    public void emptyLinkStringForNonExistingPrefPageID() {
        assertThat(PreferencePages.createLinkLabelToPreferencePage(PREFPAGE_ID_NON_EXISTING), isEmptyString());
    }

    @Test
    public void correctLinkStringForPrefPageWithoutTopCategory() {
        assertThat(PreferencePages.createLinkLabelToPreferencePage(PREFPAGE_ID_JAVA_BASE), is(equalTo("Java")));
    }

    @Test
    public void correctLinkStringForPrefPageWithOneTopCategory() {
        assertThat(PreferencePages.createLinkLabelToPreferencePage(PREFPAGE_ID_EDITORS),
                is(equalTo("General > Editors")));
    }

    @Test
    public void correctLinkStringForPrefPageWithMoreTopCategory() {
        assertThat(PreferencePages.createLinkLabelToPreferencePage(PREFPAGE_ID_SPELLING),
                is(equalTo("General > Editors > Text Editors > Spelling")));
    }
}
