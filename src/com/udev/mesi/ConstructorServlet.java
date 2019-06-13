package com.udev.mesi;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.hibernate.SessionFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.udev.mesi.messages.WsGetConstructors;
import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.models.WsConstructor;

import main.java.com.udev.mesi.entities.Constructor;

@Path("/constructor")
public class ConstructorServlet {
	
	private final String UNIT_NAME = "udevmesi";
 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public WsResponse get() throws JSONException {
 
		// Initialisation de la réponse JSON
		WsResponse response;
		
		try {
			
			// Création du gestionnaire d'entités
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			EntityManager em = emf.createEntityManager();		
			
			// Récupération des constructeurs depuis la base de données
			Query query = em.createQuery("from Constructor");
			List<Constructor> constructors = query.getResultList();
			
			// Création de la réponse JSON
			response = new WsGetConstructors("OK", null, constructors.toArray());
			
			// Fermeture du gestionnaire d'entités
			em.close();
			emf.close();
	 
			// Renvoi de la réponse
		} catch (Exception e) {
			response = new WsResponse("KO", e.getMessage());
		}
		
		return response;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	public Response create(final MultivaluedMap<String, String> formParams) throws JSONException {
		
		// Initialisation de la réponse JSON
		JSONObject jsonObject;
				
		try {
			jsonObject = new JSONObject();
			
			// Vérification des paramètres
			if (!isValidConstructor(formParams)) {
				throw new Exception("Le constructeur n'est pas correct. Veuillez renseigner les valeurs suivantes: 'name'");
			}
			
			// Création du gestionnaire d'entités
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			EntityManager em = emf.createEntityManager();
			/*EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			EntityManager em = emf.createEntityManager();*/
			
			// Création du constructeur
			Constructor constructor = new Constructor();
			constructor.name = formParams.get("name").get(0);
			
			// Persistence du constructeur
			em.getTransaction().begin();			
			em.flush();
			em.persist(constructor);
			em.getTransaction().commit();
			
			// Fermeture du gestionnaire d'entités
			em.close();
			//emf.close();
			
			jsonObject.put("status", "OK");
			
			return Response.status(200).entity(jsonObject.toString()).build();
		} catch (Exception e) {
			jsonObject = new JSONObject();			
			jsonObject.put("message", e.getMessage());
			jsonObject.put("status", "KO");
			return Response.status(500).entity(jsonObject.toString()).build();
		}
	}
	
	private boolean isValidConstructor(final MultivaluedMap<String, String> formParams) {
		if (formParams.containsKey("name")) {
			if (formParams.get("name").size() == 1) {
				return true;
			}
		}
		return false;
	}
}
