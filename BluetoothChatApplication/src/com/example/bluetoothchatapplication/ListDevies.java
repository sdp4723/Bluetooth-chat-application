package com.example.bluetoothchatapplication;

import java.util.Set;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ListDevies extends Activity {

	BluetoothAdapter adapter;
	ArrayAdapter<String> adapter_paired,adapter_scanned;
	ListView listpaireddevices,listscanneddevices;
	ProgressBar scanprogressbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_devies);
		
		initializationCode();
		showpaireddevices();
		onClickListeners();
	}
	
	public void initializationCode(){
		adapter=BluetoothAdapter.getDefaultAdapter();
		listpaireddevices=(ListView)findViewById(R.id.paireddeviceslist);
		listscanneddevices=(ListView)findViewById(R.id.scandeviceslist);
		adapter_paired=new ArrayAdapter<String>(ListDevies.this,android.R.layout.simple_list_item_1);
		adapter_scanned=new ArrayAdapter<String>(ListDevies.this,android.R.layout.simple_list_item_1);
		listpaireddevices.setAdapter(adapter_paired);
		scanprogressbar=(ProgressBar)findViewById(R.id.scanprogressbar);
		listscanneddevices.setAdapter(adapter_scanned);
		//////////////////////////////////////////////
		IntentFilter if1=new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bluetooth_listener, if1);
		IntentFilter if2=new IntentFilter(adapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(bluetooth_listener, if2);
	}

	//Initializing the menu page.
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_devies, menu);
		return true;
	}
	
	//Reusable code for toast.
	
	public void showToast(String string){
	Toast.makeText(ListDevies.this, string,Toast.LENGTH_SHORT).show();
	}
	
	//This code is particularly related to the menu section.
	//This method allows us to select the available menus present in the
	//menu sections and allows us write appropriate functionality for
	//each of them.
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.scanmenu:
			scandevices();
			return true;
		default:
			showToast("nothing");
			return true;
		}
	}
	
	//Creating an array like data structure to store all the bluetooth
	//paired devices.
	
	public void showpaireddevices(){
		Set<BluetoothDevice> device=adapter.getBondedDevices();
		for(BluetoothDevice devices:device){
			adapter_paired.add(devices.getName() + ""+devices.getAddress());
		}
	}
	
	//Once the user clicks on any list item , the item specific device's mac 
	//address to be taken to the next page [device page].
	
	public void onClickListeners(){
		listpaireddevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position==0){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device1.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==1){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device2.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==2){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device3.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==3){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device4.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==4){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device5.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else{
					showToast("no new activities left");
				}
			}
		});
		listscanneddevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position==0){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device11.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==1){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device22.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==2){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device33.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==3){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device44.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else if(position==4){
					String device=(String)parent.getItemAtPosition(position);
					String address=device.substring(device.length()-17);
					Intent i=new Intent(ListDevies.this,Device55.class);
					Bundle b=new Bundle();
					b.putString("address", address);
					i.putExtras(b);
					startActivity(i);
				}
				else{
					showToast("no new activities left to start the chat");
				}
			}
		});
	}
	
	public void scandevices(){
		scanprogressbar.setVisibility(View.VISIBLE);
		showToast("scan started");
		
		if(adapter.isDiscovering()){
			adapter.cancelDiscovery();
		}
		adapter.startDiscovery();
	}
	
	//All the general notifications will be handled by the broadcast receiver.
	public BroadcastReceiver bluetooth_listener=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getBondState()!=BluetoothDevice.BOND_BONDED){
					adapter_scanned.add(device.getName() +"  "+device.getAddress());
				}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
					scanprogressbar.setVisibility(View.GONE);
					if(adapter_scanned.getCount()==0){
						showToast("no devices found");
					}else{
						showToast("click on the device to start the chat");
					}
				}
			}
		}	
	};	
}