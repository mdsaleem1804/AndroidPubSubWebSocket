package com.amazonaws.demo.androidpubsubwebsocket;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SingleRoom extends Activity {

    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a1bdwfs1su6bdu-ats.iot.ap-southeast-1.amazonaws.com";
    private static final String COGNITO_POOL_ID = "ap-southeast-1:87280ba9-2ff1-454e-88ac-1fb47b443d46";
    private static final Regions MY_REGION = Regions.AP_SOUTHEAST_1;
    EditText edtTemperature;
    TextView tvStatus;
    Button btnSubscribe,btnPublish;
    SeekBar seekHumidity;
    public Switch xSwitchPrivacy,xSwitchMakeUpRoom,xSwitchButlerCall,xSwitchOccupancy,xSwitchMotion,xSwitchDoor,xSwitchWindow;
    public Spinner spnFanSpeed;
    AWSIotMqttManager mqttManager;
    String clientId;
    CognitoCachingCredentialsProvider credentialsProvider;
    static final String LOG_TAG = PubSubActivity.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_room);
        edtTemperature = (EditText) findViewById(R.id.edtTemperature);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnPublish = (Button) findViewById(R.id.btnPublish);
        xSwitchPrivacy= (Switch) findViewById(R.id.switchPrivacy);
        xSwitchMakeUpRoom= (Switch) findViewById(R.id.switchMakeUpRoom);
        xSwitchButlerCall= (Switch) findViewById(R.id.switchButlerCall);
        xSwitchOccupancy= (Switch) findViewById(R.id.switchOccupancy);
        xSwitchMotion= (Switch) findViewById(R.id.switchMotion);
        xSwitchDoor= (Switch) findViewById(R.id.switchDoor);
        xSwitchWindow= (Switch) findViewById(R.id.switchWindow);
        seekHumidity= (SeekBar) findViewById(R.id.seekHumidity);
        seekHumidity.setBackgroundColor(Color.GREEN);
        spnFanSpeed= findViewById(R.id.spnFanSpeed);
        final TextView txtRoomId = findViewById(R.id.txtRoom);
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

        ConnectAWS();

        btnSubscribe.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String topic = "Test";
                Toast.makeText(getApplicationContext(),"Subscribe Method Called", Toast.LENGTH_SHORT).show();
                try {
                    mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                            new AWSIotMqttNewMessageCallback() {
                                @Override
                                public void onMessageArrived(final String topic, final byte[] data) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String message = new String(data, "UTF-8");
                                                ReadJsonData(message);
                                                Toast.makeText(getApplicationContext(),"Data Recieved", Toast.LENGTH_SHORT).show();
                                            } catch (UnsupportedEncodingException | JSONException e ) {
                                                Log.e(LOG_TAG, "Message encoding error.", e);
                                            }
                                        }
                                    });
                                }
                            });
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Subscription error.", e);
                }
            }
        });
        btnPublish.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String xRoomData=GenerateJsonData();
                PublishData(xRoomData,"Test");

            }
        });

     seekHumidity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(SingleRoom.this, "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ReadJsonData(String xSubscribedData) throws JSONException {

        JSONArray jArr = new JSONArray(xSubscribedData);

        for (int count = 0; count < jArr.length(); count++) {
            JSONObject obj = jArr.getJSONObject(count);
            String name = obj.getString("Room");
            edtTemperature.setText(obj.getString("Temperature"));
            String xData=obj.getString("Privacy");
            if(obj.getString("Privacy").equals("1"))
              xSwitchPrivacy.setChecked(true);
            if(obj.getString("MakeUpRoom").equals("1"))
                xSwitchMakeUpRoom.setChecked(true);
            if(obj.getString("ButlerCall").equals("1"))
                xSwitchButlerCall.setChecked(true);
            if(obj.getString("Occupancy").equals("1"))
                xSwitchOccupancy.setChecked(true);
            if(obj.getString("Motion").equals("1"))
                xSwitchMotion.setChecked(true);

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

        JSONObject data1 = new JSONObject();
        try {
            data1.put("Room", "1001");//Room Number 1 to 99999 String
            data1.put("Temperature", "10");//16 to 30*C
            data1.put("FanSpeed", spnFanSpeed.getSelectedItem().toString());// 0 Off, 1, low, 2, Med, 3 High, 4 Auto
            data1.put("Humidity", seekHumidity.getProgress());//10 to 99
            data1.put("SetTemperatue", "20");//16 to 30*C
            data1.put("Privacy", boolToInt(xSwitchPrivacy.isChecked()));//0 Off or 1 On
            data1.put("MakeUpRoom", boolToInt(xSwitchMakeUpRoom.isChecked()));//0 Off or 1 On
            data1.put("ButlerCall", boolToInt(xSwitchButlerCall.isChecked()));//0 Off or 1 On
            data1.put("Occupancy", boolToInt(xSwitchOccupancy.isChecked()));//0 vacant or 1 occupied
            data1.put("Motion", boolToInt(xSwitchMotion.isChecked())); //0 no or 1 yes
            data1.put("Door"   ,boolToInt(xSwitchDoor.isChecked())); //0 close or 1 open
            data1.put("Window" ,boolToInt(xSwitchWindow.isChecked())); //0 close or 1 open
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
    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
}