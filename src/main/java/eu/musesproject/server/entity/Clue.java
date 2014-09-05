package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the clue database table.
 * 
 */
@Entity
@Table(name="clue")
@NamedQuery(name="Clue.findAll", query="SELECT c FROM Clue c")
public class Clue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="clue_id")
	private String clueId;

	@Lob
	private String value;

	public Clue() {
	}

	public String getClueId() {
		return this.clueId;
	}

	public void setClueId(String clueId) {
		this.clueId = clueId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}