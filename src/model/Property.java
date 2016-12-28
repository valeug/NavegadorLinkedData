package model;

public class Property {
	
	int id;
	String uri;
	String name;
	String description;
	String value;
	int is_mapping;
	int target;
	/* session */
	int show_default; //configuration
	int add; // propiedad agregar en la sesion actual
	int newProperty;
	int consolidated;
	
	public int getConsolidated() {
		return consolidated;
	}
	public void setConsolidated(int consolidated) {
		this.consolidated = consolidated;
	}
	public int getNewProperty() {
		return newProperty;
	}
	public void setNewProperty(int newProperty) {
		this.newProperty = newProperty;
	}
	public int getAdd() {
		return add;
	}
	public void setAdd(int add) {
		this.add = add;
	}
	public int getShow_default() {
		return show_default;
	}
	public void setShow_default(int show_default) {
		this.show_default = show_default;
	}
	public int getTarget() {
		return target;
	}
	public void setTarget(int target) {
		this.target = target;
	}
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
