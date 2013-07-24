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

import java.lang.reflect.Type;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PackageFragmentRootJsonUtilities implements JsonSerializer<IPackageFragmentRoot>, JsonDeserializer<IPackageFragmentRoot> {

    private static final String JSON_PACKAGEFRAGMENTROOT = "packagefragmentroot";

    @Override
    public JsonElement serialize(IPackageFragmentRoot src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add(JSON_PACKAGEFRAGMENTROOT, new JsonPrimitive(src.getHandleIdentifier()));
        return result;
    }

    @Override
    public IPackageFragmentRoot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String identifier = jsonObject.get(JSON_PACKAGEFRAGMENTROOT).getAsString();
        return (IPackageFragmentRoot) JavaCore.create(identifier);
    }

}
