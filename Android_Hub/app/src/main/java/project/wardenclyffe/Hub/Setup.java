package project.wardenclyffe.Hub;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO: Make Javadoc
 */
public class Setup extends Activity {
    private RecyclerView Paired_Devices;
    private RecycleView_Adapter Paired_Devices_Layout;
    private int row;
    private int row_anterior = -1;

    private String device_address;

    List<RecycleView_Element> data = new ArrayList<>();

    //To list every device that we have paired
    Set<BluetoothDevice> connect;

    //To list every device that we haven't chosen, but are paired
    Set<BluetoothDevice> Devices_2Use = new HashSet<>();

    private BluetoothAdapter Bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        final Button next_button = (Button) findViewById(R.id.next_button);

        //Initialize our RecycleView
        Paired_Devices = (RecyclerView) findViewById(R.id.RecycleView);
        Paired_Devices.setHasFixedSize(true);

        //Setup our layout
        Paired_Devices_Layout = new RecycleView_Adapter(getApplicationContext(), data);
        Paired_Devices.setAdapter(Paired_Devices_Layout);
        Paired_Devices.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Add touch listener to every element.
        Paired_Devices.addOnItemTouchListener(new RecycleTouchListener(getApplicationContext(), Paired_Devices, new ClickListener() {
            @Override
            public void OnClick(View v, int position) {
                int i = 0;

                Log.i("MACADDRESS_TABLEVIEW", String.valueOf(Devices_2Use));

                if(getRow() == getRow_anterior() && next_button.isEnabled()){
                    for (BluetoothDevice device : Devices_2Use) {
                        if (i == getRow()) {
                            setAddress(device.getAddress());

                            next_button.setEnabled(false);
                            next_button.setTextColor(getApplication().getResources().getColor(R.color.Text_locked));
                            int next_img = R.drawable.ic_next_disable;
                            next_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, next_img, 0);
                        }
                        i++;
                    }
                }
                else {
                    for (BluetoothDevice device : Devices_2Use) {

                         if (i == getRow() && !next_button.isEnabled()) {
                             setAddress(device.getAddress());

                             next_button.setEnabled(true);
                             next_button.setTextColor(getApplication().getResources().getColor(R.color.Text));
                             int next_img = R.drawable.ic_next_enable;
                             next_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, next_img, 0);
                             setRow_anterior(getRow());
                        }
                        i++;
                    }
                }
            }

            @Override
            public void OnLongClick(View v, int position) {
               //We dont do anything.We dont need the long click
            }
        }));


        next_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Set<String> MacAddress = null;
                Set<String> New_Mac_Address = new HashSet<String>();

                SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);
                SharedPreferences.Editor Editor = getSharedPreferences("project.wardenclyffe.Hub", 0).edit();

                try {
                    MacAddress = Reader.getStringSet("Device_MAC", null);
                }catch (Exception e){

                }
                if(MacAddress != null) {
                    for (String aux : MacAddress) {
                        New_Mac_Address.add(aux);
                    }
                }
                New_Mac_Address.add(getAddress());

                Log.i("MACADDRESS_2SAVE", String.valueOf(New_Mac_Address));

                Editor.putStringSet("Device_MAC", New_Mac_Address);
                Editor.apply();

                //We have finished the setup
                Editor.putBoolean("Setup", false);

                Editor.commit();

                Intent show_Hub = new Intent(getApplicationContext(), Hub.class);
                startActivity(show_Hub);
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();

        //Check bluetooth state
        CheckBluetoothStatus();

        //"House" cleaning
        data.clear();

        //Prepare our device
        Bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Get our paired devices list and add it to the Paired_Device list
        connect = Bluetooth.getBondedDevices();

        SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);

        Set<String> Address = null;
        try {
            Address = Reader.getStringSet("Device_MAC", null);
        } catch (NullPointerException e) {

        }

        if (Address != null) {
            if (Address.size() < connect.size()) {
                setupRecycleView();
            } else {
                displayMessage();
            }
        }else{
            if(connect.size() > 0){
                setupRecycleView();
            }
            else{
                displayMessage();
            }
        }
    }
    /**
     * Check if device support bluetooth.
     * If so, check is it's On or Off.
     * If Off, ask user to turn it On.
     */
    private void CheckBluetoothStatus() {
        Bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Check if device has bluetooth
        if (Bluetooth == null) {
            Toast.makeText(getBaseContext(), "Device doesn't have bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!Bluetooth.isEnabled()) {
                //Ask user to turn on bluetooth
                Intent enable_Bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enable_Bluetooth, 1);
            }
        }
    }

    /**
     * Check if device has been setup before
     */
    private boolean hasBeenConnected(String MAC){
        SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);
        Set<String> MacAddress = Reader.getStringSet("Device_MAC", null);

        try{
            for(String adr : MacAddress){
                if(MAC.equals(adr)){
                    return true;
                }
            }

        }catch (NullPointerException e){

        }
        return false;
    }

    public void setupRecycleView(){
        for (BluetoothDevice device : connect) {

            if (hasBeenConnected(device.getAddress())) {

            } else {
                Devices_2Use.add(device);

                RecycleView_Element current = new RecycleView_Element();
                current.Device_Name = device.getName();

                switch (device.getBluetoothClass().getMajorDeviceClass()) {
                    //Audio_Video
                    case 1024:
                        current.Device_Type = R.drawable.ic_device_speaker;
                        break;

                    //Computer
                    case 256:
                        current.Device_Type = R.drawable.ic_device_desktop;
                        break;

                    //Health
                    case 2304:
                        current.Device_Type = R.drawable.ic_device_watch;
                        break;

                    //Imaging
                    case 1536:
                        current.Device_Type = R.drawable.ic_device_image;
                        break;

                    //Misc
                    case 0:
                        current.Device_Type = R.drawable.ic_device_hub;
                        break;

                    //Networking
                    case 768:
                        current.Device_Type = R.drawable.ic_device_router;
                        break;

                    //Peripheral
                    case 1280:
                        current.Device_Type = R.drawable.ic_device_phonelink;
                        break;

                    //Phone
                    case 512:
                        current.Device_Type = R.drawable.ic_device_phone;
                        break;

                    //Toy
                    case 2048:
                        current.Device_Type = R.drawable.ic_device_toys;
                        break;

                    //Uncategorized
                    case 7936:
                        current.Device_Type = R.drawable.ic_device_unknown;
                        break;

                    //Wearable
                    case 1792:
                        current.Device_Type = R.drawable.ic_device_watch;
                        break;
                }
                data.add(current);
            }
        }
    }

    public void displayMessage(){
        Log.i("NO_DEVICES_PAIRED", "No devices paired");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("No devices paired");

        // set dialog message
        alertDialogBuilder.setMessage("Please pair a new device, from the Settings app.");

        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Go!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                startActivity(launchIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    /**
     * Setter for row anterior
     */
    public void setRow_anterior(int row){
        this.row_anterior = row;
    }

    /**
     * Getter for row anterior
     */
    public int getRow_anterior(){
        return row_anterior;
    }

    /**
     * Setter for row
     */
    public void setRow(int row){
        this.row = row;
    }

    /**
     * Getter for row
     */
    public int getRow(){
        return row;
    }

    /**
     * Setter for device MAC Address
     */
    public void setAddress(String address){
        this.device_address = address;
    }

    /**
     * Getter for device MAC Address
     */
    public String getAddress(){
        return device_address;
    }

    class RecycleTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener cl;
        public RecycleTouchListener(Context context, RecyclerView rv, final ClickListener cl){
            this.cl = cl;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;

                }

                @Override
                public void onLongPress(MotionEvent e){
                   View child = Paired_Devices.findChildViewUnder(e.getX(), e.getY());

                    if(child != null && cl !=null){
                        setRow(Paired_Devices.getChildAdapterPosition(child));
                        cl.OnLongClick(child, Paired_Devices.getChildAdapterPosition(child));

                    }
                }
            });

        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child != null && cl !=null && gestureDetector.onTouchEvent(e)){
                setRow(Paired_Devices.getChildAdapterPosition(child));
                cl.OnClick(child, Paired_Devices.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{
        public void OnClick(View v, int position);
        public void OnLongClick(View v, int position);
    }
}