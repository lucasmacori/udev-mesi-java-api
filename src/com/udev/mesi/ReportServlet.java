package com.udev.mesi;

import com.udev.mesi.messages.WsGetReportResults;
import com.udev.mesi.messages.WsGetReports;
import com.udev.mesi.messages.WsGetSingleReport;
import com.udev.mesi.services.ReportService;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/report")
public class ReportServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JSONException {
        WsGetReports response = ReportService.read();
        return Response.status(response.getCode()).entity(response).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPk(@PathParam("id") final String code, @HeaderParam("Accept-Language") final String acceptLanguage) throws JSONException {
        WsGetSingleReport response = ReportService.readOne(code, acceptLanguage);
        return Response.status(response.getCode()).entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(@HeaderParam("Accept-Language") final String acceptLanguage, final MultivaluedMap<String, String> formParams) throws JSONException {
        WsGetReportResults response = ReportService.executeReport(acceptLanguage, formParams);
        return Response.status(response.getCode()).entity(response).build();
    }
}
