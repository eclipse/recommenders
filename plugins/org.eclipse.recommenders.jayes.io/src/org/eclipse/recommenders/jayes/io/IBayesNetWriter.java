package org.eclipse.recommenders.jayes.io;

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.recommenders.jayes.BayesNet;

public interface IBayesNetWriter extends Closeable {

    void write(BayesNet bayesNet) throws IOException;

}
