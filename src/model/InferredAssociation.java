package model;

public class InferredAssociation {

	public String association_uri;
	public String association_name;
	public String concept_uri; //associated concept
	public String concept_name; //associated concept
	public String action;
	
	
	public String getAssociation_uri() {
		return association_uri;
	}
	public void setAssociation_uri(String association_uri) {
		this.association_uri = association_uri;
	}
	public String getAssociation_name() {
		return association_name;
	}
	public void setAssociation_name(String association_name) {
		this.association_name = association_name;
	}
	public String getConcept_uri() {
		return concept_uri;
	}
	public void setConcept_uri(String concept_uri) {
		this.concept_uri = concept_uri;
	}
	public String getConcept_name() {
		return concept_name;
	}
	public void setConcept_name(String concept_name) {
		this.concept_name = concept_name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
	
	
}
