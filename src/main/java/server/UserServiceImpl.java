package server;

import datastore.DataStoreManager;
import model.Session;
import model.User;
import model.UserList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


@WebService(endpointInterface = "server.UserService")
public class UserServiceImpl implements UserService {

    @WebMethod
    public void createUser(String login, String password) throws Exception {
        User newUser = new User(login, password);
        User user = DataStoreManager.getDatastore().find(User.class).field("login").equal(login).get();
//        Query<User> filter = DataStoreManager.getDatastore().createQuery(User.class).filter("login", login);
//        List<User> users = filter.asList();
        if (user == null) {
            DataStoreManager.getDatastore().save(newUser);
        } else {
            //TODO: throw exception
            throw new Exception();
        }
    }

    @WebMethod
    public void removeUser(String login) throws Exception {
        User userToBeRemoved = DataStoreManager.getDatastore().createQuery(User.class)
                .field("login").equal(login).get();
        if (userToBeRemoved == null) {
            //TODO; throw new exc
            throw new Exception();
        } else {
            DataStoreManager.getDatastore().delete(userToBeRemoved);
        }
    }

    @WebMethod
    public void login(String login, String password) throws Exception {
        User user = DataStoreManager.getDatastore().find(User.class).field("login").equal(login).get();
        if (user == null) {
            //TODO: throw exception
            throw new Exception();
        } else if (user.getPassword().equals(password)) {
            Session session = new Session(UUID.randomUUID().toString(), login);
            DataStoreManager.getDatastore().save(session);
        } else {
            throw new Exception();
        }
    }

    @WebMethod
    public void logout() throws Exception {
        DataStoreManager.getDatastore().delete(getCurrentSession());
    }

    @WebMethod
    public User getCurrentUser() throws Exception {
        Session session = DataStoreManager.getDatastore().find(Session.class).get();
        if (session == null) {
            //TODO: throw exception
            throw new Exception();
        } else {
            User login = DataStoreManager.getDatastore().find(User.class).field("login").equal(session.getUserLogin()).get();
            //TODO: throw there is no user logged in
            if (login == null) throw new Exception();
            return login;
        }
    }

    @Override
    public UserList getAllUsers() throws Exception {
        UserList userList = new UserList();
        List<User> users = new ArrayList<>();
        DataStoreManager.getDatastore().find(User.class).forEach(new Consumer<User>() {
            @Override
            public void accept(User user) {
                users.add(user);
            }
        });
        userList.setUserList(users);
        return userList;
    }

    private Session getCurrentSession() {
        return DataStoreManager.getDatastore().find(Session.class).get();
    }
}
