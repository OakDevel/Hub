package project.wardenclyffe.Hub;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerFragmentItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Hub extends AppCompatActivity {

    //Member Fields
    private BluetoothAdapter Bluetooth = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    String Lamp = null;

    // UUID service - This is the type of Bluetooth device that the BT module is
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    String[] Categories = {"All", "Computers", "Fans", "Lights", "Speakers", "TVs"};
    int[] Categories_icons = {R.drawable.ic_devices, R.drawable.ic_device_desktop,
                              R.drawable.ic_device_toys, R.drawable.ic_device_light,
                              R.drawable.ic_device_speaker, R.drawable.ic_device_tv};


    private Toolbar toolbar;

    private final int MaxWidth = 460;

    private final int MaxElevation = 10;

    private DrawerView drawer;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        SharedPreferences Reader = this.getSharedPreferences("project.wardenclyffe.Hub", 0);

        Set<String> MacAddress = Reader.getStringSet("Device_MAC", null);

        CheckBluetoothStatus();

        //INITIALIZE MAC ADDRESS
        int i = 0;
        for(String s : MacAddress){
            if(i == 0){
                Lamp = s;
            }
            i++;
        }


        final Button butao = (Button) findViewById(R.id.TURN_ON);


        butao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(butao.getText().equals("Turn on")){
                    butao.setText("Turn off");
                    sendData("01:255:255:255");

                }else{
                    butao.setText("Turn on");
                    sendData("00:255:255:255");

                }
            }
        });


        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = (DrawerView) findViewById(R.id.drawer);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.closeDrawer(drawer);
        drawer.setDrawerMaxWidth(MaxWidth);
        drawer.setElevation(MaxElevation);


        drawer.addProfile(new DrawerProfile()
                        .setId(1)
                        .setBackground(getResources().getDrawable(R.drawable.drawer_wide, null))
        );

        populateDrawer();

        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectItem(position);
                toolbar.setTitle(drawer.getItem(position).getTextPrimary());
                drawerLayout.closeDrawer(drawer);

            }
        });

        //Be default we highlight "All" Category
        drawer.selectItem(0);

    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up a pointer to the remote device using its address.
        BluetoothDevice device = Bluetooth.getRemoteDevice(Lamp);

        //Attempt to create a bluetooth socket for comms
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e1) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create Bluetooth socket", Toast.LENGTH_SHORT).show();
        }

        // Establish the connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();        //If IO exception occurs attempt to close socket
            } catch (IOException e2) {
                Toast.makeText(getBaseContext(), "ERROR - Could not close Bluetooth socket", Toast.LENGTH_SHORT).show();
            }
        }

        // Create a data stream so we can talk to the device
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create bluetooth outstream", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Pausing can be the end of an app if the device kills it or the user doesn't open it again
        //close all connections so resources are not wasted

        //Close BT socket to device
        try     {
            btSocket.close();
        } catch (IOException e2) {
            Toast.makeText(getBaseContext(), "ERROR - Failed to close Bluetooth socket", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hub, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_pair:
                Pairing();
                return true;

            case R.id.action_about:
                AboutMe();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateDrawer() {
        int i = 0;

        for (String categorie : Categories) {

            int next_img = R.drawable.ic_device_desktop;
            drawer.addItem(
                    new DrawerFragmentItem()
                            .setFragment(new Fragment())
                            .setImage(getApplication().getResources().getDrawable(Categories_icons[i], null))
                            .setTextPrimary(categorie)
            );

            if(i == 0) drawer.addDivider();

            i++;

        }

    }

    private void Pairing(){
        Toast.makeText(getApplicationContext(), "Pairing a new device", Toast.LENGTH_SHORT).show();

    }

    private void AboutMe(){
        Toast.makeText(getApplicationContext(), "About me", Toast.LENGTH_SHORT).show();

    }

    private void populateView(){

        switch(toolbar.getTitle().toString()){
            case "All":
                break;

            case "Computers":
                break;

        }
    }

    /*
    * Methods needed to add the drawer icon to the toolbar
    */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    //takes the UUID and creates a comms socket
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    //Checks bluetooth state
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


    // Method to send data to our "smart" devices
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            //attempt to place data on the outstream to the BT device
            outStream.write(msgBuffer);
        } catch (IOException e) {
            //if the sending fails this is most likely because device is no longer there
            Toast.makeText(getBaseContext(), "ERROR - Device not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }




}
