/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye, Cheng Chen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Cheng Chen - initial API and implementation.
 */

package org.eclipse.recommenders.snipmatch.preferences;

import java.io.File;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.recommenders.snipmatch.rcp.SnipMatchPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A special abstract preference page to host field editors.
 * <p>
 * Subclasses must implement the <code>createFieldEditors</code> method
 * and should override <code>createLayout</code> if a special layout of the field
 * editors is needed.
 * </p>
 */
public class GitRepositorySettingPreferencePage  extends PreferencePage
        implements IPropertyChangeListener, IWorkbenchPreferencePage{

    /**
     * Layout constant (value <code>0</code>) indicating that
     * each field editor is handled as a single component.
     */
    public static final int FLAT = 0;

    /**
     * Layout constant (value <code>1</code>) indicating that
     * the field editors' basic controls are put into a grid layout.
     */
    public static final int GRID = 1;

    /** 
     * The vertical spacing used by layout styles <code>FLAT</code> 
     * and <code>GRID</code>.
     */
    protected static final int VERTICAL_SPACING = 10;

    /** 
     * The margin width used by layout styles <code>FLAT</code> 
     * and <code>GRID</code>.
     */
    protected static final int MARGIN_WIDTH = 0;

    /** 
     * The margin height used by layout styles <code>FLAT</code> 
     * and <code>GRID</code>.
     */
    protected static final int MARGIN_HEIGHT = 0;

    /**
     * The layout style; either <code>FLAT</code> or <code>GRID</code>.
     */
    private int style;

    /** 
     * The first invalid field editor, or <code>null</code>
     * if all field editors are valid.
     */
    private FieldEditor invalidFieldEditor = null;

    /**
     * The parent composite for field editors
     */
    private Composite fieldEditorParent;
    
    private Button automicButton = null;
    private Button manulButton = null;
    private Text syncTime = null;
    
    private Button useDefaultButton = null;
    private Text snippetsDir = null;
    private Text snippetsIndexDir = null;
    
    private Button snippetsDirButton = null;
    private Button snippetsIndexDirButton = null;
    private Button updateIndexButton = null;
    
    /**
     * Initial path for the Browse dialog.
     */
    private File filterPath = null;
    /**
	 * Create a new instance of the reciever.
	 */
	public GitRepositorySettingPreferencePage() {
		super();
        this.style = 1;
		setPreferenceStore(SnipMatchPlugin.getDefault().getPreferenceStore());
		setDescription("Preference setting for local search model:");
	}
	
    /**
     * Adjust the layout of the field editors so that
     * they are properly aligned.
     */
    protected void adjustGridLayout() {
        ((GridLayout) fieldEditorParent.getLayout()).numColumns = 1;
    }

    /* (non-Javadoc)
     * Method declared on PreferencePage.
     */
    protected Control createContents(Composite parent) {
        fieldEditorParent = new Composite(parent, SWT.NULL);
    	GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fieldEditorParent.setLayout(layout);
        fieldEditorParent.setFont(parent.getFont());

        createFieldEditors(fieldEditorParent);
        loadValues();
        return fieldEditorParent;
    }

    /**
     * Creates the page's field editors.
     */
    protected void createFieldEditors(Composite parent){
    	/*Group syncGroup = new Group(parent, SWT.NONE);
    	syncGroup.setText("Synchronization setting");
    	GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        syncGroup.setLayout(layout);
        syncGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL));

        automicButton = new Button(syncGroup, SWT.RADIO);
        automicButton.setText("Automatic");
        automicButton.setSelection(true);
        //automicButton.setSize(200, 100);
        
        
        Composite composite = new Composite(syncGroup, SWT.NONE);
        composite.setLayout(new FillLayout());
        Label l1 = new Label(composite, SWT.RIGHT);
        l1.setText("Every  ");
        syncTime = new Text(composite, SWT.BORDER|SWT.CENTER);
        Label l2 = new Label(composite, SWT.NONE);
        l2.setText("  minutes");
        
        manulButton = new Button(syncGroup, SWT.RADIO);
        manulButton.setText("Manual");*/
        
        Group storeSettingGroup = new Group(parent, SWT.NONE);
        storeSettingGroup.setText("Snippets store setting");
    	GridLayout storeLayout = new GridLayout();
    	storeLayout.numColumns = 1;
    	storeLayout.marginHeight = 0;
    	storeLayout.marginWidth = 0;
    	storeSettingGroup.setLayout(storeLayout);
    	storeSettingGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL));
    	
    	useDefaultButton = new Button(storeSettingGroup, SWT.CHECK);
    	useDefaultButton.setText("Use default directory");
    	
    	Label label1 = new Label(storeSettingGroup, SWT.NONE);
    	label1.setText("Snippets directory:");
    	Composite composite1 = new Composite(storeSettingGroup, SWT.NONE);
    	composite1.setLayout(null);
    	this.snippetsDir = new Text(composite1, SWT.BORDER);
    	this.snippetsDir.setBounds(1,3,240,25);
    	
    	snippetsDirButton = new Button(composite1, SWT.NONE);
    	snippetsDirButton.setText("Browse");
    	snippetsDirButton.setBounds(250,1,60,28);
    	
    	snippetsDirButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                String newValue = changePressed(snippetsDir);
                if (newValue != null) {
                	snippetsDir.setText(newValue);
                }
            }
        });
    	
    	Label label2 = new Label(storeSettingGroup, SWT.NONE);
    	label2.setText("Search engine file path:");
    	Composite composite2 = new Composite(storeSettingGroup, SWT.NONE);
    	composite2.setLayout(null);
    	this.snippetsIndexDir = new Text(composite2, SWT.BORDER);
    	this.snippetsIndexDir.setBounds(1,3,240,25);
    	
    	snippetsIndexDirButton = new Button(composite2, SWT.NONE);
    	snippetsIndexDirButton.setText("Browse");
    	snippetsIndexDirButton.setBounds(250,1,60,28);
    	
    	updateIndexButton = new Button(composite2, SWT.NONE);
    	updateIndexButton.setText("Update index");
    	updateIndexButton.setBounds(320,1,90,28);
    	
    	snippetsIndexDirButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                String newValue = changeIndexFile(snippetsIndexDir);
                if (newValue != null) {
                	snippetsIndexDir.setText(newValue);
                }
            }
        });
    	
    	updateIndexButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
            	performOk();
            	SnipMatchPlugin.getDefault().getSearchEngine().updateIndex();
            }
        });
    	
    	useDefaultButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(useDefaultButton.getSelection())
					disableComponents();
				else enableComponents();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
    		
    	});
    }
    
    private void disableComponents(){
    	String defaultPath = System.getProperty("user.home");
    	this.snippetsDir.setText(defaultPath);
    	this.snippetsIndexDir.setText(new File(defaultPath, "simpleIndex.txt").getAbsolutePath());
    	
    	this.snippetsDir.setEnabled(false);
    	this.snippetsIndexDir.setEnabled(false);
    	this.snippetsDirButton.setEnabled(false);
    	this.snippetsIndexDirButton.setEnabled(false);
    }
    
    private void enableComponents(){
    	this.snippetsDir.setEnabled(true);
    	this.snippetsIndexDir.setEnabled(true);
    	this.snippetsDirButton.setEnabled(true);
    	this.snippetsIndexDirButton.setEnabled(true);
   }
    
    private void loadValues(){
    	boolean defaultValue = getPreferenceStore().getBoolean(PreferenceConstants.SNIPPETS_DIR_USE_DEFAULT);
    	if(defaultValue){
    		this.useDefaultButton.setSelection(true);
    		disableComponents();
    	}else{
    		this.snippetsDir.setText(getPreferenceStore().getString(PreferenceConstants.SNIPPETS_STORE_DIR));
    		this.snippetsIndexDir.setText(getPreferenceStore().getString(PreferenceConstants.SNIPPETS_INDEX_FILE));
    	}
    }
    
    private String changePressed(Text pathComp) {
        File f = new File(pathComp.getText());
        if (!f.exists()) {
			f = null;
		}
        File d = getDirectory(f);
        if (d == null) {
			return null;
		}

        return d.getAbsolutePath();
    }
    
    private File getDirectory(File startingDirectory) {
        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
        if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
        else if (filterPath != null) {
        	fileDialog.setFilterPath(filterPath.getPath());
        }
        String dir = fileDialog.open();
        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
				return new File(dir);
			}
        }

        return null;
    }
    
    private String changeIndexFile(Text pathComp) {
        File f = new File(pathComp.getText());
        if (!f.exists()) {
			f = null;
		}
        File d = getFilePath(f);
        if (d == null) {
			return null;
		}

        return d.getAbsolutePath();
    }
    
    private File getFilePath(File startingDirectory) {
       	FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
        else if (filterPath != null) {
        	fileDialog.setFilterPath(filterPath.getPath());
        }
        String dir = fileDialog.open();
        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
				return new File(dir);
			}
        }

        return null;
    }

    /**	
     * The field editor preference page implementation of an <code>IDialogPage</code>
     * method disposes of this page's controls and images.
     * Subclasses may override to release their own allocated SWT
     * resources, but must call <code>super.dispose</code>.
     */
    public void dispose() {
        super.dispose();
        /*if (fields != null) {
            Iterator e = fields.iterator();
            while (e.hasNext()) {
                FieldEditor pe = (FieldEditor) e.next();
                pe.setPage(null);
                pe.setPropertyChangeListener(null);
                pe.setPreferenceStore(null);
            }
        }*/
    }

    /**
     * Returns a parent composite for a field editor.
     * <p>
     * This value must not be cached since a new parent
     * may be created each time this method called. Thus
     * this method must be called each time a field editor
     * is constructed.
     * </p>
     *
     * @return a parent
     */
    protected Composite getFieldEditorParent() {
        if (style == FLAT) {
            // Create a new parent for each field editor
            Composite parent = new Composite(fieldEditorParent, SWT.NULL);
            parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            return parent;
        }
        // Just return the parent
        return fieldEditorParent;
    }

    /**	
     * The field editor preference page implementation of a <code>PreferencePage</code>
     * method loads all the field editors with their default values.
     */
   protected void performDefaults() {
    	String defaultPath = System.getProperty("user.home");
    	this.snippetsDir.setText(defaultPath);
    	this.snippetsIndexDir.setText(new File(defaultPath, "simpleIndex.txt").getAbsolutePath());
    	useDefaultButton.setSelection(true);
        super.performDefaults();
    }

    /** 
     * The field editor preference page implementation of this 
     * <code>PreferencePage</code> method saves all field editors by
     * calling <code>FieldEditor.store</code>. Note that this method
     * does not save the preference store itself; it just stores the
     * values back into the preference store.
     *
     * @see FieldEditor#store()
     */
    public boolean performOk() {
    	getPreferenceStore().setDefault(PreferenceConstants.SNIPPETS_DIR_USE_DEFAULT, this.useDefaultButton.getSelection());
    	getPreferenceStore().setValue(PreferenceConstants.SNIPPETS_STORE_DIR, this.snippetsDir.getText().trim());
    	getPreferenceStore().setValue(PreferenceConstants.SNIPPETS_INDEX_FILE, this.snippetsIndexDir.getText().trim());
        return true;
    }

    /**
     * The field editor preference page implementation of this <code>IPreferencePage</code>
     * (and <code>IPropertyChangeListener</code>) method intercepts <code>IS_VALID</code> 
     * events but passes other events on to its superclass.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getProperty().equals(FieldEditor.IS_VALID)) {
            boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
            // If the new value is true then we must check all field editors.
            // If it is false, then the page is invalid in any case.
            if (newValue) {

            } else {
                invalidFieldEditor = (FieldEditor) event.getSource();
                setValid(newValue);
            }
        }
    }

    /* (non-Javadoc)
     * Method declared on IDialog.
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && invalidFieldEditor != null) {
            invalidFieldEditor.setFocus();
        }
    }

	public void init(IWorkbench workbench) {
	}
}
