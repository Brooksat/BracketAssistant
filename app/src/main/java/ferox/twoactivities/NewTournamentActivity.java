package ferox.twoactivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

public class NewTournamentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tournament);
    }

    public void showHostMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.host_menu);
        popup.show();
    }

    public void showFormatMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.single_elim) {
                    findViewById(R.id.single_elim_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.round_robin_view).setVisibility(View.GONE);
                }
                if (item.getItemId() == R.id.double_elim) {
                    findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.double_elim_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.round_robin_view).setVisibility(View.GONE);
                }
                if (item.getItemId() == R.id.round_robin) {
                    findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.round_robin_view).setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        popup.inflate(R.menu.format_menu);
        popup.show();

    }


}
