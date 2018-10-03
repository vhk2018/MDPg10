package com.example.khanhvo.mdp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.khanhvo.mdp.enumType.BluetoothState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothChatService {
    //Debugging
    private static final String TAG = "BluetoothChatService";

    private static final String appName = "MDP10";

    //Unique UUID for this application
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
            //UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private BluetoothState mState;
    private static LocalBroadcastManager mBroadcaster = null;

    private ConnectedThread mConnectedThread;
    //public ConnectedThread mConnectedThread;

    public BluetoothChatService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = BluetoothState.NONE;
        start();
    }

    //This thread runs while listening for incoming connections,
    //It behaves like a server-side client.
    //It runs until a connection is accepted (or until cancelled)
    private class AcceptThread extends Thread {

        //The Local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            //Create a new listening server socket
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }
            catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running");

            BluetoothSocket socket = null;

            try{
                //This is a blocking call and will only return on a
                //Successful connection or an exception
                Log.d(TAG, "run: RFCOM server server socket start...");

                socket = mmServerSocket.accept();

                Log.d(TAG, "RUN: RFCOM server socket accepted connection.");
            }
            catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            //talk about it later
            if (socket != null){
                connected(socket, socket.getRemoteDevice());//mmDevice);
            }
            Log.i(TAG, "END mAcceptThread.");
        }

        public void cancel(){
            Log.d(TAG, "cancel: Cancelling AcceptThread");
            try{
                mmServerSocket.close();
            }
            catch (IOException e){
                Log.e(TAG, "cancel: Closing of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

    //This thread runs while attempting to make an outgoing connection
    //with a device. It runs straight through; the connection either
    //succeeds or fails.
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.d(TAG, "Run mConnectThread.");

            //Get a BluetoothSocket for a connection with the
            //given BluetoothDevice
            try{
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " +
                        MY_UUID_INSECURE);
                //tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            }
            catch (Exception e){
                Log.d(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            //Always cancel discovery because it will slow down a connection.
            mBluetoothAdapter.cancelDiscovery();

            //Make a connection to the BluetoothSocket

            try{
                //This is a blocking call and will only return on a
                //unsuccessful connection or an exception
                if (mmSocket != null){
                    mmSocket.connect();
                    connected(mmSocket, mmDevice);
                } else {
                    Log.d(TAG,"FAIL");
                }
            }
            catch (IOException e){
                //Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                }
                catch (IOException e1){
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel(){
            try{
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            }
            catch (IOException e){
                Log.e(TAG, "cancel: Close() of mSocket in ConnectThread failed. " + e.getMessage());
            }
        }
    }

    //Start the chat service. Specifically start AcceptThread to begin a
    //session in listening (server) mode. Called by the Activity onResume()
    public synchronized void start(){
        Log.d(TAG, "start");

        //Cancel any thread attempting to make a connection
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    //AcceptThread starts and sits waiting for a connection.
    //Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started.");

        //initprogress dialog
        mProgressDialog = mProgressDialog.show(mContext, "Connecting Bluetooth",
                "Please Wait", true);
        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //dismiss the progressdialog when connection is established
            try{
                mProgressDialog.dismiss();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            try{
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            mmInStream= tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){

            byte[] buffer = new byte[1024]; //buffer store for the stream

            int bytes; //bytes returned from read()

            //keep listening to the InputStream until an exception occurs
            while (true){
                //Read from the InputStream
                try{
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    cBaseApplication.incomingMessage = incomingMessage;
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage", incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                }
                catch (IOException e){
                    Log.e(TAG, "write: Error reading input stream. " + e.getMessage());
                    connectionLost();
                    BluetoothChatService.this.listen();
                    break;
                }
            }
        }

        //Call this from main activity to send data to the remote device
        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to output stream: " + text);
            try {
                mmOutStream.write(bytes);
            }
            catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        //Call this from the main activity to shutdown the connection.
        public void cancel(){
            try {
                mmSocket.close();
            }
            catch (IOException e){
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice){
        Log.d(TAG, "connected: Starting...");

        //Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    //Write to the ConnectedThread in an unsychronized manner
    //@param out The bytes to write
    //@see conncetedThread#write(byte[])
    public void write(byte[] out){
        //Create temporary object
        ConnectedThread r;

        //Sychronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
    }

    private void connectionLost()
    {
        // Send a failure message back to the Activity
        /*Message msg = mHandler.obtainMessage(Constant.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/
        setConnectedDevice(null);
        Intent btintent = new Intent("message_toast");
        btintent.putExtra("extra_string","Device connection was lost");
        mBroadcaster = LocalBroadcastManager.getInstance(mContext);
        mBroadcaster.sendBroadcast(btintent);

        mState = BluetoothState.NONE;

        // Start the service over to restart listening mode
        BluetoothChatService.this.listen();
    }

    public synchronized void listen()
    {
        if(mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        //setState(BluetoothState.LISTENING);

        /*if(mSecureAcceptThread == null)
        {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }*/

        if (mInsecureAcceptThread == null)
        {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void setConnectedDevice(BluetoothDevice device)
    {
        mmDevice = device;
    }
}
