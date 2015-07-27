/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class BrowserUtilsTest {

    private static final NameValuePair VALID_PARAMETER = new BasicNameValuePair("parameter", "value");
    private static final String BASE_URL = "http://eclipse.org";
    private static final String BASE_URL_WITH_SPACE = "http://eclipse.org";
    private static final String BASE_URL_WITH_PARAMETER = "http://eclipse.org?foo=bar";

    private String actualUrl;
    private List<? extends NameValuePair> parameters;
    private String expectedUrl;

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        scenarios.add(new Object[] { null, null, "" });
        scenarios.add(new Object[] { BASE_URL, null, BASE_URL });
        scenarios.add(new Object[] { BASE_URL, ImmutableList.of(VALID_PARAMETER), BASE_URL + "?" + VALID_PARAMETER });
        scenarios.add(new Object[] { BASE_URL_WITH_PARAMETER, ImmutableList.of(VALID_PARAMETER),
                BASE_URL_WITH_PARAMETER + "&" + VALID_PARAMETER });
        scenarios.add(new Object[] { BASE_URL, ImmutableList.of(VALID_PARAMETER, VALID_PARAMETER),
                BASE_URL + "?" + VALID_PARAMETER + "&" + VALID_PARAMETER });
        scenarios.add(new Object[] { BASE_URL_WITH_SPACE, ImmutableList.of(VALID_PARAMETER),
                BASE_URL + "%20" + "?" + VALID_PARAMETER });

        return scenarios;
    }

    public BrowserUtilsTest(String url, List<? extends NameValuePair> parameters, String expectedUrl) {
        actualUrl = url;
        this.parameters = parameters;
        this.expectedUrl = expectedUrl;
    }

    @Test
    public void testEncodeURI() throws Exception {
        assertThat(BrowserUtils.encodeURI(actualUrl, parameters), is(Matchers.equalTo(expectedUrl)));
    }
}
