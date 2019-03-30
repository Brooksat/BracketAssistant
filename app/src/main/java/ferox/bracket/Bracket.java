package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Bracket extends AppCompatActivity {


    TextView yposition;
    TextView bracketHeight;
    TextView screenHeight;


    BracketPagerAdapter mBracketPagerAdapter;
    ViewPagerNoHorsScroll mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracket);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();


        mViewPager = findViewById(R.id.fragment_container);
        mViewPager.setOffscreenPageLimit(2);
        mBracketPagerAdapter = new BracketPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mBracketPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.bracket_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);


//        mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height) / 2;
//        mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                lv.updateAnimation();
//            }
//        };
//        final int delay = 800; //milliseconds
//
//        handler.postDelayed(runnable, delay);

    }








}

