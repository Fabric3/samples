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
package org.fabric3.samples.channel;

/**
 * A channel that receives a serialized message publishes it to consumers. In a full-featured application, this channel would typically be bound using ZeroMQ or
 * another transport. Received messages could be a protocol buffer or other serialization technology.
 */
public interface WorkChannel {

    /**
     * Send the serialized event.
     *
     * @param event the event
     */
    void send(byte[] event);

}
