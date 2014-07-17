package eu.musesproject.server.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the term_values database table.
 * 
 */
@Entity
@Table(name="term_values")
@NamedQuery(name="TermValue.findAll", query="SELECT t FROM TermValue t")
public class TermValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="value_id")
	private String valueId;

	private String description;

	private String value;

	//bi-directional many-to-one association to Dictionary
	@ManyToOne
	@JoinColumn(name="term_id")
	private Dictionary dictionary;

	public TermValue() {
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