package org.eclipse.recommenders.utils;

import static org.eclipse.recommenders.utils.Logs.log;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import com.google.common.base.Optional;

public final class MethodHandleUtils {

    private MethodHandleUtils() {
        // Not meant to be instantiated
    }

    public static Optional<MethodHandle> getSuperMethodHandle(Lookup lookupCapability, String methodName,
            Class<?> returnType, Class<?>... argumentTypes) {
        try {
            Class<?> callerClass = lookupCapability.lookupClass();
            Class<?> superclass = callerClass.getSuperclass();
            return Optional.of(lookupCapability.findSpecial(superclass, methodName,
                    MethodType.methodType(returnType, argumentTypes), callerClass));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            log(LogMessages.LOG_WARNING_REFLECTION_FAILED, e, methodName);
            return Optional.absent();
        }
    }
}
