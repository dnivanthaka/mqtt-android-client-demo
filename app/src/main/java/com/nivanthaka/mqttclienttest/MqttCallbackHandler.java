package com.nivanthaka.mqttclienttest;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by dinusha on 3/22/16.
 */
public class MqttCallbackHandler implements MqttCallback {

    private final String TAG = "MQTT_CLIENT";

    public MqttCallbackHandler(){

    }

    @Override
    public void connectionLost(Throwable cause){

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, topic +" - "+ new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token){

    }
}
