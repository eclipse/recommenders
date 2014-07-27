package org.eclipse.recommenders.snipmatch;

import java.util.Map;

import com.google.common.collect.Maps;

public enum LocationConstraint {

    NONE(-1),
    FILE(0),
    JAVA(1),
    JAVA_STATEMENTS(2),
    JAVA_TYPE_MEMBERS(3),
    JAVADOC(4);

    private final int index;

    private static Map<Integer, LocationConstraint> map = Maps.newHashMap();

    static {
        for (LocationConstraint constraint : LocationConstraint.values()) {
            map.put(constraint.getIndex(), constraint);
        }
    }

    private LocationConstraint(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static LocationConstraint valueOf(int index) {
        return map.get(index);
    }
}
