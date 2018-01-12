package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;

import static datastore.Constants.USERS_COLLECTION_NAME;

@Entity(USERS_COLLECTION_NAME)
public class User {
    @Id
    private ObjectId id;
    private String login;
    private String password;
    @Reference
    List<Account> accounts;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getLogin() {
        return login;
    }

    public ObjectId getId() {
        return id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
