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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.ui.internal.SharedImages;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ContributionExtensionReaderTest {

    private final String CONTRIBUTION_ELEMENT = "linkContribution";
    private final String CONTRIBUTOR_ID = "org.eclipse.recommenders.rcp";

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
        when(contributor.getName()).thenReturn(CONTRIBUTOR_ID);
        when(element.getContributor()).thenReturn(contributor);
        return element;
    }

    @Test
    public void testNoExtensionsFound() {
        ContributionExtensionReader sut = new ContributionExtensionReader("some.pref.page.id");

        IConfigurationElement[] configElements = null;
        sut.readContributionLinks(configElements);
        List<ContributionLink> links = sut.getContributionLinks();

        assertThat(links.size(), is(0));

        sut.readContributionLinks(new IConfigurationElement[] {});

        assertThat(links.size(), is(0));
    }

    @Test
    public void testLinkExtensionOptionalAttributes() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</>", COMMAND_ID_ATTRIBUTE,
                "some.command.id"));

        ContributionExtensionReader sut = new ContributionExtensionReader("some.pref.page.id");
        sut.readContributionLinks(configElement);

        List<ContributionLink> links = sut.getContributionLinks();

        assertThat(links.size(), is(1));

        ContributionLink link = getOnlyElement(links);

        assertThat(link.getPriority(), is(Integer.MAX_VALUE));
    }

    @Test
    public void testLinkExtensionDefaultPriority() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</>", COMMAND_ID_ATTRIBUTE,
                "some.command.id", ICON_ELEMENT, SharedImages.IMG_OBJ_ELEMENT));

        ContributionExtensionReader sut = new ContributionExtensionReader("some.pref.page.id");
        sut.readContributionLinks(configElement);

        List<ContributionLink> links = sut.getContributionLinks();

        assertThat(links.size(), is(1));

        ContributionLink link = getOnlyElement(links);

        assertThat(link.getPriority(), is(Integer.MAX_VALUE));
    }

    @Test
    public void testLinkExtensionInvalidPriority() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</>", COMMAND_ID_ATTRIBUTE,
                "some.command.id", PRIORITY_ATTRIBUTE, "invalid", ICON_ELEMENT, SharedImages.IMG_OBJ_ELEMENT));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>other Label</>",
                COMMAND_ID_ATTRIBUTE, "other.command.id", PRIORITY_ATTRIBUTE, "10"));

        ContributionExtensionReader sut = new ContributionExtensionReader("some.pref.page.id");
        sut.readContributionLinks(firstConfigElement, secondConfigElement);

        List<ContributionLink> links = sut.getContributionLinks();

        assertThat(links.size(), is(1));

        ContributionLink link = getOnlyElement(links);

        assertThat(link.getPriority(), is(10));
    }

    @Test
    public void testLinkExtensionNonExistentIcon() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</>", COMMAND_ID_ATTRIBUTE,
                "some.command.id", ICON_ELEMENT, "invalid"));

        ContributionExtensionReader sut = new ContributionExtensionReader("some.pref.page.id");
        sut.readContributionLinks(configElement);

        List<ContributionLink> links = sut.getContributionLinks();

        assertThat(links.size(), is(0));
    }

    @Test
    public void testMultipleLinksContribution() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>first Label</>",
                COMMAND_ID_ATTRIBUTE, "first.command.id", PRIORITY_ATTRIBUTE, "0", ICON_ELEMENT,
                SharedImages.IMG_OBJ_ELEMENT));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>second Label</>",
                COMMAND_ID_ATTRIBUTE, "second.command.id", PRIORITY_ATTRIBUTE, "0", ICON_ELEMENT,
                SharedImages.IMG_OBJ_ELEMENT));

        ContributionExtensionReader firstPrefPageContribution = new ContributionExtensionReader("some.pref.page.id");
        firstPrefPageContribution.readContributionLinks(firstConfigElement, secondConfigElement);
        List<ContributionLink> links = firstPrefPageContribution.getContributionLinks();

        assertThat(links.size(), is(2));
    }

    @Test
    public void testLinkContributionForDifferentPreferencePages() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "first.pref.page.id", LABEL_ATTRIBUTE, "<a>first Label</>",
                COMMAND_ID_ATTRIBUTE, "first.command.id", PRIORITY_ATTRIBUTE, "0", ICON_ELEMENT,
                SharedImages.IMG_OBJ_ELEMENT));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "second.pref.page.id", LABEL_ATTRIBUTE, "<a>second Label</>",
                COMMAND_ID_ATTRIBUTE, "second.command.id", PRIORITY_ATTRIBUTE, "1", ICON_ELEMENT,
                SharedImages.IMG_OBJ_ELEMENT));

        ContributionExtensionReader firstPrefPageContribution = new ContributionExtensionReader("first.pref.page.id");
        firstPrefPageContribution.readContributionLinks(firstConfigElement, secondConfigElement);
        List<ContributionLink> links = firstPrefPageContribution.getContributionLinks();

        assertThat(links.size(), is(1));

        ContributionExtensionReader secondPrefPageContribution = new ContributionExtensionReader("second.pref.page.id");
        secondPrefPageContribution.readContributionLinks(firstConfigElement, secondConfigElement);
        links = secondPrefPageContribution.getContributionLinks();

        assertThat(links.size(), is(1));
    }
}
