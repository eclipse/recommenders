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

import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ProjectCoordinateJsonUtilities implements JsonSerializer<ProjectCoordinate>, JsonDeserializer<ProjectCoordinate> {

    private static final String GROUPID = "groupId";
    private static final String ARTIFACTID = "artifactId";
    private static final String VERSION = "version";

    @Override
    public JsonElement serialize(ProjectCoordinate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add(GROUPID, new JsonPrimitive(src.getGroupId()));
        result.add(ARTIFACTID, new JsonPrimitive(src.getArtifactId()));
        result.add(VERSION, new JsonPrimitive(src.getVersion()));
        return result;
    }

    @Override
    public ProjectCoordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String groupId = jsonObject.get(GROUPID).getAsString();
        String artifactId = jsonObject.get(ARTIFACTID).getAsString();
        String version = jsonObject.get(VERSION).getAsString();
        return new ProjectCoordinate(groupId, artifactId, version);
    }

}
