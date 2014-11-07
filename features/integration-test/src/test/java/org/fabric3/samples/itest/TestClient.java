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
package org.fabric3.samples.itest;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.implementation.junit.Fabric3Runner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasisopen.sca.annotation.Reference;

/**
 * The client test component. All methods annotated with the {@link Test} annotation will be invoked by the runtime.
 */
@RunWith(Fabric3Runner.class)
@Component(composite = "{urn:fabric3.org:samples}TestComposite")
public class TestClient {

    // Inject the EasyMock control
    @Reference
    protected IMocksControl control;

    // Inject the mock service
    @Reference
    protected ExternalService externalService;

    @Reference
    protected GatewayService gatewayService;

    @Test
    public void invokeOverRemoteTransport() {
        // setup the expectations
        control.reset();
        EasyMock.expect(externalService.invoke(EasyMock.eq("test"))).andReturn("test");
        control.replay();

        // invoke the service
        Assert.assertEquals("test", gatewayService.request("test"));

        // verify the mocks
        control.verify();
    }
}
