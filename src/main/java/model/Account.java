package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;

import static datastore.Constants.ACCOUNTS_COLLECTION_NAME;

@Entity(ACCOUNTS_COLLECTION_NAME)
public class Account {
    @Id
    private ObjectId id;
    private String name;
    private Long balance;

    @Reference
    private List<History> histories;

    public Account() {

    }

    public Account(String name) {
        this();
        this.name = name;
    }


    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
