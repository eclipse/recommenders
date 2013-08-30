package org.eclipse.recommenders.internal.calls.rcp.templates;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
import static org.eclipse.recommenders.utils.names.VmTypeName.OBJECT;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.recommenders.utils.Checks;
import org.eclipse.recommenders.utils.Throws;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.Names;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;

public class TemplateBuilder {

    private static final class ToTemplateTypeNames implements Function<ITypeName, String> {
        @Override
        public String apply(ITypeName t) {
            String res = Names.vm2srcQualifiedType(t);
            if (t.isArrayType()) {
                res = "'" + res + "'";
            }
            return res;
        }
    }

    private static final ToTemplateTypeNames TO_TEMPLATE_TYPE_NAMES = new ToTemplateTypeNames();

    private final Multiset<String> argumentCounter = HashMultiset.create();
    private final StringBuilder builder = new StringBuilder();

    /**
     * Appends the give string to the template.
     * 
     * @return
     */
    public TemplateBuilder append(String code) {
        builder.append(code);
        return this;
    }

    /**
     * appends ${id:type(someValue)}
     * 
     * @return
     */
    public TemplateBuilder appendCommand(String id, String commandId, String value) {
        builder.append("${").append(id).append(":").append(commandId).append("(").append(value).append(")}");
        return this;
    }

    /**
     * appends ${id:type(someValue,nextValue,othervalue)}
     * 
     * @return
     */
    public TemplateBuilder appendCommand(String id, String commandId, ITypeName... types) {
        return appendCommand(id, commandId, toLiterals(types));
    }

    public TemplateBuilder appendCommand(String id, String commandId, String... values) {
        return appendCommand(id, commandId, Arrays.asList(values));
    }

    /**
     * appends ${id:type(someValue,otherValue,nextValue)}
     * 
     * @return
     */
    public TemplateBuilder appendCommand(String id, String commandId, Iterable<String> values) {
        builder.append("${").append(id).append(":").append(commandId).append("(");
        Joiner.on(',').appendTo(builder, values);
        builder.append(")}");
        return this;
    }

    /**
     * Evaluates to the nth type argument of the referenced template variable. The reference should be the name of
     * another template variable. Resolves to java.lang.Object if the referenced variable cannot be found or is not a
     * parameterized type.
     * <p>
     * Example:
     * 
     * <pre>
     * ${type:argType(vector, 0)} ${first:name(type)} = ${vector:var(java.util.Vector)}.get(0)
     * </pre>
     * 
     * @return
     */
    public TemplateBuilder argType(String id, String variable, int n) {
        return appendCommand(id, "argType", variable, String.valueOf(n));
    }

    /**
     * Evaluates to a proposal for an array visible in the current scope.
     * 
     * @return
     */

    public TemplateBuilder array() {
        return append("${array}");
    }

    /**
     * Evaluates to a name for a new local variable for an element of the ${array} variable match.
     * 
     * @return
     */
    public TemplateBuilder arrayElement() {
        return append("${array_element}");
    }

    /**
     * Evaluates to the element type of the ${array} variable match.
     * 
     * @return
     */

    public TemplateBuilder arrayType() {
        return append("${array_type}");
    }

    /**
     * Evaluates to a proposal for a collection visible in the current scope.
     * 
     * @return
     */
    public TemplateBuilder collection() {
        return append("${collection}");
    }

    /**
     * Specifies the cursor position when the template edit mode is left. This is useful when the cursor should jump to
     * another place than to the end of the template on leaving template edit mode.
     * 
     * @return
     */
    public TemplateBuilder cursor() {
        return append("${cursor}");
    }

    /**
     * Evaluates to the current date.
     * 
     * @return
     */
    public TemplateBuilder date() {
        return append("${date}");
    }

    /**
     * Evaluates to the dollar symbol $. Alternatively, two dollars can be used: $$.
     * 
     * @return
     */
    public TemplateBuilder dollar() {
        return append("${dollar}");
    }

    /**
     * Evaluates to the element type of the referenced template variable. The reference should be the name of another
     * template variable that resolves to an array or an instance of java.lang.Iterable. The elemType variable type is
     * similar to ${id:argType(reference,0)}, the difference being that it also resolves the element type of an array.
     * <p>
     * ${array_type} is a shortcut for ${array_type:elemType(array)}.<br>
     * ${iterable_type} is a shortcut for ${iterable_type:elemType(iterable)}.
     * 
     * @return
     */
    public TemplateBuilder elemType(String id, String variable) {
        return appendCommand(id, "elemType", variable);
    }

