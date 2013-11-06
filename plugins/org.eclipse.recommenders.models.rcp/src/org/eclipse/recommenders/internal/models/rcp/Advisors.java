/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.util.List;
import java.util.Set;

import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;

import com.google.common.collect.Lists;

public class Advisors {

    public static final String DISABLED_FLAG = "!";
    public static final String SEPARATOR = ";";

    public enum Filter {
        ALL, ENABLED, DISABLED
    }

    public static List<IProjectCoordinateAdvisor> createAdvisorList(List<IProjectCoordinateAdvisor> arrayList,
            String advisors) {
        String[] split = advisors.split(SEPARATOR);
        List<IProjectCoordinateAdvisor> advisorList = Lists.newArrayList();
        for (String name : split) {
            if (name.startsWith(DISABLED_FLAG)) {
                continue;
            }
            for (IProjectCoordinateAdvisor advisor : arrayList) {
                if (name.equals(advisor.getClass().getName())) {
                    advisorList.add(advisor);
                    break;
                }
            }
        }
        return advisorList;
    }

    public static String createPreferenceStringFromAdvisors(List<IProjectCoordinateAdvisor> orderedAdvisors,
            Set<IProjectCoordinateAdvisor> enabledAdvisors) {
        StringBuilder sb = new StringBuilder();

        for (IProjectCoordinateAdvisor advisor : orderedAdvisors) {
            if (!enabledAdvisors.contains(advisor)) {
                sb.append(DISABLED_FLAG);
            }
            sb.append(advisor.getClass().getName());
            sb.append(SEPARATOR);
        }

        return sb.toString();
    }

    public static String createPreferenceStringFromClassNames(List<String> orderedAdvisors, Set<String> enabledAdvisors) {
        StringBuilder sb = new StringBuilder();

        for (String advisor : orderedAdvisors) {
            if (!enabledAdvisors.contains(advisor)) {
                sb.append(DISABLED_FLAG);
            }
            sb.append(advisor);
            sb.append(SEPARATOR);
        }

        return sb.toString();
    }

    public static List<String> extractAdvisors(String prefString, Filter filter) {
        List<String> result = Lists.newArrayList();

        for (String string : prefString.split(SEPARATOR)) {
            if (filter.equals(Filter.ALL)) {
                if (string.startsWith(DISABLED_FLAG)) {
                    string = string.substring(1);
                }
                result.add(string);
            } else if (filter.equals(Filter.ENABLED)) {
                if (!string.startsWith(DISABLED_FLAG)) {
                    result.add(string);
                }
            } else if (filter.equals(Filter.DISABLED)) {
                if (string.startsWith(DISABLED_FLAG)) {
                    string = string.substring(1);
                    result.add(string);
                }
            }

        }

        return result;
    }

}
