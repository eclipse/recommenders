package org.eclipse.recommenders.overrides;

import static org.eclipse.recommenders.utils.names.VmTypeName.OBJECT;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.junit.Test;

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
        sut.createPatternsNode();
        sut.createMethodNodes();
        IOverrideModel model = sut.build();

        assertThat(model.getKnownMethods().size(), is(1));
        assertThat(model.getKnownMethods(), hasItem(OBJECT_HASHCODE));

        assertThat(model.getKnownPatterns().size(), is(2));
    }

    @Test
    public void testTwoObservations() {
        JayesOverrideModelBuilder sut = new JayesOverrideModelBuilder(OBJECT, ImmutableList.of(
                observation(2, OBJECT_HASHCODE), observation(1, OBJECT_HASHCODE, OBJECT_WAIT)));
        sut.createPatternsNode();
        sut.createMethodNodes();
        IOverrideModel model = sut.build();

        assertThat(model.getKnownMethods().size(), is(2));
        assertThat(model.getKnownMethods(), hasItem(OBJECT_HASHCODE));
        assertThat(model.getKnownMethods(), hasItem(OBJECT_WAIT));

        assertThat(model.getKnownPatterns().size(), is(3));
    }

    private OverrideObservation observation(int frequency, IMethodName... overriddenMethods) {
        OverrideObservation observation = new OverrideObservation();
        observation.frequency = frequency;
        observation.overriddenMethods.addAll(Arrays.asList(overriddenMethods));
        return observation;
    }
}
