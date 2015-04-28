package org.eclipse.recommenders.utils;

import static com.google.common.base.Objects.equal;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.recommenders.utils.Throws;

@SuppressWarnings("unchecked")
public abstract class Result<T> {

    public static <T> Result<T> absent() {
        return (Result<T>) Absent.INSTANCE;
    }

    public static <T> Result<T> of(T reference) {
        return new Present<T>(ensureIsNotNull(reference));
    }

    public static <T> Result<T> fromNullable(@Nullable T nullableReference) {
        return (nullableReference == null) ? Result.<T>absent() : new Present<T>(nullableReference);
    }

    public static <T> Result<T> error(int code) {
        return (Result<T>) new Error(code, null);
    }

    public static <T> Result<T> error(Throwable exception) {
        return (Result<T>) new Error(0, exception);
    }

    public static <T> Result<T> error(int code, Throwable exception) {
        return (Result<T>) new Error(code, exception);
    }

    public abstract boolean isPresent();

    public abstract boolean isError();

    public abstract T or(T defaultValue);

    public abstract T get();

    public abstract int getCode();

    public abstract Result<Throwable> getException();

    static final class Present<T> extends Result<T> {
        private final T reference;

        protected Present(T reference) {
            this.reference = reference;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public T get() {
            return reference;
        }

        @Override
        public T or(T defaultValue) {
            return reference;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Present) {
                Present<?> other = (Present<?>) object;
                return reference.equals(other.reference);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 0x598df91c + reference.hashCode();
        }

        @Override
        public String toString() {
            return "Optional.of(" + reference + ")";
        }

        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public Result<Throwable> getException() {
            return Result.absent();
        }
    }

    static final class Absent extends Result<Object> {
        static final Absent INSTANCE = new Absent();

        protected Absent() {
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public Object or(Object defaultValue) {
            return defaultValue;
        }

        @Override
        public Object get() {
            throw Throws.throwIllegalStateException("cannot get() value from Absent");
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return object == this;
        }

        @Override
        public int hashCode() {
            return 0x598df91c;
        }

        @Override
        public String toString() {
            return "Optional.absent()";
        }

        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public Result<Throwable> getException() {
            return Result.absent();
        }
    }

    static final class Error extends Result<Object> {

        private int code;
        private Throwable exception;

        protected Error(int code, Throwable exception) {
            this.code = code;
            this.exception = exception;
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Object or(Object defaultValue) {
            return defaultValue;
        }

        @Override
        public Object get() {
            throw Throws.throwIllegalStateException("cannot get() value from Absent");
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Error) {
                Error other = (Error) object;
                return code == other.code && equal(exception, other.exception);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 0x598df91c + code + (exception != null ? exception.hashCode() : 0);
        }

        @Override
        public String toString() {
            return "Optional.absent()";
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public Result<Throwable> getException() {
            return Result.fromNullable(exception);
        }
    }

}
