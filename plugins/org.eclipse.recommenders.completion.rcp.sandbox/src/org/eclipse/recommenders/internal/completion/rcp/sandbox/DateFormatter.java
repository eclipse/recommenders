/**
 * Copyright (c) 2013 Timur Achmetow
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - initial API and implementation
 */
package org.eclipse.recommenders.internal.completion.rcp.sandbox;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.eclipse.osgi.util.NLS;

public class DateFormatter {

    public String formatUnit(String cellText, Date past, Date now) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

        if (days > 0) {
            cellText = NLS.bind("{0} days ago", days);
        } else if (hours > 0) {
            cellText = NLS.bind("{0} hours ago", hours);
        } else if (minutes >= 0) {
            cellText = NLS.bind("{0} minutes ago", minutes);
        }
        return cellText;
    }
}