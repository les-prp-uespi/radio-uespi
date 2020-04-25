package br.uespi.piripiri.radio.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.media.AudioManager;
import android.net.Uri;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;

import android.os.Handler;


import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import br.uespi.piripiri.radio.Conexao;
import br.uespi.piripiri.radio.Player.ConfPlayer;
import com.gustavorw.app.R;

public class LiveFragment extends Fragment implements Player.EventListener {

    /*
    link da documentação da lib: https://exoplayer.dev/doc/reference/com/google/android/exoplayer2/Player.html
     */


    private String playerUri;
    private SimpleExoPlayer player;
    private Handler mHandler;
    private Runnable mRunnable;
    private PlayerView playerView;
    private ProgressBar progressBar;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private Conexao conexao;
    private ImageButton btnCompartilhar, btnAvaliar;

    public LiveFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View viewRoot = inflater.inflate(R.layout.fragment_live,container, false);

        //setVolumeControlStream(AudioManager.STREAM_MUSIC);

        playerView = viewRoot.findViewById(R.id.exoplay);
        progressBar = viewRoot.findViewById(R.id.progress);
        volumeSeekbar = (SeekBar) viewRoot.findViewById(R.id.seekBar);

        playerUri = ConfPlayer.PLAYER_URL;
        setUp();

        //progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        btnAvaliar = viewRoot.findViewById(R.id.btn_avaliar);
        btnCompartilhar = viewRoot.findViewById(R.id.btn_compartilhar);
        //initControls();
        btnAvaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate();
                Toast.makeText(getContext(), R.string.loading, Toast.LENGTH_SHORT).show();
            }
        });

        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
                Toast.makeText(getContext(), R.string.loading, Toast.LENGTH_SHORT).show();
            }
        });

        return viewRoot;
    }
/*
    private void initControls(){
        try{

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
*/

    public void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.via)));
    }

    public void rate() {
        Uri uri = Uri.parse(getString(R.string.link2));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //imple
    private void setUp() {
        initializePlayer();
        if (playerUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(playerUri));
    }



    private void initializePlayer() {
        if (player == null) {
            // Cria um TrackSelector padrão com
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    ConfPlayer.MIN_BUFFER_DURATION,
                    ConfPlayer.MAX_BUFFER_DURATION,
                    ConfPlayer.MIN_PLAYBACK_START_BUFFER,
                    ConfPlayer.MIN_PLAYBACK_RESUME_BUFFER, -1, true);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // cria o player
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), trackSelector, loadControl);
            playerView.setPlayer(player);


            //mVisualizer.setAudioSessionId(player);
        }
    }

    private void buildMediaSource(Uri mUri) {

        // mede a banda antes de carrgar os dados
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // cria uma instacia do dado carregado
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this.getContext(),
                Util.getUserAgent(this.getContext(), getString(R.string.app_name)), bandwidthMeter);
        // recebe a midia a ser produzida, idependente do formato, sem a necessidade de tratamento

        // MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
        //.createMediaSource(mUri);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).setExtractorsFactory(extractorsFactory).createMediaSource(mUri);

        //MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);

        // prepara amidia para a reproducao.
        player.prepare(mediaSource);
        // define quando o arquivo sera tocado
        player.setPlayWhenReady(true);
        //  registra o player para os eventos
        player.addListener(this);

    }


    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
/*
    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }

 */

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                progressBar.setVisibility(View.VISIBLE);
                if(!conexao.isOnline(getContext())){
                    Toast.makeText(getContext(), "Sem conexão com a internet :/", Toast.LENGTH_SHORT).show();
                }
                break;
            case Player.STATE_ENDED:

                break;
            case Player.STATE_IDLE:

                break;
            case Player.STATE_READY:
                progressBar.setVisibility(View.GONE);
                /*
                if(conexao.isOnline(getContext())){
                    Toast.makeText(getContext(), "Conexão com a internet restabelecida :P", Toast.LENGTH_SHORT).show();
                }
                 */
                break;
            default:

                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
