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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ContributionsReaderTest {

    private final String CONTRIBUTION_ELEMENT = "linkContribution";
    private final String CONTRIBUTOR_ID = "org.eclipse.recommenders.rcp";

    private final String PREF_PAGE_ID_ATTRIBUTE = "preferencePageId";
    private final String LABEL_ATTRIBUTE = "label";
    private final String COMMAND_ID_ATTRIBUTE = "commandId";
    private final String PRIORITY_ATTRIBUTE = "priority";
    private final String ICON_ELEMENT = "icon";

    private final String DEFAULT_LINK_ICON = "icons/obj16/eclipse.png";

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
        IConfigurationElement[] configElements = null;
        List<ContributionLink> links = ContributionsReader.readContributionLinks("some.pref.page.id",
                configElements);

        assertThat(links.size(), is(0));

        links = ContributionsReader.readContributionLinks("some.pref.page.id", new IConfigurationElement[] {});

        assertThat(links.size(), is(0));
    }

    @Test
    public void testContributionLinkOptionalAttributes() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "some.command.id"));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("some.pref.page.id",
                configElement);

        assertThat(links.size(), is(1));

        ContributionLink link = getOnlyElement(links);

        assertThat(link.getText(), is("<a>some Label</a>"));
        assertThat(link.getCommandId(), is("some.command.id"));
        assertThat(link.getPriority(), is(Integer.MAX_VALUE));
        assertThat(link.getIcon(), is(nullValue()));
    }

    @Test
    public void testContributionLinkWithInvalidPriority() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT,
                ImmutableMap.of(PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                        COMMAND_ID_ATTRIBUTE, "some.command.id", PRIORITY_ATTRIBUTE, "invalid", ICON_ELEMENT,
                        DEFAULT_LINK_ICON));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>other Label</a>",
                COMMAND_ID_ATTRIBUTE, "other.command.id", PRIORITY_ATTRIBUTE, "10", ICON_ELEMENT, DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("some.pref.page.id",
                firstConfigElement, secondConfigElement);

        assertThat(links.size(), is(1));

        ContributionLink link = getOnlyElement(links);

        assertThat(link.getText(), is("<a>other Label</a>"));
        assertThat(link.getCommandId(), is("other.command.id"));
        assertThat(link.getPriority(), is(10));
        assertThat(link.getIcon(), is(notNullValue()));
    }

    @Test
    public void testContributionLinkWithNonExistentIcon() {
        IConfigurationElement configElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>some Label</a>",
                COMMAND_ID_ATTRIBUTE, "some.command.id", ICON_ELEMENT, "invalid"));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("some.pref.page.id",
                configElement);

        assertThat(links.size(), is(0));
    }

    @Test
    public void testMultipleLinksContribution() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>first Label</a>",
                COMMAND_ID_ATTRIBUTE, "first.command.id", PRIORITY_ATTRIBUTE, "10", ICON_ELEMENT, DEFAULT_LINK_ICON));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "some.pref.page.id", LABEL_ATTRIBUTE, "<a>second Label</a>",
                COMMAND_ID_ATTRIBUTE, "second.command.id", PRIORITY_ATTRIBUTE, "0", ICON_ELEMENT, DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("some.pref.page.id",
                firstConfigElement, secondConfigElement);

        assertThat(links.size(), is(2));
    }

    @Test
    public void testLinkContributionForDifferentPreferencePages() {
        IConfigurationElement firstConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "first.pref.page.id", LABEL_ATTRIBUTE, "<a>first Label</a>",
                COMMAND_ID_ATTRIBUTE, "first.command.id", PRIORITY_ATTRIBUTE, "0", ICON_ELEMENT, DEFAULT_LINK_ICON));

        IConfigurationElement secondConfigElement = mockConfigElement(CONTRIBUTION_ELEMENT, ImmutableMap.of(
                PREF_PAGE_ID_ATTRIBUTE, "second.pref.page.id", LABEL_ATTRIBUTE, "<a>second Label</a>",
                COMMAND_ID_ATTRIBUTE, "second.command.id", PRIORITY_ATTRIBUTE, "10", ICON_ELEMENT, DEFAULT_LINK_ICON));

        List<ContributionLink> links = ContributionsReader.readContributionLinks("first.pref.page.id",
                firstConfigElement, secondConfigElement);

        assertThat(links.size(), is(1));

        ContributionLink firstPageLink = getOnlyElement(links);

        assertThat(firstPageLink.getText(), is("<a>first Label</a>"));
        assertThat(firstPageLink.getCommandId(), is("first.command.id"));
        assertThat(firstPageLink.getPriority(), is(0));
        assertThat(firstPageLink.getIcon(), is(notNullValue()));

        links = ContributionsReader.readContributionLinks("second.pref.page.id", firstConfigElement,
                secondConfigElement);

        assertThat(links.size(), is(1));

        ContributionLink secondPageLink = getOnlyElement(links);

        assertThat(secondPageLink.getText(), is("<a>second Label</a>"));
        assertThat(secondPageLink.getCommandId(), is("second.command.id"));
        assertThat(secondPageLink.getPriority(), is(10));
        assertThat(secondPageLink.getIcon(), is(notNullValue()));
    }
}
