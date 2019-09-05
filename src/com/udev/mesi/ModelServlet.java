package com.udev.mesi;

import com.udev.mesi.messages.WsGetModels;
import com.udev.mesi.messages.WsGetPlanes;
import com.udev.mesi.messages.WsGetSingleModel;
import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.services.ModelService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/model")
public class ModelServlet {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam("Accept-Language") final String acceptLanguage, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsGetModels response = ModelService.read(acceptLanguage, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPk(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsGetSingleModel response = ModelService.readOne(id, acceptLanguage, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}/planes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightDetails(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsGetPlanes response = ModelService.readPlanes(id, acceptLanguage, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsResponse response = ModelService.create(acceptLanguage, formParams, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsResponse response = ModelService.update(acceptLanguage, formParams, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsResponse response = ModelService.delete(acceptLanguage, id, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }
}
