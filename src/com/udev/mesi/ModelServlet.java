package com.udev.mesi;

import com.udev.mesi.messages.WsGetModels;
import com.udev.mesi.services.ModelService;
import org.json.JSONException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/model")
public class ModelServlet {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JSONException {
        WsGetModels response = ModelService.read();
        return Response.status(response.getCode()).entity(response).build();
    }
}
