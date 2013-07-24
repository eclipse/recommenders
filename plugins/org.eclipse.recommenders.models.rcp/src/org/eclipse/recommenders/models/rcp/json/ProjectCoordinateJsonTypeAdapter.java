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

import java.io.IOException;

import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * {@link TypeAdapter<T>} implementation for {@link ProjectCoordinate}. To serialize a ProjectCoordinate the method
 * {@code ProjectCoordinate.toString()} is used. For the deserialization {@code ProjectCoordinate.valueof(String)} is
 * used. <br>
 * The json representation looks like: <br>
 * {"projectcoordinate":"jre:jre:1.6.0"}
 */
public class ProjectCoordinateJsonTypeAdapter extends TypeAdapter<ProjectCoordinate> {

    private static final String PROJECTCOORDINATE = "projectcoordinate";

    @Override
    public void write(JsonWriter out, ProjectCoordinate value) throws IOException {
        out.beginObject();
        out.name(PROJECTCOORDINATE).value(value.toString());
        out.endObject();
    }

    @Override
    public ProjectCoordinate read(JsonReader in) throws IOException {
        in.beginObject();
        String projectCoordinateString = in.nextString();
        in.endObject();
        return ProjectCoordinate.valueOf(projectCoordinateString);
    }

}
