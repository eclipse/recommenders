package org.eclipse.recommenders.models.advisors;

import static org.eclipse.recommenders.models.advisors.SharedManualMappingsAdvisor.matchesSuffixPattern;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class SharedManualMappingsAdvisorTest {

    private final String path;
    private final String suffixPattern;
    private final boolean expected;

    public SharedManualMappingsAdvisorTest(String path, String suffixPattern, boolean matches) {
        this.path = path;
        this.suffixPattern = suffixPattern;
        expected = matches;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(match("/home/user/workspace/lib.jar", "lib.jar"));
        scenarios.add(match("/home/user/workspace/lib.jar", "*.jar"));
        scenarios.add(mismatch("/home/user/workspace/lib.jar", "?.jar"));
        scenarios.add(match("/home/user/workspace/lib.jar", "???.jar"));

        scenarios.add(match("/home/user/workspace/lib.jar", "workspace/lib.jar"));
        scenarios.add(mismatch("/home/user/workspace/lib.jar", "ws/*.jar"));
        scenarios.add(match("/home/user/workspace/lib.jar", "*/*.jar"));

        scenarios.add(match("/home/user/workspace/lib.jar", "user/workspace/lib.jar"));
        scenarios.add(mismatch("/home/user/workspace/lib.jar", "usr/workspace/lib.jar"));

        scenarios.add(match("/home/user/workspace/lib.jar", "home/user/workspace/lib.jar"));
        scenarios.add(mismatch("/home/user/workspace/lib.jar", "/home/*/lib.jar"));

        scenarios.add(match("/home/user/workspace/lib.jar", "/home/user/workspace/lib.jar"));

        return scenarios;
    }

    private static Object[] match(String absolutePath, String suffixPattern) {
        return new Object[] { absolutePath, suffixPattern, true };
    }

    private static Object[] mismatch(String absolutePath, String suffixPattern) {
        return new Object[] { absolutePath, suffixPattern, false };
    }

    @Test
    public void testMatchesSuffixPattern() {
        assertThat(matchesSuffixPattern(path, suffixPattern), is(expected));
    }
}
