package com.example.khanhvo.mdp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.khanhvo.mdp.enumType.BluetoothState;
import com.example.khanhvo.mdp.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;



public class BluetoothService {
    private static final String TAG = "MDP";
    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mSecureAcceptThread = null, mInsecureAcceptThread = null;
    private ConnectedThread mConnectedThread = null;
    private ConnectThread mConnectThread = null;
    private Handler mHandler;
    private BluetoothState mState;
    private BluetoothDevice mConnectedDevice = null;
    private BluetoothDevice mLastConnectedDevice = null;
    private static BluetoothService instance;
    private static LocalBroadcastManager mBroadcaster = null;
    private Context mContext;
    private static ArrayList<LocalBroadcastManager> mBroadcasters = null;
    int a = 0;
    private boolean mReconnect;
    private static Timer timer = null;

    private BluetoothService(Context context)
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = BluetoothState.NONE;
        mContext = context;
    }

    public static BluetoothService getInstance(Context context)
    {
        if(instance == null)
            instance = new BluetoothService(context);
        //register(context);
        return instance;
    }

    private static void register(Context context)
    {
        mBroadcasters.add(LocalBroadcastManager.getInstance(context));
    }

    private void broadcast(Intent intent)
    {
        for(int i = 0; i < mBroadcasters.size(); i++)
            mBroadcasters.get(i).sendBroadcast(intent);
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(BluetoothState state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        //mHandler.obtainMessage(SendReceiveUI.Constant.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        Intent btintent = new Intent(Constant.MESSAGE_STATE_CHANGE);
        btintent.putExtra(Constant.EXTRA_STRING, state.getCode());
        mBroadcaster = LocalBroadcastManager.getInstance(mContext);
        mBroadcaster.sendBroadcast(btintent);
    }

    /**
     * Return the current connection state.
     */
    public synchronized BluetoothState getState() {
        return mState;
    }

    /**
     * Set the name of the connected device
     *
     * @param device Currently connected device
     */
    public void setConnectedDevice(BluetoothDevice device)
    {
        mConnectedDevice = device;
    }

    /**
     * @return Return the connected device
     */
    public BluetoothDevice getConnectedDevice()
    {
        return mConnectedDevice;
    }

    public synchronized void setLastConnectedDevice(BluetoothDevice device)
    {
        mLastConnectedDevice = device;
    }

    public synchronized boolean getReconnectionState()
    {
        return mReconnect;
    }

    public synchronized void setReconnectionState(boolean reconnect)
    {
        mReconnect = reconnect;
    }

    public synchronized BluetoothDevice getLastConnectedDevice()
    {
        return mLastConnectedDevice;
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
        Intent btintent = new Intent(Constant.MESSAGE_TOAST);
        btintent.putExtra(Constant.EXTRA_STRING,"Device connection was lost");
        mBroadcaster = LocalBroadcastManager.getInstance(mContext);
        mBroadcaster.sendBroadcast(btintent);

        mState = BluetoothState.NONE;

        // Start the service over to restart listening mode
        BluetoothService.this.listen();
    }

    private void connectionFailed()
    {
        Intent btintent = new Intent(Constant.MESSAGE_TOAST);
        btintent.putExtra(Constant.EXTRA_STRING, "Unable to connect device");
        mBroadcaster = LocalBroadcastManager.getInstance(mContext);
        mBroadcaster.sendBroadcast(btintent);

        mState = BluetoothState.NONE;

        // Start the service over to restart listening mode
        BluetoothService.this.listen();
    }

    public void setHandler(Handler handler)
    {
        mHandler = handler;
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
        setState(BluetoothState.LISTENING);

        if(mSecureAcceptThread == null)
        {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }

        if (mInsecureAcceptThread == null)
        {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    public synchronized void stop()
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

        if(mSecureAcceptThread != null)
        {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null)
        {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        setState(BluetoothState.NONE);
    }

    public void reconnect()
    {
        //mReconnect = true;
        if (Constant.LOG) {
            Log.d(TAG, "Called reconnect");
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Constant.LOG) {
                    Log.d(TAG, "Attempting reconnection to RPI");
                }
                if(mState == BluetoothState.LISTENING || mState == BluetoothState.NONE)
                    connect(getLastConnectedDevice(), false);
            }
        }, 0, 1000);
    }

    public synchronized void connect(BluetoothDevice device, boolean secure)
    {
        if(mState == BluetoothState.CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(BluetoothState.CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
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

        if(mSecureAcceptThread != null)
        {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null)
        {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        /*Message msg = mHandler.obtainMessage(Constant.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/
        setConnectedDevice(device);
        setLastConnectedDevice(getConnectedDevice());
        if (timer != null) {
            timer.cancel();
        }
        Intent btintent = new Intent(Constant.MESSAGE_DEVICE_NAME);
        btintent.putExtra(Constant.EXTRA_STRING, device.getName());
        mBroadcaster = LocalBroadcastManager.getInstance(mContext);
        mBroadcaster.sendBroadcast(btintent);

        setState(BluetoothState.CONNECTED);
        mReconnect = false;
    }


    public void write(byte[] out)
    {
        ConnectedThread ct;
        synchronized(this)
        {
            if(mState != BluetoothState.CONNECTED)
                return;
            ct = mConnectedThread;
        }
        ct.write(out);
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(boolean secure) {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                if(secure)
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(Constant.NAME_SECURE, Constant.MY_UUID_SECURE);
                else
                    tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(Constant.NAME_INSECURE, Constant.MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e("", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (mState != BluetoothState.CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    //manageMyConnectedSocket(socket);
                    synchronized (this)
                    {
                        switch(mState)
                        {
                            case LISTENING:
                            case CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                            case NONE:
                            case CONNECTED:
                                try {
                                    mmServerSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("", "Could not close the connect socket", e);
            }
        }
    }

    public class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = mBluetoothAdapter.getRemoteDevice(Constant.RPI_MAC_ADDR);
            mSocketType = secure ? "Secure" : "Insecure";

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                if(secure)
                    tmp = mmDevice.createRfcommSocketToServiceRecord(Constant.MY_UUID_SECURE);
                else
                    tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(Constant.MY_UUID_INSECURE);
            } catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                connectionFailed();
                return;
            }

            synchronized (this)
            {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            //manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()
            Log.d("bernard", String.valueOf(mState));
            // Keep listening to the InputStream until an exception occurs.
            while(true) {
                if(mState == BluetoothState.CONNECTED)
                {
                    try {
                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);
                        // Send the obtained bytes to the UI activity.
                        /*Message readMsg = mHandler.obtainMessage(
                                Constant.MESSAGE_READ, numBytes, -1,
                                mmBuffer);
                        readMsg.sendToTarget();*/
                        Intent btintent = null;
                        String readMessage = new String(mmBuffer, 0, numBytes);
                        mBroadcaster = LocalBroadcastManager.getInstance(mContext);
                        if(readMessage.toLowerCase().contains("{\"status\":\""))
                        {
                            btintent = new Intent(Constant.MESSAGE_STATUS);
                            btintent.putExtra(Constant.EXTRA_STRING, readMessage.replaceAll("(\\{\"status\":\")|(\"\\})", ""));
                            mBroadcaster.sendBroadcast(btintent);
                        }
                        btintent = new Intent(Constant.MESSAGE_READ);
                        btintent.putExtra(Constant.EXTRA_STRING, readMessage);
                        //btintent.putExtra(Constant.EXTRA_BUFFER, mmBuffer);
                        //btintent.putExtra(Constant.EXTRA_NUMBYTES, numBytes);
                        mBroadcaster.sendBroadcast(btintent);
                    } catch (IOException e) {
                        Log.d("bernard", "Input stream was disconnected", e);
                        connectionLost();
                        BluetoothService.this.listen();
                        break;
                    }
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                /*Message writtenMsg = mHandler.obtainMessage(
                        Constant.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();*/
                Intent btintent = new Intent(Constant.MESSAGE_WRITE);
                btintent.putExtra(Constant.EXTRA_BUFFER, mmBuffer);
                mBroadcaster = LocalBroadcastManager.getInstance(mContext);
                mBroadcaster.sendBroadcast(btintent);
            } catch (IOException e) {
                Log.e("", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                /*Message writeErrorMsg =
                        mHandler.obtainMessage(Constant.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);*/
                Intent btintent = new Intent(Constant.MESSAGE_TOAST);
                btintent.putExtra(Constant.EXTRA_STRING,"Couldn't send data to the other device");
                mBroadcaster = LocalBroadcastManager.getInstance(mContext);
                mBroadcaster.sendBroadcast(btintent);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("", "Could not close the connect socket", e);
            }
        }
    }

    public void resetConnection()
    {
        if(mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    public static void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
