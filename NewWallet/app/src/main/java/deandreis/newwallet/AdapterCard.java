package deandreis.newwallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ignaciodeandreisdenis on 9/21/16.
 */

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.CardViewHolder> {

    private List<Card> cardList;

    private final OnCardClickListener listener;

    public AdapterCard(List<Card> cardList, OnCardClickListener listener) {
        this.cardList = cardList;
        this.listener = listener;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row, parent, false);
        return new CardViewHolder(v);
    }


    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.setItem(cardList.get(position));
        holder.bind(cardList.get(position), listener,position -1);
    }


    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    static class CardViewHolder extends RecyclerView.ViewHolder {

        Context context;

        @BindView(R.id.cardRelativeLayout)
        RelativeLayout cardRelativeLayout;

        @BindView(R.id.textViewAmount)
        TextView textViewAmount;


        private CardViewHolder(View v) {
            super(v);
            context = v.getContext();
            ButterKnife.bind(this,v);
        }

        private void setItem(Card card) {

            textViewAmount.setText(String.valueOf(card.value));


            int marginFlatten = (int) context.getResources().getDimension(R.dimen.margin_card_flatten);
            int margin = (int) context.getResources().getDimension(R.dimen.margin);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) cardRelativeLayout.getLayoutParams();

            if(card.isExpanded()){
                params.setMargins(0, 0, 0, margin);
            }else{
                params.setMargins(0, 0, 0,marginFlatten);
            }

            cardRelativeLayout.setLayoutParams(params);
            cardRelativeLayout.setVisibility(View.VISIBLE);

        }

        public void bind(final Card card, final OnCardClickListener listener, final int previousPosition) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onCardClick(card,itemView,previousPosition);
                }
            });
        }

    }

}