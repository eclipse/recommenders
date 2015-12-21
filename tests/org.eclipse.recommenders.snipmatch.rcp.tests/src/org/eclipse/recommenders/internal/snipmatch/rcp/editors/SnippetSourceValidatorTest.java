package org.eclipse.recommenders.internal.snipmatch.rcp.editors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SnippetSourceValidatorTest {

    @Test
    public void testEmptySnippetIsValid() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("").isEmpty(), is(true));
    }

    @Test
    public void testSimpleSnippet() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("Lorem ipsum dolor").isEmpty(), is(true));
    }

    @Test
    public void testSnippetWithId() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("${id}").isEmpty(), is(true));
    }

    @Test
    public void testSnippetWithVarReference() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("${id:var(java.lang.String)}").isEmpty(), is(true));
    }

    @Test
    public void testSnippetWithEscapedDollarSign() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("$$").isEmpty(), is(true));
    }

    @Test
    public void testSnippetWithSingleDollarSign() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("$").isEmpty(), is(false));
    }

    @Test
    public void testSnippetWithUnclosedVarReference() {
        assertThat(JavaSnippetSourceValidator.isSourceValid("${id:var(java.lang.String)").isEmpty(), is(false));
    }
}
