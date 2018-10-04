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

import com.example.khanhvo.mdp.enumType.Direction;

import java.nio.charset.Charset;

public class SetCoordinates extends DialogFragment{

    View layoutView;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //public EditText xInput;
        //public EditText yInput;


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        layoutView = inflater.inflate(R.layout.activity_set_coordinates,null);
        builder.setView(layoutView);
        final NumberPicker np1 = layoutView.findViewById(R.id.numberPicker1);
        np1.setMaxValue(14);
        np1.setMinValue(0);
        final NumberPicker np2 = layoutView.findViewById(R.id.numberPicker2);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        final NumberPicker np3 = layoutView.findViewById(R.id.numberPicker3);
        np3.setMaxValue(14);
        np3.setMinValue(0);
        final NumberPicker np4 = layoutView.findViewById(R.id.numberPicker4);
        np4.setMaxValue(19);
        np4.setMinValue(0);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //final EditText xInput = (EditText) layoutView.findViewById(R.id.start_coordinate_x);
                //final EditText yInput = (EditText) layoutView.findViewById(R.id.start_coordinate_y);
                //final int x = Integer.parseInt(xInput.getText().toString());
                //final int y = Integer.parseInt(yInput.getText().toString());
                final int x = np1.getValue();
                final int y = np2.getValue();
                final int xW = np3.getValue();
                final int yW = np4.getValue();
                Log.d("set coordinates","receive coordinates");
                cBaseApplication.mazeView.setCoordinate(x,y, Direction.NORTH);
                cBaseApplication.mazeView.setWaypoint(xW,yW);
                Log.d("set coordinates","finished setting new coordinates");
                //cBaseApplication.mBluetoothChat.write(("Pexs{"+(x)+"},{"+(y)+"}").getBytes(Charset.defaultCharset()));
                cBaseApplication.mBluetoothChat.write(("Pw"+(xW)+","+(yW)).getBytes(Charset.defaultCharset()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //return super.onCreateDialog(savedInstanceState);
        return builder.create();
    }
}
