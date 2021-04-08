package com.ShacharNissan.project1;

import androidx.appcompat.app.AppCompatActivity;

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
    private ImageView arrowView;
    private TextView sotwLabel;
    private SOTWFormatter sotwFormatter;
    private Compass compass;
    private float currentAzimuth;
    private boolean com_auth = false;

    //volume
    private VoiceVolumnWrapper VoiceVolumnWrapper;
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
        arrowView = (ImageView) findViewById(R.id.arrowView);
        sotwLabel = (TextView) findViewById(R.id.comp_lock_text);
        setupCompass();

        //volume
        vol_lock_Label = (TextView) findViewById(R.id.volume_lock_text);
        vol_pb = (ProgressBar) findViewById(R.id.volume_progressBar);
        VoiceVolumnWrapper = new VoiceVolumnWrapper(this);
        VoiceVolumnWrapper.registerVolumeReceiver();

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
        VoiceVolumnWrapper.registerVolumeReceiver();
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
        VoiceVolumnWrapper.unregisterVolumeReceiver();
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
                        adjustSotwLabel(azimuth);
                    }
                });
            }
        };
    }

    private void adjustArrow(float azimuth) {
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    private void adjustSotwLabel(float azimuth) {

        if (azimuth > 50 && azimuth < 115) {
            sotwLabel.setText(R.string.unlock);
            com_auth = true;
        } else {
            sotwLabel.setText(R.string.lock);
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
            battery_label.setText(R.string.unlock);
            battery_auth = true;
        }else{
            full_battery.setVisibility(View.INVISIBLE);
            low_battery.setVisibility(View.VISIBLE);
            battery_label.setText(R.string.lock);
            battery_auth = false;
        }
        checkAuth();
    }

    public void checkVolume() {
        int currVolume = VoiceVolumnWrapper.GetMusicVoiceCurrentValue();
        double vol_pre = (currVolume / 12.0)* 100;
        vol_pb.setProgress((int)vol_pre);
        if (currVolume >= 12){
            vol_auth = true;
            vol_lock_Label.setText(R.string.unlock);
        }else {
            vol_auth = false;
            vol_lock_Label.setText(R.string.lock);
        }
        checkAuth();
    }

    public void checkAuth(){
        if(vol_auth && battery_auth && com_auth)
            auth_button.setEnabled(true);
        else
            auth_button.setEnabled(false);
    }
}