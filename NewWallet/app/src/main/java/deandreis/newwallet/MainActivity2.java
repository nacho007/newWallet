package deandreis.newwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ignaciodeandreisdenis on 27/3/17.
 */

public class MainActivity2 extends AppCompatActivity {

    @BindView(R.id.view2)
    View view2;

    @BindView(R.id.viewFix)
    View viewFix;

    boolean visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);


    }

    public void onButtonClick(View v){

        if(visible){
            viewFix.animate()
                    .translationY(viewFix.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
        }else{
            viewFix.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(300);
        }

        visible = !visible;

    }

}
