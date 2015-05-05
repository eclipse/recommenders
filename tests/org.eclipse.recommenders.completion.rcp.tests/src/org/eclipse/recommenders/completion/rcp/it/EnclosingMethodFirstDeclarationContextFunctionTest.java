package org.eclipse.recommenders.completion.rcp.it;

import static org.eclipse.recommenders.completion.rcp.CompletionContextKey.ENCLOSING_METHOD_FIRST_DECLARATION;
import static org.eclipse.recommenders.completion.rcp.it.TestUtils.createRecommendersCompletionContext;
import static org.eclipse.recommenders.testing.CodeBuilder.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.rcp.JavaElementResolver;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class EnclosingMethodFirstDeclarationContextFunctionTest {

    private static final IMethodName OBJECT_HASH_CODE = VmMethodName.get("Ljava/lang/Object.hashCode()I");
    private static final IMethodName RUNNABLE_RUN = VmMethodName.get("Ljava/lang/Runnable.run()V");

    private final JavaElementResolver resolver = new JavaElementResolver();
    private final CharSequence code;
    private final IMethodName expectedMethod;

    public EnclosingMethodFirstDeclarationContextFunctionTest(CharSequence code, IMethodName expectedMethod) {
        this.code = code;
        this.expectedMethod = expectedMethod;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(scenario(classbody("public int hashCode() { $; }"), OBJECT_HASH_CODE));
        scenarios.add(scenario(classbody("TestClass implements Runnable", "public void run() { $; }"), RUNNABLE_RUN));

        scenarios.add(scenario(method("new Thread(() -> $);"), RUNNABLE_RUN));

        return scenarios;
    }

    private static Object[] scenario(CharSequence code, IMethodName expectedMethod) {
        return new Object[] { code, expectedMethod };
    }

    @Test
    public void test() throws Exception {
        IRecommendersCompletionContext sut = createRecommendersCompletionContext(code);

        IMethod result = sut.get(ENCLOSING_METHOD_FIRST_DECLARATION, null);
        IMethodName method = resolver.toRecMethod(result).orNull();

        assertThat(method, is(equalTo(expectedMethod)));
    }
}
