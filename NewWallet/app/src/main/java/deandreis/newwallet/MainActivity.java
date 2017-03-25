package deandreis.newwallet;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnCardClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;

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
    }

    @Override
    public void onCardClick(Card card, final View view) {

        final int marginFlatten = (int) getResources().getDimension(R.dimen.margin_card_flatten);
        final int margin = (int) getResources().getDimension(R.dimen.margin);

        ValueAnimator varl;

        if (card.isExpanded()) {
            card.setExpanded(false);
            varl = ValueAnimator.ofInt(margin,marginFlatten);
        } else {
            card.setExpanded(true);
            varl = ValueAnimator.ofInt(marginFlatten,margin);
        }

        varl.setDuration(500);
        varl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                params.setMargins(0, 0, 0, (Integer) animation.getAnimatedValue());
                view.setLayoutParams(params);
            }
        });
        varl.start();

    }
}
