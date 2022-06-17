  package com.example.bluetoothchatapplication;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class First extends Activity {

	BluetoothAdapter adapter;
	final int bluetooth_enable_request=1;
	boolean valid;
	Button guidelines;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		initialization_code();
		onClickListeners();
	}
	
	//Initialization of bluetooth adapter and button.
	
	public void initialization_code(){
		adapter=BluetoothAdapter.getDefaultAdapter();
		guidelines=(Button)findViewById(R.id.guidelines);
	}
	
	//Reusable method for toast.
	
	public void showToast(String string){
	Toast.makeText(First.this, string,Toast.LENGTH_SHORT).show();
	}

	//Initializing menu page.
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.first, menu);
		return true;
	}
	
	//This code is particularly related to the menu section.
	//This method allows us to select the available menus present in the
	//menu sections and allows us write appropriate functionality for
	//each of them.
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()){
		case R.id.discoverdevice:
			discoverdevices();
			break;
		case R.id.adddevice:
			Intent list_devices_intent=new Intent(First.this,ListDevies.class);
			startActivity(list_devices_intent);
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	//Check if device supports bluetooth and if yes , check whether the bluetooth
	//is enabled or switch on the bluetooth.
	
	public void bluetoothCheckAndOn(){
		if(adapter==null){
			showToast("no bluetooth support for your device");
		}
		else{
			if(adapter.isEnabled()){
				showToast("bluetooth is already enabled");
				discoverdevices();
			}
			else{
				Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(i,bluetooth_enable_request);
				//Its corresponding method is onActivityResult and the method will
               //be executed.				
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(requestCode==bluetooth_enable_request && resultCode==RESULT_OK){
				adapter.enable();
				showToast("bluetooth is enabled");
			}
	}
	
	//We should make our bluetooth device discoverable to other bluetooth
	//enabled devices.
	
	public void discoverdevices(){
		if(adapter.getScanMode()!=BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
			Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(i);
		}
	}

	//Code will be executed for only one time when the application has started.
	@Override
	protected void onStart() {
		super.onStart();
		if(!adapter.isEnabled()){
			bluetoothCheckAndOn();
		}
		statusCheck();
	}
	
	//Navigate to the guidelines page.
	
	public void onClickListeners(){
		guidelines.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent(First.this,Guidelines.class);
				startActivity(i);
			}
		});
	}
	
	
	public void statusCheck() {
	   final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	        buildAlertMessageNoGps();
	    }
	}

	private void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	            .setCancelable(false)
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(final DialogInterface dialog, final int id) {
	                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	                    dialog.cancel();
	                }
	            })
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	                }
	            });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}	
}

//else if(resultCode==RESULT_CANCELED){
//new AlertDialog.Builder(this)
//.setTitle("warning")
//.setMessage("bluetooth must be on")
//.setCancelable(false)
//.setPositiveButton("allow", new DialogInterface.OnClickListener() {
//	@Override
//	public void onClick(DialogInterface dialog, int which) {
//		
//	}
//}).setNegativeButton("deny",new DialogInterface.OnClickListener() {
//	@Override
//	public void onClick(DialogInterface dialog, int which) {
//		First.this.finish();
//	}
//}).show();
//}