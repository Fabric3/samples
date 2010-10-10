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
package org.fabric3.samples.bigbank.api.loan;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.oasisopen.sca.annotation.Remotable;

import org.fabric3.samples.bigbank.api.message.LoanApplication;
import org.fabric3.samples.bigbank.api.message.LoanRequest;
import org.fabric3.samples.bigbank.api.message.LoanStatus;
import org.fabric3.samples.bigbank.api.message.OptionSelection;

/**
 * Service responsible for processing a loan application.
 *
 * @version $Rev$ $Date$
 */
@Remotable
@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface LoanService {
    int SUBMITTED = 1;
    int PRICING = 2;
    int AWAITING_ACCEPTANCE = 3;
    int AWAITING_APPRAISAL = 4;
    int SCHEDULED_FOR_FUNDING = 5;
    int FUNDED = 6;
    int REJECTED = -1;
    int DECLINED = -2;

    /**
     * Initiates a loan application.
     *
     * @param request the loan data
     * @return the loan tracking number
     * @throws LoanException if an error initiating the application occurs
     */
    @POST
    @Path("application")
    long apply(LoanRequest request) throws LoanException;

    /**
     * Returns an in-process loan application.
     *
     * @param id the loan tracking number
     * @return the loan application
     * @throws LoanException if a retrieval exception was encountered. For example, LoanApplicationNotFoundException.
     */
    @GET
    @Path("application/{id}")
    LoanApplication retrieve(@PathParam("id") long id) throws LoanException;

    /**
     * Returns active loan applications for the current user with the given status.
     *
     * @param status the loan status
     * @return all active loan applications for the user
     * @throws LoanException if a retrieval exception was encountered. For example, LoanApplicationNotFoundException.
     */
    @GET
    @Path("applications/{status}")
    List<LoanApplication> retrieveApplications(@PathParam("status") int status) throws LoanException;

    /**
     * Returns the status of all active loan applications for the current user.
     *
     * @return the status of all active loan applications for the user
     * @throws LoanException if a retrieval exception was encountered. For example, LoanApplicationNotFoundException.
     */
    @GET
    @Path("status")
    List<LoanStatus> getLoanStatus() throws LoanException;

    /**
     * Declines the terms of a loan application.
     *
     * @param id the loan tracking number
     * @throws LoanException if an error declining the loan occurs
     */
    void decline(long id) throws LoanException;

    /**
     * Accepts the terms of a loan application.
     *
     * @param selection contains the loan tracking number and selected terms
     * @throws LoanException ÊÂ if an error accepting the loan occurs
     */
    @POST
    @Path("/application/selection")
    void accept(OptionSelection selection) throws LoanException;

}