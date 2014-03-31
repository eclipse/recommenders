/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Model Archive State</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelArchiveState()
 * @model
 * @generated
 */
public enum ModelArchiveState implements Enumerator {
    /**
     * The '<em><b>CLOSED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CLOSED_VALUE
     * @generated
     * @ordered
     */
    CLOSED(0, "CLOSED", "CLOSED"),

    /**
     * The '<em><b>DOWNLOADING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DOWNLOADING_VALUE
     * @generated
     * @ordered
     */
    DOWNLOADING(0, "DOWNLOADING", "DOWNLOADING"),

    /**
     * The '<em><b>CACHED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CACHED_VALUE
     * @generated
     * @ordered
     */
    CACHED(0, "CACHED", "CACHED");

    /**
     * The '<em><b>CLOSED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CLOSED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CLOSED
     * @model
     * @generated
     * @ordered
     */
    public static final int CLOSED_VALUE = 0;

    /**
     * The '<em><b>DOWNLOADING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DOWNLOADING</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DOWNLOADING
     * @model
     * @generated
     * @ordered
     */
    public static final int DOWNLOADING_VALUE = 0;

    /**
     * The '<em><b>CACHED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CACHED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CACHED
     * @model
     * @generated
     * @ordered
     */
    public static final int CACHED_VALUE = 0;

    /**
     * An array of all the '<em><b>Model Archive State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ModelArchiveState[] VALUES_ARRAY =
        new ModelArchiveState[] {
            CLOSED,
            DOWNLOADING,
            CACHED,
        };

    /**
     * A public read-only list of all the '<em><b>Model Archive State</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<ModelArchiveState> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Model Archive State</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelArchiveState get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ModelArchiveState result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Model Archive State</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelArchiveState getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ModelArchiveState result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Model Archive State</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelArchiveState get(int value) {
        switch (value) {
            case CLOSED_VALUE: return CLOSED;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ModelArchiveState(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
      return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
      return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLiteral() {
      return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }
    
} //ModelArchiveState
