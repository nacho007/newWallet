package deandreis.newwallet;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnCardClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.view)
    View viewAux;

    RecyclerView.Adapter adapter;

    int top = 0;
    Card selectedCard;
    View recyclerViewHolder;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Card> cardList = Mock.getCards();

        adapter = new AdapterCard(cardList, this);
        recyclerView.setAdapter(adapter);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int originalPos[] = new int[2];
                recyclerView.getLocationOnScreen(originalPos);

                if(originalPos[1] == 0){
                   handler.postDelayed(this,20);
                }else{
                    int padding = (int) getResources().getDimension(R.dimen.padding);
                    top = originalPos[1] + padding;

                    Log.v("RECYCLER", "x:" + originalPos[0] + "y:" + originalPos[1]);
                    Log.v("RECYCLER", "top:" + top);
                }

            }
        },20);

    }


    boolean processing = false;

    public void onViewClick(View v){
        if(!processing){
            viewAux.setVisibility(View.GONE);
            onCardClick(selectedCard,recyclerViewHolder,pos);
        }
    }


    @Override
    public void onCardClick(final Card card, View view, final int previousPosition) {

        if(!processing){
            processing = true;
            Log.v("Processing","Processing card:" + card.value);
            selectedCard = card;
            recyclerViewHolder = view;
            pos = previousPosition;

            viewAux.setVisibility(View.VISIBLE);

            int originalPos[] = new int[2];
            view.getLocationOnScreen(originalPos);

            recyclerView.smoothScrollBy(0, originalPos[1] - top,new DecelerateInterpolator());

            final int marginFlatten = (int) getResources().getDimension(R.dimen.margin_card_flatten);
            final int margin = (int) getResources().getDimension(R.dimen.margin);

            final ValueAnimator varl;

            if (card.isExpanded()) {
                varl = ValueAnimator.ofInt(margin,marginFlatten);
            } else {
                varl = ValueAnimator.ofInt(marginFlatten,margin);
            }

            varl.setDuration(500);
            varl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) recyclerViewHolder.getLayoutParams();
                    params.setMargins(0, 0, 0, (Integer) animation.getAnimatedValue());
                    recyclerViewHolder.setLayoutParams(params);
                }
            });

            varl.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    int originalPos[] = new int[2];
                    recyclerViewHolder.getLocationOnScreen(originalPos);

//                    Log.v("POS END", "x:" + originalPos[0] + "y:" + originalPos[1]);

                    if(previousPosition >= 0){

                        final AdapterCard.CardViewHolder cardViewHolder = (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(previousPosition);

                        if(cardViewHolder != null){

                            if(card.isExpanded()){
                                card.setExpanded(false);
                                cardViewHolder.cardRelativeLayout.setVisibility(View.VISIBLE);
                                viewAux.setVisibility(View.GONE);
                            }else{
                                card.setExpanded(true);
                                cardViewHolder.cardRelativeLayout.setVisibility(View.GONE);
                            }

                        }
                    }

                    processing = false;

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            varl.start();
        }

    }
}
