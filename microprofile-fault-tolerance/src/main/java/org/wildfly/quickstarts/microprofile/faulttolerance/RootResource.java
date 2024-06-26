package org.wildfly.quickstarts.microprofile.faulttolerance;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getRootResponse() {
        return "MicroProfile Fault Tolerance quickstart deployed successfully. You can find the available operations in the included README file.";
    }
}
