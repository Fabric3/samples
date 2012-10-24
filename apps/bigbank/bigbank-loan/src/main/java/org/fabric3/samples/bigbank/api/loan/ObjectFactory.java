package org.fabric3.samples.bigbank.api.loan;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _LoanApplication_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "loanApplication");
    private final static QName _LoanApplicationStatus_QNAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "loanApplicationStatus");

    public ObjectFactory() {
    }

    public LoanApplication createLoanApplication() {
        return new LoanApplication();
    }

    public LoanApplicationStatus createLoanApplicationStatus() {
        return new LoanApplicationStatus();
    }

    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "loanApplication")
    public JAXBElement<LoanApplication> createLoanApplication(LoanApplication value) {
        return new JAXBElement<LoanApplication>(_LoanApplication_QNAME, LoanApplication.class, null, value);
    }

    @XmlElementDecl(namespace = "http://loan.api.bigbank.samples.fabric3.org/", name = "loanApplicationStatus")
    public JAXBElement<LoanApplicationStatus> createLoanApplicationStatus(LoanApplicationStatus value) {
        return new JAXBElement<LoanApplicationStatus>(_LoanApplicationStatus_QNAME, LoanApplicationStatus.class, null, value);
    }

}
