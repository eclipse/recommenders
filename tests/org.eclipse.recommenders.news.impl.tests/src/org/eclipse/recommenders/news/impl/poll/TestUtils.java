package org.eclipse.recommenders.news.impl.poll;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static Date now(long delta, TimeUnit timeUnit) {
        long now = System.currentTimeMillis();
        return new Date(now + timeUnit.toMillis(delta));
    }
}
