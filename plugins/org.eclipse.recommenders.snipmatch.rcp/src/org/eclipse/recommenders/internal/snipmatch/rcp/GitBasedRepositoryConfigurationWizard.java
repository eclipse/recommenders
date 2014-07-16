/**
 * Copyright (c) 2014 Olav Lenz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchFactory;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.utils.Checks;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;

public class GitBasedRepositoryConfigurationWizard extends AbstractSnippetRepositoryWizard {

    private GitBasedRepositoryConfigurationWizardPage page = new GitBasedRepositoryConfigurationWizardPage(
            Messages.WIZARD_GIT_REPOSITORY_PAGE_NAME);

    private EclipseGitSnippetRepositoryConfiguration configuration;
    private final BranchInputValidator branchInputValidator = new BranchInputValidator();

    public GitBasedRepositoryConfigurationWizard() {
        setWindowTitle(Messages.WIZARD_GIT_REPOSITORY_WINDOW_TITLE);
        page.setWizard(this);
    }

    @Override
    public boolean performFinish() {
        configuration = SnipmatchFactory.eINSTANCE.createEclipseGitSnippetRepositoryConfiguration();
        configuration.setName(page.txtName.getText());
        configuration.setUrl(page.txtUrl.getText());
        configuration.setPushUrl(page.txtPushUrl.getText());
        configuration.setPushBranchPrefix(page.txtPushBranchPrefix.getText());
        configuration.setEnabled(true);
        return true;
    }

    @Override
    public SnippetRepositoryConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void addPages() {
        addPage(page);
    }

    @Override
    public IWizardPage getStartingPage() {
        return page;
    }

    @Override
    public boolean canFinish() {
        return page.canFinish();
    }

    @Override
    public boolean isApplicable(SnippetRepositoryConfiguration configuration) {
        return configuration instanceof EclipseGitSnippetRepositoryConfiguration;
    }

    @Override
    public void setConfiguration(SnippetRepositoryConfiguration configuration) {
        this.configuration = Checks.cast(configuration);
    }

    class GitBasedRepositoryConfigurationWizardPage extends WizardPage {

        private Composite container;
        private Text txtName;
        private Text txtUrl;
        private Text txtPushUrl;
        private Text txtPushBranchPrefix;

        protected GitBasedRepositoryConfigurationWizardPage(String pageName) {
            super(pageName);
            setTitle(Messages.WIZARD_GIT_REPOSITORY_TITLE);
            setDescription(Messages.WIZARD_GIT_REPOSITORY_DESCRIPTION);
        }

        @Override
        public void createControl(Composite parent) {
            container = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            container.setLayout(layout);

            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 2;

            Label lblName = new Label(container, SWT.NONE);
            lblName.setText(Messages.WIZARD_GIT_REPOSITORY_LABEL_NAME);
            txtName = new Text(container, SWT.BORDER | SWT.SINGLE);
            txtName.setLayoutData(gd);
            txtName.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    updatePageComplete();
                }
            });

            Label lblUrl = new Label(container, SWT.NONE);
            lblUrl.setText(Messages.WIZARD_GIT_REPOSITORY_LABEL_FETCH_URL);
            txtUrl = new Text(container, SWT.BORDER | SWT.SINGLE);
            txtUrl.setLayoutData(gd);
            txtUrl.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    updatePageComplete();
                }
            });

            GridData descriptionGd = new GridData(GridData.FILL_HORIZONTAL);
            descriptionGd.horizontalSpan = 3;

            Label lblPushSettingsDescription = new Label(container, SWT.NONE);
            lblPushSettingsDescription.setText(MessageFormat.format(
                    Messages.WIZARD_GIT_REPOSITORY_PUSH_SETTINGS_DESCRIPTION, Snippet.FORMAT_VERSION));
            lblPushSettingsDescription.setLayoutData(descriptionGd);

            Label lblPushUrl = new Label(container, SWT.NONE);
            lblPushUrl.setText(Messages.WIZARD_GIT_REPOSITORY_LABEL_PUSH_URL);
            txtPushUrl = new Text(container, SWT.BORDER | SWT.SINGLE);
            txtPushUrl.setLayoutData(gd);
            txtPushUrl.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    updatePageComplete();
                }
            });

            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 1;

            Label lblPushBranchPrefix = new Label(container, SWT.NONE);
            lblPushBranchPrefix.setText(Messages.WIZRAD_GIT_REPOSITORY_LABEL_PUSH_BRANCH_PREFIX);
            txtPushBranchPrefix = new Text(container, SWT.BORDER | SWT.SINGLE);
            txtPushBranchPrefix.setLayoutData(gd);
            txtPushBranchPrefix.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    updatePageComplete();
                }
            });

            Label lblBranch = new Label(container, SWT.NONE);
            lblBranch.setText("/" + Snippet.FORMAT_VERSION); //$NON-NLS-1$

            if (configuration != null) {
                txtName.setText(configuration.getName());
                txtUrl.setText(configuration.getUrl());
                txtPushUrl.setText(configuration.getPushUrl());
                txtPushBranchPrefix.setText(configuration.getPushBranchPrefix());
            }

            txtName.forceFocus();

            setControl(container);
            updatePageComplete();
        }

        public void updatePageComplete() {
            setErrorMessage(null);
            boolean nameNotEmpty = !Strings.isNullOrEmpty(txtName.getText());
            boolean urlValid = !Strings.isNullOrEmpty(txtUrl.getText());
            boolean pushUrlValid = !Strings.isNullOrEmpty(txtPushUrl.getText());
            boolean pushBranchPrefixValid = !Strings.isNullOrEmpty(txtPushBranchPrefix.getText());

            String valid = branchInputValidator.isValid(txtPushBranchPrefix.getText());
            if (valid != null) {
                setErrorMessage(valid);
                pushBranchPrefixValid = false;
            }

            try {
                new URI(txtUrl.getText());
            } catch (URISyntaxException e) {
                setErrorMessage(Messages.WARNING_INVALID_URL_FORMAT);
                urlValid = false;
            }

            try {
                new URI(txtPushUrl.getText());
            } catch (URISyntaxException e) {
                setErrorMessage(Messages.WARNING_INVALID_URL_FORMAT);
                pushUrlValid = false;
            }

            setPageComplete(nameNotEmpty && urlValid && pushUrlValid && pushBranchPrefixValid);
        }

        public boolean canFinish() {
            return isPageComplete();
        }

    }

}
