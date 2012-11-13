package org.eclipse.recommenders.internal.completion.rcp;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.junit.Test;

import com.google.common.base.Optional;

public class BaseRecommendersCompletionContextTest {

    BaseRecommendersCompletionContextStub sut = new BaseRecommendersCompletionContextStub();

    @Test
    public void smoke() {
        sut.createTypeNamesFromKeys(new char[][] {
                // primitive:
                "I".toCharArray(),
                // normal type:
                "Ljava/lang/String;".toCharArray(),
                // generic:
                "Lcom/codetrails/analysis/GenericWalaBasedArtifactAnalyzer;:TT".toCharArray(), });
    }

    private final class BaseRecommendersCompletionContextStub extends BaseRecommendersCompletionContext {

        @Override
        public Optional<ASTNode> getCompletionNode() {
            return null;
        }

        @Override
        public Optional<ASTNode> getCompletionNodeParent() {
            return null;
        }

        @Override
        public List<IField> getVisibleFields() {
            return null;
        }

        @Override
        public List<ILocalVariable> getVisibleLocals() {
            return null;
        }

        @Override
        public List<IMethod> getVisibleMethods() {
            return null;
        }

        public Set<ITypeName> createTypeNamesFromKeys(char[][] keys) {
            return super.createTypeNamesFromKeys(keys);
        }
    }

}
