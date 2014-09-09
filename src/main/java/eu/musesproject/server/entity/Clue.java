package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the clue database table.
 * 
 */
@Entity
@Table(name="clue")
@NamedQueries({
@NamedQuery(name="Clue.findAll", query="SELECT c FROM Clue c"),
@NamedQuery(name="Clue.findByValue", 
query="SELECT c FROM Clue c where c.value = :value")
})
public class Clue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="clue_id")
	private int clueId;

	@Lob
	private String value;

	public Clue() {
	}

	public int getClueId() {
		return this.clueId;
	}

	public void setClueId(int clueId) {
		this.clueId = clueId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}