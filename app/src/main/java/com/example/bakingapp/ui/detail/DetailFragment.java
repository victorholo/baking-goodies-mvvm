package com.example.bakingapp.ui.detail;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.R;
import com.example.bakingapp.databinding.FragmentDetailBinding;
import com.example.bakingapp.di.Injectable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;
import static com.example.bakingapp.utils.RecipesUtils.STEP_ID_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements Injectable, View.OnClickListener {

    private static final String PLAYER_POSITION_STATE = "player_position_state";

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentDetailBinding mFragmentDetailBinding;

    private DetailViewModel mDetailViewModel;

    private SimpleExoPlayer mExoPlayer;

    private long mPlayerPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        return mFragmentDetailBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Boolean isTablet = getActivity().getResources().getBoolean(R.bool.is_tablet);
        final Boolean isLandscape = getActivity().getResources().getBoolean(R.bool.is_landscape);

        int recipeId, stepId;

        if (savedInstanceState == null) {
            recipeId = getArguments().getInt(RECIPE_ID_EXTRA, -1);
            stepId = getArguments().getInt(STEP_ID_EXTRA, -1);
        } else {
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION_STATE);
            recipeId = savedInstanceState.getInt(RECIPE_ID_EXTRA, -1);
            stepId = savedInstanceState.getInt(STEP_ID_EXTRA, -1);
        }

        mDetailViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DetailViewModel.class);

        if (recipeId != -1) {
            mDetailViewModel.setRecipeId(recipeId);
        }

        if (stepId != -1) {
            mDetailViewModel.setStepId(stepId);
        }

        if (!isTablet && !isLandscape) {
            mFragmentDetailBinding.previousStepButton.setOnClickListener(this);
            mFragmentDetailBinding.nextStepButton.setOnClickListener(this);
        }

        // Update the list when the data changes
        mDetailViewModel.getStep().observe(this, step -> {
            if (step != null) {

                String videoUrl = step.getVideoURL();
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    showVideoPlaceholder(false);
                    initializePlayer(Uri.parse(videoUrl));
                } else {
                    if (mExoPlayer != null) mExoPlayer.stop();

                    showVideoPlaceholder(true);
                    String thumbnailUrl = step.getThumbnailURL();
                    loadPlayerThumbnail(thumbnailUrl);
                }

                if (isTablet || !isLandscape) {
                    mFragmentDetailBinding.descriptionTitleTextview.setText(step.getDescription());
                    mFragmentDetailBinding.stepTitleTextview.setText(step.getShortDescription());
                }

                if (!isLandscape && !isTablet) {
                    ActionBar activityActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (activityActionBar != null) {
                        activityActionBar.setTitle(getString(R.string.step_number_format, step.getId() + 1));
                    }

                    if (!mDetailViewModel.hasNext())
                        mFragmentDetailBinding.nextStepButton.setVisibility(View.INVISIBLE);
                    else mFragmentDetailBinding.nextStepButton.setVisibility(View.VISIBLE);

                    if (!mDetailViewModel.hasPrevious())
                        mFragmentDetailBinding.previousStepButton.setVisibility(View.INVISIBLE);
                    else mFragmentDetailBinding.previousStepButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RECIPE_ID_EXTRA, mDetailViewModel.getRecipeId());
        outState.putInt(STEP_ID_EXTRA, mDetailViewModel.getStepId());
        if (mExoPlayer != null)
            outState.putLong(PLAYER_POSITION_STATE, mExoPlayer.getCurrentPosition());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_step_button) {
            if (mExoPlayer != null) mExoPlayer.stop();
            mDetailViewModel.getNext();
        } else if (v.getId() == R.id.previous_step_button) {
            if (mExoPlayer != null) mExoPlayer.stop();
            mDetailViewModel.getPrevious();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void loadPlayerThumbnail(String url) {
        if (url != null && !url.isEmpty()) {
            Picasso.get().load(url).placeholder(R.drawable.ic_exo_player_thumbnail).error(R.drawable.ic_exo_player_thumbnail).into(mFragmentDetailBinding.playerPlaceholder);
        } else {
            Picasso.get().load(R.drawable.ic_exo_player_thumbnail).into(mFragmentDetailBinding.playerPlaceholder);
        }
    }

    private void showVideoPlaceholder(Boolean show) {
        if (show) {
            mFragmentDetailBinding.playerPlaceholder.setVisibility(View.VISIBLE);
            mFragmentDetailBinding.playerView.setVisibility(View.INVISIBLE);
        } else {
            mFragmentDetailBinding.playerPlaceholder.setVisibility(View.INVISIBLE);
            mFragmentDetailBinding.playerView.setVisibility(View.VISIBLE);
        }
    }

    private void initializePlayer(Uri videoUri) {
        if (mExoPlayer == null) {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

            mFragmentDetailBinding.playerView.setPlayer(mExoPlayer);


        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), "BakingGoodies"), new DefaultBandwidthMeter());
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
        mExoPlayer.prepare(videoSource);

        mExoPlayer.setPlayWhenReady(true);
        if (mPlayerPosition != -1) {
            mExoPlayer.seekTo(mPlayerPosition);
            mPlayerPosition = -1;
        }

    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
}
