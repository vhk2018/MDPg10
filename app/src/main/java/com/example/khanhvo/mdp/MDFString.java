package com.example.khanhvo.mdp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.khanhvo.mdp.enumType.Direction;

import org.w3c.dom.Text;

import java.nio.charset.Charset;

public class MDFString extends DialogFragment{

    View layoutView;
    TextView mdf1;
    TextView mdf2;
    TextView arrowString;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //public EditText xInput;
        //public EditText yInput;


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        layoutView = inflater.inflate(R.layout.activity_mdf_display,null);
        builder.setView(layoutView);
        mdf1 = layoutView.findViewById(R.id.mdf1_string);
        mdf2 = layoutView.findViewById(R.id.mdf2_string);
        arrowString = layoutView.findViewById(R.id.arrow_string);
        mdf1.setText(cBaseApplication.mdf1);
        mdf2.setText(cBaseApplication.gridValue);
        arrowString.setText(cBaseApplication.arrowString);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //return super.onCreateDialog(savedInstanceState);
        return builder.create();
    }

    public void setMDF(){
        if (mdf1 != null){
            mdf1.setText(cBaseApplication.mdf1);
        }
        if (mdf2 != null){
            mdf2.setText(cBaseApplication.mazeGrid);
        }

    }
}
