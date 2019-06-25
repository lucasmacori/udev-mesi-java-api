package com.udev.mesi;

import com.udev.mesi.messages.WsGetPlanes;
import com.udev.mesi.services.PlaneService;
import org.json.JSONException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/plane")
public class PlaneServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JSONException {
        WsGetPlanes response = PlaneService.read();
        return Response.status(response.getCode()).entity(response).build();
    }
}
