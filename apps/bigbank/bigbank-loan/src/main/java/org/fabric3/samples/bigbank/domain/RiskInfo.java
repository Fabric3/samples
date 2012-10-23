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
package org.fabric3.samples.bigbank.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import org.fabric3.samples.bigbank.api.Versionable;

/**
 * @version $Revision$ $Date$
 */
@Entity
public class RiskInfo extends Versionable {
    private static final long serialVersionUID = 1427555176373119897L;

    private RiskDecision decision;
    private List<RiskReasonInfo> reasons;

    public RiskInfo(RiskDecision decision, List<RiskReasonInfo> reasons) {
        this.decision = decision;
        this.reasons = reasons;
    }

    public RiskInfo() {
    }

    public RiskDecision getDecision() {
        return decision;
    }

    public void setDecision(RiskDecision decision) {
        this.decision = decision;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<RiskReasonInfo> getReasons() {
        return reasons;
    }

    public void setReasons(List<RiskReasonInfo> reasons) {
        this.reasons = reasons;
    }

}
