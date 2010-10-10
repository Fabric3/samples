/*
 * Copyright (c) 2010 Metaform Systems
 *
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
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
package org.fabric3.samples.bigbank.services.pricing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision$ $Date$
 */
public class PriceResponse implements Serializable {
    private static final long serialVersionUID = 3009581081316393018L;

    private List<PricingOption> options = new ArrayList<PricingOption>();
    private long id;

    public PriceResponse(long id) {
        this.id = id;
    }

    public List<PricingOption> getOptions() {
        return options;
    }

    public void addOption(PricingOption option) {
        options.add(option);
    }

    public long getId() {
        return id;
    }
}
