package org.fabric3.samples.bigbank.loan.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.oasisopen.sca.annotation.Reference;

import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.loan.LoanApplication;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationSubmission;

/**
 * Maps the {@link LoanService} to REST/HTTP using JAX-RS.
 *
 * @version $Rev$ $Date$
 */
@Path("/")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface RsLoanService {
    @POST
    @Path("application")
    public void apply(LoanApplication application);

    @GET
    @Path("application/{correlation}")
    public LoanApplicationStatus getRSStatus(@PathParam("correlation") String correlation);

}
