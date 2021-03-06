package com.hyperion.ths.marvel_03.ui.hero;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hyperion.ths.marvel_03.R;
import com.hyperion.ths.marvel_03.data.source.HeroRepository;
import com.hyperion.ths.marvel_03.data.source.local.sqlite.HeroLocalDataSource;
import com.hyperion.ths.marvel_03.data.source.remote.api.HeroRemoteDataSource;
import com.hyperion.ths.marvel_03.data.source.remote.api.service.MarvelServiceClient;
import com.hyperion.ths.marvel_03.databinding.FragmentHeroBinding;
import com.hyperion.ths.marvel_03.utils.navigator.Navigator;
import com.hyperion.ths.marvel_03.utils.rx.SchedulerProvider;
import com.hyperion.ths.marvel_03.widget.dialog.DialogManager;
import com.hyperion.ths.marvel_03.widget.dialog.DialogManagerImpl;
import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeroFragment extends Fragment {

    private HeroViewModel mHeroViewModel;
    private static final String TAG = HeroFragment.class.getSimpleName();

    public HeroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        FragmentHeroBinding fragmentHeroBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_hero, container, false);
        HeroLocalDataSource heroLocalDataSource = new HeroLocalDataSource(getActivity());
        HeroRepository heroRepository =
                new HeroRepository(new HeroRemoteDataSource(MarvelServiceClient.getInstance()),
                        heroLocalDataSource);
        HeroFragmentAdapter heroFragmentAdapter = new HeroFragmentAdapter(heroRepository);
        Navigator navigator = new Navigator(getActivity());
        DialogManager dialogManager = new DialogManagerImpl(getContext());
        mHeroViewModel =
                new HeroViewModel(heroRepository, heroFragmentAdapter, navigator, dialogManager);
        mHeroViewModel.setBaseSchedulerProvider(SchedulerProvider.getInstance());
        View view = fragmentHeroBinding.getRoot();
        fragmentHeroBinding.setViewModel(mHeroViewModel);

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && mHeroViewModel != null) {
            mHeroViewModel.reLoadList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHeroViewModel == null) {
            return;
        }
        mHeroViewModel.reLoadList();
    }

    @Override
    public void onStop() {
        mHeroViewModel.onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mHeroViewModel.getAllHeroes();
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
