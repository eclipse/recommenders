/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasser Aziza - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.links;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ContributionExtensionReaderTest {

    private final String PREF_CONTRIBUTION_ID = "org.example.page";
    private final String CONTRIBUTION_ELEMENT = "linkContribution";

    private final String PREF_PAGE_ID_ATTRIBUTE = "preferencePageId";
    private final String LABEL_ATTRIBUTE = "label";
    private final String COMMAND_ID_ATTRIBUTE = "commandId";
    private final String PRIORITY_ATTRIBUTE = "priority";
    private final String ICON_ELEMENT = "icon";

    private IConfigurationElement mockConfigElement(String name, ImmutableMap<String, String> map) {
        IConfigurationElement element = mock(IConfigurationElement.class);
        IContributor contributor = mock(IContributor.class);

        when(element.getName()).thenReturn(name);

        for (String key : map.keySet()) {
            when(element.getAttribute(key)).thenReturn(map.get(key));
        }
        when(contributor.getName()).thenReturn("org.eclipse.recommenders");
        when(element.getContributor()).thenReturn(contributor);
        return element;
    }

    @Test
    public void testNoExtensionsFound() {
        ContributionExtensionReader sut = new ContributionExtensionReader(PREF_CONTRIBUTION_ID);

        IConfigurationElement[] configElements = null;
        sut.readContributionLinks(configElements);
        List<ContributionLink> links = sut.getContributionLinks();

        assertThat(links.size(), is(0));

        sut.readContributionLinks(new IConfigurationElement[] {});

        assertThat(links.size(), is(0));
    }
}
