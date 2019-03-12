package ferox.bracket;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BracketPagerAdapter extends FragmentPagerAdapter {

    public BracketPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                BracketFragment bracketFragment = new BracketFragment();
                return bracketFragment;
            case 1:
                ParticipantsFragment participantsFragment = new ParticipantsFragment();
                return participantsFragment;
            case 2:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Bracket";
            case 1:
                return "Participants";
            case 2:
                return "Settings";
            default:
                return null;
        }

    }
}
