package org.fabric3.samples.hibernate;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 */
public class Message {
    private Long id;
    private int version;
    private String text;
    private String creator;


    @XmlElement
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlElement
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    
}
