package no.appsonite.gpsping.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import no.appsonite.gpsping.R;

/**
 * Created: Belozerov Sergei
 * E-mail: belozerow@gmail.com
 * Company: APPGRANULA LLC
 * Date: 06/09/16
 */
public class IntroActivity extends AppCompatActivity {
    private static final long DELAY = 1000 * 7;
    private ViewPager viewPager;
    private Handler nextScreenHandler = new Handler();
    private Runnable nextScreen = new Runnable() {
        @Override
        public void run() {
            try {
                viewPager.setCurrentItem(Math.min(viewPager.getAdapter().getCount() - 1, viewPager.getCurrentItem() + 1), true);
                nextScreenHandler.postDelayed(nextScreen, DELAY);
            } catch (Exception ignore) {

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.viewpager);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);

        findViewById(R.id.close).setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("INTRO", MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("INTRO_COMPLETED", true).apply();
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            nextScreenHandler.removeCallbacks(nextScreen);
        } catch (Exception ignore) {

        }
    }

    private static class Adapter extends FragmentStatePagerAdapter {
        private static List<Integer> items = new ArrayList<>();

        static {
            items.add(R.drawable.intro_01);
            items.add(R.drawable.intro_02);
            items.add(R.drawable.intro_03);
            items.add(R.drawable.intro_04);
            items.add(R.drawable.intro_05);
        }

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Integer item = items.get(position);
            return IntroFragment.newInstance(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    public static class IntroFragment extends Fragment {
        private static final String ARG_IMAGE = "arg_image";

        public static IntroFragment newInstance(int imageId) {
            Bundle args = new Bundle();
            args.putInt(ARG_IMAGE, imageId);
            IntroFragment fragment = new IntroFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_intro_view, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ((ImageView) view.findViewById(R.id.introImage)).setImageResource(getArguments().getInt(ARG_IMAGE));
        }
    }
}
