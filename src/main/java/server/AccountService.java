package server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface AccountService {
    /**
     * @param user
     * @param accountNo
     * @return
     */
    @WebMethod
    int getBalance(String user, String accountNo) throws Throwable;

    /**
     * @param user
     * @param accountNo
     * @param amount
     * @throws Exception
     */
    @WebMethod
    void deposit(String user, String accountNo, int amount) throws Throwable;

    /**
     * @param user
     * @param accountNo
     * @param amount
     * @throws Exception
     */
    @WebMethod
    void withdrawal(String user, String accountNo, int amount) throws Throwable;


    /**
     * @param user
     * @return
     * @throws Exception
     */
    @WebMethod
    String createAccount(String user) throws Exception;

    /**
     * @param user
     * @param accountNo
     */
    @WebMethod
    void deleteAccount(String user, String accountNo) throws Throwable;


}
