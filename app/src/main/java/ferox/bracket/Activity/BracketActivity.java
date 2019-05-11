package ferox.bracket.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import ferox.bracket.CustomViews.BracketPagerAdapter;
import ferox.bracket.CustomViews.ViewPagerNoHorsScroll;
import ferox.bracket.R;

public class BracketActivity extends AppCompatActivity {


    TextView yposition;
    TextView bracketHeight;
    TextView screenHeight;


    BracketPagerAdapter mBracketPagerAdapter;
    ViewPagerNoHorsScroll mViewPager;
    AppBarLayout appBarLayout;


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
        appBarLayout = findViewById(R.id.app_bar);


        TabLayout tabLayout = findViewById(R.id.bracket_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) tabLayout.getLayoutParams();
                Log.d("TabPosition", String.valueOf(tab.getPosition()));
                if (tab.getPosition() == 0) {
                    appBarLayout.setExpanded(true);
                    layoutParams.setScrollFlags(0);
                    tabLayout.setLayoutParams(layoutParams);
                } else {
                    Log.d("elseran", "elseran");
                    layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                            AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                    tabLayout.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        final int delay = 800; //milliseconds

        handler.postDelayed(runnable, delay);

    }








}

