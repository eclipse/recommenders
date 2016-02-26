package org.eclipse.recommenders.news.core;

import static org.eclipse.recommenders.internal.news.rcp.Proxies.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.recommenders.internal.news.rcp.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class DownloadService implements IDownloadService {

    private static final String DOT_XML = ".xml"; //$NON-NLS-1$

    private final Executor executor;
    private final Path downloadLocation;

    public DownloadService() {
        executor = Executor.newInstance();

        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        Path stateLocation = Platform.getStateLocation(bundle).toFile().toPath();
        downloadLocation = stateLocation.resolve("downloads"); //$NON-NLS-1$
    }

    @Override
    @Nullable
    public InputStream download(URI uri, @Nullable IProgressMonitor monitor) {
        SubMonitor progress = SubMonitor.convert(monitor);
        try {
            Response resource;
            try {
                resource = getResource(uri);
                if (isResourcePresent(resource)) {
                    String prefix = URLEncoder.encode(uri.toASCIIString(), StandardCharsets.UTF_8.name());
                    Path tempFile = null;
                    tempFile = Files.createTempFile(prefix, DOT_XML);
                    try (InputStream resourceStream = resource.returnContent().asStream()) {
                        Files.copy(resourceStream, tempFile);
                    }
                }
            } catch (IOException e) {
                // TODO Return existing file, if any
            }
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    private Response getResource(URI uri) throws ClientProtocolException, IOException {
        Request request = Request.Get(uri).viaProxy(getProxyHost(uri).orNull())
                .connectTimeout((int) Constants.CONNECTION_TIMEOUT).staleConnectionCheck(true)
                .socketTimeout((int) Constants.SOCKET_TIMEOUT);
        return proxyAuthentication(executor, uri).execute(request);
    }

    private boolean isResourcePresent(Response response) {
        StatusLine statusLine;
        try {
            statusLine = response.returnResponse().getStatusLine();
            if (statusLine == null) {
                return false;
            }
            if (statusLine.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
                return false;
            }
            Content content = response.returnContent();
            if (Content.NO_CONTENT.equals(content)) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    @Nullable
    public InputStream read(URI uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nullable
    public Date getLastAttemptDate(URI uri) {
        // TODO Auto-generated method stub
        return null;
    }

}
