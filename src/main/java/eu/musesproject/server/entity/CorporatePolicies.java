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
	private String description;

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