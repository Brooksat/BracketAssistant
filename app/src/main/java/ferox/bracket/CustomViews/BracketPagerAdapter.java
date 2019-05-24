package ferox.bracket.CustomViews;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ferox.bracket.fragments.BracketFragment;
import ferox.bracket.fragments.ParticipantsFragment;
import ferox.bracket.fragments.SettingsFragment;

public class BracketPagerAdapter extends FragmentPagerAdapter {
    BracketFragment currentBracketFragment;
    ParticipantsFragment currentParticipantsFragment;
    SettingsFragment currentSettingsFragment = new SettingsFragment();
    public BracketPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                currentBracketFragment = new BracketFragment();
                return currentBracketFragment;
            case 1:
                currentParticipantsFragment = new ParticipantsFragment();
                return currentParticipantsFragment;
            case 2:
                currentSettingsFragment = new SettingsFragment();
                return currentSettingsFragment;
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

    public BracketFragment getCurrentBracketFragment() {

        return currentBracketFragment;

    }

    public ParticipantsFragment getCurrentParticipantsFragment() {
        return currentParticipantsFragment;
    }

    public SettingsFragment getCurrentSettingsFragment() {
        return currentSettingsFragment;
    }
}
