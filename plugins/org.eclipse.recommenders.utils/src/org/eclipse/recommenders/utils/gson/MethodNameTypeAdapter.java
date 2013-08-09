package org.eclipse.recommenders.utils.gson;

import java.io.IOException;

import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MethodNameTypeAdapter extends TypeAdapter<IMethodName> {

    @Override
    public void write(JsonWriter out, IMethodName value) throws IOException {
        out.value(value.getIdentifier());
    }

    @Override
    public IMethodName read(JsonReader in) throws IOException {
        String identifier = in.nextString();
        return VmMethodName.get(identifier);
    }

}
