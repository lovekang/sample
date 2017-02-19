package com.imooc.lightsensortest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;//系统所有传感器的管理器,有了它的实例后就可以调用getDefaultSensor()方法来得到任意的传感器类型
    private TextView lightLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lightLevel=(TextView)findViewById(R.id.light_level);
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        //三个参数:1.SensorEventListener的实例 2.Sensor的实例 3.用于表示传感器输出信息的更新速率
        //参数三有4个值：SENSOR_DELAY_UI,SENSOR_DELAY_NORMAL,SENSOR_DELAY_GAME,SENSOR_DELAY_FASTEST，更新速率依次递增
        //sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        //简易指南针
        Sensor magneticSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//地磁传感器
        Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        //由于方向传感器的精确度要求通常都比较高，这里我们把传感器输出信息的更新速率提高了一些，使用的是SENSOR_DELAY_GAME
        sensorManager.registerListener(listener,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);

    }
    private SensorEventListener listener=new SensorEventListener() {
        float[] accelerometerValues=new float[3];
        float[]magneticValues=new float[3];
        @Override
        public void onSensorChanged(SensorEvent event) {
            //values数组中第一个下标的值就是当前的光照强度
            //float value=event.values[0];
            //lightLevel.setText("Current light level is"+value+"lx");

            //加速度传感器
            //加速度可能会有负值，所以要取它们的绝对值
            //float xValue=Math.abs(event.values[0]);
            //float yValue=Math.abs(event.values[1]);
            //float zValue=Math.abs(event.values[2]);
            //if(xValue>15||yValue>15||zValue>15){
                //认为用户摇动了手机，触发摇一摇功能
                //Toast.makeText(MainActivity.this,"摇一摇",Toast.LENGTH_SHORT).show();
            //}

            //简易指南针
            //判断当前是加速度传感器还是地磁传感器
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                //赋值的时候要调用clone()方法
                accelerometerValues=event.values.clone();
            }else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                magneticValues=event.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R, null, accelerometerValues,
                    magneticValues);//得到一个包含旋转矩阵的R数据
            SensorManager.getOrientation(R, values);//计算手机旋转数据
            //values[0]的取值范围在-180至180度，其中正负180°表示正南方，0度表示正北方，-90度表示正西方向，90度表示正东方向
            Log.d("MainActivity", "value[0] is " + Math.toDegrees(values[0]));//z轴的旋转弧度,toDegrees()方法将弧度转换为角度

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sensorManager!=null){
            sensorManager.unregisterListener(listener);
        }
    }
}
