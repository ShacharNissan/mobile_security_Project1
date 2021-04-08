package com.ShacharNissan.project1.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ShacharNissan.project1.MainActivity;
import com.ShacharNissan.project1.R;

public class VoiceVolumnWrapper {

    private AudioManager _AudioManager;
    private Context _Context;

    private static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private MyVolumeReceiver mVolumeReceiver;

    public VoiceVolumnWrapper(Context context) {

        _Context = context;
        _AudioManager = (AudioManager) _Context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * // Call volume
     *
     * @return
     */
    public int GetCallVoiceMax() {

        return _AudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
    }

    /**
     * // Call volume
     *
     * @return
     */
    public int GetCallVoiceCurrentValue() {

        return _AudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
    }


    /**
     * // System volume
     *
     * @return
     */
    public int GetSystemVoiceMax() {

        return _AudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * // System volume
     *
     * @return
     */

    /**
     * // System volume
     *
     * @return
     */
    public int GetSystemVoiceCurrentValue() {

        return _AudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }


    /**
     * // Ringer volume
     *
     * @return
     */
    public int GetRingVoiceMax() {

        return _AudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    /**
     * // Ringer volume
     *
     * @return
     */

    /**
     * // Ringer volume
     *
     * @return
     */
    public int GetRingVoiceCurrentValue() {

        return _AudioManager.getStreamVolume(AudioManager.STREAM_RING);
    }


    /**
     * // Music volume
     *
     * @return
     */
    public int GetMusicVoiceMax() {

        return _AudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    /**
     * // Music volume
     *
     * @return
     */
    public int GetMusicVoiceCurrentValue() {

        return _AudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * Increase music volume
     *
     * @param value
     */
    public void AddMusicVoiceVolumn(int value) {
        int addValue = (GetMusicVoiceCurrentValue() + value);
        // Prevent the volume value from out of range
        addValue = addValue > GetMusicVoiceMax() ? GetMusicVoiceMax() : addValue;
        _AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, addValue, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * Register broadcast monitor
     */
    public void registerVolumeReceiver() {
        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        _Context.registerReceiver(mVolumeReceiver, filter);
    }

    /**
     * Unregister broadcast monitor
     */
    public void unregisterVolumeReceiver() {

        if (mVolumeReceiver != null) _Context.unregisterReceiver(mVolumeReceiver);

    }


    /**
     * Volume change broadcast
     */
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isReceiveVolumeChange(intent) == true) {
                ((MainActivity)context).checkVolume();
//                int currVolume = _AudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                TextView volLabel =(TextView) ((MainActivity)context).findViewById(R.id.volume_lock_text);
//                ProgressBar vol_pb =(ProgressBar) ((MainActivity)context).findViewById(R.id.volume_progressBar);
//                double vol_pre = (currVolume / 12.0)* 100;
//                vol_pb.setProgress((int)vol_pre);
//                if (currVolume >= 12){
//                    volLabel.setText(R.string.unlock);
//                }else {
//                    volLabel.setText(R.string.lock);
//                }
            }
        }
    }

    /**
     * Determine whether the music volume is changed (volume changed by the volume key)
     *
     * @param intent
     * @return
     */
    private boolean isReceiveVolumeChange(Intent intent) {
        return intent.getAction() != null
                && intent.getAction().equals(ACTION_VOLUME_CHANGED)
                && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC
                ;
    }

}
