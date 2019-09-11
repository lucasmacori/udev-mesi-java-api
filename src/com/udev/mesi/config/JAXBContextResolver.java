package com.udev.mesi.config;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.udev.mesi.messages.*;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
    private final JAXBContext context;
    private final Set<Class> types;
    private Class[] ctypes = {WsResponse.class, WsGetManufacturers.class, WsGetModels.class, WsGetPlanes.class, WsGetFlights.class, WsGetFlightDetails.class, WsGetPassengers.class, WsGetReservations.class, WsGetReports.class, WsGetReportResults.class}; //your pojo class

    public JAXBContextResolver() throws Exception {
        this.types = new HashSet(Arrays.asList(ctypes));
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(),
                ctypes); //json configuration
    }

    @Override
    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }
}
