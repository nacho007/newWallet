package deandreis.newwallet;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.TRANSLATION_Y;


public class MainActivity extends AppCompatActivity implements OnCardClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.view)
    View viewAux;

    LinearLayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    int top = 0;
    Card selectedCard;

    View currentCardViewHolder;
    int position;

    static int cardCollapsedSelectedDistanceToTop;
    public static int cardContainerHeight;
    int screenHeight;
    int padding;

    int actionBarHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
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
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
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
    public void onCardClick(final Card card, final View view, final int position, final boolean first, final boolean last) {

        if (!processing) {

            processing = true;
            selectedCard = card;
            currentCardViewHolder = view;
            this.position = position;

            if (card.isSelected()) {
                animateOpen(view,first,last);
            } else {
                animateClose(view,first,last);
            }

        }

    }


    int timeFast = 200;
    int timeSlow = 250;

    private void animateOpen(View view, final boolean first, final boolean last) {

        viewAux.setClickable(true);
        viewAux.setVisibility(View.VISIBLE);

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);


        cardCollapsedSelectedDistanceToTop = originalPos[1] - top;

        Log.v("distance", cardCollapsedSelectedDistanceToTop + "");


        final AdapterCard.CardViewHolder currentViewHolder =
                (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(position);


        int cardHeaderHeight = currentViewHolder.textViewAmountheader.getHeight();

        int cardOpenHeight;

        int tempPadding = padding;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            tempPadding = padding * 2;
        }


        if (cardCollapsedSelectedDistanceToTop < 0) {
            cardOpenHeight = cardContainerHeight + tempPadding - cardCollapsedSelectedDistanceToTop;
        } else {
            cardOpenHeight = cardContainerHeight + tempPadding;
        }


        ValueAnimator heightAnimator = ValueAnimator.ofInt(tempPadding + cardHeaderHeight, cardOpenHeight);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, -cardCollapsedSelectedDistanceToTop);
        final ObjectAnimator translationAnimation = ObjectAnimator.ofPropertyValuesHolder(view, pvhY);

        currentViewHolder.linearLayoutAux.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams paramsContainer = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardContainerHeight);
        currentViewHolder.cardExpanded.setLayoutParams(paramsContainer);

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                if (last) {
                    currentViewHolder.linearLayoutAux.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutAux.requestLayout();
                } else {
                    currentViewHolder.cardExpanded.getLayoutParams().height = value.intValue();
                    currentViewHolder.cardExpanded.requestLayout();
                }

            }
        });


        heightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(first){
                    currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.color.colorWhite);
                }else{
                    currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.color.colorCardGrayDark);
                }

                currentViewHolder.cardHelperBackgroundBottom.setVisibility(View.VISIBLE);
                currentViewHolder.cardCollapsed.setVisibility(View.GONE);
                currentViewHolder.cardExpanded.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                translationAnimation.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });



        translationAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {


            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.color.colorWhite);
                processing = false;
                Log.v("height", currentViewHolder.linearLayoutAux.getHeight() + "");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        translationAnimation.setDuration(timeSlow);
        heightAnimator.setDuration(timeFast);

        heightAnimator.start();
    }


    private void animateClose(final View view, final boolean first, final boolean last) {

        final AdapterCard.CardViewHolder currentViewHolder =
                (AdapterCard.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

        int cardHeaderHeight = currentViewHolder.textViewAmountheader.getHeight();

        final ValueAnimator heightAnimator;
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0);
        final ObjectAnimator translationAnimation = ObjectAnimator.ofPropertyValuesHolder(view, pvhY);

        if (!last) {
            heightAnimator = ValueAnimator.ofInt(cardContainerHeight, padding + cardHeaderHeight);
        } else {
            heightAnimator = ValueAnimator.ofInt(cardContainerHeight, 0);
        }


        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                if (last) {
                    currentViewHolder.linearLayoutAux.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutAux.requestLayout();
                } else {
                    currentViewHolder.cardExpanded.getLayoutParams().height = value.intValue();
                    currentViewHolder.cardExpanded.requestLayout();
                }
            }
        });


        heightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (!last) {
                    currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.drawable.shape_card_top);
                    currentViewHolder.cardCollapsed.setVisibility(View.VISIBLE);
                } else {
                    currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.color.colorCardGrayDark);
                    currentViewHolder.cardCollapsed.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (!last) {
                    currentViewHolder.cardExpanded.setVisibility(View.GONE);
                } else {
                    currentViewHolder.cardExpanded.setVisibility(View.VISIBLE);
                }

                RelativeLayout.LayoutParams paramsContainer = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.card_height));
                currentViewHolder.cardExpanded.setLayoutParams(paramsContainer);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardContainerHeight);
                currentViewHolder.linearLayoutAux.setLayoutParams(params);
                currentViewHolder.linearLayoutAux.setVisibility(View.GONE);

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


        translationAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(first){
                    currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.color.colorWhite);
                }else{
                    currentViewHolder.cardHelperBackgroundTop.setBackgroundResource(R.color.colorCardGrayDark);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                heightAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        translationAnimation.setDuration(timeSlow);
        heightAnimator.setDuration(timeSlow);

        translationAnimation.start();
    }


}
