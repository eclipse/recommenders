package org.eclipse.recommenders.internal.jayes.io.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.io.IBayesNetReader;

public class CommonsReader implements IBayesNetReader {

    private InputStream in;
    BayesNetConverter conv = new BayesNetConverter();

    public CommonsReader(InputStream in) throws IOException {
        this.in = in;

    }

    @Override
    public void close() throws IOException {
        in.close();

    }

    @SuppressWarnings("deprecation")
    @Override
    public BayesNet read() throws IOException {
        try {
            return conv.transform(BayesianNetwork.read(in));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
