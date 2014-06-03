package org.eclipse.recommenders.snipmatch;

import org.apache.lucene.search.DefaultSimilarity;

public class IgnoreDocFrequencySimilarity extends DefaultSimilarity {

    @Override
    public float idf(int docFreq, int numDocs) {
        return docFreq > 0 ? 1.0f : 0.0f;
    }

    @Override
    public float tf(float freq) {
        return freq > 0 ? 1.0f : 0.0f;
    }

}
