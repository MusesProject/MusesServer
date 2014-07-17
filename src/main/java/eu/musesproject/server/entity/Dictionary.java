package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the dictionary database table.
 * 
 */
@Entity
@NamedQuery(name="Dictionary.findAll", query="SELECT d FROM Dictionary d")
public class Dictionary implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="term_id")
	private String termId;

	private String description;

	private String position;

	@Column(name="term_name")
	private String termName;

	private String type;

	//bi-directional many-to-one association to TermValue
	@OneToMany(mappedBy="dictionary")
	private List<TermValue> termValues;

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

	public List<TermValue> getTermValues() {
		return this.termValues;
	}

	public void setTermValues(List<TermValue> termValues) {
		this.termValues = termValues;
	}

	public TermValue addTermValue(TermValue termValue) {
		getTermValues().add(termValue);
		termValue.setDictionary(this);

		return termValue;
	}

	public TermValue removeTermValue(TermValue termValue) {
		getTermValues().remove(termValue);
		termValue.setDictionary(null);

		return termValue;
	}

}