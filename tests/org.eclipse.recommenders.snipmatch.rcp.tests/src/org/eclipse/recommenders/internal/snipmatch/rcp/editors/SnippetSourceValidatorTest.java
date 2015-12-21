package org.eclipse.recommenders.internal.snipmatch.rcp.editors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class SnippetSourceValidatorTest {

    private static final boolean VALID = true;
    private static final boolean INVALID = false;

    private final String templateText;
    private final boolean isValid;

    public SnippetSourceValidatorTest(String description, String templateText, boolean isValid) {
        this.templateText = templateText;
        this.isValid = isValid;
    }

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(scenario("Empty snippet", "", VALID));
        scenarios.add(scenario("Simple snippet", "Lorem ipsum dolor", VALID));
        scenarios.add(scenario("Snippet with id", "${id}", VALID));
        scenarios.add(scenario("Snippet with var reference", "${id:var(java.lang.String)}", VALID));
        scenarios.add(scenario("Snippet with escaped dollar sign", "$$", VALID));
        scenarios.add(scenario("Snippet with single dollar sign", "$", INVALID));
        scenarios.add(scenario("Unclosed var reference", "${id:var(java.lang.String)", INVALID));

        return scenarios;
    }

    private static Object[] scenario(String description, String templateText, boolean isValid) {
        return new Object[] { description, templateText, isValid };
    }

    @Test
    public void test() {
        assertThat(JavaSnippetSourceValidator.isSourceValid(templateText).isEmpty(), is(isValid));
        assertThat(TextSnippetSourceValidator.isSourceValid(templateText).isEmpty(), is(isValid));
    }
}
