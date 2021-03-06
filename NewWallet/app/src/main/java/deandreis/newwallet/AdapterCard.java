package deandreis.newwallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static deandreis.newwallet.MainActivity.cardContainerHeight;

/**
 * Created by ignaciodeandreisdenis on 9/21/16.
 */

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.CardViewHolder> {

    private List<Card> cardList;

    private final OnCardClickListener listener;

    public AdapterCard(List<Card> cardList, OnCardClickListener listener) {
        this.setCardList(cardList);
        this.listener = listener;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row, parent, false);
        return new CardViewHolder(v);
    }


    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.setItem(getCardList().get(position),cardList.size() == 1, position, position == getCardList().size() - 1);
        holder.bind(getCardList().get(position), listener, position, position == 0, position == getCardList().size() - 1);
    }


    @Override
    public int getItemCount() {
        return getCardList().size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }


    static class CardViewHolder extends RecyclerView.ViewHolder {

        Context context;

        @BindView(R.id.cardRow)
        public RelativeLayout cardRow;

        @BindView(R.id.cardCollapsed)
        public RelativeLayout cardCollapsed;

        @BindView(R.id.cardExpanded)
        public LinearLayout cardExpanded;

        @BindView(R.id.cardHelperBackgroundTop)
        public View cardHelperBackgroundTop;

        @BindView(R.id.cardHelperBackgroundBottom)
        public View cardHelperBackgroundBottom;

        @BindView(R.id.linearLayoutAux)
        public LinearLayout linearLayoutAux;

        @BindView(R.id.cardRelativeLayout)
        public RelativeLayout cardRelativeLayout;

        @BindView(R.id.textViewAmount)
        TextView textViewAmount;

        @BindView(R.id.textViewAmountheader)
        public TextView textViewAmountheader;

        @BindView(R.id.cardRowViewTop)
        public View cardRowViewTop;


        private CardViewHolder(View v) {
            super(v);
            context = v.getContext();
            ButterKnife.bind(this, v);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardContainerHeight);
            linearLayoutAux.setLayoutParams(params);
        }

        private void setItem(Card card, boolean singleItem, int position, boolean last) {

            textViewAmount.setText(String.valueOf(card.value));
            textViewAmountheader.setText(String.valueOf(card.value));

            //COLLAPSED
            if(position == 0){
                cardRowViewTop.setVisibility(View.GONE);
            }else{
                cardRowViewTop.setVisibility(View.VISIBLE);
            }


            //EXPANDED
            if(singleItem){
                cardHelperBackgroundTop.setVisibility(View.GONE);
            }else{
                cardHelperBackgroundTop.setVisibility(View.VISIBLE);
            }

            cardHelperBackgroundTop.setBackgroundResource(R.color.colorCardGrayDark);


            //COMMON
            if (last) {
                cardExpanded.setVisibility(View.VISIBLE);
                cardCollapsed.setVisibility(View.GONE);
            } else {
                cardExpanded.setVisibility(View.GONE);
                cardCollapsed.setVisibility(View.VISIBLE);
            }


        }

        public void bind(final Card card, final OnCardClickListener listener, final int position, final boolean first, final boolean last) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    card.setSelected(!card.isSelected());
                    Log.v("On card clicked", card.value + "");
                    listener.onCardClick(card, itemView, position,first, last);

                }
            });
        }

    }

}