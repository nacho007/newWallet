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
        holder.setItem(getCardList().get(position),position == getCardList().size() - 1);
        holder.bind(getCardList().get(position), listener, position, position == getCardList().size() - 1);
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

        @BindView(R.id.linearLayoutCardContent)
        public LinearLayout linearLayoutCardContent;

        @BindView(R.id.linearLayoutCardContent2)
        public LinearLayout linearLayoutCardContent2;

        @BindView(R.id.cardRelativeLayout)
        public RelativeLayout cardRelativeLayout;

        @BindView(R.id.textViewAmount)
        TextView textViewAmount;

        @BindView(R.id.textViewAmount2)
        TextView textViewAmount2;

        @BindView(R.id.textViewAmountheader)
        public TextView textViewAmountheader;


        private CardViewHolder(View v) {
            super(v);
            context = v.getContext();
            ButterKnife.bind(this, v);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardContainerHeight);
            linearLayoutCardContent.setLayoutParams(params);
        }

        private void setItem(Card card, boolean last) {
            textViewAmount.setText(String.valueOf(card.value));
            textViewAmount2.setText(String.valueOf(card.value));

            textViewAmountheader.setText(String.valueOf(card.value));

            linearLayoutCardContent.setVisibility(View.GONE);
            cardRelativeLayout.setBackgroundResource(R.drawable.shape_card);

            if(last){
                linearLayoutCardContent2.setVisibility(View.VISIBLE);
                cardRow.setBackgroundResource(R.color.transparent);
            }else{
                linearLayoutCardContent2.setVisibility(View.GONE);
                cardRow.setBackgroundResource(R.drawable.shape_card_top);
            }

        }

        public void bind(final Card card, final OnCardClickListener listener, final int position, final boolean last) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    card.setSelected(!card.isSelected());
                    Log.v("On card clicked", card.value + "");
                    listener.onCardClick(card, itemView,position,last);

                }
            });
        }

    }

}