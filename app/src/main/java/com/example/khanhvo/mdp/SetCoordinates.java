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
        np1.setMaxValue(12);
        np1.setMinValue(0);
        final NumberPicker np2 = layoutView.findViewById(R.id.numberPicker2);
        np2.setMaxValue(6);
        np2.setMinValue(0);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //final EditText xInput = (EditText) layoutView.findViewById(R.id.start_coordinate_x);
                //final EditText yInput = (EditText) layoutView.findViewById(R.id.start_coordinate_y);
                //final int x = Integer.parseInt(xInput.getText().toString());
                //final int y = Integer.parseInt(yInput.getText().toString());
                final int x = np1.getValue();
                final int y = np2.getValue();
                Log.d("set coordinates","receive coordinates");
                cBaseApplication.mazeView.setCoordinate(x,y, Direction.NORTH);
                Log.d("set coordinates","finished setting new coordinates");
                cBaseApplication.mBluetoothChat.write(("coordinate ("+(x)+","+(y)+")").getBytes(Charset.defaultCharset()));

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
