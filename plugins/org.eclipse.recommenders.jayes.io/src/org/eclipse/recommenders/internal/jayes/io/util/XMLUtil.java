/**
 * Copyright (c) 2011 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Kutschke - initial API and implementation.
 */
package org.eclipse.recommenders.internal.jayes.io.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class XMLUtil {

    private XMLUtil() {

    }

    /**
     * this method expects the attributes in pairwise name, value form e.g. </br> attributes = [ "id", "12345", "size",
     * "15" ]
     */
    public static void surround(int offset, StringBuilder bldr, String surroundingTag, String... attributes) {
        // TODO addTab
        bldr.insert(offset, '>');

        for (int i = 0; i < attributes.length; i += 2) { // insert in reverted order
            bldr.insert(offset, "\" ");
            bldr.insert(offset, attributes[i + 1]);
            bldr.insert(offset, "=\"");
            bldr.insert(offset, attributes[i]);
        }

        bldr.insert(offset, ' ');
        bldr.insert(offset, surroundingTag);
        bldr.insert(offset, '<');

        bldr.append("</");
        bldr.append(surroundingTag);
        bldr.append('>');
    }

    /**
     * adds a tab to every line
     *
     * @param text
     * @return
     */
    public static String addTab(String text) {
        return text.replaceAll("\n", "\n\t");
    }

    public static void emptyTag(StringBuilder stringBuilder, String tagname, String... attributes) {
        stringBuilder.append('<');
        stringBuilder.append(tagname);
        stringBuilder.append(' ');

        for (int i = 0; i < attributes.length; i += 2) {
            stringBuilder.append(attributes[i]);
            stringBuilder.append("=\"");
            stringBuilder.append(attributes[i + 1]);
            stringBuilder.append("\" ");
        }

        stringBuilder.append("/>");
    }

    public static String escape(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(); // can't happen
        }
    }

    public static String unescape(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(); // can't happen
        }
    }
}
