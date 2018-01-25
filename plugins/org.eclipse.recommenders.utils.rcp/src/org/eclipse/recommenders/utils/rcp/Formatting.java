/**
 * Copyright (c) 2018 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.utils.rcp;

import java.text.MessageFormat;

import org.eclipse.recommenders.internal.utils.rcp.l10n.Messages;

public final class Formatting {

    private Formatting() {
    }

    public static String toPercentage(double probability) {
        if (Math.abs(probability) < 0.01d) {
            return MessageFormat.format(Messages.MESSAGE_FORMAT_PROMILLE, probability);
        } else {
            return MessageFormat.format(Messages.MESSAGE_FORMAT_PERCENT, probability * 100.0);
        }
    }
}
