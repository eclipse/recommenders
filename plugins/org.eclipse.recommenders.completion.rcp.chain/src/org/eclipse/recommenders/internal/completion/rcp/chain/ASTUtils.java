/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henß - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import static org.eclipse.recommenders.utils.Throws.throwUnhandledException;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class ASTUtils {

    private static Predicate<FieldBinding> STATIC_PUBLIC_FIELDS_ONLY_FILTER = new Predicate<FieldBinding>() {

        @Override
        public boolean apply(final FieldBinding m) {
            return !m.isStatic() || !m.isPublic();
        }
    };

    private static Predicate<MethodBinding> STATIC_NON_VOID_NON_PRIMITIVE_PUBLIC_METHODS_FILTER = new Predicate<MethodBinding>() {

        @Override
        public boolean apply(final MethodBinding m) {
            return !m.isStatic() || isVoid(m) || !m.isPublic() || hasPrimitiveReturnType(m);
        }
    };

    /**
     * Returns a list of all public static fields and methods declared in the given class or any of its super-classes.
     */
    public static Collection<Binding> findAllPublicStaticFieldsAndNonVoidNonPrimitiveStaticMethods(
            final TypeBinding type) {
        return findAllRelevanFieldsAndMethods(type, STATIC_PUBLIC_FIELDS_ONLY_FILTER,
                STATIC_NON_VOID_NON_PRIMITIVE_PUBLIC_METHODS_FILTER);
    }

    public static Collection<Binding> findAllRelevanFieldsAndMethods(final TypeBinding type,
            final Predicate<FieldBinding> fieldFilter, final Predicate<MethodBinding> methodFilter) {
        final LinkedHashMap<String, Binding> tmp = new LinkedHashMap<String, Binding>();
        for (final ReferenceBinding cur : findAllSupertypesIncludeingArgument(type)) {
            for (final MethodBinding method : cur.methods()) {
                if (methodFilter.apply(method)) {
                    continue;
                }
                final String key = createMethodKey(method);
                if (!tmp.containsKey(key)) {
                    tmp.put(key, method);
                }
            }
            for (final FieldBinding field : cur.fields()) {
                if (fieldFilter.apply(field)) {
                    continue;
                }
                final String key = createFieldKey(field);
                if (!tmp.containsKey(key)) {
                    tmp.put(key, field);
                }
            }
        }

        return tmp.values();
    }

    public static Collection<Binding> findAllPublicInstanceFieldsAndNonVoidNonPrimitiveInstanceMethods(
            final TypeBinding type) {
        final LinkedHashMap<String, Binding> tmp = new LinkedHashMap<String, Binding>();

        for (final ReferenceBinding cur : findAllSupertypesIncludeingArgument(type)) {
            for (final MethodBinding m : cur.methods()) {
                if (isVoid(m) || !m.isPublic() || m.isConstructor() || m.isStatic() || hasPrimitiveReturnType(m)) {
                    continue;
                }
                final String key = createMethodKey(m);
                if (!tmp.containsKey(key)) {
                    tmp.put(key, m);
                }
            }
            for (final FieldBinding field : cur.fields()) {
                if (!field.isPublic() || field.isStatic()) {
                    continue;
                }
                final String key = createFieldKey(field);
                if (!tmp.containsKey(key)) {
                    tmp.put(key, field);
                }
            }
        }
        return tmp.values();
    }

    public static boolean isAssignable(final MemberEdge edge, final TypeBinding expectedType,
            final int expectedDimension) {
        if (expectedDimension <= edge.getDimension()) {
            final TypeBinding base = removeArrayWrapper(edge.getReturnType());
            if (base instanceof BaseTypeBinding) {
                return false;
            }
            final LinkedList<ReferenceBinding> supertypes = Lists.newLinkedList();
            supertypes.add((ReferenceBinding) removeArrayWrapper(edge.getReturnType()));
            final String expectedSignature = String.valueOf(expectedType.signature());
            while (!supertypes.isEmpty()) {
                final ReferenceBinding type = supertypes.poll();
                if (String.valueOf(type.signature()).equals(expectedSignature)) {
                    return true;
                }
                final ReferenceBinding superclass = type.superclass();
                if (superclass != null) {
                    supertypes.add(superclass);
                }
                for (final ReferenceBinding intf : type.superInterfaces()) {
                    supertypes.add(intf);
                }
            }
        }
        return false;
    }

    public static TypeBinding removeArrayWrapper(final TypeBinding type) {
        TypeBinding base = type;
        while (base instanceof ArrayBinding) {
            base = ((ArrayBinding) base).elementsType();
        }
        return base;
    }

    private static List<ReferenceBinding> findAllSupertypesIncludeingArgument(final TypeBinding type) {
        final TypeBinding base = removeArrayWrapper(type);
        if (!(base instanceof ReferenceBinding)) {
            return Collections.emptyList();
        }
        final List<ReferenceBinding> supertypes = new LinkedList<ReferenceBinding>();
        ReferenceBinding superclass = (ReferenceBinding) base;
        while (superclass != null) {
            supertypes.add(superclass);
            superclass = superclass.superclass();
        }
        return supertypes;
    }

    private static String createFieldKey(final FieldBinding field) {
        return new StringBuilder().append(field.name).append(field.type.signature()).toString();
    }

    private static String createMethodKey(final MethodBinding method) {
        try {
            final String signature = String.valueOf(method.signature());
            final String signatureWithoutReturnType = StringUtils.substringBeforeLast(signature, ")");
            return new StringBuilder().append(method.readableName()).append(signatureWithoutReturnType).toString();
        } catch (final Exception e) {
            throw throwUnhandledException(e);
        }
    }

    static boolean isVoid(final MethodBinding m) {
        return hasPrimitiveReturnType(m) && m.returnType.constantPoolName()[0] == 'V';
    }

    static boolean hasPrimitiveReturnType(final MethodBinding m) {
        return m.returnType.constantPoolName().length == 1;
    }

}
