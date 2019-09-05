package com.udev.mesi;

import com.udev.mesi.messages.WsResponse;
import com.udev.mesi.services.AuthService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthServlet {

    @POST
    @Path("check")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response check(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = AuthService.verifyTokenService(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = AuthService.login(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams, @HeaderParam("username") final String username, @HeaderParam("token") final String token) throws JSONException {
        WsResponse response = AuthService.create(acceptLanguage, formParams, username, token);
        return Response.status(response.getCode()).entity(response).build();
    }
}
