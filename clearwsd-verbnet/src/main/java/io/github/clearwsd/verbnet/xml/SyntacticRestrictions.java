package io.github.clearwsd.verbnet.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * VerbNet syntactic restrictions.
 *
 * @author jgung
 */
@Data
@Accessors(fluent = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SYNRESTRS")
public class SyntacticRestrictions {

    @XmlAttribute(name = "logic")
    private String logic = "";

    @XmlElement(name = "SYNRESTR")
    private List<SyntacticRestriction> syntacticRestrictions = new ArrayList<>();

}
