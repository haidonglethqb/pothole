package com.example.pothole.MapScreen;
import android.location.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;

import com.mapbox.navigation.base.formatter.DistanceFormatterOptions;
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter;
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi;
import com.mapbox.navigation.ui.maneuver.model.Maneuver;
import com.mapbox.navigation.ui.maneuver.model.ManeuverError;
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView;
import static android.content.ContentValues.TAG;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi;
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView;
import kotlin.Triple;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.MotionEvent;
import android.widget.CheckBox;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import com.example.pothole.Other.AccelerometerService;
import com.example.pothole.Other.PotholeData;
import com.example.pothole.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.geojson.LineString;
import com.mapbox.turf.TurfMeasurement;
import com.mapbox.turf.TurfMisc;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.compass.CompassPlugin;
import com.mapbox.maps.plugin.compass.CompassView;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView;
import com.mapbox.navigation.ui.maps.route.arrow.model.InvalidPointError;
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions;
import com.mapbox.navigation.ui.maps.route.arrow.model.UpdateManeuverArrowValue;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineUpdateValue;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi;
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer;
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement;
import com.mapbox.navigation.ui.voice.model.SpeechError;
import com.mapbox.navigation.ui.voice.model.SpeechValue;
import com.mapbox.navigation.ui.voice.model.SpeechVolume;
import com.mapbox.navigation.ui.voice.view.MapboxSoundButton;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import android.widget.ImageView;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class mapdisplay extends AppCompatActivity {
    private long lastUpdateTimestamp = 0; // Lưu thời gian lần cập nhật cuối
    private Point lastUpdatePoint; // Lưu vị trí lần cập nhật cuối

    private TextView tvSpeed;
    private TextView tvDistance;
    private double totalDistance = 0.0;
    private Location lastLocation;
    private List<String> selectedPotholeTypes = new ArrayList<>();
    private boolean soundPlayed = false;
    private void showPotholeInfoDialog(Point point) {
        // Find the pothole information based on the point
        for (LocationRetriever.Quadruple<Double, Double, String, String> location : potholeLocations) {
            if (location.getFirst() == point.latitude() && location.getSecond() == point.longitude()) {
                // Inflate the custom layout
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_pothole_info, null);
                ImageView potholeImage = dialogView.findViewById(R.id.pothole_image);
                TextView potholeInfo = dialogView.findViewById(R.id.pothole_info);
                TextView potholeTime = dialogView.findViewById(R.id.pothole_time);

                // Set the pothole information
                potholeInfo.setText("Latitude: " + location.getFirst() + "\nLongitude: " + location.getSecond() + "\nSeverity: " + location.getThird());
                potholeTime.setText("Detection Time: " + location.getFourth());

                // Optionally, set the image resource based on severity or other criteria
                if (location.getThird().equals("Minor")) {
                    potholeImage.setImageResource(R.drawable.minor);
                } else if (location.getThird().equals("Medium")) {
                    potholeImage.setImageResource(R.drawable.medium);
                } else if (location.getThird().equals("Severe")) {
                    potholeImage.setImageResource(R.drawable.potholecaution);
                }

                // Create and show the AlertDialog with the custom layout
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pothole Information");
                builder.setView(dialogView);
                builder.setPositiveButton("OK", null);
                builder.show();
                break;
            }
        }
    }
    private static final String TAG = "mapdisplay";
    private Style mapStyle;
    MapView mapView;
    MaterialButton setRoute;
    FloatingActionButton focusLocationBtn;
    CompassView compassView;
    private Point destination;
    private Point pothole;
    private Point pothole2;
    private Point pothole3;
    private List<LocationRetriever.Quadruple<Double, Double, String, String>> potholeLocations;
    private DatabaseReference database;
    private MapboxManeuverView maneuverView;
    private MapboxManeuverApi maneuverApi;


    // duong dan
    List<Point> points=new ArrayList<>();


    private boolean notificationShown = false;


    //
    Bitmap bitmap;
    Bitmap bitmap2;
    Bitmap nang;
    Bitmap trungbinh;
    Bitmap nhe;
    //map compoment
    private MapboxRouteArrowApi routeArrowApi;
    private MapboxRouteArrowView routeArrowView;
    private MediaPlayer mediaPlayer;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView speedTextView;

    private double lastUpdateDistance = 0;



    //
    private PointAnnotationManager pointAnnotationManager;


    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;

    private MapboxRouteLineApi routeLineApi;

    //haihh them service
    private DatabaseReference databaseReference;
    private double latitude, longitude;
    private int potholeCount = 0;
    private String severity;
    private AccelerometerService accelerometerService;
    private boolean isBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AccelerometerService.LocalBinder binder = (AccelerometerService.LocalBinder) service;
            accelerometerService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PotholeDetectionChannel";
            String description = "Channel for pothole detection notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("POTHOLE_DETECTION_CHANNEL", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void showPotholeNotification(String severity, double latitude, double longitude) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "POTHOLE_DETECTION_CHANNEL")
                .setSmallIcon(R.drawable.placeholder) // Replace with your app's icon
                .setContentTitle("Pothole Detected")
                .setContentText("Severity: " + severity + ", Location: (" + latitude + ", " + longitude + ")")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
    private final BroadcastReceiver potholeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String severity = intent.getStringExtra("severity");
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);

            Toast.makeText(mapdisplay.this, "New pothole", Toast.LENGTH_SHORT).show();
            showPotholeNotification(severity, latitude, longitude);

        }
    };




    //navigation
    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };
    private NavigationRoute checkedRoute;
    private NavigationRoute alternativeRoute;
    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    if (mapStyle != null) {
                        routeLineView.renderRouteDrawData(mapStyle, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
        }
    };
    boolean focusLocation = true;
    private MapboxNavigation mapboxNavigation;
    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(0.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocation = false;
            getGestures(mapView).removeOnMoveListener(this);
            focusLocationBtn.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    });
    private void checkProximityToPothole(Point userLocation, Point pothole, String severity) {
        double distance = TurfMeasurement.distance(userLocation, pothole);
        double thresholdDistance = 0.10; // 100 meters
        if (distance < thresholdDistance && !notificationShown) {
            showNotification("Pothole Alert", "Slow down, you are approaching a pothole with severity: " + severity);
            Toast.makeText(mapdisplay.this, "Pothole 100 meters ahead", Toast.LENGTH_SHORT).show();
            notificationShown = true;

            // Play warning sound only once
            if (!soundPlayed && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                soundPlayed = true;
            }
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "pothole_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Pothole Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound1);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }
    private final OnIndicatorPositionChangedListener onPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(Point point) {
            // Only update if a route exists
            if (isRouteActive) {
                Expected<RouteLineError, RouteLineUpdateValue> result =
                        routeLineApi.updateTraveledRouteLine(point);
                if (mapStyle != null) {
                    routeLineView.renderRouteLineUpdate(mapStyle, result);
                }
                // Check proximity to all potholes on the route
                for (LocationRetriever.Quadruple<Double, Double, String,String> location : potholeLocations) {
                    Point potholePoint = Point.fromLngLat(location.getSecond(), location.getFirst());
                    if (checkedRoute != null && isPointOnRoute2(potholePoint, checkedRoute)) {
                        checkProximityToPothole(point, potholePoint, location.getThird());
                    }
                }
            }
        }
    };
    private boolean isPointOnRoute(Point point, NavigationRoute route) {
        LineString routeLineString = LineString.fromPolyline(route.getDirectionsRoute().geometry(), 6);
        Point nearestPoint = (Point) TurfMisc.nearestPointOnLine(point, routeLineString.coordinates()).geometry();
        double distance = TurfMeasurement.distance(point, nearestPoint);
        // Define a threshold distance (in kilometers) to consider the point as being on the route
        double thresholdDistance = 0.05; // 50 meters
        return distance < thresholdDistance;
    }
    private boolean isPointOnRoute2(Point point, NavigationRoute route) {
        if (route == null) {
            return false;
        }
        LineString routeLineString = LineString.fromPolyline(route.getDirectionsRoute().geometry(), 6);
        Point nearestPoint = (Point) TurfMisc.nearestPointOnLine(point, routeLineString.coordinates()).geometry();
        double distance = TurfMeasurement.distance(point, nearestPoint);
        // Define a threshold distance (in kilometers) to consider the point as being on the route
        double thresholdDistance = 0.005; // 50 meters
        return distance < thresholdDistance;
    }
    private MapboxSpeechApi speechApi;
    private MapboxVoiceInstructionsPlayer mapboxVoiceInstructionsPlayer;

    private MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> speechCallback = new MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>>() {
        @Override
        public void accept(Expected<SpeechError, SpeechValue> speechErrorSpeechValueExpected) {
            speechErrorSpeechValueExpected.fold(new Expected.Transformer<SpeechError, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechError input) {
                    mapboxVoiceInstructionsPlayer.play(input.getFallback(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            }, new Expected.Transformer<SpeechValue, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechValue input) {
                    mapboxVoiceInstructionsPlayer.play(input.getAnnouncement(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            });
        }
    };
    private final RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(@NonNull RouteProgress routeProgress) {
            routeLineApi.updateWithRouteProgress(routeProgress, new MapboxNavigationConsumer<Expected<RouteLineError, RouteLineUpdateValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteLineUpdateValue> result) {
                    if (mapStyle != null) {
                        routeLineView.renderRouteLineUpdate(mapStyle, result);
                    }
                }
            });
            Expected<InvalidPointError, UpdateManeuverArrowValue> updatedManeuverArrow = routeArrowApi.addUpcomingManeuverArrow(routeProgress);
            routeArrowView.renderManeuverUpdate(mapStyle, updatedManeuverArrow);

            maneuverApi.getManeuvers(routeProgress).fold(new Expected.Transformer<ManeuverError, Object>() {
                @NonNull
                @Override
                public Object invoke(@NonNull ManeuverError input) {
                    return new Object();
                }
            }, new Expected.Transformer<List<Maneuver>, Object>() {
                @NonNull
                @Override
                public Object invoke(@NonNull List<Maneuver> input) {
                    maneuverView.setVisibility(View.VISIBLE);
                    maneuverView.renderManeuvers(maneuverApi.getManeuvers(routeProgress));
                    return new Object();
                }
            });

        }
    };
    private MapboxNavigationConsumer<SpeechAnnouncement> voiceInstructionsPlayerCallback = new MapboxNavigationConsumer<SpeechAnnouncement>() {
        @Override
        public void accept(SpeechAnnouncement speechAnnouncement) {
            speechApi.clean(speechAnnouncement);
        }
    };

    VoiceInstructionsObserver voiceInstructionsObserver = new VoiceInstructionsObserver() {
        @Override
        public void onNewVoiceInstructions(@NonNull VoiceInstructions voiceInstructions) {
            speechApi.generate(voiceInstructions, speechCallback);
        }
    };

    private boolean isVoiceInstructionsMuted = false;

    // search
    private PlaceAutocomplete placeAutocomplete;
    private SearchResultsView searchResultsView;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_multi_select, null);
        builder.setView(dialogView);

        CheckBox checkboxMinor = dialogView.findViewById(R.id.checkbox_minor);
        CheckBox checkboxMedium = dialogView.findViewById(R.id.checkbox_medium);
        CheckBox checkboxSevere = dialogView.findViewById(R.id.checkbox_severe);
        RadioGroup radioGroupDateRange = dialogView.findViewById(R.id.radio_group_date_range);
        RadioButton radio7Days = dialogView.findViewById(R.id.radio_7_days);
        RadioButton radio14Days = dialogView.findViewById(R.id.radio_14_days);
        RadioButton radio30Days = dialogView.findViewById(R.id.radio_30_days);
        RadioGroup radioGroupDistance = dialogView.findViewById(R.id.radio_group_distance);
        RadioButton radio1Km = dialogView.findViewById(R.id.radio_1_km);
        RadioButton radio3Km = dialogView.findViewById(R.id.radio_3_km);
        RadioButton radio5Km = dialogView.findViewById(R.id.radio_5_km);

        // Set initial state based on selectedPotholeTypes
        checkboxMinor.setChecked(selectedPotholeTypes.contains("Minor"));
        checkboxMedium.setChecked(selectedPotholeTypes.contains("Medium"));
        checkboxSevere.setChecked(selectedPotholeTypes.contains("Severe"));

        if (selectedPotholeTypes.contains("Last 7 days")) {
            radio7Days.setChecked(true);
        } else if (selectedPotholeTypes.contains("Last 14 days")) {
            radio14Days.setChecked(true);
        } else if (selectedPotholeTypes.contains("Last 30 days")) {
            radio30Days.setChecked(true);
        }

        if (selectedPotholeTypes.contains("Within 1 km")) {
            radio1Km.setChecked(true);
        } else if (selectedPotholeTypes.contains("Within 3 km")) {
            radio3Km.setChecked(true);
        } else if (selectedPotholeTypes.contains("Within 5 km")) {
            radio5Km.setChecked(true);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPotholeTypes.clear();
                if (checkboxMinor.isChecked()) {
                    selectedPotholeTypes.add("Minor");
                }
                if (checkboxMedium.isChecked()) {
                    selectedPotholeTypes.add("Medium");
                }
                if (checkboxSevere.isChecked()) {
                    selectedPotholeTypes.add("Severe");
                }
                int selectedDateRangeId = radioGroupDateRange.getCheckedRadioButtonId();
                if (selectedDateRangeId == R.id.radio_7_days) {
                    selectedPotholeTypes.add("Last 7 days");
                } else if (selectedDateRangeId == R.id.radio_14_days) {
                    selectedPotholeTypes.add("Last 14 days");
                } else if (selectedDateRangeId == R.id.radio_30_days) {
                    selectedPotholeTypes.add("Last 30 days");
                }
                int selectedDistanceId = radioGroupDistance.getCheckedRadioButtonId();
                if (selectedDistanceId == R.id.radio_1_km) {
                    selectedPotholeTypes.add("Within 1 km");
                } else if (selectedDistanceId == R.id.radio_3_km) {
                    selectedPotholeTypes.add("Within 3 km");
                } else if (selectedDistanceId == R.id.radio_5_km) {
                    selectedPotholeTypes.add("Within 5 km");
                }
                filterAndDisplayPotholes();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void filterAndDisplayPotholes() {
        getCurrentLocation(currentLocation -> {
            if (currentLocation == null) {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                return;
            }

            pointAnnotationManager.deleteAll();
            boolean noFiltersSelected = selectedPotholeTypes.isEmpty();

            for (LocationRetriever.Quadruple<Double, Double, String, String> location : potholeLocations) {
                boolean shouldDisplay = noFiltersSelected || selectedPotholeTypes.contains(location.getThird());

                if (selectedPotholeTypes.contains("Last 7 days")) {
                    shouldDisplay = shouldDisplay && isWithinDays(location.getFourth(), 7);
                } else if (selectedPotholeTypes.contains("Last 14 days")) {
                    shouldDisplay = shouldDisplay && isWithinDays(location.getFourth(), 14);
                } else if (selectedPotholeTypes.contains("Last 30 days")) {
                    shouldDisplay = shouldDisplay && isWithinDays(location.getFourth(), 30);
                }

                if (selectedPotholeTypes.contains("Within 1 km")) {
                    shouldDisplay = shouldDisplay && isWithinDistance(currentLocation, location, 1);
                } else if (selectedPotholeTypes.contains("Within 3 km")) {
                    shouldDisplay = shouldDisplay && isWithinDistance(currentLocation, location, 3);
                } else if (selectedPotholeTypes.contains("Within 5 km")) {
                    shouldDisplay = shouldDisplay && isWithinDistance(currentLocation, location, 5);
                }

                if (shouldDisplay) {
                    addPotholeIcon(location);
                }
            }
        });
    }
    private boolean isWithinDistance(Location currentLocation, LocationRetriever.Quadruple<Double, Double, String, String> location, int distanceKm) {
        float[] results = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), location.getFirst(), location.getSecond(), results);
        return results[0] <= distanceKm * 1000;
    }
    private void getCurrentLocation(OnSuccessListener<Location> listener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the necessary permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, listener)
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get current location", e));
    }
    private boolean isWithinDays(String dateTime, int days) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateTime);
            long timeInMillis = date.getTime();
            long currentTime = System.currentTimeMillis();
            long daysInMillis = days * 24 * 60 * 60 * 1000L;
            return (currentTime - timeInMillis) <= daysInMillis;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private void updateSpeedUI(float speedKmH) {
        runOnUiThread(() -> {
            tvSpeed.setText("hi");
            Log.d("Speed", "Speed: " + speedKmH + " km/h");
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mediaPlayer = MediaPlayer.create(this, R.raw.warning); // Replace with your sound file
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0); // Reset the MediaPlayer to the beginning
            }
        });

        bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution);
        nang = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution);
        trungbinh = BitmapFactory.decodeResource(getResources(), R.drawable.medium);
        nhe = BitmapFactory.decodeResource(getResources(), R.drawable.minor);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapdisplay);




        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                float speed = location.getSpeed(); // speed in meters/second
                float speedKmH = speed * 3.6f; // convert to km/h
                Log.d("Speed", "Speed: " + speedKmH + " km/h");
                if (speedKmH > 0) {
                    // Remove the usage of tvSpeed
                    // tvSpeed.setText("Speed: " + speedKmH + " km/h");
                    Log.d("Speed", "Speed: " + speedKmH + " km/h");
                }
                if (lastLocation != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                            location.getLatitude(), location.getLongitude(), results);
                    totalDistance += results[0];
                    Log.d("Distance", "Total distance: " + totalDistance + " meters");
                }
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }
        };

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_mapdisplay);

        Spinner filterSpinner = findViewById(R.id.filter_spinner);
        filterSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showMultiSelectDialog();
                }
                return true;
            }
        });





        if (database == null) {
            FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
            //databaseInstance.setPersistenceEnabled(true);
            database = databaseInstance.getReference();
            Log.d(TAG, "Firebase initialized");
        }
        potholeLocations = new ArrayList<>();
        LocationRetriever locationRetriever = new LocationRetriever(this);
        locationRetriever.retrieveLocations(new LocationRetriever.LocationCallback() {
            @Override
            public void onLocationsRetrieved(List<LocationRetriever.Quadruple<Double, Double, String,String>> locations) {
                // Log the retrieved locations
                if (locations.isEmpty()) {
                    Log.d(TAG, "No locations retrieved from local storage.");
                } else {
                    Log.d(TAG, "Retrieved " + locations.size() + " locations from local storage.");
                    for (LocationRetriever.Quadruple<Double, Double, String,String> location : locations) {
                        Log.d(TAG, "Latitude: " + location.getFirst() + ", Longitude: " + location.getSecond() + ", Severity: " + location.getThird()+ ", Date: " + location.getFourth());
                    }
                }
                // get locations
                potholeLocations = locations;
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to retrieve locations", e);
            }
        });



        //haihh them
        Intent intent = new Intent(this, AccelerometerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        createNotificationChannel();

        // Register broadcast receiver
        IntentFilter filter = new IntentFilter("com.example.pothole.POTHOLE_DETECTED");
        registerReceiver(potholeReceiver, filter);

        points.add(pothole);
        points.add(pothole2);



        // route
        mapView = findViewById(R.id.mapView);
        focusLocationBtn = findViewById(R.id.focus_location_button);
        setRoute = findViewById(R.id.route_button);

        // route line color resources
        RouteLineColorResources routeLineColorResources = new RouteLineColorResources.Builder()
//                .routeDefaultColor(Color.TRANSPARENT)
//                .routeCasingColor(Color.TRANSPARENT)
//                .routeLineTraveledColor(Color.GRAY)
//                .routeLineTraveledCasingColor(Color.DKGRAY)  //mau vien
                .routeUnknownCongestionColor(Color.TRANSPARENT)
                .routeLowCongestionColor(Color.TRANSPARENT)
                .routeModerateCongestionColor(Color.TRANSPARENT)
                .routeSevereCongestionColor(Color.TRANSPARENT)
                .build();

        // route line resources
        RouteLineResources routeLineResources = new RouteLineResources.Builder()
                .routeLineColorResources(routeLineColorResources)
                .build();
// arrow on route line
        routeArrowApi = new MapboxRouteArrowApi();
        RouteArrowOptions arrowOptions = new RouteArrowOptions.Builder(this)
                .withAboveLayerId(RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID)
                .build();
        routeArrowView = new MapboxRouteArrowView(arrowOptions);

        // route line options
        MapboxRouteLineOptions lineOptions = new MapboxRouteLineOptions.Builder(this)
                .withVanishingRouteLineEnabled(true)
                .withRouteLineResources(routeLineResources)
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
                .build();
        routeLineView = new MapboxRouteLineView(lineOptions);
        routeLineApi = new MapboxRouteLineApi(lineOptions);


        speechApi = new MapboxSpeechApi(mapdisplay.this, "sk.eyJ1IjoiaGFpY2h1IiwiYSI6ImNtNDJpaWx1ejAxMmgya3NpZ2pld3RjM2EifQ.PHoHOWjAuC0dE7UmMo3hfg", Locale.US.toLanguageTag());
        mapboxVoiceInstructionsPlayer = new MapboxVoiceInstructionsPlayer(mapdisplay.this, Locale.US.toLanguageTag());

        NavigationOptions navigationOptions = new NavigationOptions.Builder(this).accessToken("sk.eyJ1IjoiaGFpY2h1IiwiYSI6ImNtNDJpaWx1ejAxMmgya3NpZ2pld3RjM2EifQ.PHoHOWjAuC0dE7UmMo3hfg").build();

        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver);
        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);
        mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver);

        MapboxSoundButton soundButton = findViewById(R.id.soundButton);
        soundButton.unmute();
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVoiceInstructionsMuted = !isVoiceInstructionsMuted;
                if (isVoiceInstructionsMuted) {
                    soundButton.muteAndExtend(1500L);
                    mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(0f));
                } else {
                    soundButton.unmuteAndExtend(1500L);
                    mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(1f));
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(mapdisplay.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(mapdisplay.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mapdisplay.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }

        focusLocationBtn.hide();
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(new OnIndicatorPositionChangedListener() {
            @Override
            public void onIndicatorPositionChanged(Point currentPoint) {
                long currentTime = System.currentTimeMillis();
                double distance = lastUpdatePoint == null
                        ? Double.MAX_VALUE
                        : TurfMeasurement.distance(lastUpdatePoint, currentPoint);

                // Cập nhật nếu người dùng di chuyển trên 50m hoặc đã qua 2 giây
                if (distance > 0.03 || currentTime - lastUpdateTimestamp > 700) {
                    lastUpdateTimestamp = currentTime;
                    lastUpdatePoint = currentPoint;

                    // Vẽ lại tuyến đường nếu cần
                    Expected<RouteLineError, RouteLineUpdateValue> result =
                            routeLineApi.updateTraveledRouteLine(currentPoint);
                    if (mapStyle != null) {
                        routeLineView.renderRouteLineUpdate(mapStyle, result);
                    }
                }
            }
        });
        getGestures(mapView).addOnMoveListener(onMoveListener);

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mapdisplay.this, "Please select a location in map", Toast.LENGTH_SHORT).show();

            }
        });

        // search
        placeAutocomplete = PlaceAutocomplete.create("sk.eyJ1IjoiaGFpY2h1IiwiYSI6ImNtNDJpaWx1ejAxMmgya3NpZ2pld3RjM2EifQ.PHoHOWjAuC0dE7UmMo3hfg");
        searchET = findViewById(R.id.search_bar_text);

        searchResultsView = findViewById(R.id.search_results_view);
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));

        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(mapdisplay.this));

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchResultsView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mapView.getMapboxMap().loadStyleUri(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapStyle = style;
                style.addImage("minor-icon", BitmapFactory.decodeResource(getResources(), R.drawable.minor));
                style.addImage("medium-icon", BitmapFactory.decodeResource(getResources(), R.drawable.medium));
                style.addImage("severe-icon", BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution));
                CompassPlugin compassPlugin = (CompassPlugin) mapView.getPlugin(Plugin.MAPBOX_COMPASS_PLUGIN_ID);
                if (compassPlugin != null) {
                    compassPlugin.setVisibility(false);
                    compassPlugin.setEnabled(false);
                }

                ScaleBarPlugin scaleBarPlugin = (ScaleBarPlugin) mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
                if (scaleBarPlugin != null) {
                    scaleBarPlugin.setEnabled(false);
                }

                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });




                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);

                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                filterAndDisplayPotholes();
                for (LocationRetriever.Quadruple<Double, Double, String,String> location : potholeLocations) {
                    double iconSize = 0.05;
                    Point point = Point.fromLngLat(location.getSecond(), location.getFirst());
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution);
                    if(location.getThird().equals("Minor")){
                        iconSize=0.05;
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.minor);

                    }
                    if(location.getThird().equals("Medium")){
                        iconSize=0.055;
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medium);
                    }
                    if(location.getThird().equals("Severe")){
                        iconSize=0.06;
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution);
                    }
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconSize(iconSize)
                            .withIconImage(bitmap)
                            .withPoint(point);
                    pointAnnotationManager.create(pointAnnotationOptions);
                }
                pointAnnotationManager.addClickListener(annotation -> {
                    showPotholeInfoDialog(annotation.getPoint());
                    return true;
                });


                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {


                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRoute(point);
                            }
                        });
                        return false;
                    }
                });
                focusLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        focusLocationBtn.hide();
                    }
                });

                // search
                placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
                    @Override
                    public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

                    }

                    @Override
                    public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        ignoreNextQueryUpdate = true;
                        focusLocation = false;
                        searchET.setText(placeAutocompleteSuggestion.getName());
                        searchResultsView.setVisibility(View.GONE);

                        pointAnnotationManager.deleteAll();
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                                .withPoint(placeAutocompleteSuggestion.getCoordinate())
                                .withIconSize(0.49);
                        pointAnnotationManager.create(pointAnnotationOptions);
                        updateCamera(placeAutocompleteSuggestion.getCoordinate(), 0.0);
                        destination=placeAutocompleteSuggestion.getCoordinate();
