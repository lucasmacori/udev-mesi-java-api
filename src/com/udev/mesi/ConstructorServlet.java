package com.udev.mesi;

import com.udev.mesi.messages.WsGetConstructors;
import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.services.ConstructorService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/constructor")
public class ConstructorServlet {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() throws JSONException {
		WsGetConstructors response = ConstructorService.read();
		return Response.status(response.getCode()).entity(response).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response create(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
		WsResponse response = ConstructorService.create(acceptLanguage, formParams);
		return Response.status(response.getCode()).entity(response).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response update(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
		WsResponse response = ConstructorService.update(acceptLanguage, formParams);
		return Response.status(response.getCode()).entity(response).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response delete(@HeaderParam("Accept-Language") final String acceptLanguage, MultivaluedMap<String, String> formParams) throws JSONException {
		WsResponse response = ConstructorService.delete(acceptLanguage, formParams);
		return Response.status(response.getCode()).entity(response).build();
	}
}
