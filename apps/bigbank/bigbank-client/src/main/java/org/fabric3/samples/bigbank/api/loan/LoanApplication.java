package org.fabric3.samples.bigbank.api.loan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loanApplication complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="loanApplication">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="clientCorrelation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ein" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loanApplication", propOrder = {
        "amount",
        "clientCorrelation",
        "ein",
        "notificationAddress"
})
public class LoanApplication {

    protected double amount;
    protected String clientCorrelation;
    protected String ein;
    protected String notificationAddress;

    /**
     * Gets the value of the amount property.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Gets the value of the clientCorrelation property.
     *
     * @return possible object is {@link String }
     */
    public String getClientCorrelation() {
        return clientCorrelation;
    }

    /**
     * Sets the value of the clientCorrelation property.
     *
     * @param value allowed object is {@link String }
     */
    public void setClientCorrelation(String value) {
        this.clientCorrelation = value;
    }

    /**
     * Gets the value of the ein property.
     *
     * @return possible object is {@link String }
     */
    public String getEin() {
        return ein;
    }

    /**
     * Sets the value of the ein property.
     *
     * @param value allowed object is {@link String }
     */
    public void setEin(String value) {
        this.ein = value;
    }

    public String getNotificationAddress() {
        return notificationAddress;
    }

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }
}
