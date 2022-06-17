package com.example.bluetoothchatapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerClientMessage {

	    private BluetoothAdapter bluetoothAdapter;
	    ///
	    private ConnectThread connectThread;
	    private AcceptThread acceptThread;
	    private ConnectedThread connectedThread;
		private Context context;
	    private final Handler handler;

	    private final UUID APP_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	    private final String APP_NAME = "BluetoothChatApp";

	    public static final int STATE_NONE = 0;
	    public static final int STATE_LISTEN = 1;
	    public static final int STATE_CONNECTING = 2;
	    public static final int STATE_CONNECTED = 3;

	    private int state;

	    
	    public ServerClientMessage(Context context, Handler handler) {
	        this.context = context;
	        this.handler = handler;

	        state = STATE_NONE;
	        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    }

	    public int getState() {
	        return state;
	    }

	    public synchronized void setState(int state) {
	        this.state = state;
	        handler.obtainMessage(Device1.message_state_changed, state, -1).sendToTarget();
	    }

	    private synchronized void start() {
	        if (connectThread != null) {
	            connectThread.cancel();
	            connectThread = null;
	        }

	        if (acceptThread == null) {
	            acceptThread = new AcceptThread();
	            acceptThread.start();
	        }

	        if (connectedThread != null) {
	            connectedThread.cancel();
	            connectedThread = null;
	        }

	        setState(STATE_LISTEN);
	    }

	    public synchronized void stop() {
	        if (connectThread != null) {
	            connectThread.cancel();
	            connectThread = null;
	        }
	        if (acceptThread != null) {
	            acceptThread.cancel();
	            acceptThread = null;
	        }

	        if (connectedThread != null) {
	            connectedThread.cancel();
	            connectedThread = null;
	        }

	        setState(STATE_NONE);
	    }

	    public void connect(BluetoothDevice device) {
	        if (state == STATE_CONNECTING) {
	            connectThread.cancel();
	            connectThread = null;
	        }

	        connectThread = new ConnectThread(device);
	        connectThread.start();

	        if (connectedThread != null) {
	            connectedThread.cancel();
	            connectedThread = null;
	        }

	        setState(STATE_CONNECTING);
	    }

	    public void write(byte[] buffer) {
	        ConnectedThread connThread;
	        synchronized (this) {
	            if (state != STATE_CONNECTED) {
	                return;
	            }
	            connThread = connectedThread;
	        }
	        connThread.write(buffer);
	    }

	    //SERVER THREAD
	    private class AcceptThread extends Thread {
	        private BluetoothServerSocket serverSocket;
	        public AcceptThread() {
	            BluetoothServerSocket tmp = null;
	            try {
	            	//Server creates a socket and attaches it to the piconet
	            	//and starts listening at the specified uuid address.
	                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
	            } catch (IOException e) {
	                Log.e("Accept->Constructor", e.toString());
	            }

	            serverSocket = tmp;
	        } 
	        
	        //Server is in listening mode and ready 
	        //to accept the connections.
	        
	        public void run() {
	            BluetoothSocket socket = null;
	            try {
	                socket = serverSocket.accept();
	            } catch (IOException e) {
	                Log.e("Accept->Run", e.toString());
	                try {
	                    serverSocket.close();
	                } catch (IOException e1) {
	                    Log.e("Accept->Close", e.toString());
	                }
	            }

	            if (socket != null) {
	                switch (state) {
	                    case STATE_LISTEN:
	                    case STATE_CONNECTING:
	                        connected(socket, socket.getRemoteDevice());
	                        break;
	                    case STATE_NONE:
	                    case STATE_CONNECTED:
	                        try {
	                            socket.close();
	                        } catch (IOException e) {
	                            Log.e("Accept->CloseSocket", e.toString());
	                        }
	                        break;
	                }
	            }
	        }

	        public void cancel() {
	            try {
	                serverSocket.close();
	            } catch (IOException e) {
	                Log.e("Accept->CloseServer", e.toString());
	            }
	        }
	    }

	    //Client Thread
	    
	    private class ConnectThread extends Thread {
	        private final BluetoothSocket socket;
	        private final BluetoothDevice device;

	        public ConnectThread(BluetoothDevice device) {
	            this.device = device;
	            BluetoothSocket tmp = null;
	            try {
	            	//Client creates a socket and attaches it to the piconet
	            	//and it reaches to same uuid address.
	                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
	            } catch (IOException e) {
	                Log.e("Connect->Constructor", e.toString());
	            }
	            socket = tmp;
	        }

	        //Connection happens automatically as the client thread
	        //runs.
	        
	        public void run() {
	            try {
	                socket.connect();
	            } catch (IOException e) 
	            {
	                Log.e("Connect->Run", e.toString());
	                try {
	                    socket.close();
	                } 
	                //catch block only executes if the try block fails.	
	                catch (IOException e1) 
	                {
	                    Log.e("Connect->CloseSocket", e.toString());
	                }
	                connectionFailed();
	                return;
	            }

	            synchronized (ServerClientMessage.this) {
	                connectThread = null;
	            }

	            connected(socket, device);
	        }

	        public void cancel() {
	            try {
	                socket.close();
	            } catch (IOException e) {
	                Log.e("Connect->Cancel", e.toString());
	            }
	        }
	    }

	    //Message Thread
	    
	    private class ConnectedThread extends Thread {
	        private final BluetoothSocket socket;
	        private final InputStream inputStream;
	        private final OutputStream outputStream;

	        //Get the inputstream and outputstream of the 
	        //connected sockets.
	        
	        public ConnectedThread(BluetoothSocket socket) {
	            this.socket = socket;

	            InputStream tmpIn = null;
	            OutputStream tmpOut = null;

	            try {
	                tmpIn = socket.getInputStream();
	                tmpOut = socket.getOutputStream();
	            } catch (IOException e) {
	            }

	            inputStream = tmpIn;
	            outputStream = tmpOut;
	        }

	        //Inputstream should be always ready listening for the 
	        //messages from the sender/receiver.
	        
	        public void run() {
	            byte[] buffer = new byte[1024];
	            int bytes;
	           while(true){ 
	            try {
	                bytes = inputStream.read(buffer);
	                handler.obtainMessage(Device1.message_read, bytes, -1, buffer).sendToTarget();
	            } catch (IOException e) {
	                connectionLost();
	            }
	           }
	        }

	        public void write(byte[] buffer) {
	            try {
	                outputStream.write(buffer);
	                handler.obtainMessage(Device1.message_write, -1, -1, buffer).sendToTarget();
	            } catch (IOException e) {

	            }
	        }

	        public void cancel() {
	            try {
	                socket.close();
	            } catch (IOException e) {

	            }
	        }
	    }

	    private void connectionLost() {
	        Message message = handler.obtainMessage(Device1.message_toast);
	        Bundle bundle = new Bundle();
	        bundle.putString(Device1.toast, "Connection Lost");
	        message.setData(bundle);
	        handler.sendMessage(message);
	        ServerClientMessage.this.start();
	    }

	    private synchronized void connectionFailed() {
	        Message message = handler.obtainMessage(Device1.message_toast);
	        Bundle bundle = new Bundle();
	        bundle.putString(Device1.toast, "Cant connect to the device");
	        message.setData(bundle);
	        handler.sendMessage(message);
	        ServerClientMessage.this.start();
	    }

	    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
	        	//Client Thread
	    	if (connectThread != null) {
	            connectThread.cancel();
	            connectThread = null;
	        }

	    		//Message Thread
	        if (connectedThread != null) {
	            connectedThread.cancel();
	            connectedThread = null;
	        }

	        connectedThread = new ConnectedThread(socket);
	        connectedThread.start();

	        Message message = handler.obtainMessage(Device1.message_device_name);
	        Bundle bundle = new Bundle();
	        bundle.putString(Device1.device_name, device.getName());
	        message.setData(bundle);
	        handler.sendMessage(message);
	        setState(STATE_CONNECTED);
	    }
	}