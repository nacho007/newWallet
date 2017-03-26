package deandreis.newwallet;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
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

    View currentCardViewHolder;
    AdapterCard.CardViewHolder previousCardViewHolder;
    boolean isGone;
    int previousPosition;

    static int cardCollapsedSelectedTopDistance;
    public static int cardContainerHeight;
    int screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Card> cardList = Mock.getCards();

        adapter = new AdapterCard(cardList, this);
        recyclerView.setAdapter(adapter);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        final int padding = (int) getResources().getDimension(R.dimen.padding);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            cardContainerHeight = screenHeight - actionBarHeight - padding - getStatusBarHeight();
            Log.v("cardContainerHeight", cardContainerHeight + "");
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int originalPos[] = new int[2];
                recyclerView.getLocationOnScreen(originalPos);

                if (originalPos[1] == 0) {
                    handler.postDelayed(this, 20);
                } else {

                    top = originalPos[1] + padding;

                    Log.v("RECYCLER", "x:" + originalPos[0] + "y:" + originalPos[1]);
                    Log.v("RECYCLER", "top:" + top);

                }

            }
        }, 20);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("Status bar", result + "");
        return result;
    }

    boolean processing = false;

    public void onViewClick(View v) {
        if(!processing){
            currentCardViewHolder.performClick();
        }

//        onCardClick(selectedCard, currentCardViewHolder, previousPosition);
    }


    @Override
    public void onCardClick(final Card card, View view, final int previousPosition) {

        if (!processing) {
            processing = true;
            selectedCard = card;
            currentCardViewHolder = view;
            this.previousPosition = previousPosition;

            int originalPos[] = new int[2];
            view.getLocationOnScreen(originalPos);

            Log.v("clicked on click", originalPos[1] + "");

            if (card.isSelected()) {
                viewAux.setClickable(true);
                viewAux.setVisibility(View.VISIBLE);

                cardCollapsedSelectedTopDistance = originalPos[1] - top;

                if(previousPosition >= 0){

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);

                                    if (newState == 0) {

                                        if (!isGone) {
                                            isGone = true;
                                            processing = false;

                                            if(previousPosition >= 0){
                                                previousCardViewHolder = (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(previousPosition);
                                                previousCardViewHolder.cardRow.setVisibility(View.GONE);
                                            }

                                            recyclerView.removeOnScrollListener(this);
                                        }

                                    }

                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                }
                            });

                            recyclerView.smoothScrollBy(0, cardCollapsedSelectedTopDistance, new DecelerateInterpolator());
                        }
                    }, 100);

                }else{
                    isGone = true;
                    processing = false;
                }

            } else {
                processing = false;
                viewAux.setClickable(false);
                viewAux.setVisibility(View.GONE);
                if (isGone) {
                    isGone = false;
                    if(previousPosition >= 0){
                        previousCardViewHolder.cardRow.setVisibility(View.VISIBLE);
                    }

                }
                cardCollapsedSelectedTopDistance = -cardCollapsedSelectedTopDistance;
                recyclerView.scrollBy(0, cardCollapsedSelectedTopDistance);
            }


            Log.v("top distance", cardCollapsedSelectedTopDistance + "");

        }

    }
}
