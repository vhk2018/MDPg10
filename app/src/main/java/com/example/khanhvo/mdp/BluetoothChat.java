package com.example.khanhvo.mdp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khanhvo.mdp.util.ReceiveCommand;

import java.nio.charset.Charset;

public class BluetoothChat extends AppCompatActivity {
    private final String TAG = "Bluetooth Chat: ";
    Button btn_Send;
    TextView tv_Receive;
    EditText et_Send;
    BluetoothDevice mBTDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);
        btn_Send = (Button)findViewById(R.id.btn_Send);
        //btn_Send.setEnabled(false);
        et_Send = (EditText)findViewById(R.id.et_Send);
        tv_Receive = (TextView)findViewById(R.id.tv_Receive);
        //scrollable textview
        tv_Receive.setMovementMethod(new ScrollingMovementMethod());
        //String action = intent.getAction();
        //mBTDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//getIntent().getExtras().getParcelable("btDevice");


        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        btn_Send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String Message = et_Send.getText().toString();
                if (Message.matches("")){
                    Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    byte[] bytes = et_Send.getText().toString().getBytes(Charset.defaultCharset());
                    //mBluetoothChat.write(bytes);
                    ((cBaseApplication)getApplicationContext()).mBluetoothChat.write(bytes);

                    //display my output in text view
                    //String myName = mBluetoothAdapter.getName();
                    String outgoingMessage = ("ME" + ": " + et_Send.getText());
                    Display(outgoingMessage);
                    et_Send.setText("");
                }
            }
        });
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            //cBaseApplication.mazeGrid =text;
            Log.d(TAG, text);
            String incomingMessage = ("YOU" + ": " + text);//(mBTDevice.getName() + ": " + text);
            Display(incomingMessage);

        }
    };

    public void Display(final String s){
        tv_Receive.append(s + "\n");
    }
}
