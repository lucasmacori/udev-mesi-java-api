package com.udev.mesi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.util.MultivaluedMapImpl;
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
	public Response get() throws JSONException {
 
		// Initialisation de la réponse JSON
		int status = 200;
		WsResponse response;
		
		try {
			
			// Création du gestionnaire d'entités
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			EntityManager em = emf.createEntityManager();		
			
			// Récupération des constructeurs depuis la base de données
			Query query = em.createQuery("FROM Constructor WHERE is_active = true");
			List<Constructor> constructors = query.getResultList();
			
			// Création de la réponse JSON
			response = new WsGetConstructors("OK", null, constructors.toArray());
			
			// Fermeture du gestionnaire d'entités
			em.close();
			emf.close();
	 
			// Renvoi de la réponse
		} catch (Exception e) {
			if (status == 200) status = 500;
			response = new WsResponse("KO", e.getMessage());
		}
		
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response create(final MultivaluedMap<String, String> formParams) throws JSONException {

		// Initialisation de la réponse JSON
		int status = 200;
		WsResponse response;
				
		try {

			// Vérification des paramètres
			if (!isValidConstructor(formParams, false)) {
				status = 400;
				throw new Exception("Le constructeur n'est pas correct. Veuillez renseigner les valeurs suivantes: 'name'");
			}
			
			// Création du constructeur
			// TODO: Vérifier si le constructeur n'existe pas déja (si inatif, le réactiver)
			Constructor constructor = new Constructor();
			constructor.name = formParams.get("name").get(0);

			// Persistence du constructeur
			saveConstructor(constructor);
			
			response = new WsResponse("OK", null);
		} catch (Exception e) {
			if (status == 200) status = 500;
			response = new WsResponse("KO", e.getMessage());
		}

		return Response.status(status).entity(response).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response update(final MultivaluedMap<String, String> formParams) throws JSONException {

		// Initialisation de la réponse JSON
		int status = 200;
		WsResponse response;

		try {

			// Vérification des paramètres
			if (!isValidConstructor(formParams, true)) {
				status = 400;
				throw new Exception("Le constructeur n'est pas correct. Veuillez renseigner les valeurs suivantes: 'id', 'name'");
			}

			// Création du gestionnaire d'entités
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			EntityManager em = emf.createEntityManager();

			// Récupération du constructeur
			try {
				long id = Long.parseLong(formParams.get("id").get(0));

				Constructor constructor = em.find(Constructor.class, id);

				if (constructor == null || !constructor.isActive) {
					throw new Exception("Le constructeur avec l'ID '" + id + "' n'existe pas");
				}

				// Modification du constructeur
				constructor.name = formParams.get("name").get(0);

				// Persistence du constructeur
				saveConstructor(constructor);

				// Fermeture du gestionnaire d'entités
				em.close();
				emf.close();

				response = new WsResponse("OK", null);
			} catch (NumberFormatException e) {
				status = 400;
				throw new Exception("L'id entré n'est pas un nombre entier");
			}
		} catch (Exception e) {
			if (status == 200) status = 500;
			response = new WsResponse("KO", e.getMessage());

		}

		return Response.status(status).entity(response).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response delete(MultivaluedMap<String, String> formParams) throws JSONException {
		// TODO: Appeler la méthode de modification pour passer is_active à false
		return Response.status(200).build();
	}

	private boolean isValidConstructor(final MultivaluedMap<String, String> formParams, boolean isUpdate) {
		if (isUpdate && !formParams.containsKey("id")) return false;
		if (!formParams.containsKey("name")) return false;
		return true;
	}

	private void saveConstructor(Constructor constructor) {
		// Création du gestionnaire d'entités
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
		EntityManager em = emf.createEntityManager();

		// Validation des changements
		em.getTransaction().begin();
		em.flush();
		em.persist(constructor);
		em.getTransaction().commit();

		// Fermeture du gestionnaire d'entités
		em.close();
		emf.close();
	}
}
