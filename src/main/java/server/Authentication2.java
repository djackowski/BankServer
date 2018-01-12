package server;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Authentication2 {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("login")
    public Response login(@HeaderParam("authorization") String authString) {
        if(!isUserAuthenticated(authString)){
            return Response.status(401).build();
        } else {
            return Response.ok().build();
        }
    }

    private boolean isUserAuthenticated(String authString){
        AuthenticationService authenticationService = new AuthenticationService();
        return authenticationService.authenticate(authString);
    }
}
