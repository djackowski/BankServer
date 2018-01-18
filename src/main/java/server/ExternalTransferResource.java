package server;

import datastore.DataStoreManager;
import model.Account;
import model.History;
import model.Transfer;
import org.mongodb.morphia.query.UpdateOperations;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("accounts")
public class ExternalTransferResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accountNo}/history")
    public Response receiveMoney(@PathParam("accountNo") String accountNo, @HeaderParam("authorization") String authString, Transfer transfer) throws Exception {
        if (!isUserAuthenticated(authString)) {
            return Response.status(401).build();
        }

        List<Map<String, String>> errorFieldMap = new ArrayList<>();

        String errorKey = "error: ";
        String fieldKey = "field: ";

        if(!AccountNameProvider.checkIfExistsInDB(accountNo)) {
            String error = "Target account does not exist";
            String field = "Account number in param";
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(errorKey, error);
            stringStringMap.put(fieldKey, field);
            errorFieldMap.add(stringStringMap);
        }

        if (!AccountNameProvider.validateAccount(transfer.getSource_account())) {
            String error = "Source account is not correct";
            String field = "source_account";
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(errorKey, error);
            stringStringMap.put(fieldKey, field);
            errorFieldMap.add(stringStringMap);
        }

        if (transfer.getAmount() <= 0) {
            String error = "Amount must be positive";
            String field = "amount";
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(errorKey, error);
            stringStringMap.put(fieldKey, field);
            errorFieldMap.add(stringStringMap);
        }

        if (transfer.getTitle().length() < 1 || transfer.getTitle().length() > 255) {
            String error = "Title length must be greater than 1 char and less than 255 chars";
            String field = "title";
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(errorKey, error);
            stringStringMap.put(fieldKey, field);
            errorFieldMap.add(stringStringMap);
        }

        if (transfer.getSource_name().length() < 1 || transfer.getSource_name().length() > 255) {
            String error = "Source name length must be greater than 1 char and less than 255 chars";
            String field = "source_name";
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(errorKey, error);
            stringStringMap.put(fieldKey, field);
            errorFieldMap.add(stringStringMap);
        }

        if (transfer.getDestination_name().length() < 1 || transfer.getDestination_name().length() > 255) {
            String error = "Destination name length must be greater than 1 char and less than 255 chars";
            String field = "destination_name";
            Map<String, String> stringStringMap = new HashMap<>();
            stringStringMap.put(errorKey, error);
            stringStringMap.put(fieldKey, field);
            errorFieldMap.add(stringStringMap);
        }

        if (!errorFieldMap.isEmpty()) {
            return Response.status(400).entity(errorFieldMap).build();
        }

        Account account = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get();


        //TODO: throw exception
        if (account == null) throw new Exception();

        Long amount = transfer.getAmount();

        UpdateOperations<Account> newBalance = DataStoreManager.getDatastore().createUpdateOperations(Account.class).inc("balance", amount);

        DataStoreManager.getDatastore().update(account, newBalance);

        Long newBalanceFromDB = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get().getBalance();

        addToHistory(transfer.getTitle(), transfer.getAmount(), transfer.getSource_account(), account, newBalanceFromDB);
        //TODO: proper response
        return Response.status(201).build();
    }

    private boolean isUserAuthenticated(String authString) {
        AuthenticationService authenticationService = new AuthenticationService();
        return authenticationService.authenticate(authString);
    }

    private void addToHistory(String title, Long amount, String operation, Account currentAccount, Long newBalanceFromDB) {
        History history = new History(title, amount, operation, newBalanceFromDB);

        DataStoreManager.getDatastore().save(history);

        UpdateOperations<Account> newValues = DataStoreManager.getDatastore()
                .createUpdateOperations(Account.class)
                .add("histories", history);

        DataStoreManager.getDatastore().update(currentAccount, newValues);
    }

}
