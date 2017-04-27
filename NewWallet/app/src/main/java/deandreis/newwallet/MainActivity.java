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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.TRANSLATION_Y;


public class MainActivity extends AppCompatActivity implements OnCardClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.viewCardsBackground)
    View viewCardsBackground;

    @BindView(R.id.view)
    View viewAux;

    LinearLayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private int overallYScroll = 0;
    private int scrollOnceEnteredLast = 0;

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


        viewCardsBackground.setTag("");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                overallYScroll = overallYScroll + dy;


//                Log.i("check", "overall->" + overallYScroll);

//                Log.i("lastcomplete", layoutManager.findLastCompletelyVisibleItemPosition() + "");

                if (layoutManager.findFirstVisibleItemPosition() < 1 && layoutManager.findLastVisibleItemPosition() == (adapter.getItemCount() - 1)) {
                    Log.i("Scroll", "Case 1");
                    scrollOnceEnteredLast = 0;
                    checkBorderCases(1);
                } else if (layoutManager.findFirstVisibleItemPosition() < 1) {
                    Log.i("Scroll", "Case 2");
                    scrollOnceEnteredLast = 0;
                    checkBorderCases(2);
                } else if (layoutManager.findLastVisibleItemPosition() == (adapter.getItemCount() - 1)) {
                    Log.i("Scroll", "Case 3");

                    scrollOnceEnteredLast = scrollOnceEnteredLast + dy;

//                    Log.i("Scroll", scrollOnceEnteredLast + "");

                    if (scrollOnceEnteredLast >= actionBarHeight / 2) {
                        Log.i("Scroll", "Case 3.1");
                        if (!viewCardsBackground.getTag().equals("shape_card_bottom")) {
                            Log.v("Log", "shape_card_bottom");
                            viewCardsBackground.setTag("shape_card_bottom");
                            viewCardsBackground.setBackgroundResource(R.drawable.shape_card_bottom);
                        }
                    } else {
                        Log.i("Scroll", "Case 3.2");
                        setMediumZone();
                    }

                } else if (layoutManager.findFirstVisibleItemPosition() >= 1) {
                    Log.i("Scroll", "Case 4");
                    scrollOnceEnteredLast = 0;
                    setMediumZone();
                }

            }

        });

    }

    private void setMediumZone() {
        if (!viewCardsBackground.getTag().equals("colorCardGrayDark")) {
            viewCardsBackground.setTag("colorCardGrayDark");
            viewCardsBackground.setBackgroundResource(R.color.colorCardGrayDark);
        }
    }

    private void checkBorderCases(int scenario) {
        if (overallYScroll >= actionBarHeight / 2) {

            Log.i("Scroll", "Case 2.1");

            if (layoutManager.findLastVisibleItemPosition() == (adapter.getItemCount() - 1)) {
                Log.i("Scroll", "Case 2.1.1");
                if (!viewCardsBackground.getTag().equals("shape_card_bottom")) {
                    viewCardsBackground.setTag("shape_card_bottom");
                    viewCardsBackground.setBackgroundResource(R.drawable.shape_card_bottom);
                }
            } else {
                Log.i("Scroll", "Case 2.1.2");
                if (!viewCardsBackground.getTag().equals("colorCardGrayDark")) {
                    viewCardsBackground.setTag("colorCardGrayDark");
                    viewCardsBackground.setBackgroundResource(R.color.colorCardGrayDark);
                }
            }

        } else {
            Log.i("Scroll", "Case 2.2");

            if (scenario == 1) {
                Log.i("Scroll", "Case 2.2.1");

                if (recyclerView.canScrollVertically(View.SCROLL_AXIS_VERTICAL)) {
                    if (!viewCardsBackground.getTag().equals("shape_card_top")) {
                        viewCardsBackground.setTag("shape_card_top");
                        viewCardsBackground.setBackgroundResource(R.drawable.shape_card_top);
                    }
                } else {
                    if (!viewCardsBackground.getTag().equals("shape_card_dark")) {
                        viewCardsBackground.setTag("shape_card_dark");
                        viewCardsBackground.setBackgroundResource(R.drawable.shape_card_dark);
                    }
                }

            }

            if (scenario == 2) {
                Log.i("Scroll", "Case 2.2.2");
                if (!viewCardsBackground.getTag().equals("shape_card_top")) {
                    viewCardsBackground.setTag("shape_card_top");
                    viewCardsBackground.setBackgroundResource(R.drawable.shape_card_top);
                }
            }


        }
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


    int time = 350;
    int time2 = 350;

    private void animateOpen(View view, final boolean last) {

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

        if (cardCollapsedSelectedDistanceToTop < 0) {
            cardOpenHeight = cardContainerHeight + padding - cardCollapsedSelectedDistanceToTop;
        } else {
            cardOpenHeight = cardContainerHeight + padding;
        }

        ValueAnimator heightAnimator = ValueAnimator.ofInt((padding * 2) + cardHeaderHeight, cardOpenHeight);

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                if (last) {
                    currentViewHolder.linearLayoutAux.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutAux.requestLayout();
                } else {
//                    currentViewHolder.linearLayoutCardContent.getLayoutParams().height = value.intValue();
//                    currentViewHolder.linearLayoutCardContent.requestLayout();
                }

            }
        });


        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, -cardCollapsedSelectedDistanceToTop);
        ObjectAnimator translationAnimation = ObjectAnimator.ofPropertyValuesHolder(view, pvhY);

        translationAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                currentViewHolder.linearLayoutCardContent.setBackgroundResource(R.color.transparent);

                currentViewHolder.linearLayoutAux.setVisibility(View.VISIBLE);

//                currentViewHolder.linearLayoutCardContent.setVisibility(View.VISIBLE);
                currentViewHolder.textViewAmountheader.setVisibility(View.GONE);
                currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
                currentViewHolder.cardRelativeLayout.setBackgroundResource(R.drawable.shape_card);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
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

        ValueAnimator heightAnimator = ValueAnimator.ofInt(cardContainerHeight, (padding * 2) + cardHeaderHeight);


        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                if (last) {
                    currentViewHolder.linearLayoutAux.getLayoutParams().height = value.intValue();
                    currentViewHolder.linearLayoutAux.requestLayout();
                } else {
//                    currentViewHolder.linearLayoutCardContent.getLayoutParams().height = value.intValue();
//                    currentViewHolder.linearLayoutCardContent.requestLayout();
                }

            }
        });


        heightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
//                currentViewHolder.linearLayoutCardContent.setBackgroundResource(R.color.transparent);
                currentViewHolder.textViewAmountheader.setVisibility(View.VISIBLE);
                currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardContainerHeight);
//                currentViewHolder.linearLayoutAux.setLayoutParams(params);

                if (!last) {
                    currentViewHolder.cardRow.setBackgroundResource(R.drawable.shape_card_top);
//                    currentViewHolder.linearLayoutCardContent.setVisibility(View.GONE);
                } else {
                    currentViewHolder.cardRow.setBackgroundResource(R.color.transparent);
//                    currentViewHolder.linearLayoutCardContent.setVisibility(View.VISIBLE);
                }

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


        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0);
        ObjectAnimator translationAnimation = ObjectAnimator.ofPropertyValuesHolder(view, pvhY);

        translationAnimation.setDuration(time2);
        heightAnimator.setDuration(time);


        heightAnimator.start();
        translationAnimation.start();

    }


}
