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
import org.fabric3.samples.bigbank.api.message.LoanApplication;
import org.fabric3.samples.bigbank.api.message.LoanApplicationStatus;
import org.fabric3.samples.bigbank.api.message.LoanApplicationSubmission;

/**
 * Maps the {@link LoanService} to REST/HTTP using JAX-RS.
 *
 * @version $Rev$ $Date$
 */
@Path("/")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class RsLoanService {
    @Reference
    protected LoanService loanService;

    @POST
    @Path("application")
    public LoanApplicationSubmission apply(LoanApplication application) {
        return loanService.apply(application);
    }

    @GET
    @Path("application/{tracking}")
    public LoanApplicationStatus getStatus(@PathParam("tracking") String trackingNumber) {
        LoanApplicationStatus status = loanService.getStatus(trackingNumber);
        if (LoanApplicationStatus.INVALID.equals(status.getStatus())) {
            throw new WebApplicationException(404);
        }
        return status;

    }

}
