
package org.eclipse.recommenders.internal.subwords.rcp;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.CoreMatchers.*;

public class SubwordsCompletionTest {

    public String token;
    public String firstCandidate;
    public String secondCandidate;

    //creates the test data: test consider the following sequence:token,firstCandidate,secondCandidate
    //Tests pass if the first candidate gives greater score than the second candidate
    //we consider prefix match
    //we consider match with camel case letters
    //In case of a two similar good match, we consider the distance also
    //we also consider the case that user can type the camel case letters in small letters also; Example: aal->addActionListener
    @Parameters
    public static Collection<Object[]> data() {
            Object[][] data = new Object[][] {
                    {"gval", "gvalExample", "getFormattedValue"},
                    {"gval", "getValue", "getValidators"},
                    {"gVal", "getValue", "gvalExample"},

                    {"getl", "getLiterals", "getLiteralExamples"},

                    {"gle", "getLiteralExamples", "getLiterals"},
                    {"gle", "getLiteralExamples", "getLiteralValues"},

                    {"glv", "getLiteralValues", "getLiteralExamples"},
                    {"gle", "getLiteralExamples", "getLiteralValues"},

                    {"gr", "groff", "groflow"},
                    {"gr", "groflow", "getRawValue"},
                    {"gR", "getRawValue", "groff"},
                    {"gR", "getRawValue", "getRawValues"},
                    {"gr", "getRawValue", "getRawValues"},
                    {"grv", "getRawValue", "getRawData"},

                    {"gl", "glSomething", "getLiteralValue"},
                    {"glv", "getLiteralValue", "glSomething"},

                    {"it", "iterator", "isType"},
                    {"iT", "isType", "iterator"},

                    {"get", "getFtp", "getFileTypeProvider"},
                    {"getftp", "getFtp", "getFileTypeProvider"},
                    {"getFtp", "getFtp", "getFileTypeProvider"},
                    {"getFTP", "getFtp", "getFileTypeProvider"},
                    {"getFTP", "getFtp", "getFileTypeProvider"},
                    {"getFTP", "getFileTypeprovider", "getConnectionProvider"},
                    {"getFTP", "getFileTypeprovider", "getFileTypeConnectionProvider"},

                    {"gb", "getBounds", "getbackground"},
                    {"gb", "gbColours", "getBounds"},
                    {"gB", "gbColours", "getBounds"},
                    {"gB", "getBound",  "gbColours"},

                    {"aml", "addMouseListener", "addInputMethodListener"},
                    {"aML", "addMouseListener", "addInputMethodListener"},

                    {"ia", "isActive", "isAlwaysOnTop"},
                    {"ia", "isActive", "invalidate"},

                    {"ss", "setSize", "dispose"},
                    {"ss", "setSize", "dispose"},
                    {"ss", "setSize", "isShowing"},
                    {"ss", "isShowing", "dispose"},

                    {"aal", "addActionListener", "paintAll"},
                    {"pa", "paintAll", "addActionListener"},

                    {"sl", "setLayout", "setLayeredPane"},
                    {"setl", "setLayout", "setLayeredPane"},
                    {"setL", "setLayout", "setLayeredPane"},
                    {"slp", "setLayeredPane", "setLayout"},

                    {"sT", "setText", "list"},
                    {"st", "setText", "list"},

                    {"ps", "isPreferrredSize", "getPreferredSize"},
                    {"pS", "isPreferrredSize", "getPreferredSize"},
                    {"getps", "getPreferredSize", "isPreferredSize"},
                    {"getps", "getPreferredSize", "getSize"},

                    {"setvg", "setVgap", "setHgap"},
                    {"gg", "getGraphics", "getDebugGraphicsOptions"},
                    {"iva", "isValid", "validate"},
                    {"va", "validate", "isValid"},

        };
        return Arrays.asList(data);
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
        assertThat(score1, greaterThan(score2));

    }

}
