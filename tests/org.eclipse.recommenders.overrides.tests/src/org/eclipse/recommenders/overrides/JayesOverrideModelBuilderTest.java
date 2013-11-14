package org.eclipse.recommenders.overrides;

import static org.eclipse.recommenders.utils.names.VmTypeName.OBJECT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.google.common.collect.ImmutableList;

public class JayesOverrideModelBuilderTest {

    private static final IMethodName OBJECT_HASHCODE = VmMethodName.get("Ljava/lang/Object.hashCode()I");
    private static final IMethodName OBJECT_WAIT = VmMethodName.get("Ljava/lang/Object.wait()V");

    @Test(expected = IllegalArgumentException.class)
    public void testNoObservation() {
        @SuppressWarnings("unused")
        JayesOverrideModelBuilder sut = new JayesOverrideModelBuilder(OBJECT,
                Collections.<OverrideObservation>emptyList());
    }

    @Test
    public void testOneObservation() {
        JayesOverrideModelBuilder sut = new JayesOverrideModelBuilder(OBJECT, ImmutableList.of(observation(1,
                OBJECT_HASHCODE)));
        IOverrideModel model = sut.build();

        assertThat(model.getKnownMethods().size(), is(1));
        assertThat(model.getKnownMethods(), CoreMatchers.hasItem(OBJECT_HASHCODE));
    }

    OverrideObservation observation(int frequency, IMethodName... overriddenMethods) {
        OverrideObservation observation = new OverrideObservation();
        observation.frequency = frequency;
        observation.overriddenMethods.addAll(Arrays.asList(overriddenMethods));
        return observation;
    }
}
