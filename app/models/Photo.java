package models;

public class Photo {
	private String id;
	private String secret;
	private String owner;
	private String title;
	private String url;
	
	public Photo(String id, String secret, String owner, String title, String url) {
		super();
		this.id = id;
		this.secret = secret;
		this.owner = owner;
		this.title = title;
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
