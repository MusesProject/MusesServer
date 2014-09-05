package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the outcome database table.
 * 
 */
@Entity
@Table(name="outcome")
@NamedQuery(name="Outcome.findAll", query="SELECT o FROM Outcome o")
public class Outcome implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="outcome_id")
	private int outcomeId;

	private double costbenefit;

	@Lob
	private String description;

	//bi-directional many-to-one association to Threat
	@ManyToOne
	@JoinColumn(name="threat_id")
	private Threat threat;

	public Outcome() {
	}

	public int getOutcomeId() {
		return this.outcomeId;
	}

	public void setOutcomeId(int outcomeId) {
		this.outcomeId = outcomeId;
	}

	public double getCostbenefit() {
		return this.costbenefit;
	}

	public void setCostbenefit(double costbenefit) {
		this.costbenefit = costbenefit;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Threat getThreat() {
		return this.threat;
	}

	public void setThreat(Threat threat) {
		this.threat = threat;
	}

}