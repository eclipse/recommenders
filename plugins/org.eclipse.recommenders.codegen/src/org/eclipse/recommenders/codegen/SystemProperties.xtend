package org.eclipse.recommenders.codegen

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.List
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.Visibility

@Active(typeof(SystemPropertiesCompilationParticipant))
annotation SystemProperties {
}

class SystemPropertiesCompilationParticipant implements TransformationParticipant<MutableClassDeclaration> {
    override doTransform(List<? extends MutableClassDeclaration> classes, extension TransformationContext context) {
        for (clazz : classes) {

            for (field : clazz.declaredFields) {
                val fieldName = field.simpleName
                val fieldType = field.type

                // generate field name constant for reference in code
                clazz.addField("P_" + fieldName.toUpperCase) [
                    visibility = Visibility.PUBLIC
                    static = true
                    final = true
                    type = typeof(String).newTypeReference
                    initializer = ['''"«fieldName.replace('_', '.').toLowerCase»"''']
                ]

            }

        }
    }
}
