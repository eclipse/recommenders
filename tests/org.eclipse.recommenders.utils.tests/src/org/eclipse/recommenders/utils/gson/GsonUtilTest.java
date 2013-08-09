/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.utils.gson;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.recommenders.utils.NamesTest;
import org.eclipse.recommenders.utils.names.IFieldName;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmFieldName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.reflect.TypeToken;

public class GsonUtilTest {

    static class GsonTypeAdapterTestStruct {

        File file;
        IMethodName method;
        ITypeName type;
        VmMethodName vmMethod;
        VmTypeName vmType;
        VmFieldName vmField;
        IFieldName field;

        public GsonTypeAdapterTestStruct(File file, IMethodName method, ITypeName type, IFieldName field) {
            this.file = file;
            vmMethod = (VmMethodName) method;
            this.method = method;
            vmType = (VmTypeName) type;
            this.type = type;
            vmField = (VmFieldName) field;
            this.field = field;
        }

        @Override
        public boolean equals(Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Test
    public void testGsonUtil() throws IOException {
        GsonTypeAdapterTestStruct expected = new GsonTypeAdapterTestStruct(new File("C:/test/test.json"),
                NamesTest.STRING_NEW, NamesTest.STRING, NamesTest.EVENT_FIELD);
        String serialize = GsonUtil.serialize(expected);
        GsonTypeAdapterTestStruct actual = GsonUtil.deserialize(serialize, GsonTypeAdapterTestStruct.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyFileDeserialization() throws IOException {
        // setup
        File f = File.createTempFile("tmp", ".json");
        f.deleteOnExit();

        // exercise
        Object res = GsonUtil.deserialize(f, new TypeToken<Map<File, String>>() {
        }.getType());

        // it's actually null :)
        assertNull(res);
    }

    @Test
    public void testPrettyPrint() {
        final GsonTestStruct struct = GsonTestStruct.create("string", 0.43d, "s1", "s2");
        GsonUtil.serialize(struct);
    }

    @Test
    public void testSerializationRoundTrip() {
        // setup
        final GsonTestStruct input = GsonTestStruct.create("string", 0.43d, "s1", "s2");
        // exercise
        final String json = GsonUtil.serialize(input);
        final GsonTestStruct output = GsonUtil.deserialize(json, GsonTestStruct.class);
        // verify
        assertEquals(input, output);
    }

    @Test
    public void testSerializationRoundTrip_ViaInputStream() {
        // setup
        final GsonTestStruct input = GsonTestStruct.create("string", 0.43d, "s1", "s2");
        // exercise
        final String json = GsonUtil.serialize(input);
        final GsonTestStruct output = GsonUtil.deserialize(new ByteArrayInputStream(json.getBytes()),
                GsonTestStruct.class);
        // verify
        assertEquals(input, output);
    }

    @Test
    public void testSerializationPrettyRoundTrip_ViaInputStream() {
        // setup
        final GsonTestStruct input = GsonTestStruct.create("string", 0.43d, "s1", "s2");
        // exercise
        final String prettyJson = GsonUtil.serialize(input);
        final GsonTestStruct output = GsonUtil.deserialize(prettyJson, GsonTestStruct.class);
        // verify
        assertEquals(input, output);
    }

    @Test
    public void testMultimapOfStrings() {
        // setup:
        final Multimap<String, String> map = HashMultimap.create();
        map.put("key", "value1");
        map.put("key", "value2");
        // exercise:
        final String json = GsonUtil.getInstance().toJson(map, new TypeToken<Multimap<String, String>>() {
        }.getType());
        final Multimap<String, String> output = GsonUtil.deserialize(json, new TypeToken<Multimap<String, String>>() {
        }.getType());
        // verify
        assertEquals(map, output);
    }

}
