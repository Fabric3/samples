
package org.fabric3.samples.bigbank.api.loan;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.fabric3.samples.bigbank.api.loan package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LoanApplication_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "loanApplication");
    private final static QName _ApplyResponse_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "applyResponse");
    private final static QName _GetStatusResponse_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "getStatusResponse");
    private final static QName _Apply_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "apply");
    private final static QName _LoanApplicationStatus_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "loanApplicationStatus");
    private final static QName _GetStatus_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "getStatus");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.fabric3.samples.bigbank.api.loan
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LoanApplicationStatus }
     * 
     */
    public LoanApplicationStatus createLoanApplicationStatus() {
        return new LoanApplicationStatus();
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link LoanApplication }
     * 
     */
    public LoanApplication createLoanApplication() {
        return new LoanApplication();
    }

    /**
     * Create an instance of {@link ApplyResponse }
     * 
     */
    public ApplyResponse createApplyResponse() {
        return new ApplyResponse();
    }

    /**
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link Apply }
     * 
     */
    public Apply createApply() {
        return new Apply();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoanApplication }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "loanApplication")
    public JAXBElement<LoanApplication> createLoanApplication(LoanApplication value) {
        return new JAXBElement<LoanApplication>(_LoanApplication_QNAME, LoanApplication.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ApplyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "applyResponse")
    public JAXBElement<ApplyResponse> createApplyResponse(ApplyResponse value) {
        return new JAXBElement<ApplyResponse>(_ApplyResponse_QNAME, ApplyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "getStatusResponse")
    public JAXBElement<GetStatusResponse> createGetStatusResponse(GetStatusResponse value) {
        return new JAXBElement<GetStatusResponse>(_GetStatusResponse_QNAME, GetStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Apply }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "apply")
    public JAXBElement<Apply> createApply(Apply value) {
        return new JAXBElement<Apply>(_Apply_QNAME, Apply.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoanApplicationStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "loanApplicationStatus")
    public JAXBElement<LoanApplicationStatus> createLoanApplicationStatus(LoanApplicationStatus value) {
        return new JAXBElement<LoanApplicationStatus>(_LoanApplicationStatus_QNAME, LoanApplicationStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "getStatus")
    public JAXBElement<GetStatus> createGetStatus(GetStatus value) {
        return new JAXBElement<GetStatus>(_GetStatus_QNAME, GetStatus.class, null, value);
    }

}
