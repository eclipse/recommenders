package org.eclipse.recommenders.models.dependencies.impl;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.models.dependencies.DependencyType.JAR;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.dependencies.DependencyInfo;
import org.eclipse.recommenders.models.dependencies.DependencyType;
import org.eclipse.recommenders.utils.Fingerprints;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class MavenCentralFingerprintSearchStrategy extends AbstractStrategy {

    private static final URL SEARCH_MAVEN_ORG;
    private static final List<String> SUPPORTED_PACKAGINGS = Arrays.asList("jar", "war", "bundle");

    static {
        try {
            SEARCH_MAVEN_ORG = new URL("http://search.maven.org/solrsearch");
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }
    }

    private SolrServer server;

    public MavenCentralFingerprintSearchStrategy() {
        server = createSolrServer();
    }

    @Override
    public boolean isApplicable(DependencyType dependencyType) {
        return dependencyType == JAR;
    }

    @Override
    protected Optional<ProjectCoordinate> extractProjectCoordinateInternal(DependencyInfo dependencyInfo) {
        try {
            SolrQuery query = new SolrQuery();
            query.setQuery("1:\"" + Fingerprints.sha1(dependencyInfo.getFile()) + "\"");
            query.setRows(1);
            QueryResponse response = server.query(query);
            SolrDocumentList results = response.getResults();

            for (SolrDocument document : results) {
                if (!SUPPORTED_PACKAGINGS.contains((String) document.get("p")))
                    continue;

                String groupId = (String) document.get("g");
                String artifactId = (String) document.get("a");
                String version = (String) document.get("v");

                return of(new ProjectCoordinate(groupId, artifactId, version));
            }

            return absent();
        } catch (SolrServerException e) {
            return absent();
        }
    }

    private SolrServer createSolrServer() {
        // Need to create our own HttpClient here rather than let CommonsHttpSolrServer do it, as that fails a with ClassDefNotFoundError.
        // Apparently, there's an Import-Package missing for org.apache.commons.httpclient.params(.HttpMethodParams).
        final HttpClient httpClient = new HttpClient();
        final CommonsHttpSolrServer server = new CommonsHttpSolrServer(SEARCH_MAVEN_ORG, httpClient);

        server.setAllowCompression(true);
        server.setParser(new XMLResponseParser());

        return server;
    }
}
