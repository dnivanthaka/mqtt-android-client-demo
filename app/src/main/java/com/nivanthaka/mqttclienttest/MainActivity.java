package com.nivanthaka.mqttclienttest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

//http://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MQTT_CLIENT";
    //private MqttAndroidClient client;
    private Connection connection;
    private EditText server;
    private EditText port;
    private EditText subTopic;
    private EditText pubTopic;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        server = (EditText)findViewById(R.id.txtServer);
        port   = (EditText)findViewById(R.id.txtPort);
        subTopic  = (EditText)findViewById(R.id.txtSubTopic);
        pubTopic  = (EditText)findViewById(R.id.txtPubTopic);
        message  = (EditText)findViewById(R.id.txtMessage);

        connection = Connection.getInstance();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clientConnection(View view){
        Log.d(TAG, "onClientConnection");
        //tcp://broker.hivemq.com:1883

        String clientId = MqttClient.generateClientId();
        /*client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);*/

        connection.createConnection(clientId, server.getText().toString(), Integer.parseInt(port.getText().toString()), null, this.getApplicationContext(), false);
        //connection.createConnection(clientId, "broker.hivemq.com", 1883, null, this.getApplicationContext(), false);
        //MqttAndroidClient client = connection.getClient();

        MqttConnectOptions options = new MqttConnectOptions();
        //Last will message
        String topic = "users/last/will";
        byte[] payload = "some payload".getBytes();
        options.setWill(topic, payload, 1, false);

        try {
            IMqttToken token = connection.getClient().connect(options);
            connection.setStatus(Connection.ConnectionStatus.CONNECTING);

            ActionListener listener = new ActionListener(connection, ActionListener.Action.CONNECT, this.getApplicationContext());
            token.setActionCallback(listener);

            /*token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(getApplicationContext(), "Successfully Connected", Toast.LENGTH_SHORT).show();

                    mqttSubscribe();

                    asyncActionToken.getClient().setCallback(new MqttCallbackHandler());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Toast.makeText(getApplicationContext(), "Cannot Connect to the Server", Toast.LENGTH_SHORT).show();
                }
            });*/
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void mqttSubscribe(View view){
        //final String topic = "dinu/bar";

        final String topic = subTopic.getText().toString();

        int qos = 1;
        //MqttAndroidClient client;
        //client = connection.getClient();
        //Log.d(TAG, client.toString());
        if(connection.isConnected()) {
            try {
                IMqttToken subToken = connection.getClient().subscribe(topic, qos);
                ActionListener listener = new ActionListener(connection, ActionListener.Action.SUBSCRIBE, this.getApplicationContext());
                subToken.setActionCallback(listener);
            /*subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Toast.makeText(getApplicationContext(), "Successfully Subscribed to Topic " + topic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });*/
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else{
            Log.d(TAG, "Client is still not connected");
        }
    }

    public void mqttUnsubscribe(View view){
        //final String topic = "dinu/bar";

        final String topic = subTopic.getText().toString();

        //MqttAndroidClient client;
        //client = connection.getClient();


        try {

            IMqttToken unsubToken = connection.getClient().unsubscribe(topic);
            ActionListener listener = new ActionListener(connection, ActionListener.Action.UNSUBSCRIBE, this.getApplicationContext());
            unsubToken.setActionCallback(listener);
            /*
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });*/
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(View view){
        //MqttAndroidClient client;
        //client = connection.getClient();

        try {
            IMqttToken disconToken = connection.getClient().disconnect();
            ActionListener listener = new ActionListener(connection, ActionListener.Action.DISCONNECT, this.getApplicationContext());
            disconToken.setActionCallback(listener);
            /*
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });*/
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(View view){
        //String topic = "foo/bar";
        //String payload = "the payload";

        final String topic = pubTopic.getText().toString();
        final String payload = message.getText().toString();

        byte[] encodedPayload = new byte[0];

        //MqttAndroidClient client;
        //client = connection.getClient();

        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            connection.getClient().publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishRetained(View view){
        String topic = "foo/bar";
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];

        //MqttAndroidClient client;
        //client = connection.getClient();

        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            connection.getClient().publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

}
