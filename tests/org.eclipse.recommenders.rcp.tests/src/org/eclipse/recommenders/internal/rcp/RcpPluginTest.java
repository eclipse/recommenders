package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.Constants.ACTIVATIONS_BEFORE_SURVEY;
import static org.eclipse.recommenders.internal.rcp.Constants.FIRST_ACTIVATION_DATE;
import static org.eclipse.recommenders.internal.rcp.Constants.NUMBER_OF_ACTIVATIONS;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_OPT_OUT;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_TAKEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RcpPluginTest {

    @Mock
    IPreferenceStore preferences;

    @Test
    public void testSurveyTaken() {
        Mockito.when(preferences.getBoolean(SURVEY_TAKEN)).thenReturn(true);
        Mockito.when(preferences.getBoolean(SURVEY_OPT_OUT)).thenReturn(false);
        Mockito.when(preferences.getInt(NUMBER_OF_ACTIVATIONS)).thenReturn(ACTIVATIONS_BEFORE_SURVEY + 1);
        Mockito.when(preferences.getLong(FIRST_ACTIVATION_DATE)).thenReturn(1L);

        assertFalse(RcpPlugin.shouldDisplaySurveyDialog(preferences));
    }

    @Test
    public void testSurveyOptOut() {
        Mockito.when(preferences.getBoolean(SURVEY_TAKEN)).thenReturn(false);
        Mockito.when(preferences.getBoolean(SURVEY_OPT_OUT)).thenReturn(true);
        Mockito.when(preferences.getInt(NUMBER_OF_ACTIVATIONS)).thenReturn(ACTIVATIONS_BEFORE_SURVEY + 1);
        Mockito.when(preferences.getLong(FIRST_ACTIVATION_DATE)).thenReturn(1L);

        assertFalse(RcpPlugin.shouldDisplaySurveyDialog(preferences));
    }

    @Test
    public void testNotEnoughActivations() {
        Mockito.when(preferences.getBoolean(SURVEY_TAKEN)).thenReturn(false);
        Mockito.when(preferences.getBoolean(SURVEY_OPT_OUT)).thenReturn(false);
        Mockito.when(preferences.getInt(NUMBER_OF_ACTIVATIONS)).thenReturn(0);
        Mockito.when(preferences.getLong(FIRST_ACTIVATION_DATE)).thenReturn(1L);

        assertFalse(RcpPlugin.shouldDisplaySurveyDialog(preferences));
    }

    @Test
    public void testNotEnoughTime() {
        Mockito.when(preferences.getBoolean(SURVEY_TAKEN)).thenReturn(false);
        Mockito.when(preferences.getBoolean(SURVEY_OPT_OUT)).thenReturn(false);
        Mockito.when(preferences.getInt(NUMBER_OF_ACTIVATIONS)).thenReturn(ACTIVATIONS_BEFORE_SURVEY + 1);
        Mockito.when(preferences.getLong(FIRST_ACTIVATION_DATE)).thenReturn(System.currentTimeMillis());

        assertFalse(RcpPlugin.shouldDisplaySurveyDialog(preferences));
    }

    @Test
    public void testShowDisplay() {
        Mockito.when(preferences.getBoolean(SURVEY_TAKEN)).thenReturn(false);
        Mockito.when(preferences.getBoolean(SURVEY_OPT_OUT)).thenReturn(false);
        Mockito.when(preferences.getInt(NUMBER_OF_ACTIVATIONS)).thenReturn(ACTIVATIONS_BEFORE_SURVEY + 1);
        Mockito.when(preferences.getLong(FIRST_ACTIVATION_DATE)).thenReturn(1L);

        assertTrue(RcpPlugin.shouldDisplaySurveyDialog(preferences));
    }
}
