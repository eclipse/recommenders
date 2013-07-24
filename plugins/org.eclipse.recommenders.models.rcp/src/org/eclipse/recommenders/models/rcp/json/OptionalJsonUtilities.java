/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.models.rcp.json;

import static com.google.common.base.Optional.fromNullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link JsonSerializer<Optional<T>>} and {@link JsonDeserializer<Optional<T>>} implementation for {@link Optional<T>}.
 */
public class OptionalJsonUtilities<T> implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    @Override
    public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonElement element = context.serialize(src.orNull());
        final JsonArray result = new JsonArray();
        result.add(element);
        return result;
    }

    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final JsonArray asJsonArray = json.getAsJsonArray();
        final JsonElement jsonElement = asJsonArray.get(0);
        final T entry = context.deserialize(jsonElement, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
        return fromNullable(entry);
    }

}
