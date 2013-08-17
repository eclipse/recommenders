package org.eclipse.recommenders.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Objects;

public abstract class AbstractUniqueName<T> implements IUniqueName<T> {

    private final T name;
    private final ProjectCoordinate pc;

    public AbstractUniqueName(ProjectCoordinate pc, T name) {
        this.name = name;
        this.pc = pc;
    }

    @Override
    public T getName() {
        return name;
    }

    @Override
    public ProjectCoordinate getProjectCoordinate() {
        return pc;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", getName()).add("qualifier", getProjectCoordinate()).toString();
    }
}
