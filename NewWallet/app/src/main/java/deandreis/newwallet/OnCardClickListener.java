package deandreis.newwallet;

import android.view.View;

/**
 * Created by ignaciodeandreisdenis on 25/3/17.
 */

public interface OnCardClickListener {
    void onCardClick(Card card,View view, int position,boolean first, boolean last);
}
