package com.example.LR1;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "receivers", namespace = "http://namespaceGrandvil.com/System")
public class Receivers {
    Integer idref;

    public Integer getIdref() {
        return idref;
    }

    @XmlAttribute(name = "idref")
    public void setIdref(Integer idref) {
        this.idref = idref;
    }
}
