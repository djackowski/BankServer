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
    private Long amount;
    private String source;
    private Long balanceAfterOperation;


    public History() {
    }

    public History(String title, Long amount, String source, Long balanceAfterOperation) {
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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getBalanceAfterOperation() {
        return balanceAfterOperation;
    }

    public void setBalanceAfterOperation(Long balanceAfterOperation) {
        this.balanceAfterOperation = balanceAfterOperation;
    }
}
