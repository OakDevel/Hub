package project.wardenclyffe.Hub;

import android.bluetooth.BluetoothAdapter;
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
import android.widget.Toast;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerFragmentItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import java.util.Set;


public class Hub extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;

    //Member Fields
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

    }

    @Override
    public void onPause() {
        super.onPause();

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


    //Checks bluetooth state
    private void CheckBluetoothStatus() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Check if device has bluetooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Device doesn't have bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                //Ask user to turn on bluetooth
                Intent enable_Bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enable_Bluetooth, 1);
            }
        }
    }

}
