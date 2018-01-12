package datastore;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class DataStoreManager {
    private static Datastore datastore;

    public static void initialMongo(String host, int port) {
        final Morphia morphia = new Morphia();
        datastore = morphia.createDatastore(new MongoClient(host, port), Constants.DB_NAME);
        datastore.ensureIndexes();
    }

    public static Datastore getDatastore() {
        return datastore;
    }

}
