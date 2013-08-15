package org.eclipse.recommenders.internal.rcp;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.eclipse.recommenders.rcp.utils.PreferencesHelper.createLinkLabelToPreferencePage;

import java.net.URL;

import org.eclipse.recommenders.utils.Urls;

public class Constants {

    /**
     * The ID of the Survey preference page
     */
    protected static final String SURVEY_PREFERENCE_PAGE_ID = "org.eclipse.recommenders.rcp.survey.preferencepage";

    /**
     * How many milliseconds to wait to display the survey dialog after the conditions have been met.
     */
    public static final long SURVEY_DISPLAY_DELAY = 60000l;

    /**
     * The description of the survey.
     */
    public static final String SURVEY_DESCRIPTION = "To help us develop Code Recommenders to your needs, we ask you for a few moments of your time to fill out our user survey.";

    /**
     * A hint to inform the user to go to the preferences if he wants to take the survey later.
     */
    public static final String SURVEY_PREFERENCES_HINT = "This dialog will not appear again. If you want to take the survey at a later date, you can always go to <a>"
            + createLinkLabelToPreferencePage(SURVEY_PREFERENCE_PAGE_ID) + "</a>. Do you want to take the survey now?";

    /**
     * The URL of the survey.
     */
    public static final URL SURVEY_URL = Urls
            .toUrl("https://docs.google.com/a/codetrails.com/forms/d/1SqzZh1trpzS6UNEMjVWCvQTzGTBvjBFV-ZdwPuAwm5o/viewform");

    /**
     * Number of plugin activations before survey dialog may be displayed.
     */
    public static final int ACTIVATIONS_BEFORE_SURVEY = 20;

    /**
     * Number of minutes since first plugin activation before survey dialog may be displayed.
     */
    public static final long MILLIS_BEFORE_SURVEY = DAYS.toMillis(7);

    /**
     * Bundle symbolic name of the o.e.r.rcp bundle.
     */
    public static final String BUNDLE_NAME = "org.eclipse.recommenders.rcp";

    /**
     * Number of plugin activations. Used determine when the survey dialog may be displayed.
     */
    public static final String NUMBER_OF_ACTIVATIONS = "activation_count";

    /**
     * Date the plugin was first activated. Used determine the earliest time before the survey dialog may be displayed.
     */
    public static final String FIRST_ACTIVATION_DATE = "first_activation_date";

    /**
     * Whether the survey dialog has already been displayed to the user.
     */
    public static final String SURVEY_TAKEN = "survey_already_displayed";

    /**
     * Whether the user has elected not to take the survey.
     */
    public static final String SURVEY_OPT_OUT = "survey_opt_out";
}
