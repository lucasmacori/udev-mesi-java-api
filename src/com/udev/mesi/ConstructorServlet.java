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
import com.udev.mesi.services.ConstructorService;
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
		WsGetConstructors response = ConstructorService.read();
		return Response.status(response.getCode()).entity(response).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response create(final MultivaluedMap<String, String> formParams) throws JSONException {
		WsResponse response = ConstructorService.create(formParams);
		return Response.status(response.getCode()).entity(response).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response update(final MultivaluedMap<String, String> formParams) throws JSONException {
		WsResponse response = ConstructorService.update(formParams);
		return Response.status(response.getCode()).entity(response).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response delete(MultivaluedMap<String, String> formParams) throws JSONException {
		WsResponse response = ConstructorService.delete(formParams);
		return Response.status(response.getCode()).entity(response).build();
	}
}
