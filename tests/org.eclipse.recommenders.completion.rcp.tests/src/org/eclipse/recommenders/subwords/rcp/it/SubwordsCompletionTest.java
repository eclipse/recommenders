
package org.eclipse.recommenders.subwords.rcp.it;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.eclipse.recommenders.internal.subwords.rcp.LCSS;
import org.hamcrest.BaseMatcher;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class SubwordsCompletionTest {

    private  final String token;
    private  final String firstCandidate;
    private  final String secondCandidate;

    //A three argument constructor that assign field's value
    public SubwordsCompletionTest ( String description, String token, String firstCandidate, String secondCandidate) {
       this.token = token;
       this.firstCandidate = firstCandidate;
       this.secondCandidate = secondCandidate;
    }

    //creates the test data: test consider the following sequence:token,firstCandidate,secondCandidate
    //Tests pass if the first candidate gives greater score than the second candidate
    //we consider prefix match
    //we consider match with camel case letters
    //In case of a two similar good match, we consider the distance also
    //we also consider the case that user can type the camel case letters in small letters also; Example: aal->addActionListener
    @Parameters (name = "{index}: Test Description: {0}")
    public static Collection<Object[]> createScenarios() {
            LinkedList<Object[]> scenarios  = Lists.newLinkedList();

            scenarios.add(scenario("prefix match", "gval", "gvalExample", "getFormattedValue"));
            scenarios.add(scenario("matches with camel case letters + shorter length", "gval", "getValue", "getValidators"));
            scenarios.add(scenario("matches with highest camel case letters", "gVal", "getValue", "gvalExample"));

            scenarios.add(scenario("Not a prefix match but camel case matches + shorter length", "getl", "getLiterals", "getLiteralExamples"));

            scenarios.add(scenario("matches with highest camel case letters", "gle", "getLiteralExamples", "getLiterals"));
            scenarios.add(scenario("matches with highest camel case letters", "gle", "getLiteralExamples", "getLiteralValues"));

            scenarios.add(scenario("matches with highest camel case letters", "glv", "getLiteralValues", "getLiteralExamples"));
            scenarios.add(scenario("matches with highest camel case letters", "gle", "getLiteralExamples", "getLiteralValues"));

            scenarios.add(scenario("prefix match + short length", "gr", "groff", "groflow"));
            scenarios.add(scenario("prefix match + short length", "gr", "groflow", "getRawValue"));
            scenarios.add(scenario("Not a prefx match but camel case match", "gR", "getRawValue", "groff"));
            scenarios.add(scenario("camel case match + short length", "gR", "getRawValue", "getRawValues"));
            scenarios.add(scenario("Not a prefix match but camel case match", "gr", "getRawValue", "getRawValues"));
            scenarios.add(scenario("Highest camel case letter matches", "grv", "getRawValue", "getRawData"));

            scenarios.add(scenario("prefix match", "gl", "glSomething", "getLiteralValue"));
            scenarios.add(scenario("camel case letter matches", "glv", "getLiteralValue", "glSomething"));

            scenarios.add(scenario("prefix match", "it", "iterator", "isType"));
            scenarios.add(scenario("Not a prefix match, but camel case letter match", "iT", "isType", "iterator"));

            scenarios.add(scenario("prefix match + shorter length", "get", "getFtp", "getFileTypeProvider"));
            scenarios.add(scenario("Not a prefix match but camel case letter matches", "getftp", "getFtp", "getFileTypeProvider"));
            scenarios.add(scenario("prefix match", "getFtp", "getFtp", "getFileTypeProvider"));
            scenarios.add(scenario("Not a prefix match, camel case letter matches", "getFTP", "getFileTypeProvider", "getFtp"));
            scenarios.add(scenario("Not a prefix match, camel case letter matches", "getFTp", "getFileTypeProvider", "getFtp"));
            scenarios.add(scenario("Prefix match", "getFT",  "getFTP", "getFileTypeProvider"));

            scenarios.add(scenario("Camel case letter matches", "getFTP", "getFileTypeprovider", "getConnectionProvider"));
            scenarios.add(scenario("Highest camel case letter matches", "getFTP", "getFileTypeprovider", "getFileTypeConnectionProvider"));

            scenarios.add(scenario("Camel case letter matches", "gb", "getBounds", "getbackground"));
            scenarios.add(scenario("prefix match", "gb", "gbColours", "getBounds"));
            scenarios.add(scenario("Not a prefix match, camel case letter matches", "gB", "gbColours", "getBounds"));
            scenarios.add(scenario("Not a prefix match, camel case letter matches", "gB", "getBound",  "gbColours"));

            scenarios.add(scenario("Camel case matches", "aml", "addMouseListener", "addInputMethodListener"));
            scenarios.add(scenario("Camel case letter matches", "aML", "addMouseListener", "addInputMethodListener"));

            scenarios.add(scenario("Highest camel case matches", "ia", "isActive", "isAlwaysOnTop"));
            scenarios.add(scenario("camel case matches", "ia", "isActive", "invalidate"));

            scenarios.add(scenario("Camel case letter matches", "ss", "setSize", "dispose"));
            scenarios.add(scenario("Camel case letter matches", "sS", "setSize", "dispose"));
            scenarios.add(scenario("Camel case letter matches", "ss", "setSize", "isShowing"));
            scenarios.add(scenario("character + camel case letter matches", "ss", "isShowing", "dispose"));

            scenarios.add(scenario("camel case letter matches", "aal", "addActionListener", "paintAll"));
            scenarios.add(scenario("camel case matches", "pa", "paintAll", "addActionListener"));

            scenarios.add(scenario("higghest camel case matches", "sl", "setLayout", "setLayeredPane"));
            scenarios.add(scenario("character + camel case matches", "setl", "setLayout", "setLayeredPane"));
            scenarios.add(scenario("prefix match + shorter length", "setL", "setLayout", "setLayeredPane"));
            scenarios.add(scenario("Highest camel case matcehes", "slp", "setLayeredPane", "setLayout"));

            scenarios.add(scenario("camel case matches", "sT", "setText", "list"));
            scenarios.add(scenario("camel case matches", "st", "setText", "list"));

            scenarios.add(scenario("shorter length + camel case matches", "ps", "isPreferredSize", "getPreferredSize"));
            scenarios.add(scenario("shorter length + camel case matches", "pS", "isPreferrredSize", "getPreferredSize"));
            scenarios.add(scenario("character match (g,e,t) + camel case matches", "getps", "getPreferredSize", "isPreferredSize"));
            scenarios.add(scenario("character match (g,e,t) + camel case matches", "getps", "getPreferredSize", "getSize"));

            scenarios.add(scenario("Not a prefix match, character match + camel case match", "setvg", "setVgap", "setHgap"));
            scenarios.add(scenario("camel case matches + shorter length", "gg", "getGraphics", "getDebugGraphicsOptions"));
            scenarios.add(scenario("character + camel case match", "iva", "isValid", "validate"));
            scenarios.add(scenario("prefix match", "va", "validate", "isValid"));

            return scenarios;
    }


    private static Object[] scenario(String description, String t, String fc, String sc) {
        return new Object[]{description, t, fc, sc};
    }

    public static Matcher greaterThan(final Object first) {
         return new BaseMatcher() {

            protected Object comparingTo = first;
            @Override
            public boolean matches(Object second) {
            // TODO Auto-generated method stub
                  if (((Integer) first) > ((Integer) second))
                      return true;
                  else return false;

            }

            @Override
            public void describeTo(Description description) {
            // TODO Auto-generated method stub
               description.appendText("Comparing To" + first);
            }
        };
    }

    @Test
    public void testRelevantPosition() {
        int score1 = LCSS.calculateBestScore(firstCandidate, token);
        int score2 = LCSS.calculateBestScore(secondCandidate, token);
        assertThat(score1, is(greaterThan(score2)));

    }

}
