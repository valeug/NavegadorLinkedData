package model;

import java.util.List;

public class Concept {

	private String name;
	private String description;
	private List<Concept> linkedTerms;
	
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
	public List<Concept> getLinkedTerms() {
		return linkedTerms;
	}
	public void setLinkedTerms(List<Concept> linkedTerms) {
		this.linkedTerms = linkedTerms;
	}
	
}
