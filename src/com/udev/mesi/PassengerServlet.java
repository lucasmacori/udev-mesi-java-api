package com.udev.mesi;

import com.udev.mesi.messages.WsExists;
import com.udev.mesi.messages.WsGetPassengers;
import com.udev.mesi.messages.WsGetSinglePassenger;
import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.services.PassengerService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/passenger")
public class PassengerServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JSONException {
        WsGetPassengers response = PassengerService.read();
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPk(@PathParam("id") final long id, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsGetSinglePassenger response = PassengerService.readOne(id, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("emailExists/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmailExists(@PathParam("email") final String email, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsExists response = PassengerService.emailExists(email, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("phoneNumberExists/{phoneNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhoneNumberExists(@PathParam("phoneNumber") final String phoneNumber, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsExists response = PassengerService.phoneNumberExists(phoneNumber, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("IDNumberExists/{IDNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIDNumberExists(@PathParam("IDNumber") final String IDNumber, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsExists response = PassengerService.IDNumberExists(IDNumber, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = PassengerService.create(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = PassengerService.update(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response delete(@HeaderParam("Accept-Language") final String acceptLanguage, MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = PassengerService.delete(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }
}
