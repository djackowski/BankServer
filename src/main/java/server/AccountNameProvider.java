package server;

import datastore.DataStoreManager;
import javafx.util.Pair;
import model.Account;
import model.AccountIncrement;
import model.AccountUrlList;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static datastore.Constants.BANK_ID;
import static datastore.Constants.EXTERNAL_ADDRESSES_FILE;

public class AccountNameProvider {

    public static List<Pair<String, String>> getAccountListFromCSV() throws URISyntaxException, IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(EXTERNAL_ADDRESSES_FILE);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        List<Pair<String, String>> accountPairs = new ArrayList<>();
        br.lines().forEach(line -> {
            String splitted[] = line.split(",");
            Pair<String, String> stringStringPair = new Pair<>(splitted[0], splitted[1]);
            accountPairs.add(stringStringPair);
        });

        return accountPairs;
    }

    public static AccountUrlList getAccountsNumbersFromCSV() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(EXTERNAL_ADDRESSES_FILE);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        List<String> accountPairs = new ArrayList<>();
        br.lines().forEach(new Consumer<String>() {
            @Override
            public void accept(String line) {
                String splitted[] = line.split(",");
                accountPairs.add(splitted[1]);
            }
        });
        AccountUrlList accountUrlList = new AccountUrlList();
        accountUrlList.setAccountUrlList(accountPairs);
        return accountUrlList;
    }

    public static Account generateAccountName() {
        //String.format("%016d", count);
        AccountIncrement accountIncrement = DataStoreManager.getDatastore().find(AccountIncrement.class).get();
        if (accountIncrement == null) {
            AccountIncrement newIncrement = new AccountIncrement();
            newIncrement.setValue(1);
            DataStoreManager.getDatastore().save(newIncrement);
        } else {
            UpdateOperations<AccountIncrement> value = DataStoreManager.getDatastore().createUpdateOperations(AccountIncrement.class).inc("value");
            DataStoreManager.getDatastore().update(accountIncrement, value);
        }

        Account account = new Account();
        int incrementedValue = DataStoreManager.getDatastore().find(AccountIncrement.class).get().getValue();
        String accountId = String.format("%016d", incrementedValue);
        String name = generateSum(BANK_ID, accountId) + BANK_ID + accountId;
        account.setName(name);
        return account;
    }


    //98-(aaaaaaaabbbbbbbbbbbbbbbb252100%97)=
    private static String generateSum(String bankId, String accountId) {
        char[] charArray = bankId.toCharArray();
        int indexToDelete = 0;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '0') {
                bankId = deleteCharAt(bankId, indexToDelete);
                indexToDelete = i - 1;
            }
            indexToDelete++;
        }
        String concated = bankId + accountId + "252100";
        BigInteger parsedConcated = new BigInteger(concated);
        BigInteger sum = new BigInteger("98").subtract(parsedConcated.mod(new BigInteger("97")));
        return String.valueOf(sum);
    }

    private static String deleteCharAt(String strValue, int index) {
        return strValue.substring(0, index) + strValue.substring(index + 1);

    }
    //to validate: aaaaaaaabbbbbbbbbbbbbbbb2521cc%97=

    public static boolean isBankIdCorrect(String givenBankId, String targetAccount) throws Exception {
        if (!targetAccount.contains(givenBankId)) throw new Exception();
        //TODO: exception create
        return true;
    }

    public static boolean validateAccount(String account) {
        return account.length() == 26 && validateCheckSum(account);
    }

    private static boolean validateCheckSum(String account) {
        String sum = account.substring(0, 2);

        String concated = account.substring(2, account.length()) + "2521" + sum;
        BigInteger parsedConcated = new BigInteger(concated);
        BigInteger validate = parsedConcated.mod(new BigInteger("97"));
        return validate.equals(new BigInteger("1"));
    }

    public static boolean checkIfExistsInDB(String accountNo) {
        Account account = DataStoreManager.getDatastore().find(Account.class).field("name").equal(accountNo).get();
        return account != null;
    }
}
