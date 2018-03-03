package com.example.steven.testapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    //Declare socket information.
    private Socket gpsSocket;
    private final int SERVERPORT = 31234;
    private final String SERVERIP = "35.231.35.232";
    protected PrintWriter socketWriter;

    //Define asynchronous task to send GPS data over socket.
    class GpsDataTransfer extends AsyncTask<Double,Void,Void> {

        @Override
        protected Void doInBackground(Double... args){

            try {
                //Read in values passed during execution, set them as local double primitives.

                Double latObject, lonObject;
                latObject = args[0];
                lonObject = args[1];


                //Declare a new address object to hold server IP.
                InetAddress serverAddr = Inet4Address.getByName(SERVERIP);
                String serverAddrString = serverAddr.getHostAddress();
                TextView socketStatus = findViewById(R.id.socket_info);
                socketStatus.setText("Attempting connection to " + serverAddrString + ":" + SERVERPORT);
                gpsSocket = new Socket(serverAddr, SERVERPORT);

                socketWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(gpsSocket.getOutputStream())));
                socketWriter.print(latObject.toString() + ',' + lonObject.toString());
                socketWriter.flush();
                if (gpsSocket.isConnected()) {
                    socketStatus.setText("Connection Made");
                } else {
                    socketStatus.setText("Connection Failed");
                }

            } catch (UnknownHostException e) {
                String errorMessage = e.getMessage();
                TextView socketStatus = findViewById(R.id.socket_info);
                socketStatus.setText(errorMessage);
            } catch (IOException e) {
                String errorMessage = e.getMessage();
                TextView socketStatus = findViewById(R.id.socket_info);
                socketStatus.setText(errorMessage);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                TextView socketStatus = findViewById(R.id.socket_info);
                socketStatus.setText(errorMessage);
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,new LocationListener(){
                @Override
                public void onLocationChanged(Location location) {
                    //Call if new location is found.
                    double longitude = location.getLongitude();
                    Double lonObject = new Double(longitude);
                    double latitude = location.getLatitude();
                    Double latObject = new Double(latitude);
                    TextView gpsDisplayText = findViewById(R.id.gps_data);
                    String gpsText = "Latitude: " + latitude + "\nLongitude " + longitude;
                    gpsDisplayText.setText(gpsText);

                    //Place gathered coordinates into a double object array to pass to async.
                    Double[] coordinateHolder = {latObject, lonObject};
                    new GpsDataTransfer().execute(coordinateHolder);

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    //Do nothing.
                }

                @Override
                public void onProviderEnabled(String s) {
                    //Do nothing.
                }

                @Override
                public void onProviderDisabled(String s) {
                    //Do nothing.
                }
            });
            } catch(SecurityException e) {
                    //If fail to get location service, implement a way to get it.
            }
            }

    }
