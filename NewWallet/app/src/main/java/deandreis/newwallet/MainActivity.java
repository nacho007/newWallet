package deandreis.newwallet;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.TRANSLATION_Y;
import static deandreis.newwallet.R.id.view;

public class MainActivity extends AppCompatActivity implements OnCardClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(view)
    View viewAux;

    RecyclerView.Adapter adapter;

    int top = 0;
    Card selectedCard;

    View currentCardViewHolder;
    int position;

    static int cardCollapsedSelectedDistanceToTop;
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
            cardContainerHeight = screenHeight - actionBarHeight - getStatusBarHeight();
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
    public void onCardClick(final Card card, final View view, final int position, final boolean last) {

        if (!processing) {

            processing = true;
            selectedCard = card;
            currentCardViewHolder = view;
            this.position = position;

            if (card.isSelected()) {
                animateOpen(view, last);
            } else {
                animateClose(view, last);
            }

        }

    }


    int time = 700;
    int time2 = 350;

    private void animateOpen(View view, final boolean last) {

        viewAux.setClickable(true);
        viewAux.setVisibility(View.VISIBLE);

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        final int marginFlatten = (int) getResources().getDimension(R.dimen.margin_card_flatten);

        cardCollapsedSelectedDistanceToTop = originalPos[1] - top;

        Log.v("distance", cardCollapsedSelectedDistanceToTop + "");

        final AdapterCard.CardViewHolder currentViewHolder =
                (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

        int cardHeaderHeight = currentViewHolder.textViewAmountheader.getHeight();

        int cardOpenHeight;

        if (cardCollapsedSelectedDistanceToTop < 0) {
            cardOpenHeight = cardContainerHeight + padding - cardCollapsedSelectedDistanceToTop;
        } else {
            cardOpenHeight = cardContainerHeight + padding;
        }

        ValueAnimator heightAnimator = ValueAnimator.ofInt((padding * 3) + cardHeaderHeight, cardOpenHeight);

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                if (last) {
                    currentViewHolder.linearLayoutAux.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutAux.requestLayout();
                } else {
                    currentViewHolder.linearLayoutCardContent.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutCardContent.requestLayout();
                }

            }
        });

        heightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (last) {
                    currentViewHolder.linearLayoutAux.setVisibility(View.VISIBLE);
                }

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) currentViewHolder.linearLayoutCardContent.getLayoutParams();
                lp.setMargins(0, 0, 0, marginFlatten);
                currentViewHolder.linearLayoutCardContent.setLayoutParams(lp);

                currentViewHolder.linearLayoutCardContent.setVisibility(View.VISIBLE);
                currentViewHolder.textViewAmountheader.setVisibility(View.GONE);
                currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
                currentViewHolder.cardRelativeLayout.setBackgroundResource(R.drawable.shape_card);

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, -cardCollapsedSelectedDistanceToTop);
        ObjectAnimator translationAnimation = ObjectAnimator.ofPropertyValuesHolder(view, pvhY);

        translationAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentViewHolder.linearLayoutCardContent.setBackgroundResource(R.color.colorWhite);
                processing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        translationAnimation.setDuration(time);
        heightAnimator.setDuration(time2);

        heightAnimator.start();
        translationAnimation.start();
    }


    private void animateClose(final View view, final boolean last) {

        final AdapterCard.CardViewHolder currentViewHolder =
                (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

        int cardHeaderHeight = currentViewHolder.textViewAmountheader.getHeight();

        ValueAnimator heightAnimator = ValueAnimator.ofInt(cardContainerHeight, (padding * 3) + cardHeaderHeight);


        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                if (last) {
                    currentViewHolder.linearLayoutAux.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutAux.requestLayout();
                } else {
                    currentViewHolder.linearLayoutCardContent.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutCardContent.requestLayout();
                }

            }
        });


        heightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                currentViewHolder.linearLayoutCardContent.setBackgroundResource(R.color.transparent);
                currentViewHolder.textViewAmountheader.setVisibility(View.VISIBLE);
                currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (!last) {
                    currentViewHolder.cardRow.setBackgroundResource(R.drawable.shape_card_top);
                    currentViewHolder.linearLayoutCardContent.setVisibility(View.GONE);
                    currentViewHolder.linearLayoutAux.setVisibility(View.GONE);
                } else {
                    currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
                    currentViewHolder.linearLayoutCardContent.setVisibility(View.VISIBLE);
                    currentViewHolder.linearLayoutAux.setVisibility(View.VISIBLE);
                    currentViewHolder.cardRelativeLayout.setBackgroundResource(R.drawable.shape_last_card);
                }


                processing = false;
                viewAux.setClickable(false);
                viewAux.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0);
        ObjectAnimator translationAnimation = ObjectAnimator.ofPropertyValuesHolder(view, pvhY);

        translationAnimation.setDuration(time2);
        heightAnimator.setDuration(time);


        heightAnimator.start();
        translationAnimation.start();

    }


}
