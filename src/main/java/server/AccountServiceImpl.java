package server;

import datastore.DataStoreManager;
import model.Account;
import model.History;
import model.User;
import org.mongodb.morphia.query.UpdateOperations;

import javax.jws.WebMethod;
import javax.jws.WebService;

import static model.History.NAMES.DEPOSIT;
import static model.History.NAMES.WITHDRAWAL;

@WebService(endpointInterface = "server.AccountService")
public class AccountServiceImpl implements AccountService {

    //TODO: authorization
    @WebMethod
    public Long getBalance(String user, String accountNo) throws Throwable {
        User currentUser = DataStoreManager.getDatastore().find(User.class).field("login").equal(user).get();
        if(currentUser == null) {
            throw new Exception();
            //TODO: throw
        }
//        Account account = currentUser.getAccounts()
//                .stream()
//                .filter(account1 -> account1.getName().equals(accountNo))
//                .findFirst()
//                .orElseThrow((Supplier<Throwable>) Exception::new);
        Account account = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get();

        return account.getBalance();
    }

    @WebMethod
    public void deposit(String user, String accountNo, Long amount) throws Throwable {
        //TODO: throw exception
        if (amount <= 0) throw new Exception();

        User currentUser = DataStoreManager.getDatastore().find(User.class).field("login").equal(user).get();
        Account account = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get();
//        Account account = currentUser.getAccounts()
//                .stream()
//                .filter(account1 -> account1.getName().equals(accountNo))
//                .findFirst()
//                .orElseThrow((Supplier<Throwable>) Exception::new);

        UpdateOperations<Account> newBalance = DataStoreManager.getDatastore()
                .createUpdateOperations(Account.class).inc("balance", amount);

        DataStoreManager.getDatastore().update(account, newBalance);

        Long newBalanceFromDB = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get().getBalance();

        addToHistory(DEPOSIT.name(), amount, DEPOSIT.name(), account, newBalanceFromDB);

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
    public void withdrawal(String user, String accountNo, Long amount) throws Throwable {
        //TODO: throw exc
        if (amount <= 0) throw new Exception();

        User currentUser = DataStoreManager.getDatastore().find(User.class).field("login").equal(user).get();
        Account account = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get();
//        Account account = currentUser.getAccounts()
//                .stream()
//                .filter(account1 -> account1.getName().equals(accountNo))
//                .findFirst()
//                .orElseThrow((Supplier<Throwable>) Exception::new);

        //TODO: throw exc if balance is less than amount to withdraw
        if (account.getBalance() < amount) throw new Exception();

        UpdateOperations<Account> newBalance = DataStoreManager.getDatastore()
                .createUpdateOperations(Account.class).inc("balance", -amount);

        DataStoreManager.getDatastore().update(account, newBalance);

        Long newBalanceFromDB = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get().getBalance();

        addToHistory(WITHDRAWAL.name(), amount, WITHDRAWAL.name(), account, newBalanceFromDB);
    }

    @WebMethod
    public String createAccount(String user) throws Exception {
        User currentUser = DataStoreManager.getDatastore().find(User.class).field("login").equal(user).get();
        if (currentUser == null) {
            //TODO; throw exception
            throw new Exception();
        } else {
            Account account = AccountNameProvider.generateAccountName();
            DataStoreManager.getDatastore().save(account);

            UpdateOperations<User> newValues = DataStoreManager.getDatastore()
                    .createUpdateOperations(User.class)
                    .add("accounts", account);

            DataStoreManager.getDatastore().update(currentUser, newValues);
            return account.getName();
        }
    }

    @WebMethod
    public void deleteAccount(String user, String accountNo) throws Throwable {
        User currentUser = DataStoreManager.getDatastore().find(User.class).field("login").equal(user).get();
        Account account = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get();

//        Account account = currentUser.getAccounts()
//                .stream()
//                .filter(account1 -> account1.getName().equals(accountNo))
//                .findFirst()
//                .orElseThrow((Supplier<Throwable>) Exception::new);

        UpdateOperations<User> accounts = DataStoreManager.getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("accounts", account);

        DataStoreManager.getDatastore().delete(account);
        DataStoreManager.getDatastore().update(currentUser, accounts);
    }
}
