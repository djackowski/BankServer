package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import static datastore.Constants.SESSION_COLLECTION_NAME;

@Entity(SESSION_COLLECTION_NAME)
public class Session {
    @Id
    private ObjectId id;
    private String session;
    private String userLogin;


    public Session() {
    }

    public Session(String session, String userLogin) {
        this.session = session;
        this.userLogin = userLogin;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getSession() {
        return session;
    }
}
