package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class UsuarioF extends Model {

    public String usernome;
    public String token;
    public String secret;

    public UsuarioF(String usernome) {
        this.usernome = usernome;
    }

    public static UsuarioF findOrCreate(String usernome) {
    	UsuarioF usuario = UsuarioF.find("usernome", usernome).first();
        if (usuario == null) {
        	usuario = new UsuarioF(usernome);
        }
        return usuario;
    }

}
