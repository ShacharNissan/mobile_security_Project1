package com.ShacharNissan.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ShacharNissan.project1.compess.Compass;
import com.ShacharNissan.project1.compess.SOTWFormatter;
import com.ShacharNissan.project1.volume.VoiceVolumnWrapper;

public class MainActivity extends AppCompatActivity {
    //compass
    private SOTWFormatter sotwFormatter;
    private Compass compass;
    private ImageView arrow_View;
    private TextView comp_label;
    private float current_dir;
    private boolean com_auth = false;

    //volume
    private VoiceVolumnWrapper voiceVolumnWrapper;
    private TextView vol_lock_Label;
    private ProgressBar vol_pb;
    private boolean vol_auth = false;

    //Battery
    private ImageView low_battery;
    private ImageView full_battery;
    private TextView battery_label;
    private boolean battery_auth = false;

    //auth
    private Button auth_button;
    private EditText auth_pass;
    private final String AUTH_PASS = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //compass sensor
        sotwFormatter = new SOTWFormatter(this);
        arrow_View = (ImageView) findViewById(R.id.arrowView);
        comp_label = (TextView) findViewById(R.id.comp_lock_text);
        setupCompass();

        //volume
        vol_lock_Label = (TextView) findViewById(R.id.volume_lock_text);
        vol_pb = (ProgressBar) findViewById(R.id.volume_progressBar);
        voiceVolumnWrapper = new VoiceVolumnWrapper(this);
        voiceVolumnWrapper.registerVolumeReceiver();

        //Battery
        low_battery = (ImageView) findViewById(R.id.low_battery);
        full_battery = (ImageView) findViewById(R.id.full_battery);
        battery_label = (TextView) findViewById(R.id.battery_lock_text);

        //auth
        auth_pass = (EditText) findViewById(R.id.password_text);
        auth_button = (Button) findViewById(R.id.password_button);
        auth_button.setOnClickListener(e ->
        {
            Toast t;
            if (auth_pass.getText().toString().equals(AUTH_PASS))
                t = Toast.makeText(this, R.string.good_pass, Toast.LENGTH_LONG);
            else
                t = Toast.makeText(this, R.string.bad_pass, Toast.LENGTH_SHORT);
            t.show();

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        compass.start();
        voiceVolumnWrapper.registerVolumeReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
        checkPhoneBattery();
        checkVolume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compass.stop();
        voiceVolumnWrapper.unregisterVolumeReceiver();
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustArrow(azimuth);
                        adjust_Label(azimuth);
                    }
                });
            }
        };
    }

    private void adjustArrow(float dir) {
        Animation an = new RotateAnimation(-current_dir, -dir,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        current_dir = dir;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrow_View.startAnimation(an);
    }

    private void adjust_Label(float azimuth) {

        if (azimuth > 50 && azimuth < 115) {
            setUnlock(comp_label);
            com_auth = true;
        } else {
            setLock(comp_label);
            com_auth = false;
        }
        checkAuth();
    }

    private int getBatteryPercentage(){
        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void checkPhoneBattery() {
        if (getBatteryPercentage() > 60){
            full_battery.setVisibility(View.VISIBLE);
            low_battery.setVisibility(View.INVISIBLE);
            setUnlock(battery_label);
            battery_auth = true;
        }else{
            full_battery.setVisibility(View.INVISIBLE);
            low_battery.setVisibility(View.VISIBLE);
            setLock(battery_label);
            battery_auth = false;
        }
        checkAuth();
    }

    public void checkVolume() {
        int currVolume = voiceVolumnWrapper.GetMusicVoiceCurrentValue();
        double vol_pre = (currVolume / 12.0)* 100;
        vol_pb.setProgress((int)vol_pre);
        if (currVolume >= 12){
            vol_auth = true;
            setUnlock(vol_lock_Label);
        }else {
            vol_auth = false;
            setLock(vol_lock_Label);
        }
        checkAuth();
    }

    private void setUnlock(TextView view){
        view.setText(R.string.unlock);
        view.setTextSize(16);
        view.setTextColor(Color.GREEN);
    }

    private void setLock(TextView view){
        view.setText(R.string.lock);
        view.setTextSize(16);
        view.setTextColor(Color.RED);
    }

    public void checkAuth(){
        if(vol_auth && battery_auth && com_auth)
            auth_button.setEnabled(true);
        else
            auth_button.setEnabled(false);
    }
}