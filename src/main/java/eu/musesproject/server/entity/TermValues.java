package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the term_values database table.
 * 
 */
@Entity
@Table(name="term_values")
@NamedQuery(name="TermValues.findAll", query="SELECT t FROM TermValues t")
public class TermValues implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="value_id", unique=true, nullable=false)
	private String valueId;

	@Column(length=100)
	private String description;

	@Column(nullable=false, length=50)
	private String value;

	//bi-directional many-to-one association to Dictionary
	@ManyToOne
	@JoinColumn(name="term_id", nullable=false)
	private Dictionary dictionary;

	public TermValues() {
	}

	public String getValueId() {
		return this.valueId;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Dictionary getDictionary() {
		return this.dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

}