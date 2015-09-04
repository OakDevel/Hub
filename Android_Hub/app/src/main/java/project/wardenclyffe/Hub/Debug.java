package project.wardenclyffe.Hub;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Debug extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        checkBTState();

        String Debug_MAC = "20:15:05:27:56:64";
        final Device aux = new Device(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(Debug_MAC));


        Button setup = (Button) findViewById(R.id.setup);
        Button hub = (Button) findViewById(R.id.main);
        Button con = (Button) findViewById(R.id.connecting);

        final Switch state = (Switch) findViewById(R.id.state);
        Button categorie = (Button) findViewById(R.id.categorie);

        state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    aux.mConnectedThread.write("01");
                }else{
                    aux.mConnectedThread.write("00");
                }
            }
        });


        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent show_Setup = new Intent(getApplicationContext(), Setup.class);
                startActivity(show_Setup);

            }
        });
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent show_Setup = new Intent(getApplicationContext(), Connecting.class);
                startActivity(show_Setup);

            }
        });


        hub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent show_main = new Intent(getApplicationContext(), Hub.class);
                startActivity(show_main);
            }
        });

        categorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aux.mConnectedThread.write("02");
                Toast.makeText(getApplicationContext(), aux.getType(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(BluetoothAdapter.getDefaultAdapter() == null) {
            finish();
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

}
