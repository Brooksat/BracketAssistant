package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyLog;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VolleyLog.DEBUG = true;
        ChallongeRequests.setApiKey("hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07");

    }

    public void goToSecondActivity(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
