package deandreis.newwallet;

/**
 * Created by ignaciodeandreisdenis on 25/3/17.
 */

public class Card {

    static int start = 0;
    public int value = 0;
    String currency;
    String amount;
    String cardNumber;
    String expiration;
    String cvv;
    boolean selected;


    public Card(){
        start++;
        value = start;
    }

    public Card(String currency,String amount,String cardNumber,String expiration,String cvv){
        this.currency = currency;


    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
