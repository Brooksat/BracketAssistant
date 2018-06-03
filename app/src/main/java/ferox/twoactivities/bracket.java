package ferox.twoactivities;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class bracket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracket);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.bracket_root);
        LineDrawView lineDrawView = new LineDrawView(getApplicationContext(),findViewById(R.id.toptext), findViewById(R.id.bottomtext));
        ConstraintSet set = new ConstraintSet();
        constraintLayout.addView(lineDrawView);

    }


}
