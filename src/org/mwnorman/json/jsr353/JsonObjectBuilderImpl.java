/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 * 
 * Mike Norman (mwnorman) elects to include this software in this distribution under the CDDL license.
 * Portions Copyright 2013 Mike Norman (mwnorman)
 *     - move to org.mwnorman.json.jsr353 package
 *     - misc. formating changes
 */
package org.mwnorman.json.jsr353;

//javase imports
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//JSR-353 imports
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;

class JsonObjectBuilderImpl implements JsonObjectBuilder {
    
    private final Map<String, JsonValue> valueMap;

    JsonObjectBuilderImpl() {
        this.valueMap = new LinkedHashMap<String, JsonValue>();
    }

    public JsonObjectBuilder add(String name, JsonValue value) {
        validateName(name);
        validateValue(value);
        valueMap.put(name, value);
        return this;
    }

    public JsonObjectBuilder add(String name, String value) {
        validateName(name);
        validateValue(value);
        valueMap.put(name, new JsonStringImpl(value));
        return this;
    }

    public JsonObjectBuilder add(String name, BigInteger value) {
        validateName(name);
        validateValue(value);
        valueMap.put(name, new JsonNumberImpl(value));
        return this;
    }

    public JsonObjectBuilder add(String name, BigDecimal value) {
        validateName(name);
        validateValue(value);
        valueMap.put(name, new JsonNumberImpl(value));
        return this;
    }

    public JsonObjectBuilder add(String name, int value) {
        validateName(name);
        valueMap.put(name, new JsonNumberImpl(value));
        return this;
    }

    public JsonObjectBuilder add(String name, long value) {
        validateName(name);
        valueMap.put(name, new JsonNumberImpl(value));
        return this;
    }

    public javax.json.JsonObjectBuilder add(String name, double value) {
        validateName(name);
        valueMap.put(name, new JsonNumberImpl(value));
        return this;
    }

    public JsonObjectBuilder add(String name, boolean value) {
        validateName(name);
        valueMap.put(name, value ? JsonValue.TRUE : JsonValue.FALSE);
        return this;
    }

    public JsonObjectBuilder addNull(String name) {
        validateName(name);
        valueMap.put(name, JsonValue.NULL);
        return this;
    }

    public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        validateName(name);
        if (builder == null) {
            throw new NullPointerException(
                "Object builder that is used to create a value in JsonObject's name/value pair cannot be null");
        }
        valueMap.put(name, builder.build());
        return this;
    }

    public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        validateName(name);
        if (builder == null) {
            throw new NullPointerException(
                "Array builder that is used to create a value in JsonObject's name/value pair cannot be null");
        }
        valueMap.put(name, builder.build());
        return this;
    }

    public JsonObject build() {
        Map<String, JsonValue> snapshot = new LinkedHashMap<String, JsonValue>(valueMap);
        return new JsonObjectImpl(Collections.unmodifiableMap(snapshot));
    }

    private void validateName(String name) {
        if (name == null) {
            throw new NullPointerException("Name in JsonObject's name/value pair cannot be null");
        }
    }

    private void validateValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Value in JsonObject's name/value pair cannot be null");
        }
    }

    private static final class JsonObjectImpl extends AbstractMap<String, JsonValue> implements
        JsonObject {
        private final Map<String, JsonValue> valueMap; // unmodifiable

        JsonObjectImpl(Map<String, JsonValue> valueMap) {
            this.valueMap = valueMap;
        }

        @Override
        public JsonArray getJsonArray(String name) {
            return (JsonArray)get(name);
        }

        @Override
        public JsonObject getJsonObject(String name) {
            return (JsonObject)get(name);
        }

        @Override
        public JsonNumber getJsonNumber(String name) {
            return (JsonNumber)get(name);
        }

        @Override
        public JsonString getJsonString(String name) {
            return (JsonString)get(name);
        }

        @Override
        public String getString(String name) {
            return getJsonString(name).getString();
        }

        @Override
        public String getString(String name, String defaultValue) {
            try {
                return getString(name);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public int getInt(String name) {
            return getJsonNumber(name).intValue();
        }

        @Override
        public int getInt(String name, int defaultValue) {
            try {
                return getInt(name);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public boolean getBoolean(String name) {
            JsonValue value = get(name);
            if (value == null) {
                throw new NullPointerException();
            }
            else if (value == JsonValue.TRUE) {
                return true;
            }
            else if (value == JsonValue.FALSE) {
                return false;
            }
            else {
                throw new ClassCastException();
            }
        }

        @Override
        public boolean getBoolean(String name, boolean defaultValue) {
            try {
                return getBoolean(name);
            }
            catch (Exception e) {
                return defaultValue;
            }
        }

        @Override
        public boolean isNull(String name) {
            return get(name).equals(JsonValue.NULL);
        }

        @Override
        public ValueType getValueType() {
            return ValueType.OBJECT;
        }

        @Override
        public Set<Entry<String, JsonValue>> entrySet() {
            return valueMap.entrySet();
        }

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriterImpl(sw);
            jw.write(this);
            jw.close();
            return sw.toString();
        }
    }

}