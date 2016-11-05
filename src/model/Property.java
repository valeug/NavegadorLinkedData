package model;

public class Property {
	
	int id;
	String uri;
	String name;
	String description;
	String value;
	int is_mapping;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIs_mapping() {
		return is_mapping;
	}
	public void setIs_mapping(int is_mapping) {
		this.is_mapping = is_mapping;
	}
	
	
	
	
}