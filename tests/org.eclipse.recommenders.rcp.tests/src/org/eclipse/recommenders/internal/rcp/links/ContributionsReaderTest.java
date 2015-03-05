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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ContributionsReaderTest {

    private final String CONTRIBUTION_ELEMENT = "linkContribution";

    // $BUNDLE_ID Required in order to load the DEFAULT_LINK_ICON
    private final String CONTRIBUTOR_ID = "org.eclipse.recommenders.rcp.tests";

    private final String PREF_PAGE_ID_ATTRIBUTE = "preferencePageId";
    private final String LABEL_ATTRIBUTE = "label";
    private final String COMMAND_ID_ATTRIBUTE = "commandId";
    private final String PRIORITY_ATTRIBUTE = "priority";
    private final String ICON_ELEMENT = "icon";

    private final String DEFAULT_LINK_ICON = "icons/obj16/eclipse.png";

    private IConfigurationElement mockConfigElement(String name, Map<String, String> map) {
        IConfigurationElement element = mock(IConfigurationElement.class, RETURNS_DEEP_STUBS);

        when(element.getName()).thenReturn(name);

        for (String key : map.keySet()) {
            when(element.getAttribute(key)).thenReturn(map.get(key));
        }
        when(element.getContributor().getName()).thenReturn(CONTRIBUTOR_ID);
        return element;
    }

    @Test
    public void testNoExtensionsFound() {
        IConfigurationElement[] configElements = null;
        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                configElements);

        assertThat(links.size(), is(0));

        links = ContributionsReader.readContributionLinks("org.example.pref.page.id", new IConfigurationElement[] {});

        assertThat(links.size(), is(0));
    }

    @Test
    public void testHappyContributionReader() {
        IConfigurationElement coonfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.command.id", PRIORITY_ATTRIBUTE, "10", ICON_ELEMENT,
                DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                coonfigElement);

        assertThat(links.size(), is(1));

        ContributionLink firstPageLink = getOnlyElement(links);

        assertThat(firstPageLink.getText(), is("<a>some Label</a>"));
        assertThat(firstPageLink.getCommandId(), is("org.example.command.id"));
        assertThat(firstPageLink.getPriority(), is(10));
        assertThat(firstPageLink.getIcon(), is(notNullValue()));
    }

    @Test
    public void testContributionReaderWithoutIcon() {
        IConfigurationElement coonfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.command.id", PRIORITY_ATTRIBUTE, "10"));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                coonfigElement);

        assertThat(links.size(), is(1));

        ContributionLink firstPageLink = getOnlyElement(links);

        assertThat(firstPageLink.getText(), is("<a>some Label</a>"));
        assertThat(firstPageLink.getCommandId(), is("org.example.command.id"));
        assertThat(firstPageLink.getPriority(), is(10));
        assertThat(firstPageLink.getIcon(), is(nullValue()));
    }

    @Test
    public void testContributionReaderWithNonExistingIcon() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.command.id", ICON_ELEMENT, "invalid"));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                configElement);

        assertThat(links.size(), is(0));
    }

    @Test
    public void testContributionReaderDefaultPriority() {
        IConfigurationElement coonfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.command.id", ICON_ELEMENT, DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                coonfigElement);

        assertThat(links.size(), is(1));

        ContributionLink firstPageLink = getOnlyElement(links);

        assertThat(firstPageLink.getText(), is("<a>some Label</a>"));
        assertThat(firstPageLink.getCommandId(), is("org.example.command.id"));
        assertThat(firstPageLink.getPriority(), is(Integer.MAX_VALUE));
        assertThat(firstPageLink.getIcon(), is(notNullValue()));
    }

    @Test
    public void testContributionReaderInvalidPriority() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.exmaple.command.id", PRIORITY_ATTRIBUTE, "invalid", ICON_ELEMENT,
                DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                configElement);

        assertThat(links.size(), is(0));
    }

    @Test
    public void testContributionReaderWithMultipleLinks() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>first Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.first.command.id", PRIORITY_ATTRIBUTE, "10", ICON_ELEMENT,
                DEFAULT_LINK_ICON));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>broken Label</a>"));

        IConfigurationElement thirdConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.pref.page.id", LABEL_ATTRIBUTE, "<a>third Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.third.command.id", PRIORITY_ATTRIBUTE, "20", ICON_ELEMENT,
                DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.pref.page.id",
                firstConfigElement, secondConfigElement, thirdConfigElement);

        assertThat(links.size(), is(2));
    }

    @Test
    public void testContributionReaderWithDifferentPreferencePages() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.first.pref.page.id", LABEL_ATTRIBUTE, "<a>first Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.first.command.id", PRIORITY_ATTRIBUTE, "0", ICON_ELEMENT,
                DEFAULT_LINK_ICON));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "org.example.second.page.id", LABEL_ATTRIBUTE, "<a>second Label</a>",
                COMMAND_ID_ATTRIBUTE, "org.example.second.command.id", PRIORITY_ATTRIBUTE, "10", ICON_ELEMENT,
                DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("org.example.first.pref.page.id",
                firstConfigElement, secondConfigElement);

        assertThat(links.size(), is(1));

        links = ContributionsReader.readContributionLinks("org.example.second.page.id", firstConfigElement,
                secondConfigElement);

        assertThat(links.size(), is(1));

        links = ContributionsReader.readContributionLinks("org.example.non.existent.page.id", firstConfigElement,
                secondConfigElement);

        assertThat(links.size(), is(0));
    }
}