    /**
     * Evaluates to the name of the enclosing method.
     * 
     * @return
     */
    public TemplateBuilder enclosingMethod() {
        return append("${enclosing_method}");
    }

    /**
     * Evaluates to a comma separated list of argument names of the enclosing method. This variable can be useful when
     * generating log statements for many methods.
     */
    public TemplateBuilder EnclosingMethodArguments() {
        return append("${enclosing_method_arguments}");
    }

    /**
     * Evaluates to the name of the enclosing package.
     * 
     * @return
     */
    public TemplateBuilder enclosingPackage() {
        return append("${enclosing_package}");
    }

    /**
     * Evaluates to the name of the enclosing project.
     * 
     * @return
     */
    public TemplateBuilder enclosingProject() {
        return append("${enclosing_project}");
    }

    /**
     * Evaluates to the name of the enclosing type.
     * 
     * @return
     */
    public TemplateBuilder enclosingType() {
        return append("${enclosing_type}");
    }

    /**
     * Exception variable name in catch blocks.
     * 
     * @return
     */
    public TemplateBuilder exceptionVariableName() {
        return append("${exception_variable_name}");
    }

    /**
     * Evaluates to a field in the current scope that is a subtype of any of the given types. If no type is specified,
     * any non-primitive field matches.
     * <p>
     * Example:
     * 
     * <pre>
     * ${count:field(int)}
     * </pre>
     * 
     * @return
     */
    public TemplateBuilder field(String id, ITypeName... types) {
        return appendCommand(id, "field", types);
    }

    /**
     * Evaluates to the name of the file.
     * 
     * @return
     */
    public TemplateBuilder file() {
        return append("${file}");
    }

    /**
     * Adds an import statement for each type. Does nothing if the import already exists. Does nothing if a conflicting
     * import exists. Evaluates to nothing.
     * <p>
     * Example:
     * 
     * <pre>
     * ${:import(java.util.List, java.util.Collection)}
     * </pre>
     * 
     * @return
     */
    public TemplateBuilder imports(ITypeName... types) {
        return imports("", types);
    }

    public TemplateBuilder imports(String id, ITypeName... types) {
        return appendCommand(id, "import", types);
    }

    /**
     * Adds a static import statement for each qualified name that is not already imported. The qualifiedName is the
     * fully qualified name of a static field or method, or it is the qualified name of a type plus a .* suffix,
     * enclosed in single quotes ''. Does nothing if a conflicting import exists. Evaluates to nothing.
     * <p>
     * Example:
     * 
     * <pre>
     * ${is:importStatic(java.util.Collections.EMPTY_SET, 'java.lang.System.*')}
     * </pre>
     */
    public TemplateBuilder importStatic(String id, IMethodName names) {
        throw Throws.throwUnsupportedOperation();
        // ${:importStatic([qualifiedName[,qualifiedName]*])}
    }

    /**
     * Evaluates to a proposal for an undeclared array index.
     * 
     * @return
     */
    public TemplateBuilder index() {
        return append("${index}");
    }

    /**
     * Evaluates to a proposal for an iterable or array visible in the current scope.
     * 
     * @return
     */
    public TemplateBuilder iterable() {
        return append("${iterable}");
    }

    /**
     * Evaluates to a name for a new local variable for an element of the ${iterable} variable match.
     */
    public TemplateBuilder iterableElement() {
        return append("${iterable_element}");
    }

    /**
     * Evaluates to the element type of the ${iterable} variable match.
     */
    public TemplateBuilder iterableType() {
        return append("${iterable_type}");
    }

    /**
     * Evaluates to an unused name for a new local variable of type java.util.Iterator.
     */
    public TemplateBuilder iterator() {
        return append("${iterator}");
    }

    /**
     * Evaluates to content of all currently selected lines.
     */
    public TemplateBuilder lineSelection() {
        return append("${line_selection}");
    }

