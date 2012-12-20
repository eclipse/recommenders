/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Patrick Gottschaemmer, Olav Lenz - initial Implementation.
 */
package org.eclipse.recommenders.tests.rcp.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.recommenders.internal.rcp.repo.ServiceBasedProxySelector;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonatype.aether.repository.Proxy;
import org.sonatype.aether.repository.ProxySelector;
import org.sonatype.aether.repository.RemoteRepository;

public class ProxySelectorTest {

    private static final boolean PROXY_ENABLED = true;
    private static final boolean PROXY_DISABLED = false;
    private static final IProxyData[] PROXYDATA_EMPTY = new IProxyData[] {};
    private static final IProxyService PROXYSERVICE_NULL = null;

    @Test
    public void noProxyEnabled() {
        IProxyService proxyService = mockProxyService(PROXY_DISABLED, PROXYDATA_EMPTY);
        ProxySelector proxySelector = new ServiceBasedProxySelector(proxyService);

        assertNull(proxySelector.getProxy(mockRemoteRepository("http://remote.com")));
    }

    @Test
    public void proxyServiceNull() {
        ProxySelector proxySelector = new ServiceBasedProxySelector(PROXYSERVICE_NULL);

        assertNull(proxySelector.getProxy(mockRemoteRepository("http://remote.com")));
    }

    @Test
    public void noProxyDataEntries() {
        IProxyService proxyService = mockProxyService(PROXY_ENABLED, PROXYDATA_EMPTY);
        ProxySelector proxySelector = new ServiceBasedProxySelector(proxyService);

        assertNull(proxySelector.getProxy(mockRemoteRepository("http://remote.com")));
    }

    @Test
    public void singleProxyTest() {
        IProxyData mock = mockProxyData("HTTP", "foo.com", 0);
        IProxyService proxyService = mockProxyService(PROXY_ENABLED, new IProxyData[] { mock });
        ProxySelector proxySelector = new ServiceBasedProxySelector(proxyService);

        Proxy proxy = proxySelector.getProxy(mockRemoteRepository("http://remote.com"));
        assertEquals(proxy.getType(), "http");
        assertEquals(proxy.getHost(), "foo.com");
        assertEquals(proxy.getPort(), 0);
    }

    @Test
    public void multiProxyTest() {
        IProxyData mock1 = mockProxyData("HTTP", "foo.com", 0);
        IProxyData mock2 = mockProxyData("HTTP", "foo2.com", 0);
        IProxyService proxyService = mockProxyService(PROXY_ENABLED, new IProxyData[] { mock1, mock2 });
        ProxySelector proxySelector = new ServiceBasedProxySelector(proxyService);

        // When there are more than one ProxyData, the ProxySelector should choose the first one
        Proxy proxy = proxySelector.getProxy(mockRemoteRepository("http://remote.com"));
        assertEquals(proxy.getType(), "http");
        assertEquals(proxy.getHost(), "foo.com");
        assertEquals(proxy.getPort(), 0);
    }

    private IProxyService mockProxyService(boolean proxyEnabled, IProxyData[] entries) {
        IProxyService proxyService = mock(IProxyService.class);
        when(proxyService.isProxiesEnabled()).thenReturn(proxyEnabled);
        when(proxyService.select(Mockito.any(URI.class))).thenReturn(entries);
        return proxyService;
    }

    private IProxyData mockProxyData(String type, String host, int port) {
        IProxyData proxyData = mock(IProxyData.class);
        when(proxyData.getType()).thenReturn(type);
        when(proxyData.getHost()).thenReturn(host);
        when(proxyData.getPort()).thenReturn(port);
        return proxyData;
    }

    private RemoteRepository mockRemoteRepository(String url) {
        RemoteRepository remote = mock(RemoteRepository.class);
        when(remote.getUrl()).thenReturn(url);
        return remote;
    }
}