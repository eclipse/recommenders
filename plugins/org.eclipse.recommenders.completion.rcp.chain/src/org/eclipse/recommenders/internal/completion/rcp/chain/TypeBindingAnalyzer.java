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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.recommenders.utils.Throws;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("restriction")
public final class TypeBindingAnalyzer {

    private static final Predicate<FieldBinding> NON_STATIC_FIELDS_ONLY_FILTER = new Predicate<FieldBinding>() {

        @Override
        public boolean apply(final FieldBinding m) {
            return m.isStatic();
        }
    };

    private static final Predicate<MethodBinding> RELEVANT_NON_STATIC_METHODS_ONLY_FILTER = new Predicate<MethodBinding>() {

        @Override
        public boolean apply(final MethodBinding m) {
            if (m.isStatic() || isVoid(m) || m.isConstructor() || hasPrimitiveReturnType(m)) {
                return true;
            }
            final String key = String.valueOf(m.computeUniqueKey());
            return key.startsWith("Ljava/lang/Object;") || key.startsWith("Ljava/lang/Class<");
        }
    };

    private static final Predicate<FieldBinding> STATIC_FIELDS_ONLY_FILTER = new Predicate<FieldBinding>() {

        @Override
        public boolean apply(final FieldBinding m) {
            return !m.isStatic();
        }
    };

    private static final Predicate<MethodBinding> STATIC_NON_VOID_NON_PRIMITIVE_METHODS_ONLY_FILTER = new Predicate<MethodBinding>() {

        @Override
        public boolean apply(final MethodBinding m) {
            return !m.isStatic() || isVoid(m) || m.isConstructor() || hasPrimitiveReturnType(m);
        }
    };

    private TypeBindingAnalyzer() {
    }

    private static boolean isVoid(final MethodBinding m) {
        return hasPrimitiveReturnType(m) && m.returnType.constantPoolName()[0] == 'V';
    }

    static boolean hasPrimitiveReturnType(final MethodBinding m) {
        return m.returnType.constantPoolName().length == 1;
    }

    public static Collection<Binding> findVisibleInstanceFieldsAndRelevantInstanceMethods(final TypeBinding type,
            final IType contextEnclosingType) {
        return findFieldsAndMethods(type, contextEnclosingType, NON_STATIC_FIELDS_ONLY_FILTER,
                RELEVANT_NON_STATIC_METHODS_ONLY_FILTER);
    }

    public static Collection<Binding> findAllPublicStaticFieldsAndNonVoidNonPrimitiveStaticMethods(
            final TypeBinding type, final IType contextEnclosingType) {
        return findFieldsAndMethods(type, contextEnclosingType, STATIC_FIELDS_ONLY_FILTER,
                STATIC_NON_VOID_NON_PRIMITIVE_METHODS_ONLY_FILTER);
    }

    private static Collection<Binding> findFieldsAndMethods(final TypeBinding type, final IType contextEnclosingType,
            final Predicate<FieldBinding> fieldFilter, final Predicate<MethodBinding> methodFilter) {
        final Map<String, Binding> tmp = Maps.newLinkedHashMap();
        final boolean isEverythingVisible = isVisibleFromCompletionContext(contextEnclosingType, type);
        for (final ReferenceBinding cur : findAllSupertypesIncludeingArgument(type)) {
            for (final MethodBinding method : cur.methods()) {
                if (methodFilter.apply(method) || !isEverythingVisible && !method.isPublic()) {
                    continue;
                }
                final String key = createMethodKey(method);
                if (!tmp.containsKey(key)) {
                    tmp.put(key, method);
                }
            }
            for (final FieldBinding field : cur.fields()) {
                if (fieldFilter.apply(field) || !isEverythingVisible && !field.isPublic()) {
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

    private static boolean isVisibleFromCompletionContext(final IType contextEnclosingType, final TypeBinding element) {
        // TODO Make proper visibility check.
        final String key = StringUtils.removeEnd(contextEnclosingType.getKey(), ";");
        return String.valueOf(element.signature()).startsWith(key);
    }

    private static List<ReferenceBinding> findAllSupertypesIncludeingArgument(final TypeBinding type) {
        final TypeBinding base = removeArrayWrapper(type);
        if (!(base instanceof ReferenceBinding)) {
            return Collections.emptyList();
        }
        final List<ReferenceBinding> supertypes = Lists.newLinkedList();
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
            throw Throws.throwUnhandledException(e);
        }
    }

    public static boolean isAssignable(final MemberEdge edge, final TypeBinding expectedType,
            final int expectedDimension) {
        if (expectedDimension <= edge.getDimension()) {
            final TypeBinding base = removeArrayWrapper(edge.getReturnType());
            if (base instanceof BaseTypeBinding) {
                return false;
            }
            // TODO: This check should be sufficient once every TypeBinding is resolved.
            if (base.isCompatibleWith(expectedType)) {
                return true;
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

}