    /**
     * Evaluates to id if the list of proposals is empty, evaluates to the first proposal otherwise. The evaluated value
     * is put into linked mode. A proposal window shows all the given proposals.
     * <p>
     * Example:
     * 
     * <pre>
     * java.util.Collections.${kind:link(EMPTY_SET, EMPTY_LIST, EMPTY_MAP)}
     * </pre>
     * 
     */
    public TemplateBuilder link(String id, String... proposals) {
        return appendCommand(id, "link", proposals);
    }

    /**
     * Evaluates to a local variable or parameter visible in the current scope that is a subtype of any of the given
     * type. If no type is specified, any non-primitive local variable matches.
     * <p>
     * ${array} is a shortcut for ${array:localVar('java.lang.Object[]')}, but also matches arrays of primitive types.<br>
     * ${collection} is a shortcut for ${collection:localVar(java.util.Collection)}. ${iterable} is a shortcut for
     * ${iterable:localVar(java.lang.Iterable)}, but also matches arrays.
     */
    public TemplateBuilder localVar(String id, ITypeName... types) {
        return appendCommand(id, "localVar", types);
    }

    /**
     * Evaluates to an non-conflicting name for a new local variable of the type specified by the reference. The
     * reference may either be a Java type name or the name of another template variable. The generated name respects
     * the code style settings.
     * <p>
     * ${index} is a shortcut for ${index:newName(int)}.<br>
     * ${iterator} is a shortcut for ${iterator:newName(java.util.Iterator)}.<br>
     * ${array_element} is a shortcut for ${array_element:newName(array)}.<br>
     * ${iterable_element} is a shortcut for ${iterable_element:newName(iterable)}.<br>
     */
    public TemplateBuilder newName(String id, ITypeName reference) {
        return appendCommand(id, "newName", reference);
    }

    public TemplateBuilder newType(String id, ITypeName type) {
        return appendCommand(id, "newType", toLiteral(type));
    }

    /**
     * Evaluates to the name primary type of the current compilation unit.
     */
    public TemplateBuilder primaryTypeName() {
        return append("${primary_type_name}");
    }

    /**
     * Evaluates to the return type of the enclosing method.
     */
    public TemplateBuilder returnType() {
        return append("${return_type}");
    }

    /**
     * Suggests a free variable id based on the given method. For constructors it suggests the name of the declaring
     * class, for any other method names it returns the method name excluding a "get" prefix if available.
     */
    public String suggestId(IMethodName method) {
        String varName = null;
        if (method.isInit()) {
            varName = method.getDeclaringType().getClassName();
        } else {
            String name = method.getName();
            // we have methods like List.get(23). Handle that:
            int start = name.startsWith("get") && !name.equals("get") ? 3 : 0;
            varName = StringUtils.substring(name, start);
        }
        varName = varName.replaceAll("\\W", "").toLowerCase();
        return suggestId(varName);
    }

    private String suggestId(String varName) {
        String suffix = argumentCounter.add(varName, 1) == 0 ? "" : "" + (argumentCounter.count(varName) - 1);
        return varName + suffix;
    }

    /**
     * Evaluates to the current time.
     */
    public TemplateBuilder time() {
        return append("${time}");
    }

    /**
     * Evaluates to a proposal for the currently specified default task tag.
     */
    public TemplateBuilder todo() {
        return append("${todo}");
    }

    public String toLiteral(ITypeName type) {
        return TO_TEMPLATE_TYPE_NAMES.apply(type);
    }

    public Iterable<String> toLiterals(ITypeName... types) {
        return Iterables.transform(Arrays.asList(types), TO_TEMPLATE_TYPE_NAMES);
    }

    /**
     * Returns the contents of the template.
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * Evaluates to the user name.
     */
    public TemplateBuilder user() {
        return append("${user}");
    }

    /**
     * Evaluates to a field, local variable or parameter visible in the current scope that is a subtype of any of the
     * given types. If no type is specified, any non-primitive variable matches.
     * <p>
     * Example:
     * 
     * <pre>
     * ${container:var(java.util.List, 'java.lang.Object[]')}
     * </pre>
     */
    public TemplateBuilder var(String id, ITypeName... types) {
        return appendCommand(id, "var", types);
    }

    /**
     * Evaluates to the content of the current text selection.
     */
    public TemplateBuilder wordSelection() {
        return append("${word_selection}");
    }

