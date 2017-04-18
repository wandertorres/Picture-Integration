package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.GenericModel;

@Entity
public class User extends GenericModel {

    @Id
    //@GeneratedValue
	public String id;
	public String email;
	
}
