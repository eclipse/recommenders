/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.PathResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DownloadServiceTest {

    private static Server server;
    private static URI serverUri;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);

        server.setStopAtShutdown(true);

        HandlerList handlerList = new HandlerList();
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(new PathResource(FileSystems.getDefault().getPath("resources")));
        handlerList.addHandler(resourceHandler);
        handlerList.addHandler(new DefaultHandler());
        server.setHandler(handlerList);

        server.start();

        String host = connector.getHost() == null ? "localhost" : connector.getHost();
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("http://%s:%d/", host, port));
    }

    @Test
    public void testDownloadPresentResource() throws Exception {
        DownloadService sut = new DownloadService(temp.getRoot().toPath());

        sut.download(serverUri.resolve("present.txt"), null);
    }

    @Test(expected = IOException.class)
    public void testDownloadMissingResource() throws Exception {
        DownloadService sut = new DownloadService(temp.getRoot().toPath());

        sut.download(serverUri.resolve("missing.txt"), null);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        server.stop();
    }
}
