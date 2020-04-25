package br.uespi.piripiri.radio;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import br.uespi.piripiri.radio.Fragments.LiveFragment;
import br.uespi.piripiri.radio.Fragments.NewsFragment;

public class MyFragmentPagerAdapter  extends FragmentPagerAdapter {
    private String[] arrayTab;
    private Context context;
    public MyFragmentPagerAdapter(@NonNull FragmentManager fm, String[] arrayTab, Context context) {
        super(fm);
        this.arrayTab = arrayTab;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LiveFragment liveFragment = new LiveFragment();
                return liveFragment;
            case 1:
                NewsFragment newsFragment = new NewsFragment(context);
                return newsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.arrayTab.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.arrayTab[position];
    }
}
