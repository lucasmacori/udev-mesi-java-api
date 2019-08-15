package com.udev.mesi;

import com.udev.mesi.messages.WsGetFlightDetails;
import com.udev.mesi.messages.WsGetFlights;
import com.udev.mesi.messages.WsGetSingleFlight;
import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.services.FlightService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/flight")
public class FlightServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JSONException {
        WsGetFlights response = FlightService.read();
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPk(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsGetSingleFlight response = FlightService.readOne(id, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}/flightDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlightDetails(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsGetFlightDetails response = FlightService.readFlightDetails(id, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = FlightService.create(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = FlightService.update(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsResponse response = FlightService.delete(acceptLanguage, id);
        return Response.status(response.getCode()).entity(response).build();
    }
}
