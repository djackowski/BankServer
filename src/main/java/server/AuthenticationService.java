package server;

import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

public class AuthenticationService {
    public boolean authenticate(String credential) {
        if (null == credential) {
            return false;
        }
        final String encodedUserPassword = credential.replaceFirst("Basic" + " ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        Properties properties = new Properties();
        FileInputStream input ;
        File file = new File(AuthenticationService.class.getClassLoader().getResource("config.properties")
                .getPath());
        try {
            input = new FileInputStream(file);
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String login = properties.get("login").toString();
        String pass = properties.get("password").toString();

        return login.equals(username) && pass.equals(password);
    }
}
