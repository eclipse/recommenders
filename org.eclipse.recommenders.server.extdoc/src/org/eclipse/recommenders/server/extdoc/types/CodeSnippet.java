package org.eclipse.recommenders.server.extdoc.types;

import static org.eclipse.recommenders.commons.utils.Checks.ensureIsNotEmpty;

import org.eclipse.recommenders.commons.utils.names.IMethodName;

public class CodeSnippet {

    public static CodeSnippet create(final IMethodName origin, final String code) {
        ensureIsNotEmpty(code, "empty code fragments not allowed.");
        final CodeSnippet res = new CodeSnippet();
        res.origin = origin;
        res.code = code;
        return res;
    }

    private IMethodName origin;
    private String code;

    public IMethodName getOrigin() {
        return origin;
    }

    public String getCode() {
        return code;
    }
}
