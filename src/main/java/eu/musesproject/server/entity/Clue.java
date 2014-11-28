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
		@NamedQuery(name="Clue.findAll", 
					query="SELECT c FROM Clue c"),
		@NamedQuery(name="Clue.findByValue", 
					query="SELECT c FROM Clue c where c.value = :value"),
		@NamedQuery(name="Clue.deleteClueByValue", 
					query="delete FROM Clue c where c.value = :value")
})
public class Clue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="clue_id", unique=true, nullable=false)
	private String clueId;

	@Lob
	@Column(nullable=false)
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