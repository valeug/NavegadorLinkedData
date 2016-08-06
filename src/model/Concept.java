package model;

import java.util.List;

public class Concept {

	private String uri;
	private String name;
	private String definition;
	private List<Concept> linkedTerms;
	private List<Concept> similarTerms;
	
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
	
	public List<Concept> getLinkedTerms() {
		return linkedTerms;
	}
	public void setLinkedTerms(List<Concept> linkedTerms) {
		this.linkedTerms = linkedTerms;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public List<Concept> getSimilarTerms() {
		return similarTerms;
	}
	public void setSimilarTerms(List<Concept> similarTerms) {
		this.similarTerms = similarTerms;
	}
}
