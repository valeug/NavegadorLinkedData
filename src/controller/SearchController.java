package controller;

import model.Concept;

public class SearchController {

	public static Concept getConcept(String cad){
		//demo (deberia obtener info de un query Sparql)
		Concept c = new Concept();
		c.setName(cad); // primero buscar si existe el termino en algun dataset (if)
		c.setDescription("cute thing");
		c.setLinkedTerms(null);
		
		return c;
	}
}
