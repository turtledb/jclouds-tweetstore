/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.demo.tweetstore.config.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import com.google.common.io.Closeables;
import com.google.inject.Provider;

/**
 * @author Andrew Phillips
 */
public class PropertiesLoader implements Provider<Properties>{
    private static final String PROPERTIES_FILE = "/WEB-INF/jclouds.properties";

    private final Properties properties;

    public PropertiesLoader(ServletContext context) {
        properties = loadJcloudsProperties(context);
    }

    private static Properties loadJcloudsProperties(ServletContext context) {
        InputStream input = context.getResourceAsStream(PROPERTIES_FILE);
        Properties props = new Properties();
        try {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Closeables.close(input, true);
            } catch (IOException ignored) {
            }
        }
        return props;
    }

    @Override
    public Properties get() {
        return properties;
    }
}