    /**
     * Evaluates to the current year.
     */
    public TemplateBuilder year() {
        return append("${year}");
    }

    /**
     * Creates
     * "${buttonType:newType(org.eclipse.swt.widgets.Button)} ${button:newName(org.eclipse.swt.widgets.Button)}= new ${buttonType}(${parent:var(org.eclipse.swt.widgets.Composite)}, ${style:link(SWT.PUSH, SWT.TOGGLE, SWT.RADIO, SWT.CHECK, SWT.FLAT)});"
     * 
     * @return a variable string in "${generated-id}" format
     */
    public String appendCtor(IMethodName ctor, String... argNames) {
        Checks.ensureIsTrue(ctor.isInit());
        ITypeName type = ctor.getDeclaringType();
        String receiverId = suggestId(ctor);
        String receiverTypeId = receiverId + "Type";

        newType(receiverTypeId, type).ws().newName(receiverId, type).ws().eq().ws().new_().ws().ref(receiverTypeId)
                .append("(");
        appendParameters(ctor, argNames);
        append(")").sc().nl();
        return "${" + receiverId + "}";
    }

    public Optional<String> appendCall(IMethodName method, String receiver, String... argNames) {
        Checks.ensureIsFalse(method.isInit(), "method must not be a constructor");
        String returnId = null;
        if (!method.isVoid()) {
            ITypeName type = method.getReturnType();
            String defId = suggestId(method);
            newType(defId + "Type", type).ws();
            newName(defId, type).ws().eq().ws();
            returnId = "${" + defId + "}";
        }
        if (!isEmpty(receiver)) {
            append(receiver).dot();
        }
        append(method.getName()).append("(").appendParameters(method, argNames).append(")").sc().nl();
        return Optional.fromNullable(returnId);
    }

