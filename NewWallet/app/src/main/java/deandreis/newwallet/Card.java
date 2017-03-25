package deandreis.newwallet;

/**
 * Created by ignaciodeandreisdenis on 25/3/17.
 */

public class Card {

    String currency;
    String amount;
    String cardNumber;
    String expiration;
    String cvv;
    boolean expanded;

    public Card(){

    }

    public Card(String currency,String amount,String cardNumber,String expiration,String cvv){
        this.currency = currency;

    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
