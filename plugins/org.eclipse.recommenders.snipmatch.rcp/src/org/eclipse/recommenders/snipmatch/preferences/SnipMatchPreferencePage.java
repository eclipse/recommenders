package org.eclipse.recommenders.snipmatch.preferences;

import java.util.Iterator;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.recommenders.snipmatch.rcp.SnipMatchPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is used to set up whether to use
 * local search engine or remote search engine .
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class SnipMatchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public SnipMatchPreferencePage() {
		super(GRID);
		setPreferenceStore(SnipMatchPlugin.getDefault().getPreferenceStore());
		setDescription("Setting pages for Recommenders SnipMatch");
	}

	/**
	 * Creates the local search engine & remote search engine selection
	 * checkbox.
	 */
	public void createFieldEditors() {
		/**
		 * String[][] labelAndValues = new String[][] { { "&Local search model", PreferenceConstants.SEARCH_MODEL_LOCAL },
		 *		{ "&Remote search model", PreferenceConstants.SEARCH_MODEL_REMOTE }, { "&Mixed(both local and remote) search model", PreferenceConstants.SEARCH_MODEL_MIXED } };
		 
		String[][] labelAndValues = new String[][] { { "&Local search model", PreferenceConstants.SEARCH_MODEL_LOCAL },
				{ "&Remote search model", PreferenceConstants.SEARCH_MODEL_REMOTE }};
		RadioGroupFieldEditor workModelGroup = new RadioGroupFieldEditor(PreferenceConstants.SEARCH_MODEL,
				"&Snippets search model", 3, labelAndValues, getFieldEditorParent(), true);
		addField(workModelGroup);*/
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

}