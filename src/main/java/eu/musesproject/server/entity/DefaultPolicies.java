package eu.musesproject.server.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the corporate_policies database table.
 * 
 */
@Entity
@Table(name="default_policies")
@NamedQuery(name="DefaultPolicies.findAll", query="SELECT c FROM DefaultPolicies c")
public class DefaultPolicies implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="default_policy_id", unique=true, nullable=false)
	private int defaultPolicyId;

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

	public DefaultPolicies() {
	}

	public int getCorporatePolicyId() {
		return this.defaultPolicyId;
	}

	public void setCorporatePolicyId(int defaultPolicyId) {
		this.defaultPolicyId = defaultPolicyId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescriptionEn() {
		return this.description;
	}

	public void setDescriptionEn(String description) {
		this.description = description;
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