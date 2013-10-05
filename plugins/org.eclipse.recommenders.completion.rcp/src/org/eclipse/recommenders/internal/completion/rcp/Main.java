package org.eclipse.recommenders.internal.completion.rcp;

import org.eclipse.recommenders.internal.completion.rcp.ContentAssistScope.Hero;
import org.eclipse.recommenders.internal.completion.rcp.ContentAssistScope.MiddleEarthModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    public static void main(String[] args) {
        MiddleEarthModule module;

        module = new MiddleEarthModule();

        Injector injector;

        injector = Guice.createInjector(module);

        System.out.println("entering scope");
        module.enter();
        injector.getInstance(Hero.class);
        injector.getInstance(Hero.class);
        injector.getInstance(Hero.class);
        injector.getInstance(Hero.class);

        System.out.println("exiting scope");
        module.exit();
        injector.getInstance(Hero.class);
        injector.getInstance(Hero.class);
        injector.getInstance(Hero.class);
        injector.getInstance(Hero.class);
    }
}