/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Patrick Gottschaemmer, Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.calls.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.recommenders.completion.rcp.calls.l10n.messages"; //$NON-NLS-1$
    public static String PrefPage_description;
    public static String PrefPage_enable_call_completion;
    public static String PrefPage_max_number_of_proposals;
    public static String PrefPage_min_probability_of_proposal;
    public static String PrefPage_table_descripion;
    public static String PrefPage_table_column_file;
    public static String PrefPage_model_not_available;
    public static String PrefPage_model_available;
    public static String PrefPage_unknown_dependency_details;
    public static String PrefPage_know_dependency_details;
    public static String PrefPage_package_root_info;
    public static String PrefPage_package_root_name;
    public static String PrefPage_package_root_version;
    public static String PrefPage_package_root_fingerprint;
    public static String PrefPage_model_info;
    public static String PrefPage_model_coordinate;
    public static String PrefPage_model_resolution_status;

    public static String ExtdocProvider_no_recommendations_are_made;
    public static String ExtdocProvider_recommendations_are_made;
    public static String ExtdocProvider_recommendation_percentage;
    public static String ExtdocProvider_defined_by;
    public static String ExtdocProvider_undefined;
    public static String ExtdocProvider_observed;
    public static String ExtdocProvider_call;
    public static String ExtdocProvider_proposal_computed_untrained;
    public static String ExtdocProvider_proposal_computed;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
