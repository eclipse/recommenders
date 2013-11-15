package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.internal.models.rcp.WorkspaceMappingsAdvisor.matchesSuffixPattern;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.recommenders.internal.models.rcp.WorkspaceMappingsAdvisor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class WorkspaceMappingsAdvisorTest {

    @Test
    public void testNoWildcards() {
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "lib.jar"), is(true));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "workspace/lib.jar"), is(true));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "user/workspace/lib.jar"), is(true));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "home/user/workspace/lib.jar"), is(true));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "/home/user/workspace/lib.jar"), is(true));
    }

    @Test
    public void testWildcards() {
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "?.jar"), is(false));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "???.jar"), is(true));

        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "*.jar"), is(true));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "*/*.jar"), is(true));
        assertThat(matchesSuffixPattern("/home/user/workspace/lib.jar", "/home/*/lib.jar"), is(false));
    }
}
