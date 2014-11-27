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
		if (file == null) {
			this.file = new byte[0];
		} else {
			this.file = Arrays.copyOf(file, file.length);
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}