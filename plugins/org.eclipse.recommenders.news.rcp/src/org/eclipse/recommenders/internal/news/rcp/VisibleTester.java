package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Inject;

import org.eclipse.core.expressions.PropertyTester;

public class VisibleTester extends PropertyTester {

    private NewsRcpPreferences preferences;

    @Inject
    public VisibleTester(NewsRcpPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        return preferences.isEnabled();
    }

}
