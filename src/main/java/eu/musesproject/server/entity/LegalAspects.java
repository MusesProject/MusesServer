package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the legal_aspects database table.
 * 
 */
@Entity
@Table(name="legal_aspects")
@NamedQuery(name="LegalAspects.findAll", query="SELECT l FROM LegalAspects l")
public class LegalAspects implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false, length=50)
	private String description;

	@Column(name="data_complete_erasure", nullable=false)
	private byte[] dataCompleteErasure;

	@Column(nullable=false)
	private int EP_hard_limit;

	@Column(nullable=false)
	private int KRS_hard_limit;

	@Column(nullable=false)
	private int RT2AE_hard_limit;

	public LegalAspects() {
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getDataCompleteErasure() {
		return this.dataCompleteErasure;
	}

	public void setDataCompleteErasure(byte[] dataCompleteErasure) {
		this.dataCompleteErasure = dataCompleteErasure;
	}

	public int getEP_hard_limit() {
		return this.EP_hard_limit;
	}

	public void setEP_hard_limit(int EP_hard_limit) {
		this.EP_hard_limit = EP_hard_limit;
	}

	public int getKRS_hard_limit() {
		return this.KRS_hard_limit;
	}

	public void setKRS_hard_limit(int KRS_hard_limit) {
		this.KRS_hard_limit = KRS_hard_limit;
	}

	public int getRT2AE_hard_limit() {
		return this.RT2AE_hard_limit;
	}

	public void setRT2AE_hard_limit(int RT2AE_hard_limit) {
		this.RT2AE_hard_limit = RT2AE_hard_limit;
	}

}