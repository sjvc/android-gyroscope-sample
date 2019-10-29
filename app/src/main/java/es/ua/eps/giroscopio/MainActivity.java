package es.ua.eps.giroscopio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final static float DRAWABLE_SCALE = 6f;

    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;

    private GyroscopeView mGyroscopeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroscopeView = findViewById(R.id.gyroscopeView);

        // Añadimos las capas (drawables) a mostrar
        mGyroscopeView.addLayer(R.drawable.layer03, 1.00f, DRAWABLE_SCALE);
        mGyroscopeView.addLayer(R.drawable.layer02, 0.65f, DRAWABLE_SCALE);
        mGyroscopeView.addLayer(R.drawable.layer01, 0.65f, DRAWABLE_SCALE);
        mGyroscopeView.addLayer(R.drawable.layer00, 0.25f, DRAWABLE_SCALE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGyroscopeSensor != null){
            mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        if (mGyroscopeSensor != null){
            mSensorManager.unregisterListener(this);
        }

        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            mGyroscopeView.onGyroscopeChanged(event.values[1], event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
