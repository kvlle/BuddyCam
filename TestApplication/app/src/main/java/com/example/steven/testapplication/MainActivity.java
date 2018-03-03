package com.example.steven.testapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Socket gpsSocket;
    private final int SERVERPORT = 31234;
    private final String SERVERIP = "35.231.35.232";
    PrintWriter output;

    //
    protected void sendGpsData(double lon, double lat) {

        try{
            InetAddress serverAddr = Inet6Address.getByName(SERVERIP);
            gpsSocket = new Socket(serverAddr, SERVERPORT);
            output = new PrintWriter(new OutputStreamWriter(gpsSocket.getOutputStream()));
            output.print(lon + "," + lat);
        } catch(UnknownHostException e) {
            e.printStackTrace();
    }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new LocationListener(){
                @Override
                public void onLocationChanged(Location location) {
                    //Call if new location is found.
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    TextView gpsDisplayText = findViewById(R.id.gps_data);
                    String gpsText = "Latitude: " + latitude + "\nLongitude " + longitude;
                    gpsDisplayText.setText(gpsText);
                    sendGpsData(longitude,latitude);
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

            }
            }

    }
