package com.example.pothole.MapScreen;
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
import android.widget.Toast;
import android.Manifest;

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

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class mapdisplay extends AppCompatActivity {
    private void showPotholeInfoDialog(Point point) {
        // Find the pothole information based on the point
        for (Pair<Double, Double> location : potholeLocations) {
            if (location.first == point.latitude() && location.second == point.longitude()) {
                // Create and show the AlertDialog with pothole information
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pothole Information");
                builder.setMessage("Latitude: " + location.first + "\nLongitude: " + location.second);
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
    private List<Pair<Double, Double>> potholeLocations;
    private DatabaseReference database;
    private MapboxManeuverView maneuverView;
    private MapboxManeuverApi maneuverApi;


    // duong dan
    List<Point> points=new ArrayList<>();


    private boolean notificationShown = false;


    //
    Bitmap bitmap;
    Bitmap bitmap2;
    //map compoment
    private MapboxRouteArrowApi routeArrowApi;
    private MapboxRouteArrowView routeArrowView;



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
    private void checkProximityToPothole(Point userLocation, Point pothole) {
        double distance = TurfMeasurement.distance(userLocation, pothole);
        double thresholdDistance = 0.10; // 100 meters
        if (distance < thresholdDistance && !notificationShown) {
            showNotification("Pothole Alert", "Slow down, you are approaching a pothole." );
            Toast.makeText(mapdisplay.this, "Pothole 100 meters ahead", Toast.LENGTH_SHORT).show();
            notificationShown = true;

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
                for (Pair<Double, Double> location : potholeLocations) {
                    Point potholePoint = Point.fromLngLat(location.second, location.first);
                    if (isPointOnRoute2(potholePoint, checkedRoute)) {
                        checkProximityToPothole(point, potholePoint);
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapdisplay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
            public void onLocationsRetrieved(List<Pair<Double, Double>> locations) {
                // Log the retrieved locations
                if (locations.isEmpty()) {
                    Log.d(TAG, "No locations retrieved from local storage.");
                } else {
                    Log.d(TAG, "Retrieved " + locations.size() + " locations from local storage.");
                    for (Pair<Double, Double> location : locations) {
                        Log.d(TAG, "Latitude: " + location.first + ", Longitude: " + location.second);
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
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onPositionChangedListener);
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
                for (Pair<Double, Double> location : potholeLocations) {
                    Point point = Point.fromLngLat(location.second, location.first);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution);
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconSize(0.05)
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
                                .withPoint(placeAutocompleteSuggestion.getCoordinate());
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
    private void addPotholeIcon(Point potholePoint) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.potholecaution); // Replace with your icon resource
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withTextAnchor(TextAnchor.CENTER)
                .withIconSize(0.05)
                .withPoint(potholePoint)
                .withIconImage(bitmap2);
        pointAnnotationManager.create(pointAnnotationOptions);
    }



    @SuppressLint("MissingPermission")
    private void fetchRoute(Point point) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(mapdisplay.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                setRoute.setEnabled(false);
                setRoute.setText("Fetching route...");
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point));



                builder.alternatives(true);

                builder.profile(DirectionsCriteria.PROFILE_DRIVING);
                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {


                        mapboxNavigation.setNavigationRoutes(list);
                        checkedRoute = list.get(0);
                        for (Pair<Double, Double> location : potholeLocations) {
                            Point potholePoint = Point.fromLngLat(location.second, location.first);
                            if (isPointOnRoute(potholePoint, checkedRoute)) {
                                addPotholeIcon(potholePoint);
                            }
                        }

//                        if(isPointOnRoute2(pothole3,checkedRoute)){
//                            Toast.makeText(mapdisplay.this, "Pothole detected", Toast.LENGTH_SHORT).show();
//                            PointAnnotationOptions potholeAnnotationOptions3 = new PointAnnotationOptions()
//                                    .withTextAnchor(TextAnchor.CENTER)
//                                    .withIconImage(bitmap)
//                                    .withPoint(pothole3);
//                            pointAnnotationManager.create(potholeAnnotationOptions3);
//
//
//                        }




                        focusLocationBtn.performClick();
                        setRoute.setEnabled(true);


                        isRouteActive = true;

                        setRoute.setText("Clear route");
                        maneuverView.setVisibility(View.VISIBLE);
                        findViewById(R.id.search_bar).setVisibility(View.GONE);
                        int potholeonRoute2 = 0;
                        for (Pair<Double, Double> location : potholeLocations)
                        {
                            if(isPointOnRoute2(Point.fromLngLat(location.second, location.first),checkedRoute)){
                                potholeonRoute2++;
                                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap2)
                                        .withIconSize(0.05)
                                        .withPoint(Point.fromLngLat(location.second, location.first));

                                pointAnnotationManager.create(pointAnnotationOptions);
                            }
                        }
                        Toast.makeText(mapdisplay.this, "Number of potholes on route: " + potholeonRoute2, Toast.LENGTH_SHORT).show();

                        addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull Point point) {
                                int index=1;
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
                                checkedRoute=alternativeRoute;
                                pointAnnotationManager.deleteAll();

                                int potholeonRoute = 0;
                                for (Pair<Double, Double> location : potholeLocations)
                                {
                                    if(isPointOnRoute2(Point.fromLngLat(location.second, location.first),checkedRoute)){
                                        potholeonRoute++;
                                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap2)
                                                .withIconSize(0.05)
                                                .withPoint(Point.fromLngLat(location.second, location.first));

                                        pointAnnotationManager.create(pointAnnotationOptions);
                                    }
                                }
                                Toast.makeText(mapdisplay.this, "Number of potholes on route: " + potholeonRoute, Toast.LENGTH_SHORT).show();

//                                if(isPointOnRoute2(pothole2,checkedRoute)){
//                                    Toast.makeText(mapdisplay.this, "Pothole detected", Toast.LENGTH_SHORT).show();
//                                    PointAnnotationOptions potholeAnnotationOptions2 = new PointAnnotationOptions()
//                                            .withTextAnchor(TextAnchor.CENTER)
//                                            .withIconImage(bitmap)
//                                            .withPoint(pothole2);
//                                    pointAnnotationManager.create(potholeAnnotationOptions2);
//
//
//
//                                }
//                                if(isPointOnRoute2(pothole3,checkedRoute)){
//                                    Toast.makeText(mapdisplay.this, "Pothole detected", Toast.LENGTH_SHORT).show();
//                                    PointAnnotationOptions potholeAnnotationOptions3 = new PointAnnotationOptions()
//                                            .withTextAnchor(TextAnchor.CENTER)
//                                            .withIconImage(bitmap)
//                                            .withPoint(pothole3);
//                                    pointAnnotationManager.create(potholeAnnotationOptions3);
//
//
//                                }
//
//
//
//                                if(isPointOnRoute2(pothole,checkedRoute)){
//                                    Toast.makeText(mapdisplay.this, "Pothole detected", Toast.LENGTH_SHORT).show();
//                                    PointAnnotationOptions potholeAnnotationOptions2 = new PointAnnotationOptions()
//                                            .withTextAnchor(TextAnchor.CENTER)
//                                            .withIconImage(bitmap)
//                                            .withPoint(pothole);
//                                    pointAnnotationManager.create(potholeAnnotationOptions2);
//
//
//                                }






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
                                            findViewById(R.id.search_bar).setVisibility(View.VISIBLE);// Hide the maneuver view
                                        }
                                    }
                                });
                                return true;
                            }
                        });

                    }







                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        setRoute.setEnabled(true);
                        setRoute.setText("Set route");
                        Toast.makeText(mapdisplay.this, "Route request failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxNavigation != null) {
            mapboxNavigation.onDestroy();
        }
        //haihh them
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        unregisterReceiver(potholeReceiver);
    }
}
