package com.udev.mesi;

import com.udev.mesi.messages.WsGetModels;
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
    public Response get() throws JSONException {
        WsGetModels response = ModelService.read();
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = ModelService.create(formParams);
        return Response.status(response.getCode()).entity(response).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(final MultivaluedMap<String, String> formParams) throws JSONException {
        WsResponse response = ModelService.update(formParams);
        return Response.status(response.getCode()).entity(response).build();
    }
}
