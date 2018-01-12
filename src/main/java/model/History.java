package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import static datastore.Constants.HISTORY_COLLECTION_NAME;

//Pobranie historii operacji (tytuł operacji kwota winien/ma,
//        źródło pochodzenia pieniędzy: nr konta
//        lub wpłata/wypłata/opłata bankowa, saldo po operacji),
@Entity(HISTORY_COLLECTION_NAME)
public class History {

    public enum NAMES {
        DEPOSIT,
        WITHDRAWAL,
        FEE
    }

    @Id
    private ObjectId id;
    private String title;
    private int amount;
    private String source;
    private int balanceAfterOperation;


    public History() {
    }

    public History(String title, int amount, String source, int balanceAfterOperation) {
        this.title = title;
        this.amount = amount;
        this.source = source;
        this.balanceAfterOperation = balanceAfterOperation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getBalanceAfterOperation() {
        return balanceAfterOperation;
    }

    public void setBalanceAfterOperation(int balanceAfterOperation) {
        this.balanceAfterOperation = balanceAfterOperation;
    }
}
