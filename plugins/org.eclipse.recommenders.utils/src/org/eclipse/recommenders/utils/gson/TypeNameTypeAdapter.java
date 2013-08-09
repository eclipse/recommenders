package org.eclipse.recommenders.utils.gson;

import java.io.IOException;

import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TypeNameTypeAdapter extends TypeAdapter<ITypeName> {

    @Override
    public void write(JsonWriter out, ITypeName value) throws IOException {
        out.value(value.getIdentifier());
    }

    @Override
    public ITypeName read(JsonReader in) throws IOException {
        String identifier = in.nextString();
        return VmTypeName.get(identifier);
    }

}
