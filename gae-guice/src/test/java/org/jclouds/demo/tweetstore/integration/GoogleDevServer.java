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
package org.jclouds.demo.tweetstore.integration;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.google.appengine.tools.KickStart;
import com.google.appengine.tools.info.SdkInfo;

/**
 * Basic functionality to start a local google app engine instance.
 *
 * @author Adrian Cole
 */
public class GoogleDevServer {

    Thread server;

    public void writePropertiesAndStartServer(final String address,
                                              final String port, final String warfile, Properties props)
            throws IOException, InterruptedException {
        String filename = String.format(
                "%1$s/WEB-INF/jclouds.properties", warfile);
        System.err.println("file: " + filename);
        storeProperties(filename, props);
        assert new File(filename).exists();
        this.server = new Thread(new Runnable() {
            public void run() {
                String sdkRoot = checkNotNull(System.getProperty(SdkInfo.SDK_ROOT_PROPERTY), SdkInfo.SDK_ROOT_PROPERTY);
                KickStart.main(new String[] {
                        KickStarter.systemProperty("java.util.logging.config.file", 
                                format("%s/WEB-INF/logging.properties", warfile)),
                        KickStarter.systemProperty(SdkInfo.SDK_ROOT_PROPERTY, sdkRoot),
                        "com.google.appengine.tools.development.DevAppServerMain",
                        "--disable_update_check", 
                        format("--sdk_root=%s", sdkRoot), 
                        "-a", address, "-p", port, warfile });
            }
        });
        server.start();
        TimeUnit.SECONDS.sleep(60);
    }

    private static void storeProperties(String filename, Properties props) throws IOException {
        FileOutputStream targetFile = new FileOutputStream(filename);
        try {
            props.store(targetFile, "test");
        } finally {
            closeQuietly(targetFile);
        }
    }

    public void stop() throws Exception {
        // KickStart.main opens a process and calls process.waitFor(), which is interruptable
        server.interrupt();
    }
    
    private static class KickStarter {
        private static String systemProperty(String key, String value) {
            return format("--jvm_flag=-D%s=%s", key, value);
        }
    }
}