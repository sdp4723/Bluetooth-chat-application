package com.example.bluetoothchatapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Device1 extends Activity {

	Context context;
	
	ListView chat;
	EditText message;
	Button send,listen;
	ArrayAdapter<String> chat_adapter;
	BluetoothAdapter bluetooth_adapter;
	
	final static int message_state_changed=10;
	final static int message_read=20;
	final static int message_write=30;
	final static int message_device_name=40;
	final static int message_toast=50;

	ServerClientMessage server_client_message;
	Dbhelper1 d1;
	
    static String connecteddevice;
	static String device_name="default";
	static String toast="toast";
	
	Handler h=new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case message_state_changed:
				switch(msg.arg1){
				case ServerClientMessage.STATE_NONE:
					set_subtitle("not connected");
					break;
				case ServerClientMessage.STATE_LISTEN:
					set_subtitle("not connected");
					break;
				case ServerClientMessage.STATE_CONNECTING:
					set_subtitle("connecting");
					break;
				case ServerClientMessage.STATE_CONNECTED:
					set_subtitle("connected");
					break;
				}
				break;
			case message_read:
				byte[] buffer = (byte[]) msg.obj;
                String inpuBuffer = new String(buffer, 0, msg.arg1);
                chat_adapter.add(connecteddevice + ": " + inpuBuffer);
                
                //new type of data : -
                d1.insert_message(connecteddevice , "Me" , inpuBuffer );
                
				break;				
			case message_write:
				byte[] buffer1 = (byte[]) msg.obj;
                String outputBuffer = new String(buffer1);
                chat_adapter.add("Me: " + outputBuffer);
                
                //new type of data : -
                d1.insert_message("Me" , connecteddevice , outputBuffer );
   
				break;				
			case message_device_name:
				connecteddevice=msg.getData().getString(device_name);
				display_toast(connecteddevice);
				break;
			case message_toast:
				display_toast(msg.getData().getString(toast));
				break;
			default :
				display_toast("nothing");
			}
			return true;
		}
	});

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device1);
		
		initialization_code();
		on_click_listeners();
		
		//The operation actually starts at the check function.
		
		server_client_message=new ServerClientMessage(this, h);
		check();
		d1=new Dbhelper1(this);

		if(chat_adapter.getCount()==0){
			Cursor c=d1.retrieve_chat();
			if(c.getCount()==0){
				display_toast("no chat history found");
			}
			else{
				while(c.moveToNext()){
					chat_adapter.add(c.getString(1) +": "+c.getString(3));
					chat_adapter.notifyDataSetChanged();
				}
			}
		}
		else{
			display_toast("start chat");
		}
	}
	
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void set_subtitle(CharSequence message){
		getActionBar().setSubtitle(message);
	}
	
	//Reusable method for toast
	
	public void display_toast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	//Initiazing the app components
	
	public void initialization_code(){
		chat=(ListView)findViewById(R.id.chat);
		message=(EditText)findViewById(R.id.message);
		send=(Button)findViewById(R.id.send);
		chat_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		chat.setAdapter(chat_adapter);
		bluetooth_adapter=BluetoothAdapter.getDefaultAdapter();
			}
	
	public void on_click_listeners(){
		send.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				String messages=message.getText().toString();
				if(!messages.isEmpty()){
					message.setText("");
					server_client_message.write(messages.getBytes());
				}
			}
		});
		chat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) 		{	
				final String message=(String)parent.getItemAtPosition(position);
				new AlertDialog.Builder(Device1.this)
				.setTitle("Are you sure")
				.setMessage("do you want to delete this message ")
				.setPositiveButton("yes", new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						chat_adapter.remove(message);
						chat_adapter.notifyDataSetChanged();
					}
				})
				.setNegativeButton("no", null)
				.show();
				return true;
			}
		});
	}
	
	//Getting the MAC address of the device.
	
	public void check(){
		Bundle b=getIntent().getExtras();
		String address=b.getString("address");
		server_client_message.connect(bluetooth_adapter.getRemoteDevice(address));
	}	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(server_client_message!=null){
			server_client_message.stop();
		}
	}
}