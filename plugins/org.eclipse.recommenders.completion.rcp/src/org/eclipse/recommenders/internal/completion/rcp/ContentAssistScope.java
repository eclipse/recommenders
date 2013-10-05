package org.eclipse.recommenders.internal.completion.rcp;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.ScopeAnnotation;

public class ContentAssistScope implements Scope {
    private HashMap<Key<?>, Object> values = new HashMap<Key<?>, Object>();

    public void enter() {
        values.clear();
    }

    public void exit() {
        values.clear();
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T get() {
                Object value = values.get(key);
                if (value == null) {
                    value = unscoped.get();
                    values.put(key, value);
                }
                return (T) value;
            }
        };
    }

    @Target({ TYPE, METHOD })
    @Retention(RUNTIME)
    @ScopeAnnotation
    public static @interface ContentAssist {
    }

    public static interface Hero {

    }

    @ContentAssist
    public static class Hobbit implements Hero {
        public Hobbit() {
            System.out.println("created " + toString());
        }
    }

    public static class MiddleEarthModule extends AbstractModule {
        private final ContentAssistScope scope;

        public MiddleEarthModule() {
            scope = new ContentAssistScope();
        }

        @Override
        protected void configure() {
            bindScope(ContentAssist.class, scope);

            bind(Hero.class).to(Hobbit.class);
        }

        public void enter() {
            scope.enter();
        }

        public void exit() {
            scope.exit();
        }
    }
}
