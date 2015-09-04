package project.wardenclyffe.Hub;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by André on 06/08/15.
 * Class to handle connection between android and arduino
 */
public class Device {


    private String Address;
    private String Type;

    private BluetoothDevice Device;
    BluetoothSocket mmSocket = null;

    // Status for Handler
    static final int RECIEVE_MESSAGE = 1;

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private StringBuilder stringBuilder = new StringBuilder();

    ConnectedThread mConnectedThread;

    public Device(BluetoothDevice Device) {
        this.Device = Device;
        this.Address = Device.getAddress();
        setType("Nope");

        ConnectThread mConnectThread = new ConnectThread();
        mConnectThread.start();
    }

    /*
    * Returns device MAC address
    */
    public String getAddress(){
        return this.Address;
    }

    /*
    * Returns device
    */
    public BluetoothDevice getDevice(){
        return this.Device;
    }

    /*
    * Sets device type
    */
    public void setType(String Type){
        this.Type = Type;
    }

    /*
    * Returns device type
    */
    public String getType(){
        return this.Type;
    }



    public class ConnectThread extends Thread{
        public ConnectThread(){
            try {
                mmSocket = createBluetoothSocket(Device);
            } catch (IOException e) {
            }

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                }
            }

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }
    }

    /**
     * Handles our connection between the 2 devices
     */
    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();        // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d("Device_Writing", "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d("Device_Writing", "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
            }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RECIEVE_MESSAGE:                                                     // if receive massage
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);                   // create string from bytes array
                    stringBuilder.append(strIncom);                                       // append string
                    int endOfLineIndex = stringBuilder.indexOf("#");                      // determine the end-of-line
                    if (endOfLineIndex > 0) {                                             // if end-of-line,
                        String sbprint = stringBuilder.substring(0, endOfLineIndex);      // extract string
                        stringBuilder.delete(0, stringBuilder.length());                  // and clear
                        setType(sbprint);
                    }
                    break;
            }
        };
    };

}