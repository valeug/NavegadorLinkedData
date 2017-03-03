package model;

import java.util.List;

public class Concept {

	public String uri;
	public String name;
	private String definition;
	private String dataset;	
	private String classType;
	private List<Property> properties; //podria incluir "name", "definition" (aunque son propiedades importantes)
	private List<PropertyGroup> propertyGroups; // list of list of properties	
	private List<Concept> linkedTerms;
	private List<Concept> similarTerms;
	
	
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	public List<PropertyGroup> getPropertyGroups() {
		return propertyGroups;
	}
	public void setPropertyGroups(List<PropertyGroup> propertyGroups) {
		this.propertyGroups = propertyGroups;
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
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public List<Concept> getLinkedTerms() {
		return linkedTerms;
	}
	public void setLinkedTerms(List<Concept> linkedTerms) {
		this.linkedTerms = linkedTerms;
	}
	public List<Concept> getSimilarTerms() {
		return similarTerms;
	}
	public void setSimilarTerms(List<Concept> similarTerms) {
		this.similarTerms = similarTerms;
	}
	
}
