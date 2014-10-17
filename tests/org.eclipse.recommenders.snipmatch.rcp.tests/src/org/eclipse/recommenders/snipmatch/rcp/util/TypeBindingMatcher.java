package org.eclipse.recommenders.snipmatch.rcp.util;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.mockito.ArgumentMatcher;

public class TypeBindingMatcher extends ArgumentMatcher<ITypeBinding> {

    private String typeName;

    public TypeBindingMatcher(String typeName) {
        this.typeName = typeName;
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean matches(Object argument) {
        if (argument instanceof ITypeBinding) {
            ITypeBinding type = (ITypeBinding) argument;
            String name = type.getName();
            if (name.equals(typeName)) {
                return true;
            }
        }
        return false;
    }

}
