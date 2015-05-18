package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the list_ofpossible_risktreatments database table.
 * 
 */
@Entity
@Table(name="list_ofpossible_risktreatments")
@NamedQueries({
	@NamedQuery(name="ListOfpossibleRisktreatment.findbyId", 
			query="SELECT l FROM ListOfpossibleRisktreatment l where l.listofpossiblerisktreatmentId = :risktreatment_id"),
@NamedQuery(name="ListOfpossibleRisktreatment.findAll", query="SELECT l FROM ListOfpossibleRisktreatment l")
})
public class ListOfpossibleRisktreatment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="listofpossiblerisktreatment_id")
	private int listofpossiblerisktreatmentId;

	private String description;

	private String french;

	private String german;

	private String spanish;

	public ListOfpossibleRisktreatment() {
	}

	public int getListofpossiblerisktreatmentId() {
		return this.listofpossiblerisktreatmentId;
	}

	public void setListofpossiblerisktreatmentId(int listofpossiblerisktreatmentId) {
		this.listofpossiblerisktreatmentId = listofpossiblerisktreatmentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFrench() {
		return this.french;
	}

	public void setFrench(String french) {
		this.french = french;
	}

	public String getGerman() {
		return this.german;
	}

	public void setGerman(String german) {
		this.german = german;
	}

	public String getSpanish() {
		return this.spanish;
	}

	public void setSpanish(String spanish) {
		this.spanish = spanish;
	}

}