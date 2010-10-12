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
package org.fabric3.samples.bigbank.backoffice.risk;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Producer;
import org.fabric3.samples.bigbank.api.channel.LoanChannel;
import org.fabric3.samples.bigbank.api.domain.LoanRecord;
import org.fabric3.samples.bigbank.api.event.ManualRiskAssessmentComplete;

/**
 * Manages loan applications that have been sent for manual approval.
 *
 * @version $Rev$ $Date$
 */
@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//@RolesAllowed("underwriter")
@Scope("COMPOSITE")
@ManagedTransaction
public class RiskAssessmentWorkQueue {
    private EntityManager em;
    private LoanChannel channel;

    @Producer("loanChannel")
    public void setChannel(LoanChannel channel) {
        this.channel = channel;
    }

    @PersistenceContext(unitName = "loanApplication")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @GET
    @Path("records")
    public List<LoanRecord> getRecords() {
        Query query = em.createQuery("SELECT l FROM LoanRecord l WHERE l.status = 2");
        return cast(query.getResultList());
    }

    @PUT
    @Path("assessment")
    public void assess(ManualRiskAssessmentComplete assessment) {
        channel.publish(assessment);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T cast(Object object) {
        return (T) object;
    }


}
