package com.udev.mesi;

import com.udev.mesi.messages.WsGetFlightDetails;
import com.udev.mesi.messages.WsGetPlanes;
import com.udev.mesi.messages.WsGetSinglePlane;
import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.services.PlaneService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/plane")
public class PlaneServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JSONException {
        WsGetPlanes response = PlaneService.read();
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPk(@PathParam("id") final String ARN, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsGetSinglePlane response = PlaneService.readOne(ARN, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}/flightDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightDetails(@PathParam("id") final String ARN, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsGetFlightDetails response = PlaneService.readFlightDetails(ARN, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = PlaneService.create(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = PlaneService.update(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @DELETE
    @Path("{ARN}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("ARN") final String ARN, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsResponse response = PlaneService.delete(acceptLanguage, ARN);
        return Response.status(response.getCode()).entity(response).build();
    }
}
