package server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;
import javax.xml.ws.BindingType;

@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class Authentication {

    @WebMethod
    public Response login(@HeaderParam("authorization") String authString) {
        if (!isUserAuthenticated(authString)) {
            return Response.status(401).build();
        } else {
            return Response.ok().build();
        }
    }

    private boolean isUserAuthenticated(String authString) {
        AuthenticationService authenticationService = new AuthenticationService();
        return authenticationService.authenticate(authString);
    }
}
