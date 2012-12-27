package org.eclipse.recommenders.models;


// REVIEW do you have a better name?
public interface IBasedName<T> {

    T getName();

    ProjectCoordinate getBase();

}