    public TemplateBuilder appendParameters(IMethodName method, String... argNames) {
        ITypeName[] argTypes = method.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            String arg = argNames[i];
            appendVariable(argTypes[i], arg);
            if (i < argTypes.length - 1) {
                c().ws();
            }
        }
        return this;
    }

    private void appendVariable(ITypeName type, String id) {
        if (isVariable(id)) {
            ref(id);
        } else if (OBJECT.equals(type)) {
            ref(suggestId(id));
        } else {
            var(suggestId(id), type);
        }
    }

    private boolean isVariable(String arg) {
        return arg.startsWith("${") && arg.endsWith("}");
    }

    public TemplateBuilder c() {
        return append(",");
    }

    public TemplateBuilder ws() {
        return append(" ");
    }

    public TemplateBuilder dot() {
        return append(".");
    }

    public TemplateBuilder sc() {
        return append(";");
    }

    public TemplateBuilder nl() {
        return append(LINE_SEPARATOR);
    }

    public TemplateBuilder new_() {
        return append("new");
    }

    public TemplateBuilder ref(String referencedVariable) {
        builder.append("${").append(referencedVariable).append("}");
        return this;
    }

    public TemplateBuilder eq() {
        return append("=");
    }

    // public Optional<String> addMethodCall(String receiver, IMethodName method, String... parameterNames) {
    // Checks.ensureIsFalse(method.isInit(), "constructors not allowed");
    // String result = null;
    // if (!method.isVoid()) {
    // result = newVariableDeclaration(method);
    // }
    // appendVariableDeclaration(method);
    // appendInvocationPrefix(method);
    // appendMethodCall(method);
    // builder.append(";");
    // builder.append(LINE_SEPARATOR);
    // return fromNullable(result);
    // }
    //
    // private String newVariableDeclaration(IMethodName method) {
    // ITypeName returnType = method.getReturnType();
    // String varName = newVarname(method);
    // builder.append(format("${%s:newType(%s)}%s", varName, returnType, ""));
    // return varName;
    // }

    // private void appendVariableDeclaration(final IMethodName method) {
    // if (method.isInit()) {
    // builder.append(format("${constructedType:newType(%s)}", getTypeIdentifier(method.getDeclaringType())));
    // builder.append(" ");
    // builder.append(getNewVariableNameFromMethod(method));
    // builder.append(" = ");
    // } else if (!method.isVoid()) {
    // String returnType = Names.vm2srcQualifiedType(method.getReturnType());
    // if (method.getReturnType().isPrimitiveType()) {
    // builder.append(returnType);
    // } else {
    // final String returnTypeTemplateVariable = returnType.replaceAll("\\W", "_");
    // builder.append(format("${%s:newType(%s)}%s", returnTypeTemplateVariable, returnType, ""));
    // }
    // builder.append(" ");
    // builder.append(getNewVariableNameFromMethod(method));
    // builder.append(" = ");
    // }
    // }
    //
    // private void appendInvocationPrefix(final IMethodName method) {
    // if (method.isInit()) {
    // builder.append("new ");
    // } else if (!variableName.isEmpty()) {
    // builder.append(variableName);
    // builder.append(".");
    // }
    // }
    //
    // private void appendMethodCall(final IMethodName method) {
    // if (method.isInit()) {
    // builder.append("${constructedType}");
    // } else {
    // builder.append(method.getName());
    // }
    // appendParameters(method);
    // }
    //
    // private void appendParameters(final IMethodName method) {
    // Optional<IMethod> jdtMethod = resolver.toJdtMethod(method);
    // ITypeName[] parameterTypes = method.getParameterTypes();
    // String[] parameterNames = new String[parameterTypes.length];
    // if (jdtMethod.isPresent()) {
    // try {
    // parameterNames = jdtMethod.get().getParameterNames();
    // } catch (JavaModelException e) {
    // // TODO duplicated code
    // for (int i = 0; i < parameterNames.length; i++) {
    // parameterNames[i] = "arg" + i;
    // }
    // }
    // } else {
    // for (int i = 0; i < parameterNames.length; i++) {
    // parameterNames[i] = "arg" + i;
    // }
    // }
    // builder.append("(");
    // for (int i = 0; i < parameterNames.length; ++i) {
    // appendParameter(parameterNames[i], parameterTypes[i]);
    // if (i + 1 < parameterNames.length) {
    // builder.append(", ");
    // }
    // }
    // builder.append(")");
    // }
    //
    // private void appendParameter(final String parameterName, final ITypeName parameterType) {
    // String appendix = "";
    // // TODO: Appendix for more types, add array support.
    // if (parameterType.isDeclaredType()) {
    // if (!parameterType.getIdentifier().startsWith("Ljava")) {
    // final String typeName = Names.vm2srcTypeName(parameterType.getIdentifier());
    // appendix = String.format(":var(%s)", typeName);
    // }
    // } else if (parameterType == VmTypeName.BOOLEAN) {
    // appendix = ":link(false, true)";
    // } else if (parameterType == VmTypeName.INT || parameterType == VmTypeName.DOUBLE
    // || parameterType == VmTypeName.FLOAT || parameterType == VmTypeName.LONG
    // || parameterType == VmTypeName.SHORT) {
    // appendix = ":link(0)";
    // }
    // builder.append(String.format("${%s%s}", getParameterName(parameterName), appendix));
    // }
    //
    // private String getParameterName(final String parameterName) {
    // final String name = parameterName.length() <= 5 && parameterName.startsWith("arg") ? "arg" : parameterName;
    // if (argumentCounter.containsKey(name)) {
    // final Integer counter = argumentCounter.get(name);
    // argumentCounter.put(name, counter + 1);
    // return String.format("%s%s", name, counter + 1);
    // } else {
    // argumentCounter.put(name, 1);
    // }
    // return name;
    // }
    //
    // private String getNewVariableNameFromMethod(final IMethodName method) {
    // if (method.isInit()) {
    // variableName = "${unconstructed}";
    // return String.format("${unconstructed:newName(%s)}", getTypeIdentifier(method.getDeclaringType()));
    // } else if (!method.isVoid()) {
    // int startIndex = method.getName().startsWith("get") ? 3 : 0;
    // return StringUtils.uncapitalize(method.getName().substring(startIndex));
    // } else {
    // throw new IllegalStateException();
    // }
    // }
    //
    // public String build() {
    // return String.format("%s${cursor}", builder.toString());
    // }
    //
    // private String getTypeIdentifier(final ITypeName typeName) {
    // return typeName.isPrimitiveType() ? Names.vm2srcSimpleTypeName(typeName) : typeName.getIdentifier()
    // .replace('/', '.').substring(1);
    // }
}