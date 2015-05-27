package org.eclipse.recommenders.completion.rcp.utils;

import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture;
import org.eclipse.recommenders.utils.Pair;

public class TemporaryProject {

    private JavaProjectFixture jpf;

    protected TemporaryProject(IWorkspace ws) {
        this.jpf = new JavaProjectFixture(ws, RandomStringUtils.random(8));
    }

    public TemporaryFile createFile(CharSequence code) throws CoreException {
        Pair<ICompilationUnit, Set<Integer>> struct = jpf.createFileAndParseWithMarkers(code);

        return new TemporaryFile(struct.getFirst(), struct.getSecond().iterator().next());
    }
}
