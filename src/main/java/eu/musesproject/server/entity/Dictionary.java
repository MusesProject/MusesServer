package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the dictionary database table.
 * 
 */
@Entity
@Table(name="dictionary")
@NamedQuery(name="Dictionary.findAll", query="SELECT d FROM Dictionary d")
public class Dictionary implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="term_id", unique=true, nullable=false)
	private String termId;

	@Column(nullable=false, length=100)
	private String description;

	@Column(nullable=false, length=1)
	private String position;

	@Column(name="term_name", nullable=false, length=50)
	private String termName;

	@Column(nullable=false, length=30)
	private String type;

	//bi-directional many-to-one association to TermValues
	@OneToMany(mappedBy="dictionary")
	private List<TermValues> termValues;

	public Dictionary() {
	}

	public String getTermId() {
		return this.termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTermName() {
		return this.termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<TermValues> getTermValues() {
		return this.termValues;
	}

	public void setTermValues(List<TermValues> termValues) {
		this.termValues = termValues;
	}

	public TermValues addTermValue(TermValues termValue) {
		getTermValues().add(termValue);
		termValue.setDictionary(this);

		return termValue;
	}

	public TermValues removeTermValue(TermValues termValue) {
		getTermValues().remove(termValue);
		termValue.setDictionary(null);

		return termValue;
	}

}