//                        pothole=Point.fromLngLat(106.796219, 10.881042);
//                        PointAnnotationOptions potholeAnnotationOptions = new PointAnnotationOptions()
//                                .withTextAnchor(TextAnchor.CENTER)
//                                .withIconImage(bitmap)
//                                .withPoint(pothole);
//                        pointAnnotationManager.create(potholeAnnotationOptions);



                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRoute(destination);
                            }
                        });
                    }

                    @Override
                    public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        //queryEditText.setText(placeAutocompleteSuggestion.getName());
                    }

                    @Override
                    public void onError(@NonNull Exception e) {

                    }
                });
            }
        });
// maneuver
        maneuverView = findViewById(R.id.maneuverView);
        maneuverApi = new MapboxManeuverApi(new MapboxDistanceFormatter(new DistanceFormatterOptions.Builder(mapdisplay.this).build()));
        routeArrowView = new MapboxRouteArrowView(new RouteArrowOptions.Builder(mapdisplay.this).build());
    }








    private boolean isRouteActive = false;
    private void addPotholeIcon(LocationRetriever.Quadruple<Double, Double, String, String> location) {
        double iconSize = 0.05;
        Point point = Point.fromLngLat(location.getSecond(), location.getFirst());
        String iconName = "severe-icon"; // Default icon

        String severity = location.getThird();
        if (severity != null) {
            switch (severity) {
                case "Minor":
                    iconSize = 0.05;
                    iconName = "minor-icon";
                    break;
                case "Medium":
                    iconSize = 0.055;
                    iconName = "medium-icon";
                    break;
                case "Severe":
                    iconSize = 0.06;
                    iconName = "severe-icon";
                    break;
            }
        }

        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withTextAnchor(TextAnchor.CENTER)
                .withIconSize(iconSize)
                .withIconImage(iconName)
                .withPoint(point);
        pointAnnotationManager.create(pointAnnotationOptions);
    }




    @SuppressLint("MissingPermission")
    private void fetchRoute(Point point) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(mapdisplay.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location == null) {
                    handleRouteFailure("Failed to get current location");
                    return;
                }

                setRoute.setEnabled(false);
                setRoute.setText("Fetching route...");
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point))
                        .alternatives(true)
                        .profile(DirectionsCriteria.PROFILE_DRIVING)
                        .bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        handleRoutesReady(list);
                    }

                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        handleRouteFailure("Route request failed");
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {
                        handleRouteFailure("Route request canceled");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                handleRouteFailure("Failed to get location: " + exception.getMessage());
            }
        });
    }

    private void handleRoutesReady(@NonNull List<NavigationRoute> list) {
        mapboxNavigation.setNavigationRoutes(list);
        checkedRoute = list.get(0);

        // Filter and display potholes based on selected types
        pointAnnotationManager.deleteAll();
        for (LocationRetriever.Quadruple<Double, Double, String, String> location : potholeLocations) {
            if (selectedPotholeTypes.contains(location.getThird())) {
                Point potholePoint = Point.fromLngLat(location.getSecond(), location.getFirst());
                if (isPointOnRoute2(potholePoint, checkedRoute)) {
                    addPotholeIcon(location); // Pass the Quadruple object directly
                }
            }
        }

        focusLocationBtn.performClick();
        setRoute.setEnabled(true);
        isRouteActive = true;
        setRoute.setText("Clear route");
        maneuverView.setVisibility(View.VISIBLE);
        findViewById(R.id.search_bar).setVisibility(View.GONE);

        addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull Point point) {
                handleMapClick(point, list);
                return true;
            }
        });
    }

    private void handleMapClick(@NonNull Point point, @NonNull List<NavigationRoute> list) {
        int index = 1;
        if (list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                alternativeRoute = list.get(i);
                if (isPointOnRoute(point, alternativeRoute)) {
                    index = i;
                    break;
                }
            }
            list.remove(index);
            list.add(0, alternativeRoute);
        }

        mapboxNavigation.setNavigationRoutes(list);
        checkedRoute = alternativeRoute;
        pointAnnotationManager.deleteAll();

        // Filter and display potholes based on selected types
        for (LocationRetriever.Quadruple<Double, Double, String, String> location : potholeLocations) {
            if (selectedPotholeTypes.contains(location.getThird())) {
                Point potholePoint = Point.fromLngLat(location.getSecond(), location.getFirst());
                if (isPointOnRoute2(potholePoint, checkedRoute)) {
                    addPotholeIcon(location); // Pass the Quadruple object directly
                }
            }
        }

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRouteActive) {
                    fetchRoute(destination);
                } else {
                    isRouteActive = false;
                    mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                    setRoute.setText("Set route");
                    maneuverView.setVisibility(View.GONE);
                    findViewById(R.id.search_bar).setVisibility(View.VISIBLE); // Hide the maneuver view
                }
            }
        });
    }

    private void handleRouteFailure(String message) {
        setRoute.setEnabled(true);
        setRoute.setText("Set route");
        Toast.makeText(mapdisplay.this, message, Toast.LENGTH_SHORT).show();
        Log.e("fetchRoute", message);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry getting the current location
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                }
            } else {
                Toast.makeText(this, "Location permission is required to display speed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxNavigation != null) {
            mapboxNavigation.onDestroy();
        }
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        unregisterReceiver(potholeReceiver);
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}