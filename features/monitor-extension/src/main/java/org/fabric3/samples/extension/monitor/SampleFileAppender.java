/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.fabric3.samples.extension.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.api.host.runtime.HostInfo;
import org.fabric3.monitor.spi.appender.Appender;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

/**
 * A monitor appender extension that writes monitor events to a file located in the runtimes/[runtime name]/data directory.
 */
public class SampleFileAppender implements Appender {
    private HostInfo info;
    private String fileName = "sample.log";

    private File file;
    private FileOutputStream stream;
    private FileChannel fileChannel;

    public SampleFileAppender(@Reference HostInfo info) {
        this.info = info;
    }

    /**
     * Optionally injects a file name
     *
     * @param fileName the file name
     */
    @Property(required = false)
    public void setFileName(String fileName) {

        this.fileName = fileName;
    }

    @Init
    public void start() {
        this.file = new File(info.getDataDir(), fileName);
        initializeChannel();
    }

    @Destroy
    public void stop() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                throw new Fabric3Exception(e);
            }
            stream = null;
        }
    }

    public void write(ByteBuffer buffer) {
        try {
            fileChannel.write(buffer);
        } catch (IOException e) {
            throw new Fabric3Exception(e);
        }
    }

    private void initializeChannel() {
        try {
            stream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            throw new Fabric3Exception(e);
        }
        fileChannel = stream.getChannel();
    }

}
