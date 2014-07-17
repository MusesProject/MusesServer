package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the corporate_policies database table.
 * 
 */
@Entity
@Table(name="corporate_policies")
@NamedQuery(name="CorporatePolicy.findAll", query="SELECT c FROM CorporatePolicy c")
public class CorporatePolicy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="corporate_policy_id")
	private int corporatePolicyId;

	@Temporal(TemporalType.DATE)
	private Date date;

	private String description;

	@Lob
	private byte[] file;

	private String name;

	public CorporatePolicy() {
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getFile() {
		return this.file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}