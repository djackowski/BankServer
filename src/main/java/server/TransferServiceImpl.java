package server;

import com.sun.jersey.core.util.Base64;
import datastore.DataStoreManager;
import exceptions.AccountNotValidateException;
import javafx.util.Pair;
import model.Account;
import model.AccountUrlList;
import model.History;
import model.User;
import org.mongodb.morphia.query.UpdateOperations;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.function.Supplier;

@WebService(endpointInterface = "server.TransferService")
public class TransferServiceImpl implements TransferService {


    @WebMethod
    public void sendExternal(String user, String sourceAccountName, String targetAccountName, Long amount, String title, String destinationName) throws Throwable {
        makeExternalTransfer(sourceAccountName, targetAccountName, amount, title, destinationName);
    }

    @WebMethod
    public void sendInternal(String user, String sourceAccountName, String targetAccountName, Long amount, String title) throws Throwable {
        makeInternalTransfer(user, sourceAccountName, targetAccountName, amount, title);
    }

    private void addToHistory(String title, Long amount, String operation, Account currentAccount, Long newBalanceFromDB) {
        History history = new History(title, amount, operation, newBalanceFromDB);

        DataStoreManager.getDatastore().save(history);

        UpdateOperations<Account> newValues = DataStoreManager.getDatastore()
                .createUpdateOperations(Account.class)
                .add("histories", history);

        DataStoreManager.getDatastore().update(currentAccount, newValues);
    }

    @WebMethod
    @Override
    public AccountUrlList getExternalAccountsName() {
        return AccountNameProvider.getAccountsNumbersFromCSV();
    }

    private void makeInternalTransfer(String user, String sourceAccountName, String targetAccountName, Long amount, String title) throws Exception {
        User dbUser = DataStoreManager.getDatastore().find(User.class).field("login").equal(user).get();
        Account sourceAccount = DataStoreManager.getDatastore().find(Account.class).field("name").equal(sourceAccountName).get();
        Account targetAccount = DataStoreManager.getDatastore().find(Account.class).field("name").equal(targetAccountName).get();
        //TODO: throw exception if target does not exist
        if (targetAccount == null) throw new Exception();
        if (sourceAccount == null) throw new Exception();

        Long sourceBalance = sourceAccount.getBalance();

        if (sourceBalance < amount) throw new Exception();

        UpdateOperations<Account> updateTargetBalance = DataStoreManager.getDatastore().createUpdateOperations(Account.class).inc("balance", amount);
        UpdateOperations<Account> updateSourceBalance = DataStoreManager.getDatastore().createUpdateOperations(Account.class).inc("balance", -amount);

        DataStoreManager.getDatastore().update(sourceAccount, updateSourceBalance);
        DataStoreManager.getDatastore().update(targetAccount, updateTargetBalance);


        Long newBalanceFromDBSourceAccount = DataStoreManager.getDatastore().find(Account.class).field("name").equal(sourceAccountName).get().getBalance();
        Long newBalanceFromDBTargetAccount = DataStoreManager.getDatastore().find(Account.class).field("name").equal(targetAccountName).get().getBalance();

        addToHistory(title, -amount, targetAccountName, sourceAccount, newBalanceFromDBSourceAccount);
        addToHistory(title, amount, sourceAccountName, targetAccount, newBalanceFromDBTargetAccount);
    }

    private void makeExternalTransfer(String sourceAccountName, String targetAccountName, Long amount, String title, String destinationName) throws Throwable {
        //00134496,http://192.168.1.152:8030/accounts/17001344960000000000000007/history

        if (!AccountNameProvider.validateAccount(targetAccountName)) {
            throw new AccountNotValidateException();
        }
        Account sourceAccount = DataStoreManager.getDatastore().find(Account.class).field("name").equal(sourceAccountName).get();
        if (amount > sourceAccount.getBalance()) {
            throw new NoSufficientMoneyException();
        }
        //TODO: check variety csv accounts
        String resourceAddress = AccountNameProvider.getAccountListFromCSV()
                .stream()
                .filter(new Predicate<Pair<String, String>>() {
                    @Override
                    public boolean test(Pair<String, String> stringStringPair) {
                        boolean bankIdCorrect = false;
                        try {
                            bankIdCorrect = AccountNameProvider.isBankIdCorrect(stringStringPair.getKey(), targetAccountName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //TODO; exception
                        }
                        return bankIdCorrect;
                    }
                })
                .findFirst()
                //TODO: throw proper exception
                .orElseThrow((Supplier<Throwable>) Exception::new)
                .getValue();

        int lengthOfResourceAddress = resourceAddress.length();
        if (resourceAddress.charAt(lengthOfResourceAddress - 1) != '/') {
            resourceAddress = resourceAddress.concat("/");
        }
        resourceAddress = resourceAddress + "accounts/" + targetAccountName + "/history";
        System.out.println(resourceAddress);

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(resourceAddress).openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
        urlConnection.setRequestProperty("Content-Type", "application/json;charset=" + "UTF-8");


        File file = new File(AuthenticationService.class.getClassLoader().getResource("config.properties")
                .getPath());
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(file);
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String login = properties.get("login").toString();
        String pass = properties.get("password").toString();
//        String login = "admin";
//        String pass = "ninja";
        String userCredentials = login + ":" + pass;
        String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
        urlConnection.setRequestProperty("Authorization", basicAuth);

        String data = "{\"source_account\":" + "\"" + sourceAccountName + "\"" + "," + "\"amount\":" + "\"" + amount + "\"" + "," + "\"title\":"
                + "\"" + title + "\"" + "," + "\"source_name\":" + "\"" + sourceAccountName + "\"" + "," + "\"destination_name\":" + "\"" + destinationName + "\"}";
        OutputStream requestBody = urlConnection.getOutputStream();
        requestBody.write(data.getBytes("UTF-8"));
        requestBody.close();
        urlConnection.connect();


        //TODO: handle responses
        int responseCode = urlConnection.getResponseCode();
        System.out.println(responseCode);

        if (responseCode == 201) {

            UpdateOperations<Account> updateSourceBalance = DataStoreManager.getDatastore().createUpdateOperations(Account.class).inc("balance", -amount);
            DataStoreManager.getDatastore().update(sourceAccount, updateSourceBalance);

            Long newBalanceFromDB = DataStoreManager.getDatastore().find(Account.class).field("name").equal(sourceAccountName).get().getBalance();

            addToHistory(title, -amount, targetAccountName, sourceAccount, newBalanceFromDB);
        } else if (responseCode == 401) {
            throw new Exception();//TODO: unatuhorized
        } else if (responseCode == 404) {
            throw new Exception();//TODO: not found
        } else if (responseCode == 400) {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            System.out.println(stringBuilder.toString());
        }


    }
}
