package com.example.smartonstreetparking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.app.AlertDialog;
import android.content.DialogInterface;


public class MapActivity extends Activity {
    private MapView mapView;
    private Marker startMarker, selectedEndPoint;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        dbHelper = new DatabaseHelper(this);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        setupMarkers();

        Button navigateButton = findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(view -> {
            if (startMarker != null && selectedEndPoint != null) {
                openGoogleMapsForNavigation(startMarker.getPosition(), selectedEndPoint.getPosition());
            } else {
                Toast.makeText(MapActivity.this, "Please select an endpoint first.", Toast.LENGTH_LONG).show();
            }
        });

        Button homeButton = findViewById(R.id.homeButton);
        // Set up the button to navigate back to the home screen
        homeButton.setOnClickListener(v -> {
            // Navigate back to the HomeActivity
            onBackPressed();
        });

        Button scanQRButton = findViewById(R.id.scanQRButton);
        scanQRButton.setOnClickListener(v -> startQRScanner());

        mapView.getController().setZoom(20);
        mapView.invalidate();
    }

    private void setupMarkers() {
        GeoPoint startPoint = new GeoPoint(38.400627, 27.0609162);
        startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start Point");
        mapView.getOverlays().add(startMarker);
        mapView.getController().setCenter(startPoint);

        addParkSpot(new GeoPoint(38.45798, 27.212984), "1111");
        addParkSpot(new GeoPoint(38.457987, 27.213189), "2222");
        addParkSpot(new GeoPoint(38.455755, 27.215669), "3333");
        addParkSpot(new GeoPoint(38.455183, 27.216676), "4444");
        addParkSpot(new GeoPoint(38.453456, 27.213302), "5555");

    }

    private void addParkSpot(GeoPoint parkSpot, String spotId) {
        Marker parkSpotMarker = new Marker(mapView);
        parkSpotMarker.setPosition(parkSpot);
        parkSpotMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //parkSpotMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.marker_default)); // Default marker icon
        parkSpotMarker.setTitle("Park Spot " + spotId);
        parkSpotMarker.setOnMarkerClickListener((marker, mapView) -> {
            if (selectedEndPoint != null) {
                //selectedEndPoint.setIcon(ContextCompat.getDrawable(this, R.drawable.marker_default));
            }
            selectedEndPoint = marker;
            //selectedEndPoint.setIcon(ContextCompat.getDrawable(this, R.drawable.marker_selected));
            selectedEndPoint.setTitle("End Point");
            Toast.makeText(this, "End Spot has been selected!", Toast.LENGTH_SHORT).show();
            mapView.invalidate(); // Refresh the map to show changes
            return true;
        });
        mapView.getOverlays().add(parkSpotMarker);
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned The QR Code", Toast.LENGTH_LONG).show();
                int spotId = Integer.parseInt(result.getContents()); // Convert QR code result to spot_id
                showStartParkingDialog(spotId);  // Ask user to start the parking session
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showStartParkingDialog(int spotId) {
        ParkingSpotController controller = new ParkingSpotController(this);
        boolean isStartingSession = controller.checkAndUpdateSpotAvailability(spotId);
        String session;
        if (isStartingSession)
            session = "end";
        else
            session = "start";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start Parking Session");
        builder.setMessage("Do you want to " + session + " the parking session for this spot?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toggleParkingSpotAvailability(spotId, isStartingSession);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toggleParkingSpotAvailability(int spotId, boolean sessionStatus) {
        if (sessionStatus) {
            Toast.makeText(this, "To end the parking session, go for payment.", Toast.LENGTH_LONG).show();

            // Fetch the parking duration
            int parkingDuration = dbHelper.fetchParkingDuration(spotId);
            Intent intent = new Intent(MapActivity.this, PaymentActivity.class);
            intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1)); // Retrieve user_id passed from HomeActivity
            intent.putExtra("spot_id", spotId);
            intent.putExtra("hours", parkingDuration);
            startActivity(intent);
        } else {
            // Toast.makeText(this, "Parking session has been started!", Toast.LENGTH_LONG).show();
            // Start ParkingDurationActivity and pass user_id and spot_id
            Intent intent = new Intent(MapActivity.this, ParkingDurationActivity.class);
            intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1)); // Retrieve user_id passed from HomeActivity
            intent.putExtra("spot_id", spotId);
            startActivity(intent);
        }
    }




    private void openGoogleMapsForNavigation(GeoPoint startPoint, GeoPoint endPoint) {
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                "&origin=" + startPoint.getLatitude() + "," + startPoint.getLongitude() +
                "&destination=" + endPoint.getLatitude() + "," + endPoint.getLongitude() +
                "&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}