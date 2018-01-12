package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import static datastore.Constants.ACCOUNTS_INCREMENT_COLLECTION_NAME;

@Entity(ACCOUNTS_INCREMENT_COLLECTION_NAME)
public class AccountIncrement {

    @Id
    private ObjectId objectId;
    private int value;


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
