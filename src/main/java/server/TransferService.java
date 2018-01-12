package server;

import model.AccountUrlList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface TransferService {

    /**
     *
     * @param user
     * @param sourceAccountName
     * @param targetAccountName
     * @param amount
     * @param title
     * @throws Exception
     */
    @WebMethod
    void sendExternal(String user, String sourceAccountName, String targetAccountName, int amount, String title, String destinationName) throws Throwable;

    @WebMethod
    void sendInternal(String user, String sourceAccountName, String targetAccountName, int amount, String title) throws Throwable;

    @WebMethod
    AccountUrlList getExternalAccountsName();

}
