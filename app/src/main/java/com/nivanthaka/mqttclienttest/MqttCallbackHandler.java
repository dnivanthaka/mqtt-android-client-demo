package com.nivanthaka.mqttclienttest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by dinusha on 3/22/16.
 */
public class MqttCallbackHandler implements MqttCallback {
    private Context context;

    private final String TAG = "MQTT_CLIENT";

    public MqttCallbackHandler(Context ctx){
        context = ctx;
    }

    @Override
    public void connectionLost(Throwable cause){

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, topic +" - "+ new String(message.getPayload()));
        Toast.makeText(context, "Message : "+topic +" - "+ new String(message.getPayload()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token){

    }
}
