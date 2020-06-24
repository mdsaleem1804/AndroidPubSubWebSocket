package com.amazonaws.demo.androidpubsubwebsocket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SingleRoom extends Activity {

    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a1bdwfs1su6bdu-ats.iot.ap-southeast-1.amazonaws.com";
    private static final String COGNITO_POOL_ID = "ap-southeast-1:87280ba9-2ff1-454e-88ac-1fb47b443d46";
    private static final Regions MY_REGION = Regions.AP_SOUTHEAST_1;
    EditText txtSubscribe;
    TextView tvStatus;
    Button btnSaveRoom;
    public Switch switchPrivacy;
    public Spinner spnFanSpeed;
    AWSIotMqttManager mqttManager;
    String clientId;
    CognitoCachingCredentialsProvider credentialsProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_room);
        txtSubscribe = (EditText) findViewById(R.id.edtLightStatus);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        btnSaveRoom = (Button) findViewById(R.id.btnSaveRoom);
        switchPrivacy= (Switch) findViewById(R.id.switchPrivacy);
        spnFanSpeed= findViewById(R.id.spnFanSpeed);
        final TextView txtRoomId = findViewById(R.id.txtRoomId);
        List<String> xFanList = new ArrayList<String>();
        xFanList.add("0");
        xFanList.add("1");
        xFanList.add("2");
        xFanList.add("3");
        xFanList.add("4");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, xFanList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFanSpeed.setAdapter(dataAdapter);

        clientId = UUID.randomUUID().toString();

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
        // The following block uses a Cognito credentials provider for authentication with AWS IoT.
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Conneced-Main", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
        btnSaveRoom.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String xRoomData=GenerateJsonData();
                PublishData(xRoomData,"Test");

            }
        });

        ConnectAWS();

        GetAWSSubscribe("Test");
    }

    private void GetAWSSubscribe(String  xTopic){
        try {
            mqttManager.subscribeToTopic(xTopic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException e) {
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {

        }
    }
    private void ConnectAWS(){
        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status, final Throwable throwable) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                tvStatus.setText("Connecting...");

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                tvStatus.setText("Connected");

                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                }
                                tvStatus.setText("Reconnecting");
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                tvStatus.setText("Disconnected");
                            } else {
                                tvStatus.setText("Disconnected");

                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
        }
    }
    private void PublishData(String xMessage,String xTopic){
        try {
            mqttManager.publishString(xMessage, xTopic, AWSIotMqttQos.QOS0);
            Toast.makeText(getApplicationContext(),"Published", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }

    }
    private String  GenerateJsonData(){
        String  xSwitchPrivacy;
        if (switchPrivacy.isChecked())
            xSwitchPrivacy = switchPrivacy.getTextOn().toString();
        else
            xSwitchPrivacy = switchPrivacy.getTextOff().toString();

        JSONObject data1 = new JSONObject();
        try {
            data1.put("Room", "1001");//Room Number 1 to 99999 String
            data1.put("Temperature", "10");//16 to 30*C
            data1.put("FanSpeed", spnFanSpeed.getSelectedItem().toString());// 0 Off, 1, low, 2, Med, 3 High, 4 Auto
            data1.put("Humidity", "68");//10 to 99
            data1.put("SetTemperatue", "20");//16 to 30*C
            data1.put("Privacy", xSwitchPrivacy);//0 Off or 1 On
            data1.put("makeUpRoom", xSwitchPrivacy);//0 Off or 1 On
            data1.put("butlerCall", xSwitchPrivacy);//0 Off or 1 On
            data1.put("Occupancy", xSwitchPrivacy);//0 vacant or 1 occupied
            data1.put("Motion", xSwitchPrivacy); //0 no or 1 yes
            data1.put("Door"   ,"0"); //0 close or 1 open
            data1.put("Window" ,"0"); //0 close or 1 open
            data1.put("Master" ,"0"); //0 off or 1 on
            data1.put("Switch1","0");
            data1.put("Switch2","0");
            data1.put("Switch3","0");
            data1.put("Switch4","0");
            data1.put("Switch5","0");
            data1.put("Switch6","0");
            data1.put("Switch7","0");
            data1.put("Switch8","0");
            data1.put("Curtain","0");
            data1.put("Blind"  ,"0");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(data1);
        String xData=jsonArray.toString();
        return xData;
        //JSONObject studentsObj = new JSONObject();
        //studentsObj.put("Students", jsonArray);

    }
}