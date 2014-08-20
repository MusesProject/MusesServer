package eu.musesproject.server.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the user_authorization database table.
 * 
 */
@Entity
@Table(name="user_authorization")
@NamedQueries({
	@NamedQuery(name="UserAuthorization.findAll", 
				query="SELECT u FROM UserAuthorization u"),
	@NamedQuery(name="UserAuthorization.findByUserID", 
				query="SELECT u FROM UserAuthorization u where u.userId = :userId")	
})
public class UserAuthorization implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_authorization_id")
	private int userAuthorizationId;

	@Column(name="role_id")
	private int roleId;

	@Column(name="user_id")
	private int userId;

	public UserAuthorization() {
	}

	public int getUserAuthorizationId() {
		return this.userAuthorizationId;
	}

	public void setUserAuthorizationId(int userAuthorizationId) {
		this.userAuthorizationId = userAuthorizationId;
	}

	public int getRoleId() {
		return this.roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}