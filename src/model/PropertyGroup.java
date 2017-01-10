package model;

import java.util.List;

public class PropertyGroup {

	String name;
	String uri;
	int show_default;	
	int consolidated;
	int mapping;
	int instances;
	List<Property> propertyList;
	
		
	public int getInstances() {
		return instances;
	}
	public void setInstances(int instances) {
		this.instances = instances;
	}
	public int getMapping() {
		return mapping;
	}
	public void setMapping(int mapping) {
		this.mapping = mapping;
	}
	public int getShow_default() {
		return show_default;
	}
	public void setShow_default(int show_default) {
		this.show_default = show_default;
	}
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
