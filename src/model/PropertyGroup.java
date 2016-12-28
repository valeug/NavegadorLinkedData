package model;

import java.util.List;

public class PropertyGroup {

	String name;
	String uri;
	int consolidated;
	List<Property> propertyList;
	
	
	public String getName() {
		return name;
	}
	public int getConsolidated() {
		return consolidated;
	}
	public void setConsolidated(int consolidated) {
		this.consolidated = consolidated;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Property> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(List<Property> propertyList) {
		this.propertyList = propertyList;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
