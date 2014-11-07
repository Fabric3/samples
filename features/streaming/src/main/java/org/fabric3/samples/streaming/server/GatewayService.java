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
package org.fabric3.samples.streaming.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;

/**
 * Main entry point for the streaming application example.
 * <p/>
 * This example demonstrates how to receive large batch files that are processed in a streaming fashion. The batch file consists of individual records that are
 * processed by {@link EntryHandler}s based on record type (the QName of the entry element). This design supports message extensibility by allowing handlers to
 * be added that support new entry types.
 * <p/>
 * The input stream source is configured by SCA bindings in the composite file for this application. Two bindings are configured: JAX-RS (REST) and the file
 * system (an input directory).
 */
@Path("/")
@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
public interface GatewayService {

    /**
     * Receives an XML consisting of multiple records and dispatches them to a corresponding processor.
     *
     * @param stream the source stream
     */
    @POST
    @Path("/upload")
    void receive(InputStream stream);

}
