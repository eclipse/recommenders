package org.eclipse.recommenders.utils.gson;

import java.io.IOException;

import org.eclipse.recommenders.utils.names.IFieldName;
import org.eclipse.recommenders.utils.names.VmFieldName;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class FieldNameTypeAdapter extends TypeAdapter<IFieldName> {

    @Override
    public void write(JsonWriter out, IFieldName value) throws IOException {
        out.value(value.getIdentifier());
    }

    @Override
    public IFieldName read(JsonReader in) throws IOException {
        String identifier = in.nextString();
        return VmFieldName.get(identifier);
    }
}
