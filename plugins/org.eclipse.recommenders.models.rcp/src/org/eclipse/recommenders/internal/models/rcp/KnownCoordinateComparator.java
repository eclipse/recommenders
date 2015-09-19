/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Varun Gupta - initial implementation
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.util.Comparator;
import java.util.StringTokenizer;

import org.eclipse.recommenders.coordinates.ProjectCoordinate;
import org.eclipse.recommenders.internal.models.rcp.ModelRepositoriesView.KnownCoordinate;

public class KnownCoordinateComparator implements Comparator<KnownCoordinate> {

    @Override
    public int compare(KnownCoordinate kc1, KnownCoordinate kc2) {
        ProjectCoordinate pc1 = kc1.pc, pc2 = kc2.pc;

        int value = compareStrings(pc1.getGroupId(), pc2.getGroupId());

        if (value == 0) {
            value = compareStrings(pc1.getArtifactId(), pc2.getArtifactId());
            if (value == 0) {
                return compareStrings(pc1.getVersion(), pc2.getVersion());
            }
        }
        return value;
    }

    public int compareStrings(String str1, String str2) {
        StringTokenizer st1, st2;
        String token1, token2;
        st1 = new StringTokenizer(str1, ".");
        st2 = new StringTokenizer(str2, ".");
        int value = 1, x = 0, y = 0;
        while (st1.hasMoreTokens() && st2.hasMoreTokens()) {
            token1 = st1.nextToken();
            token2 = st2.nextToken();

            if (token1.matches("[0-9]+") && token2.matches("[0-9]+")) {
                x = Integer.parseInt(token1);
                y = Integer.parseInt(token2);
                value = x - y;
            } else if (token1.matches("[0-9]+")) {
                value = -1;
            } else if (token2.matches("[0-9]+")) {
                value = 1;
            } else {
                value = token1.compareToIgnoreCase(token2);
            }

            if (value != 0) {
                break;
            }
        }
        if (value == 0) {
            if (st1.hasMoreTokens()) {
                return -1;
            } else if (st2.hasMoreTokens()) {
                return 1;
            }
        }
        return value;
    }
}
