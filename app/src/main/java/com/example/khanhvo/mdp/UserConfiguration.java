package com.example.khanhvo.mdp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.Charset;

public class UserConfiguration extends AppCompatActivity {

    Button btn_F1, btn_F2, btn_Reconfigure, btn_Save;
    String F1Text, F2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_configuration);

        btn_F1 = (Button) findViewById(R.id.btn_F1);
        btn_F2 = (Button) findViewById(R.id.btn_F2);
        btn_Reconfigure = (Button) findViewById(R.id.btn_Reconfigure);
        btn_Save = (Button) findViewById(R.id.btn_Save);

        btn_F1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("userConfig", Context.MODE_PRIVATE);
                F1Text = sharedPref.getString("btnF1", "");
                Toast.makeText(getApplicationContext(),F1Text, Toast.LENGTH_SHORT).show();
                ((cBaseApplication) getApplicationContext()).mBluetoothChat.write(F1Text.toString().getBytes(Charset.defaultCharset()));
            }
        });

        btn_F2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("userConfig", Context.MODE_PRIVATE);
                F2Text = sharedPref.getString("btnF2", "");
                Toast.makeText(getApplicationContext(),F2Text, Toast.LENGTH_SHORT).show();
                ((cBaseApplication) getApplicationContext()).mBluetoothChat.write(F2Text.toString().getBytes(Charset.defaultCharset()));
            }
        });

        btn_Reconfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserConfiguration.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit, null);
                final EditText et_F1 = (EditText) mView.findViewById(R.id.et_F1);;
                final EditText et_F2= (EditText) mView.findViewById(R.id.et_F2);
                Button mSave = (Button) mView.findViewById(R.id.btn_Save);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                //load data from shared preferences
                SharedPreferences sharedPref = getSharedPreferences("userConfig", Context.MODE_PRIVATE);
                F1Text = sharedPref.getString("btnF1", "");
                F2Text = sharedPref.getString("btnF2", "");

                et_F1.setText(F1Text);
                et_F2.setText(F2Text);

                //set cursor at the end
                et_F1.setSelection(F1Text.length());
                et_F2.setSelection(F2Text.length());

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String etf1Check = et_F1.getText().toString();
                        String etf2Check = et_F2.getText().toString();

                        if(!TextUtils.isEmpty(etf1Check) && !TextUtils.isEmpty(etf2Check)) {
                            SharedPreferences sharedPref = getSharedPreferences("userConfig", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("btnF1", et_F1.getText().toString());
                            editor.putString("btnF2", et_F2.getText().toString());
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(etf1Check)) {
                            et_F1.setError("Please enter an input.");
                            return;
                        }
                        else if (TextUtils.isEmpty(etf2Check)){
                            et_F2.setError("Please enter an input.");
                            return;
                        }
                        else {

                        }
                    }
                });
            }
        });

    }

}
