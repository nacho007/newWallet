package deandreis.newwallet;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

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
    int padding;


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

        padding = (int) getResources().getDimension(R.dimen.padding);

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
        if (!processing) {
            currentCardViewHolder.performClick();
        }

    }


    @Override
    public void onCardClick(final Card card, final View view, final int previousPosition, final boolean last) {

        if (!processing) {

            processing = true;
            selectedCard = card;
            currentCardViewHolder = view;
            this.previousPosition = previousPosition;

            if (card.isSelected()) {
                animateOpen(view, last);
            } else {
                animateClose(last);
            }

        }

    }

    private void animateOpen(View view, final boolean last) {

        viewAux.setClickable(true);
        viewAux.setVisibility(View.VISIBLE);

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        final int marginFlatten = (int) getResources().getDimension(R.dimen.margin_card_flatten);

        cardCollapsedSelectedTopDistance = originalPos[1] - top;


        final AdapterCard.CardViewHolder currentViewHolder =
                (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(previousPosition + 1);

        int start = currentViewHolder.textViewAmountheader.getHeight();
        final int margin = (int) getResources().getDimension(R.dimen.margin);


        ValueAnimator valueAnimator;
        valueAnimator = ValueAnimator.ofInt((padding * 3) + start, cardContainerHeight);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new LinearInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                currentViewHolder.linearLayoutCardContent.getLayoutParams().height = value.intValue();
                currentViewHolder.linearLayoutCardContent.requestLayout();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (!last) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) currentViewHolder.linearLayoutCardContent.getLayoutParams();
                    lp.setMargins(0, 0, 0, marginFlatten);
                    currentViewHolder.linearLayoutCardContent.setLayoutParams(lp);
                }

                currentViewHolder.linearLayoutCardContent.setVisibility(View.VISIBLE);
                currentViewHolder.textViewAmountheader.setVisibility(View.GONE);
                currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (!last) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) currentViewHolder.linearLayoutCardContent.getLayoutParams();
                    lp.setMargins(0, 0, 0, margin);
                    currentViewHolder.linearLayoutCardContent.setLayoutParams(lp);
                }

                recyclerView.addOnScrollListener(onScrollListener);

                if (cardCollapsedSelectedTopDistance == 0) {
                    scrollEnded();
                }

                recyclerView.smoothScrollBy(0, cardCollapsedSelectedTopDistance, new DecelerateInterpolator());
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        valueAnimator.start();

    }



    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == 0) {
                scrollEnded();
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private void scrollEnded() {
        if (!isGone) {
            isGone = true;
            processing = false;

            if (previousPosition >= 0) {
                previousCardViewHolder = (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(previousPosition);
                previousCardViewHolder.cardRow.setVisibility(View.GONE);
            }

            recyclerView.removeOnScrollListener(onScrollListener);
        }
    }


    private void animateClose(final boolean last) {

        final int marginFlatten = (int) getResources().getDimension(R.dimen.margin_card_flatten);

        processing = false;
        viewAux.setClickable(false);
        viewAux.setVisibility(View.GONE);

        if (isGone) {
            isGone = false;
            if (previousPosition >= 0) {
                previousCardViewHolder.cardRow.setVisibility(View.VISIBLE);
            }

        }


        final AdapterCard.CardViewHolder currentViewHolder =
                (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(previousPosition + 1);

        int start = currentViewHolder.textViewAmountheader.getHeight();


        ValueAnimator valueAnimator;
        valueAnimator = ValueAnimator.ofInt(cardContainerHeight, (padding * 3) + start);
        valueAnimator.setDuration(150);
        valueAnimator.setInterpolator(new LinearInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                currentViewHolder.linearLayoutCardContent.getLayoutParams().height = value.intValue();
                currentViewHolder.linearLayoutCardContent.requestLayout();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (!last) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) currentViewHolder.linearLayoutCardContent.getLayoutParams();
                    lp.setMargins(0, 0, 0, marginFlatten);
                    currentViewHolder.linearLayoutCardContent.setLayoutParams(lp);
                }

                currentViewHolder.textViewAmountheader.setVisibility(View.VISIBLE);
                currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (!last) {
                    currentViewHolder.cardRow.setBackgroundResource(R.drawable.shape_card_top);
                }

                currentViewHolder.linearLayoutCardContent.setVisibility(View.GONE);

//                        cardCollapsedSelectedTopDistance = -cardCollapsedSelectedTopDistance;
//                        recyclerView.smoothScrollBy(0, cardCollapsedSelectedTopDistance, new DecelerateInterpolator());
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        valueAnimator.start();

//                cardCollapsedSelectedTopDistance = -cardCollapsedSelectedTopDistance;
//                recyclerView.scrollBy(0, cardCollapsedSelectedTopDistance);


    }


}
