package server;

import model.User;
import model.UserList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface UserService {
    /**
     *
     * @param login
     * @param password
     * @throws Exception
     */
    @WebMethod
    void createUser(String login, String password) throws Exception;

    /**
     *
     * @param login
     * @throws Exception
     */
    @WebMethod
    void removeUser(String login) throws Exception;

    /**
     *
     * @param login
     * @param password
     * @throws Exception
     */
    @WebMethod
    void login(String login, String password) throws Exception;

    /**
     *
     * @throws Exception
     */
    @WebMethod
    void logout() throws Exception;

    /**
     *
     * @return
     * @throws Exception
     */
    @WebMethod
    User getCurrentUser() throws Exception;


    @WebMethod
    UserList getAllUsers() throws Exception;

}

