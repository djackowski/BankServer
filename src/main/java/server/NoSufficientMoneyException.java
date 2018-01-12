package server;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "NoSufficientMoneyException", namespace = "http://server/")
public class NoSufficientMoneyException extends Exception {
    public NoSufficientMoneyException() {
        super("You have no sufficient money");
    }
}
