package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Arrays;
import java.util.Date;


/**
 * The persistent class for the corporate_policies database table.
 * 
 */
@Entity
@Table(name="corporate_policies")
@NamedQuery(name="CorporatePolicies.findAll", query="SELECT c FROM CorporatePolicies c")
public class CorporatePolicies implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="corporate_policy_id", unique=true, nullable=false)
	private int corporatePolicyId;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date date;

	@Column(nullable=false, length=2000)
	private String description_en;

	@Column(nullable=false, length=2000)
	private String description_es;
	
	@Lob
	@Column(nullable=false)
	private byte[] file;

	@Column(nullable=false, length=2000)
	private String name;

	public CorporatePolicies() {
	}

	public int getCorporatePolicyId() {
		return this.corporatePolicyId;
	}

	public void setCorporatePolicyId(int corporatePolicyId) {
		this.corporatePolicyId = corporatePolicyId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescriptionEn() {
		return this.description_en;
	}

	public void setDescriptionEn(String description_en) {
		this.description_en = description_en;
	}

	public String getDescriptionEs() {
		return this.description_es;
	}

	public void setDescriptionEs(String description_es) {
		this.description_es = description_es;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] fileArray) {
		this.file = Arrays.copyOf(fileArray,fileArray.length);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}