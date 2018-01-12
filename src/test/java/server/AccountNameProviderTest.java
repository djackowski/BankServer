package server;

import javafx.util.Pair;
import org.junit.Test;

import java.util.List;

public class AccountNameProviderTest {

    @Test
    public void getAccountListFromCSV() throws Exception {
        //Given
        //When
        List<Pair<String, String>> accountListFromCSV = AccountNameProvider.getAccountListFromCSV();
        //Then
        System.out.println(accountListFromCSV.toString());
    }

}