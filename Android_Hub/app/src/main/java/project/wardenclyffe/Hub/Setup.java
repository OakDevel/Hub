package project.wardenclyffe.Hub;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;



/**
 * TODO: Make Javadoc, Store selected device MAC Address, Enable Next Button when device is selected
 */
public class Setup extends Activity {
    private RecyclerView Paired_Devices;
    private RecycleView_Adapter Paired_Devices_Layout;
    private int row;
    private int row_anterior = -1;

    private String device_address;

    List<RecycleView_Element> data = new ArrayList<>();
    Set<BluetoothDevice> connect;

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

                if(getRow() == getRow_anterior()){
                    for (BluetoothDevice device : connect) {
                        if (i == getRow()) {

                            next_button.setEnabled(false);
                            next_button.setTextColor(getApplication().getResources().getColor(R.color.Text_locked));
                            int next_img = R.drawable.ic_next_disable;
                            next_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, next_img, 0);
                            //DEBUG
                            Toast.makeText(getApplicationContext(), "Row is the same as before", Toast.LENGTH_SHORT).show();
                        }
                        i++;
                    }
                }
                else {
                    for (BluetoothDevice device : connect) {
                        if (i == getRow()) {

                            setAddress(device.getAddress());

                            next_button.setEnabled(true);
                            next_button.setTextColor(getApplication().getResources().getColor(R.color.Text));
                            int next_img = R.drawable.ic_next_enable;
                            next_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, next_img, 0);
                            setRow_anterior(getRow());

                            //DEBUG
                            Toast.makeText(getApplicationContext(), getAddress(), Toast.LENGTH_SHORT).show();
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
                Set<String> MacAddress;

                SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);
                MacAddress = Reader.getStringSet("Device_MAC", null);
                SharedPreferences.Editor Editor = getSharedPreferences("project.wardenclyffe.Hub", 0).edit();

                //Is the first device we setup
                if(MacAddress == null){
                    MacAddress.add(getAddress());
                    Editor.putStringSet("Device_MAC", MacAddress);
                   // Editor.putBoolean("Setup", false);
                    Editor.commit();
                } else {
                    MacAddress.add(getAddress());
                    Editor.putStringSet("Device_MAC", MacAddress);
                    Editor.commit();
                }

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
        Set<String> Address = Reader.getStringSet("Device_MAC", null);

        if (connect.size() > 0) {
            for (BluetoothDevice device : connect) {

                RecycleView_Element current = new RecycleView_Element();
                current.Device_Name = device.getName();

                switch (device.getBluetoothClass().getMajorDeviceClass()){
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
                        current.Device_Type =  R.drawable.ic_device_watch;
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
                        current.Device_Type =  R.drawable.ic_device_unknown;
                        break;

                    //Wearable
                    case 1792:
                        current.Device_Type =  R.drawable.ic_device_watch;
                        break;
                    }
                data.add(current);
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