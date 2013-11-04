package org.eclipse.recommenders.jayes.io;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.recommenders.jayes.BayesNet;

public interface IBayesNetReader extends Closeable {
    BayesNet read() throws IOException;
}
