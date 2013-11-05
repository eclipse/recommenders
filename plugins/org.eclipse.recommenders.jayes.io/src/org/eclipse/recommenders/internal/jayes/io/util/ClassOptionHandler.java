package org.eclipse.recommenders.internal.jayes.io.util;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class ClassOptionHandler extends OptionHandler<Class<?>> {

    public ClassOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Class<?>> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        String parameter = params.getParameter(0);
        try {
            this.setter.addValue(Class.forName(parameter));
        } catch (ClassNotFoundException e) {
            throw new CmdLineException(owner, e);
        }
        return 1;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "<CLASS>";
    }

}
