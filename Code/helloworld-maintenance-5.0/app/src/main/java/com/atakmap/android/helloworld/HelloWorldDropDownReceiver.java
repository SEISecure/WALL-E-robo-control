
package com.atakmap.android.helloworld;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import com.atakmap.android.helloworld.widgets.SeekBarControl_SEI.Subject;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.*;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.contact.Connector;
import com.atakmap.android.contact.Contact;
import com.atakmap.android.contact.Contacts;
import com.atakmap.android.contact.IndividualContact;
import com.atakmap.android.contact.IpConnector;
import com.atakmap.android.contact.PluginConnector;
import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.cot.detail.SensorDetailHandler;
import com.atakmap.android.drawing.mapItems.DrawingCircle;
import com.atakmap.android.drawing.mapItems.DrawingEllipse;
import com.atakmap.android.drawing.mapItems.DrawingRectangle;
import com.atakmap.android.drawing.mapItems.DrawingShape;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.emergency.tool.EmergencyManager;
import com.atakmap.android.emergency.tool.EmergencyType;
import com.atakmap.android.helloworld.heatmap.SimpleHeatMapLayer;
import com.atakmap.android.helloworld.layers.LayerDownloadExample;
import com.atakmap.android.helloworld.navstack.NavigationStackDropDown;
import com.atakmap.android.helloworld.plugin.R;
import com.atakmap.android.helloworld.samplelayer.ExampleLayer;
import com.atakmap.android.helloworld.samplelayer.ExampleMultiLayer;
import com.atakmap.android.helloworld.samplelayer.GLExampleLayer;
import com.atakmap.android.helloworld.samplelayer.GLExampleMultiLayer;
import com.atakmap.android.helloworld.speechtotext.SpeechBloodHound;
import com.atakmap.android.helloworld.speechtotext.SpeechBrightness;
import com.atakmap.android.helloworld.speechtotext.SpeechDetailOpener;
import com.atakmap.android.helloworld.speechtotext.SpeechItemRemover;
import com.atakmap.android.helloworld.speechtotext.SpeechLinker;
import com.atakmap.android.helloworld.speechtotext.SpeechNavigator;
import com.atakmap.android.helloworld.speechtotext.SpeechNineLine;
import com.atakmap.android.helloworld.speechtotext.SpeechPointDropper;
import com.atakmap.android.helloworld.speechtotext.SpeechToActivity;
import com.atakmap.android.helloworld.widgets.SeekBarControl_SEI;
import com.atakmap.android.icons.UserIcon;
import com.atakmap.android.image.quickpic.QuickPicReceiver;
import com.atakmap.android.importexport.CotEventFactory;
import com.atakmap.android.importfiles.sort.ImportMissionPackageSort.ImportMissionV1PackageSort;
import com.atakmap.android.importfiles.sort.ImportResolver;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.DefaultMapGroup;
import com.atakmap.android.maps.Ellipse;
import com.atakmap.android.maps.MapActivity;
import com.atakmap.android.maps.MapComponent;
import com.atakmap.android.maps.MapData;
import com.atakmap.android.maps.MapEvent;
import com.atakmap.android.maps.MapEventDispatcher;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.MapView.RenderStack;
import com.atakmap.android.maps.Marker;
import com.atakmap.android.maps.MultiPolyline;
import com.atakmap.android.maps.PointMapItem;
import com.atakmap.android.maps.PointMapItem.OnPointChangedListener;
import com.atakmap.android.maps.RootMapGroup;
import com.atakmap.android.maps.SensorFOV;
import com.atakmap.android.maps.Shape;
import com.atakmap.android.menu.PluginMenuParser;
import com.atakmap.android.missionpackage.file.MissionPackageBuilder;
import com.atakmap.android.missionpackage.file.MissionPackageManifest;
import com.atakmap.android.preference.AtakPreferences;
import com.atakmap.android.routes.Route;
import com.atakmap.android.routes.RouteMapComponent;
import com.atakmap.android.routes.RouteMapReceiver;
import com.atakmap.android.toolbar.widgets.TextContainer;
import com.atakmap.android.user.PlacePointTool;
import com.atakmap.android.util.ATAKUtilities;
import com.atakmap.android.util.AbstractMapItemSelectionTool;
import com.atakmap.android.util.Circle;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.comms.CotServiceRemote;
import com.atakmap.comms.CotStreamListener;
import com.atakmap.comms.NetConnectString;
import com.atakmap.comms.TAKServer;
import com.atakmap.coremap.conversions.CoordinateFormat;
import com.atakmap.coremap.conversions.CoordinateFormatUtilities;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.coords.GeoCalculations;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.coords.GeoPointMetaData;
import com.atakmap.coremap.maps.time.CoordinatedTime;
import com.atakmap.map.CameraController;
import com.atakmap.map.layer.feature.Feature;
import com.atakmap.map.layer.opengl.GLLayerFactory;
import com.javacodegeeks.android.contentprovidertest.BirthProvider;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.UUID;

/**
 * The DropDown Receiver should define the visual experience
 * that a user might have while using this plugin.   At a
 * basic level, the dropdown can be a view of your own design
 * that is inflated.   Please be wary of the type of context
 * you use.   As noted in the Map Component, there are two
 * contexts - the plugin context and the atak context.
 * When using the plugin context - you cannot build thing or
 * post things to the ui thread.   You use the plugin context
 * to lookup resources contained specifically in the plugin.
 */
public class HelloWorldDropDownReceiver extends DropDownReceiver implements
        OnStateListener, SensorEventListener, Subject {

//    private final NotificationManager nm;

    public static final String TAG = "Project-WALL-E";

    public static final String SHOW_HELLO_WORLD = "com.atakmap.android.helloworld.SHOW_HELLO_WORLD";
    public static final String CHAT_HELLO_WORLD = "com.atakmap.android.helloworld.CHAT_HELLO_WORLD";
    public static final String SEND_HELLO_WORLD = "com.atakmap.android.helloworld.SEND_HELLO_WORLD";
    public static final String LAYER_DELETE = "com.atakmap.android.helloworld.LAYER_DELETE";
    public static final String LAYER_VISIBILITY = "com.atakmap.android.helloworld.LAYER_VISIBILITY";
    private final View helloView;

    private final Context pluginContext;
    private final Contact helloContact;
    private RouteEventListener routeEventListener = null;
    private final HelloWorldMapOverlay mapOverlay;
//    private final RecyclerViewDropDown recyclerView;
//    private final TabViewDropDown tabView;
    private NavigationStackDropDown navstackView;

    // example menu factory
//    final MenuFactory menuFactory;

    // inspection map selector
    final InspectionMapItemSelectionTool imis;

    private Timer issTimer = null;


    private ExampleLayer exampleLayer;
    private final Map<Integer, ExampleMultiLayer> exampleMultiLayers = new HashMap<>();
    private SimpleHeatMapLayer simpleHeatMapLayer;

    private final JoystickListener _joystickView;

    private LayerDownloadExample layerDownloader;

    private double currWidth = HALF_WIDTH;
    private double currHeight = HALF_HEIGHT;


    private final CameraActivity.CameraDataListener cdl = new CameraActivity.CameraDataListener();
    private final CameraActivity.CameraDataReceiver cdr = new CameraActivity.CameraDataReceiver() {
        public void onCameraDataReceived(Bitmap b) {
            Log.d(TAG, "==========img received======>" + b);
            b.recycle();
        }
    };

    private final SpeechToTextActivity.SpeechDataListener sd1 = new SpeechToTextActivity.SpeechDataListener();
    private final SpeechToTextActivity.SpeechDataReceiver sdr = new SpeechToTextActivity.SpeechDataReceiver() {
        public void onSpeechDataReceived(HashMap<String, String> s) {
            Log.d(TAG, "==========speech======>" + s);
            createSpeechMarker(s);

        }
    };
    /**
     * This receives the intent from SpeechToActivity.
     * It uses the info from the activityInfoBundle to decide
     * what to do next. The bundle always contains a destination
     * and an activity intent. Other stuff is added on a case-by-case basis.
     * See SpeechToActivity for more details.
     */
    private final SpeechToActivity.SpeechDataListener sd1a = new SpeechToActivity.SpeechDataListener();
    private final SpeechToActivity.SpeechDataReceiver sdra = new SpeechToActivity.SpeechDataReceiver() {
        /**
         * This receives the activityInfoBundle from SpeechToActivity. The switch case decides what classes to call
         * @param activityInfoBundle - Bundle containing the activity intent, destination, origin, marker type and more.
         */
        public void onSpeechDataReceived(Bundle activityInfoBundle) {
            MapView view = getMapView();
            switch (activityInfoBundle
                    .getInt(SpeechToActivity.ACTIVITY_INTENT)) {
                //This case is for drawing and navigating routes
                case SpeechToActivity.NAVIGATE_INTENT:
                    new SpeechNavigator(view,
                            activityInfoBundle
                                    .getString(SpeechToActivity.DESTINATION),
                            activityInfoBundle
                                    .getBoolean(SpeechToActivity.QUICK_INTENT));
                    break;
                // This case is for plotting down markers
                case SpeechToActivity.PLOT_INTENT:
                    new SpeechPointDropper(
                            activityInfoBundle
                                    .getString(SpeechToActivity.DESTINATION),
                            view, pluginContext);
                    break;
                //This case is for bloodhounding to markers,routes, or addresses
                case SpeechToActivity.BLOODHOUND_INTENT:
                    new SpeechBloodHound(view,
                            activityInfoBundle
                                    .getString(SpeechToActivity.DESTINATION),
                            pluginContext);
                    break;
                //This case is for launching the 9 Line window on a target
                case SpeechToActivity.NINE_LINE_INTENT:
                    new SpeechNineLine(
                            activityInfoBundle
                                    .getString(SpeechToActivity.DESTINATION),
                            view, pluginContext);
                    break;
                //DOESNT WORK//This case is to open the compass on your self marker
                case SpeechToActivity.COMPASS_INTENT:
                    AtakBroadcast.getInstance()
                            .sendBroadcast(new Intent()
                                    .setAction(
                                            "com.atakmap.android.maps.COMPASS")
                                    .putExtra("targetUID",
                                            view.getSelfMarker().getUID()));
                    break;
                //This case toggles the brightness slider
                case SpeechToActivity.BRIGHTNESS_INTENT:
                    new SpeechBrightness(view, pluginContext, activityInfoBundle
                            .getString(SpeechToActivity.DESTINATION));
                    break;
                //this case deletes a shape, marker, or route
                case SpeechToActivity.DELETE_INTENT:
                    new SpeechItemRemover(
                            activityInfoBundle
                                    .getString(SpeechToActivity.DESTINATION),
                            view, pluginContext);
                    break;
                //this case opens the hostiles window from fire tools
                case SpeechToActivity.SHOW_HOSTILES_INTENT:
                    AtakBroadcast.getInstance()
                            .sendBroadcast(new Intent().setAction(
                                    "com.atakmap.android.maps.MANAGE_HOSTILES"));
                    break;
                //This case opens a markers detail menu
                case SpeechToActivity.OPEN_DETAILS_INTENT:
                    new SpeechDetailOpener(activityInfoBundle
                            .getString(SpeechToActivity.DESTINATION), view);
                    break;
                //this case starts an emergency
                case SpeechToActivity.EMERGENCY_INTENT:
                    EmergencyManager.getInstance()
                            .setEmergencyType(EmergencyType.fromDescription(
                                    activityInfoBundle.getString(
                                            SpeechToActivity.EMERGENCY_TYPE)));
                    EmergencyManager.getInstance().initiateRepeat(
                            EmergencyType.fromDescription(
                                    activityInfoBundle.getString(
                                            SpeechToActivity.EMERGENCY_TYPE)),
                            false);
                    EmergencyManager.getInstance().setEmergencyOn(true);
                    break;
                //This case draws a R&B line between 2 map items
                case SpeechToActivity.LINK_INTENT:
                    new SpeechLinker(
                            activityInfoBundle
                                    .getString(SpeechToActivity.DESTINATION),
                            view, pluginContext);
                    break;
                //This case launches the camera
                case SpeechToActivity.CAMERA_INTENT:
                    AtakBroadcast.getInstance().sendBroadcast(
                            new Intent().setAction(QuickPicReceiver.QUICK_PIC));
                    break;
                default:
                    Toast.makeText(getMapView().getContext(),
                            "I did not understand please try again",
                            Toast.LENGTH_SHORT).show();

            }
        }
    };

    private final CotServiceRemote csr;
    private boolean connected = false;

    final CotServiceRemote.ConnectionListener cl = new CotServiceRemote.ConnectionListener() {
        @Override
        public void onCotServiceConnected(Bundle fullServiceState) {
            Log.d(TAG, "onCotServiceConnected: ");
            connected = true;
        }

        @Override
        public void onCotServiceDisconnected() {
            Log.d(TAG, "onCotServiceDisconnected: ");
            connected = false;
        }

    };

    final CotStreamListener csl;
    final CotServiceRemote.OutputsChangedListener _outputsChangedListener = new CotServiceRemote.OutputsChangedListener() {
        @Override
        public void onCotOutputRemoved(Bundle descBundle) {
            Log.d(TAG, "stream removed");
        }

        @Override
        public void onCotOutputUpdated(Bundle descBundle) {
            Log.v(TAG,
                    "Received ADD message for "
                            + descBundle
                                    .getString(TAKServer.DESCRIPTION_KEY)
                            + ": enabled="
                            + descBundle.getBoolean(
                                    TAKServer.ENABLED_KEY, true)
                            + ": connected="
                            + descBundle.getBoolean(
                                    TAKServer.CONNECTED_KEY, false));
        }
    };

//    private final LRFPointTool lrfPointTool;
//    private final LRFPointTool.LRFCallback lrfCallback = new LRFPointTool.LRFCallback() {
//        @Override
//        public void onResults(double distance, double azimuth, double inclination, boolean success) {
//            Log.d(TAG, distance + ", " +
//                            azimuth + ", " +
//                            inclination +
//                            " point from self: " +
//                                   GeoCalculations.pointAtDistance(getMapView().getSelfMarker().getPoint(),
//                                           distance, azimuth, inclination));
//            getMapView().post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getMapView().getContext(), distance + ", " +
//                            azimuth + ", " + inclination, Toast.LENGTH_SHORT).show();
//                    helloView.findViewById(R.id.lrfTool).setSelected(false);
//                }
//            });
//        }
//    };
 // SEI Sliders
    private ZMQRelay mRelay;
    private ZMQRelay cameraRelay;
    private Queue<String> commandBuffer;

    private static class SEIsavedLists implements Serializable  {
        private List<AStar.MapNode> StoredStartAndEndPoints;
        private List<AStar.MapUnit> OppositeUnits;
        public SEIsavedLists(){
            StoredStartAndEndPoints = new ArrayList<>();
            OppositeUnits = new ArrayList<>();
        }
        public List<AStar.MapNode> getStoredStartAndEndPoints(){
            if (StoredStartAndEndPoints == null)  StoredStartAndEndPoints = new ArrayList<>();
            return StoredStartAndEndPoints;
        }
        public List<AStar.MapUnit> getOppositeUnits(){
            if (OppositeUnits == null)  OppositeUnits = new ArrayList<>();
            return OppositeUnits;
        }
        public void setStoredStartAndEndPoints(List<AStar.MapNode> input){
            if (input != null) {
                StoredStartAndEndPoints.addAll(input);
            }
        }
        public void setOppositeUnits(List<AStar.MapUnit> input){
            if (input != null)  {
                OppositeUnits.addAll(input);
            }
        }
    }
    private final SEIsavedLists Data;

    String mode = "tracks";
    int throttle=1500;
    int rudder=1500;

    ZContext context;
    ZMQ.Socket socket;

    private String currCommand = "";
    private boolean sendThreadRunning = false;
    private boolean cancelSendThread = false;
    private Handler handler = new Handler();

    /**************************** CONSTRUCTOR *****************************/

    public HelloWorldDropDownReceiver(final MapView mapView,
            final Context context, HelloWorldMapOverlay overlay) {
        super(mapView);
        this.pluginContext = context;
        this.Data = new SEIsavedLists();

        this.mapOverlay = overlay;
        final Activity parentActivity = (Activity) mapView.getContext();

        _joystickView = new JoystickListener();

        csr = new CotServiceRemote();
        csr.setOutputsChangedListener(_outputsChangedListener);

        csr.connect(cl);

        imis = new InspectionMapItemSelectionTool();

        csl = new CotStreamListener(mapView.getContext(), TAG, null) {
            @Override
            public void onCotOutputRemoved(Bundle bundle) {
                Log.d(TAG, "stream outputremoved");
            }

            @Override
            protected void enabled(TAKServer port,
                    boolean enabled) {
                Log.d(TAG, "stream enabled");
            }

            @Override
            protected void connected(TAKServer port,
                    boolean connected) {
                Log.d(TAG, "stream connected");
            }

            @Override
            public void onCotOutputUpdated(Bundle descBundle) {
                Log.d(TAG, "stream added/updated");
            }

        };

        printNetworks();

        AtakBroadcast.DocumentedIntentFilter dif = new AtakBroadcast.DocumentedIntentFilter(
                "com.atakmap.android.helloworld.FAKE_PHONE_CALL");
        AtakBroadcast.getInstance().registerReceiver(fakePhoneCallReceiver,
                dif);

        // If you are using a custom layout you need to make use of the PluginLayoutInflator to clear
        // out the layout cache so that the plugin can be properly unloaded and reloaded.

        //set up for relay related actions
        commandBuffer = new PriorityQueue<>();
        helloView = PluginLayoutInflater.inflate(pluginContext,
                R.layout.hello_world_layout, null);
        cameraRelay = new ZMQRelay(commandBuffer, (ImageView) helloView.findViewById(R.id.cameraImage));
        mRelay = new ZMQRelay(null, null);
        toast("START UP RELAYS  ");


        // Add "Hello World" contact
        this.helloContact = addPluginContact(pluginContext.getString(
                R.string.hello_world));

        TextView infoText = helloView.findViewById(R.id.infoText);

        TextView modeText = helloView.findViewById(R.id.modeText);
        modeText.setText("Current Mode: "+mRelay.mode);
        //modeText.setText("Current Mode: "+mRelay.mode);

        EditText ipBox = helloView.findViewById(R.id.IpBox);
        ipBox.setText("192.168.100.148:11994");

        connectToIP();

        //Find buttons by id and implement code for long click
        View.OnLongClickListener longClickListener = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v){
                int id = v.getId();
                if (id == R.id.buttonForward) {
                    //toast(context.getString(R.string.newAddedButton));
                } else if (id == R.id.buttonBack) {
                    //toast(context.getString(R.string.newAddUnits));
                } else if (id == R.id.buttonLeft) {
                    //toast(context.getString(R.string.newSaveUnits));
                } else if (id == R.id.buttonRight) {
                    //toast(context.getString(R.string.velocity_selector));
                    //} else if (id == R.id.cover_selector) {
                    //    toast(context.getString(R.string.cover_selector));
                } else if (id == R.id.buttonMode) {
                    toast("swaps modes between tracks and screws");
                }
                return true;
            }
        };

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //toast("action in");
                int id = view.getId();
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    infoText.setText("Button Pressed");

                    if (id == R.id.buttonForward) {
                        toast("forward");
                        currCommand = "forward";
                        //mRelay.sendMSG("forward");
                        //sendMSG("forward");
                    } else if (id == R.id.buttonBack) {
                        toast("back");
                        currCommand = "back";
                        //mRelay.sendMSG("back");
                        //sendMSG("back");
                    } else if (id == R.id.buttonLeft) {
                        toast("left");
                        currCommand = "turnL";
                        //mRelay.sendMSG("turnL");
                        //sendMSG("turnL");
                    } else if (id == R.id.buttonRight) {
                        toast("right");
                        currCommand = "turnR";
                        //mRelay.sendMSG("turnR");
                        //sendMSG("turnR");
                    }

                    handleDeleteDown();
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    infoText.setText("Hold Buttons for Robot Movement");
                    handleSendUp();
                }

                return true;
            }
        };

        View.OnGenericMotionListener joystickListner = new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {

                // Check that the event came from a game controller
                if ((motionEvent.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                        InputDevice.SOURCE_JOYSTICK &&
                        motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    // Process all historical movement samples in the batch
                    final int historySize = motionEvent.getHistorySize();

                    // Process the movements starting from the
                    // earliest historical position in the batch
                    for (int i = 0; i < historySize; i++) {
                        // Process the event at historical position i

                        mRelay.sendJoystickMSG(motionEvent,i);
                        //processJoystickInput(event, i);
                    }

                    // Process the current movement sample in the batch (position -1)
                    //processJoystickInput(event, -1);
                    mRelay.sendJoystickMSG(motionEvent,-1);
                    return true;
                }

                return true;
            }
        };

        /* ************************ Start SEI Buttons *********************** */

        final Button forwardButton = helloView.findViewById(R.id.buttonForward);

        final Button backButton = helloView.findViewById(R.id.buttonBack);

        final Button leftButton = helloView.findViewById(R.id.buttonLeft);

        final Button rightButton = helloView.findViewById(R.id.buttonRight);

        /*
        final Button coverButton = helloView.findViewById(R.id.cover_selector);
        coverButton.setOnClickListener(v -> {

            if (!this.Cover_seekBarControl.initialized) this.Cover_seekBarControl.Initialize(R.id.cover_seek_bar_control);
            if (this.Cover_seekBarControl._showingControl) {
                this.Cover_seekBarControl.dismiss();
                this.Cover_seekBarControl._showingControl = false;
            } else {
                this.Cover_seekBarControl.show(this, 500000);
                this.Cover_seekBarControl._showingControl = true;
            }
        });
        */
        final Button modeButton = helloView.findViewById(R.id.buttonMode);
        modeButton.setOnClickListener(v -> {

            //uses mRelay.mode
            /*
            if (mRelay.mode.equals("tracks")){
                mRelay.mode = "screw";
            }else{
                mRelay.mode = "tracks";
            }
            modeText.setText("Current Mode: "+mRelay.mode);
            */

            if (mode.equals("tracks")){
                mode = "screw";
            }else{
                mode = "tracks";
            }

            modeText.setText("Current Mode: "+mode);
            sendMSG("change Mode");
        });

        final Button connectButton = helloView.findViewById(R.id.connectButton);
        connectButton.setOnClickListener(v -> {

            toast("change Connection");
            connectToIP();
        });

        /** ************************* End SEI Buttons ************************* **/

        /*
        forwardButton.setOnLongClickListener(longClickListener);
        newAddUnits.setOnLongClickListener(longClickListener);
        newSaveUnits.setOnLongClickListener(longClickListener);
        velocityButton.setOnLongClickListener(longClickListener);
         */
        modeButton.setOnLongClickListener(longClickListener);

        forwardButton.setOnTouchListener(touchListener);
        backButton.setOnTouchListener(touchListener);
        leftButton.setOnTouchListener(touchListener);
        rightButton.setOnTouchListener(touchListener);

        //generic motion listener function for view.
        helloView.setOnGenericMotionListener(joystickListner);

    }


    /**
     * This class makes use of a compact class to aid with the selection of map items.   Prior to
     * 3.12, this all had to be manually done playing with the dispatcher and listening for map
     * events.
     */
    public class InspectionMapItemSelectionTool
            extends AbstractMapItemSelectionTool {
        public InspectionMapItemSelectionTool() {
            super(getMapView(),
                    "com.atakmap.android.helloworld.InspectionMapItemSelectionTool",
                    "com.atakmap.android.helloworld.InspectionMapItemSelectionTool.Finished",
                    "Select Map Item on the screen",
                    "Invalid Selection");
        }

        @Override
        protected boolean isItem(MapItem mi) {
            return true;
        }

    }

//    final BroadcastReceiver inspectionReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            AtakBroadcast.getInstance().unregisterReceiver(this);
//            final Button itemInspect = helloView
//                    .findViewById(R.id.itemInspect);
//            itemInspect.setSelected(false);
//
//            String uid = intent.getStringExtra("uid");
//            if (uid == null)
//                return;
//
//            MapItem mi = getMapView().getMapItem(uid);
//
//            if (mi == null)
//                return;
//
//            Log.d(TAG, "class: " + mi.getClass());
//            Log.d(TAG, "type: " + mi.getType());
//
//            final CotEvent cotEvent = CotEventFactory
//                    .createCotEvent(mi);
//
//            String val;
//            if (cotEvent != null)
//                val = cotEvent.toString();
//            else if (mi.hasMetaValue("nevercot"))
//                val = "map item set to never persist (nevercot)";
//            else
//                val = "error turning a map item into CoT";
//
//            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
//                    getMapView().getContext());
//            TextView showText = new TextView(getMapView().getContext());
//            showText.setText(val);
//            showText.setTextIsSelectable(true);
//            showText.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    // Copy the Text to the clipboard
//                    ClipboardManager manager = (ClipboardManager) getMapView()
//                            .getContext()
//                            .getSystemService(Context.CLIPBOARD_SERVICE);
//                    TextView showTextParam = (TextView) v;
//                    manager.setText(showTextParam.getText());
//                    Toast.makeText(v.getContext(),
//                            "copied the data", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });
//
//            builderSingle.setTitle("Resulting CoT");
//            builderSingle.setView(showText);
//            builderSingle.show();
//        }
//    };

    synchronized public void runSim() {
        Marker item = getMapView().getSelfMarker();
        if (item != null) {

            final MapData data = getMapView().getMapData();

            GeoPoint gp = new GeoPoint(-44.0, 22.0); // decimal degrees

            data.putDouble("mockLocationSpeed", 20); // speed in meters per second
            data.putFloat("mockLocationAccuracy", 5f); // accuracy in meters

            data.putString("locationSourcePrefix", "mock");
            data.putBoolean("mockLocationAvailable", true);

            data.putString("mockLocationSource", "Hello World Plugin");
            data.putString("mockLocationSourceColor", "#FFAFFF00");
            data.putBoolean("mockLocationCallsignValid", true);

            data.putParcelable("mockLocation", gp);

            data.putLong("mockLocationTime", SystemClock.elapsedRealtime());

            data.putLong("mockGPSTime",
                    new CoordinatedTime().getMilliseconds()); // time as reported by the gps device

            data.putInt("mockFixQuality", 2);

            Intent gpsReceived = new Intent();

            gpsReceived
                    .setAction("com.atakmap.android.map.WR_GPS_RECEIVED");
            AtakBroadcast.getInstance().sendBroadcast(gpsReceived);

            Log.d(TAG,
                    "received gps for: " + gp
                            + " with a fix quality: " + 2 +
                            " setting last seen time: "
                            + data.getLong("mockLocationTime"));

        }

    }

    /**
     * Slower of the two methods to create a circle, but more accurate.
     */
    public void createEllipse(final Marker marker) {
        final Ellipse _accuracyEllipse = new Ellipse(UUID.randomUUID()
                .toString());
        _accuracyEllipse.setCenterHeightWidth(marker.getGeoPointMetaData(), 20,
                20);
        _accuracyEllipse.setFillColor(Color.argb(50, 238, 187, 255));
        _accuracyEllipse.setFillStyle(2);
        _accuracyEllipse.setStrokeColor(Color.GREEN);
        _accuracyEllipse.setStrokeWeight(4);
        _accuracyEllipse.setMetaString("shapeName", "Error Ellipse");
        _accuracyEllipse.setMetaBoolean("addToObjList", false);
        getMapView().getRootGroup().addItem(_accuracyEllipse);
        marker.addOnPointChangedListener(new OnPointChangedListener() {
            @Override
            public void onPointChanged(final PointMapItem item) {
                _accuracyEllipse.setCenterHeightWidth(
                        item.getGeoPointMetaData(), 20, 20);
            }
        });
        MapEventDispatcher dispatcher = getMapView().getMapEventDispatcher();
        dispatcher.addMapEventListener(MapEvent.ITEM_REMOVED,
                new MapEventDispatcher.MapEventDispatchListener() {
                    public void onMapEvent(MapEvent event) {
                        if (event.getType().equals(MapEvent.ITEM_REMOVED)) {
                            MapItem item = event.getItem();
                            if (item.getUID().equals(marker.getUID()))
                                getMapView().getRootGroup().removeItem(
                                        _accuracyEllipse);
                        }
                    }
                });

    }

    /**
     * Faster of the two methods to create a circle.
     */
    public void createCircle(final Marker marker) {
        final Circle _accuracyCircle = new Circle(marker.getGeoPointMetaData(),
                20);

        _accuracyCircle.setFillColor(Color.argb(50, 238, 187, 255));
        //_accuracyCircle.setFillStyle(2);
        _accuracyCircle.setStrokeColor(Color.GREEN);
        _accuracyCircle.setStrokeWeight(4);
        _accuracyCircle.setMetaString("shapeName", "Error Ellipse");
        _accuracyCircle.setMetaBoolean("addToObjList", false);

        getMapView().getRootGroup().addItem(_accuracyCircle);
        marker.addOnPointChangedListener(new OnPointChangedListener() {
            @Override
            public void onPointChanged(final PointMapItem item) {
                _accuracyCircle.setCenterPoint(marker.getGeoPointMetaData());
                _accuracyCircle.setRadius(20);
            }
        });
        MapEventDispatcher dispatcher = getMapView().getMapEventDispatcher();
        dispatcher.addMapEventListener(MapEvent.ITEM_REMOVED,
                new MapEventDispatcher.MapEventDispatchListener() {
                    public void onMapEvent(MapEvent event) {
                        if (event.getType().equals(MapEvent.ITEM_REMOVED)) {
                            MapItem item = event.getItem();
                            if (item.getUID().equals(marker.getUID()))
                                getMapView().getRootGroup().removeItem(
                                        _accuracyCircle);
                        }
                    }
                });

    }

    private void manipulateFakeContentProvider() {
        // delete all the records and the table of the database provider
        String URL = "content://com.javacodegeeks.provider.Birthday/friends";
        Uri friends = Uri.parse(URL);
        int count = pluginContext.getContentResolver().delete(
                friends, null, null);
        String countNum = "Javacodegeeks: " + count + " records are deleted.";
        toast(countNum);

        String[] names = new String[] {
                "Joe", "Bob", "Sam", "Carol"
        };
        String[] dates = new String[] {
                "01/01/2001", "01/01/2002", "01/01/2003", "01/01/2004"
        };
        for (int i = 0; i < names.length; ++i) {
            ContentValues values = new ContentValues();
            values.put(BirthProvider.NAME, names[i]);
            values.put(BirthProvider.BIRTHDAY, dates[i]);
            Uri uri = pluginContext.getContentResolver().insert(
                    BirthProvider.CONTENT_URI, values);
            toast("Javacodegeeks: " + uri + " inserted!");
        }

    }

    /**************************** PUBLIC METHODS *****************************/

    private void startSendThread() {

        Thread r = new Thread() {

            @Override
            public void run() {
                try {

                    sendThreadRunning = true;
                    while (!cancelSendThread) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendMSG(currCommand);

                            }
                        });

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(
                                    "Could not wait send.", e);
                        }
                    }
                }
                finally
                {
                    sendThreadRunning = false;
                    cancelSendThread = false;
                }
            }
        };

        // actually start the delete char thread
        r.start();
    }

    private void handleDeleteDown() {

        if (!sendThreadRunning)
            startSendThread();
    }

    private void handleSendUp() {
        cancelSendThread = true;
    }

    private Boolean sendMSG(String command){

        try {
            //toast("entered send message");

            /*ZContext context = new ZContext();

            EditText ipBox = helloView.findViewById(R.id.IpBox);
            String connectionAddress = ipBox.getText().toString();

            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            //ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.connect("tcp://"+connectionAddress);
            //socket.connect("tcp://192.168.100.148:11994");

             */

            //I HAVE ZERO BASIS FOR WHAT THESE CONTROL NUMBERS MEAN
            if (command.equals("change Mode")) {

                /* mode change happens on button activation
                if (mode.equals("tracks")) {
                    mode = "screw";
                } else {
                    mode = "tracks";
                }*/

                throttle = 0;
                rudder = 0;
            }
            else if (command.equals("forward")) {
                throttle = 1500;
                rudder = 0;
            }
            else if (command.equals("back")) {
                throttle = -1500;
                rudder = 0;
            }
            else if (command.equals("turnL")) {
                throttle = 500;
                rudder = -1000;
            }
            else if (command.equals("turnR")) {
                throttle = 500;
                rudder = 1000;
            }

            String msg = String.format("%s %s %s %s", "Commands" , mode, throttle, rudder );

            //Thread.sleep(1);
            int counter = 0;

            //while (!Thread.currentThread().isInterrupted()) { //does not send message without but while makes it send 4ever

                socket.sendMore("RoboCommands");
                socket.send(msg);
                //toast("message sent"+counter);

                if(counter>5){
                    //break;
                }
                counter++;
            //}

            //context.destroySocket(socket);
        }catch (Exception e){
            toast(e.toString());
            System.out.println(e.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void connectToIP(){
        if (context==null){
            context = new ZContext();
        }
        try {
            EditText ipBox = helloView.findViewById(R.id.IpBox);
            String connectionAddress = ipBox.getText().toString();

            // Socket to talk to clients
            socket = context.createSocket(SocketType.PUB);
            socket.connect("tcp://" + connectionAddress);
        }catch (Exception e){
            toast(e.toString());
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void disposeImpl() {
        // Remove Hello World contact
        removeContact(this.helloContact);
        try {
            AtakBroadcast.getInstance().unregisterReceiver(fordReceiver);
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }

        _joystickView.dispose();

        if (issTimer != null) {
            issTimer.cancel();
            issTimer.purge();
            issTimer = null;
        }

        AtakBroadcast.getInstance().unregisterReceiver(fakePhoneCallReceiver);

        SensorManager sensorManager = (SensorManager) getMapView().getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(HelloWorldDropDownReceiver.this);
        TextContainer.getTopInstance().closePrompt();

        imis.dispose();
//        if (lrfPointTool != null)
//            lrfPointTool.dispose();
//
//        // make sure we unregister, say when a new version is hot loaded ...
//        MapMenuReceiver.getInstance()
//                .unregisterMapMenuFactory(menuFactory);

        try {
            if (exampleLayer != null) {
                getMapView().removeLayer(RenderStack.MAP_SURFACE_OVERLAYS,
                        exampleLayer);
                GLLayerFactory.unregister(GLExampleLayer.SPI);
            }
            exampleLayer = null;
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        GLLayerFactory.unregister(GLExampleMultiLayer.SPI);

    }

    /**************************** INHERITED METHODS *****************************/

    @Override
    public int getValue(int ID) {
        return 0;
    }

    @Override
    public void setValue(int value, int ID) {

    }

    @Override
    public void onControlDismissed(int ID) {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "showing hello world drop down");

        final String action = intent.getAction();
        if (action == null)
            return;

        // Show drop-down
        switch (action) {
            case SHOW_HELLO_WORLD:
                if (!isClosed()) {
                    Log.d(TAG, "the drop down is already open");
                    unhideDropDown();
                    return;
                }

                showDropDown(helloView, HALF_WIDTH, FULL_HEIGHT,
                        FULL_WIDTH, HALF_HEIGHT, false, this);
                setAssociationKey("helloWorldPreference");
                List<Contact> allContacts = Contacts.getInstance()
                        .getAllContacts();
                for (Contact c : allContacts) {
                    if (c instanceof IndividualContact)
                        Log.d(TAG, "Contact IP address: "
                                + getIpAddress((IndividualContact) c));
                }

                break;

            // Chat message sent to Hello World contact
            case CHAT_HELLO_WORLD:
                Bundle cotMessage = intent.getBundleExtra(
                        ChatManagerMapComponent.PLUGIN_SEND_MESSAGE_EXTRA);

                String msg = cotMessage.getString("message");

                if (!FileSystemUtils.isEmpty(msg)) {
                    // Display toast to show the message was received
                    toast(helloContact.getName() + " received: " + msg);
                }
                break;

            // Sending CoT to Hello World contact
            case SEND_HELLO_WORLD:
                // Map item UID
                String uid = intent.getStringExtra("targetUID");
                MapItem mapItem = getMapView().getRootGroup().deepFindUID(uid);
                if (mapItem != null) {
                    // Display toast to show the CoT was received
                    toast(helloContact.getName() + " received request to send: "
                            + ATAKUtilities.getDisplayName(mapItem));
                }
                break;

            // Toggle visibility of example layer
            case LAYER_VISIBILITY: {
                Log.d(TAG,
                        "used the custom action to toggle layer visibility on: "
                                + intent
                                        .getStringExtra("uid"));
                ExampleLayer l = mapOverlay.findLayer(intent
                        .getStringExtra("uid"));
                if (l != null) {
                    l.setVisible(!l.isVisible());
                } else {
                    ExampleMultiLayer ml = mapOverlay.findMultiLayer(intent
                            .getStringExtra("uid"));
                    if (ml != null)
                        ml.setVisible(!ml.isVisible());
                }
                break;
            }

            // Delete example layer
            case LAYER_DELETE: {
                Log.d(TAG,
                        "used the custom action to delete the layer on: "
                                + intent
                                        .getStringExtra("uid"));
                ExampleLayer l = mapOverlay.findLayer(intent
                        .getStringExtra("uid"));
                if (l != null) {
                    getMapView().removeLayer(RenderStack.MAP_SURFACE_OVERLAYS,
                            l);
                } else {
                    ExampleMultiLayer ml = mapOverlay.findMultiLayer(intent
                            .getStringExtra("uid"));
                    if (ml != null)
                        getMapView().removeLayer(
                                RenderStack.MAP_SURFACE_OVERLAYS, ml);
                }
                break;
            }
        }
    }

    public NetConnectString getIpAddress(IndividualContact ic) {
        Connector ipConnector = ic.getConnector(IpConnector.CONNECTOR_TYPE);
        if (ipConnector != null) {
            String connectString = ipConnector.getConnectionString();
            return NetConnectString.fromString(connectString);
        } else {
            return null;
        }

    }

    @Override
    protected void onStateRequested(int state) {
        if (state == DROPDOWN_STATE_FULLSCREEN) {
            if (!isPortrait()) {
                if (Double.compare(currWidth, HALF_WIDTH) == 0) {
                    resize(FULL_WIDTH - HANDLE_THICKNESS_LANDSCAPE,
                            FULL_HEIGHT);
                }
            } else {
                if (Double.compare(currHeight, HALF_HEIGHT) == 0) {
                    resize(FULL_WIDTH, FULL_HEIGHT - HANDLE_THICKNESS_PORTRAIT);
                }
            }
        } else if (state == DROPDOWN_STATE_NORMAL) {
            if (!isPortrait()) {
                resize(HALF_WIDTH, FULL_HEIGHT);
            } else {
                resize(FULL_WIDTH, HALF_HEIGHT);
            }
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownClose() {

    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
        currWidth = width;
        currHeight = height;
    }

//    @Override
//    public void onDropDownClose() {
//
//        // make sure that if the Map Item inspector is running
//        // turn off the map item inspector
//        final Button itemInspect = helloView
//                .findViewById(R.id.itemInspect);
//        boolean val = itemInspect.isSelected();
//        if (val) {
//            itemInspect.setSelected(false);
//            imis.requestEndTool();
//
//        }
//
//    }

    /************************* Helper Methods *************************/

    public ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

    private RouteMapReceiver getRouteMapReceiver() {

        // TODO: this code was copied from another plugin.
        // Not sure why we can't just callRouteMapReceiver.getInstance();
        MapActivity activity = (MapActivity) getMapView().getContext();
        MapComponent mc = activity.getMapComponent(RouteMapComponent.class);
        if (mc == null || !(mc instanceof RouteMapComponent)) {
            Log.w(TAG, "Unable to find route without RouteMapComponent");
            return null;
        }

        RouteMapComponent routeComponent = (RouteMapComponent) mc;
        return routeComponent.getRouteMapReceiver();
    }

    final BroadcastReceiver fordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getMapView().getContext(),
                    "Ford Tow Truck Application", Toast.LENGTH_SHORT).show();
        }
    };

    private void toast(String str) {
        Toast.makeText(getMapView().getContext(), str,
                Toast.LENGTH_LONG).show();
    }

    public void createSpeechMarker(HashMap<String, String> s) {
        final GeoPoint mgrsPoint;
        try {
            String[] coord = new String[] {
                    s.get("numericGrid") + s.get("alphaGrid"),
                    s.get("squareID"),
                    s.get("easting"),
                    s.get("northing")
            };
            mgrsPoint = CoordinateFormatUtilities.convert(coord,
                    CoordinateFormat.MGRS);

        } catch (IllegalArgumentException e) {
            String msg = "An error has occurred getting the MGRS point";
            Log.e(TAG, msg, e);
            toast(msg);
            return;
        }

        Marker m = new Marker(mgrsPoint, UUID
                .randomUUID().toString());
        Log.d(TAG, "creating a new unit marker for: " + m.getUID());

        switch (s.get("markerType").charAt(0)) {
            case 'U':
                m.setType("a-u-G-U-C-F");
                break;
            case 'N':
                m.setType("a-n-G-U-C-F");
                break;
            case 'F':
                m.setType("a-f-G-U-C-F");
                break;
            case 'H':
            default:
                m.setType("a-h-G-U-C-F");
                break;
        }

        new Thread(new Runnable() {
            public void run() {
                CameraController.Programmatic.zoomTo(
                        getMapView().getRenderer3(),
                        .00001d, false);

                CameraController.Programmatic.panTo(
                        getMapView().getRenderer3(),
                        mgrsPoint, false);

                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }

            }
        }).start();

        m.setMetaBoolean("readiness", true);
        m.setMetaBoolean("archive", true);
        m.setMetaString("how", "h-g-i-g-o");
        m.setMetaBoolean("editable", true);
        m.setMetaBoolean("movable", true);
        m.setMetaBoolean("removable", true);
        m.setMetaString("entry", "user");
        m.setMetaString("callsign", "Speech Marker");
        m.setTitle("Speech Marker");

        MapGroup _mapGroup = getMapView().getRootGroup()
                .findMapGroup("Cursor on Target")
                .findMapGroup(s.get("markerType"));
        _mapGroup.addItem(m);

        m.persist(getMapView().getMapEventDispatcher(), null,
                this.getClass());

        Intent new_cot_intent = new Intent();
        new_cot_intent.setAction("com.atakmap.android.maps.COT_PLACED");
        new_cot_intent.putExtra("uid", m.getUID());
        AtakBroadcast.getInstance().sendBroadcast(
                new_cot_intent);
    }

    public void createAircraftWithRotation() {
        PlacePointTool.MarkerCreator mc = new PlacePointTool.MarkerCreator(
                getMapView().getPointWithElevation());
        mc.setUid(UUID.randomUUID().toString());
        mc.setCallsign("SNF");
        mc.setType("a-f-A");
        mc.showCotDetails(false);
        mc.setNeverPersist(true);
        Marker m = mc.placePoint();
        // the stle of the marker is by default set to show an arrow, this will allow for full
        // rotation.   You need to enable the heading mask as well as the noarrow mask
        m.setStyle(m.getStyle()
                | Marker.STYLE_ROTATE_HEADING_MASK
                | Marker.STYLE_ROTATE_HEADING_NOARROW_MASK);
        m.setTrack(310, 20);
        m.setMetaInteger("color", Color.YELLOW);
        m.setMetaString(UserIcon.IconsetPath,
                "34ae1613-9645-4222-a9d2-e5f243dea2865/Military/A10.png");
        m.refresh(getMapView().getMapEventDispatcher(), null,
                this.getClass());

    }

    public void createOrModifySensorFOV() {
        final MapView mapView = getMapView();
        final String cameraID = "sensor-fov-example-uid";
        final GeoPointMetaData point = mapView.getCenterPoint();
        final int color = 0xFFFF0000;

        MapItem mi = mapView.getMapItem(cameraID);
        if (mi == null) {
            PlacePointTool.MarkerCreator markerCreator = new PlacePointTool.MarkerCreator(
                    point);
            markerCreator.setUid(cameraID);
            //this settings automatically pops open to CotDetails page after dropping the marker
            markerCreator.showCotDetails(false);
            //this settings determines if a CoT persists or not.
            markerCreator.setArchive(true);
            //this is the type of the marker.  Could be set to a known 2525B value or custom
            markerCreator.setType("b-m-p-s-p-loc");
            //this shows under the marker
            markerCreator.setCallsign("Sensor");
            //this also determines if the marker persists or not??
            markerCreator.setNeverPersist(false);
            mi = markerCreator.placePoint();

        }
        // blind cast, ensure this is really a marker.
        Marker camera1 = (Marker) mi;
        camera1.setPoint(point);

        mi = mapView.getMapItem(camera1.getUID() + "-fov");
        if (mi instanceof SensorFOV) {
            SensorFOV sFov = (SensorFOV) mi;
            float r = ((0x00FF0000 & color) >> 16) / 256f;
            float g = ((0x0000FF00 & color) >> 8) / 256f;
            float b = ((0x000000FF & color) >> 0) / 256f;

            sFov.setColor(color); // currently broken
            sFov.setColor(r, g, b);
            sFov.setMetrics((int) (90 * Math.random()),
                    (int) (70 * Math.random()), 400);
        } else { // use this case
            float r = ((0x00FF0000 & color) >> 16) / 256f;
            float g = ((0x0000FF00 & color) >> 8) / 256f;
            float b = ((0x000000FF & color) >> 0) / 256f;
            SensorDetailHandler.addFovToMap(camera1, 90, 70, 400, new float[] {
                    r, g, b, 90
            }, true);
        }
    }

    public void createUnit() {

        Marker m = new Marker(getMapView().getPointWithElevation(), UUID
                .randomUUID().toString());
        Log.d(TAG, "creating a new unit marker for: " + m.getUID());
        m.setType("a-f-G-U-C-I");
        // m.setMetaBoolean("disableCoordinateOverlay", true); // used if you don't want the coordinate overlay to appear
        m.setMetaBoolean("readiness", true);
        m.setMetaBoolean("archive", true);
        m.setMetaString("how", "h-g-i-g-o");
        m.setMetaBoolean("editable", true);
        m.setMetaBoolean("movable", true);
        m.setMetaBoolean("removable", true);
        m.setMetaString("entry", "user");
        m.setMetaString("callsign", "Test Marker");
        m.setTitle("Test Marker");
        m.setMetaString("menu", getMenu());

        MapGroup _mapGroup = getMapView().getRootGroup()
                .findMapGroup("Cursor on Target")
                .findMapGroup("Friendly");
        _mapGroup.addItem(m);

        m.persist(getMapView().getMapEventDispatcher(), null,
                this.getClass());

        Intent new_cot_intent = new Intent();
        new_cot_intent.setAction("com.atakmap.android.maps.COT_PLACED");
        new_cot_intent.putExtra("uid", m.getUID());
        AtakBroadcast.getInstance().sendBroadcast(
                new_cot_intent);

    }

    void printNetworks() {
        /*
         *    TAKServer.DESCRIPTION_KEY
         *    TAKServer.ENABLED_KEY
         *    TAKServer.CONNECTED_KEY
         *    TAKServer.CONNECT_STRING_KEY
         */
        Bundle b = CommsMapComponent.getInstance().getAllPortsBundle();
        Bundle[] streams = (Bundle[]) b.getParcelableArray("streams");
        Bundle[] outputs = (Bundle[]) b.getParcelableArray("outputs");
        Bundle[] inputs = (Bundle[]) b.getParcelableArray("inputs");
        if (inputs != null) {
            for (Bundle input : inputs)
                Log.d(TAG, "input " + input.getString(TAKServer.DESCRIPTION_KEY)
                        + ": " + input.getString(TAKServer.CONNECT_STRING_KEY));
        }
        if (outputs != null) {
            for (Bundle output : outputs)
                Log.d(TAG, "output " + output.getString(TAKServer.DESCRIPTION_KEY)
                        + ": " + output.getString(TAKServer.CONNECT_STRING_KEY));
        }
        if (streams != null) {
            for (Bundle stream : streams)
                Log.d(TAG, "stream " + stream.getString(TAKServer.DESCRIPTION_KEY)
                        + ": " + stream.getString(TAKServer.CONNECT_STRING_KEY));
        }
    }

    void drawShapes() {
        MapView mapView = getMapView();
        MapGroup group = mapView.getRootGroup().findMapGroup(
                "Drawing Objects");

        List<DrawingShape> dslist = new ArrayList<>();

        MapItem mi = mapView.getMapItem("ds-1");
        if (mi != null) mi.removeFromGroup();
        mi = mapView.getMapItem("ds-2");
        if (mi != null) mi.removeFromGroup();
        mi = mapView.getMapItem("ds-3");
        if (mi != null) mi.removeFromGroup();
        mi = mapView.getMapItem("ds-4");
        if (mi != null) mi.removeFromGroup();
        mi = mapView.getMapItem("list-1");
        if (mi != null) mi.removeFromGroup();

        DrawingShape ds = new DrawingShape(mapView, "ds-1");
        ds.setMetaBoolean("archive", false);
        ds.setTitle("DrawingShape-1");
        ds.setStrokeColor(Color.RED);
        ds.setPoints(new GeoPoint[] {
                new GeoPoint(0, 0), new GeoPoint(1, 1), new GeoPoint(2, 1)
        });
        ds.setHeight(100);
        //group.addItem(ds);
        dslist.add(ds);
        // test to set closed after adding to a group
        ds.setClosed(true);
        // setEditable has a completely different meaning than setMetaBoolean("editable")
        // setMetaBoolean("editable", true/false) is used to turn on or off the editing capability
        // setEditable is use to start the editing mode for the graphic
        ds.setMetaBoolean("editable", false);

        ds = new DrawingShape(mapView, "ds-2");
        ds.setMetaBoolean("archive", false);
        ds.setTitle("DrawingShape-2");
        ds.setPoints(new GeoPoint[] {
                new GeoPoint(0, 0), new GeoPoint(-1, -1), new GeoPoint(-2, -1)
        });
        ds.setHeight(200);
        ds.setClosed(true);
        ds.setMetaBoolean("editable", false);
        ds.setStrokeColor(Color.BLUE);

        //group.addItem(ds);
        dslist.add(ds);

        MultiPolyline mp = new MultiPolyline(mapView, group, dslist, "list-1");
        mp.setMetaBoolean("archive", false);
        group.addItem(mp);
        mp.setMetaBoolean("editable", false);
        
        mp.setMovable(false);
        ds = new DrawingShape(mapView, "ds-3");
        ds.setMetaBoolean("archive", false);
        ds.setTitle("DrawingShape-3");
        ds.setPoints(new GeoPoint[] {
                new GeoPoint(0, 0), new GeoPoint(2, 0), new GeoPoint(2, -1)
        });
        ds.setClosed(true);
        ds.setStrokeColor(Color.YELLOW);
        ds.setHeight(300);
        ds.setMovable(false);
        ds.setMetaBoolean("editable", false);
        group.addItem(ds);


        ds = new DrawingShape(mapView, "ds-4");
        ds.setMetaBoolean("archive", false);
        ds.setTitle("DrawingShape-4");
        ds.setPoints(new GeoPoint[] {
                new GeoPoint(0, 0), new GeoPoint(-2, 0), new GeoPoint(-2, 1)
        });
        ds.setStrokeColor(Color.GREEN);
        ds.setMetaBoolean("gotcha", false);
        ds.setHeight(400);
        ds.setClosed(true);
        ds.setMovable(false);
        ds.setMetaBoolean("editable", false);
        group.addItem(ds);


        mi = mapView.getMapItem("rect-1");
        if (mi != null) mi.removeFromGroup();
        MapGroup mg = new DefaultMapGroup("r1");
        group.addGroup(mg);
        // legacy - rectangles need to be in their own group
        DrawingRectangle drawingRectangle = new DrawingRectangle(mg,
                GeoPointMetaData.wrap(new GeoPoint(3, 3)),
                GeoPointMetaData.wrap(new GeoPoint(3, 2.5)),
                GeoPointMetaData.wrap(new GeoPoint(2.5, 2.5)),
                GeoPointMetaData.wrap(new GeoPoint(2.5, 3)),
                "rect-1");
        drawingRectangle.setMetaBoolean("archive", false);
        drawingRectangle.setMetaBoolean("editable", false);
        drawingRectangle.setTitle("rectangle");
        group.addItem(drawingRectangle);

        mi = mapView.getMapItem("circle-1");
        if (mi != null) mi.removeFromGroup();
        DrawingCircle drawingCircle = new DrawingCircle(mapView, "circle-1");

        drawingCircle.setMetaBoolean("archive", false);
        drawingCircle.setMetaBoolean("editable", false);
        drawingCircle.setTitle("circle");
        drawingCircle.setCenterPoint(GeoPointMetaData.wrap(new GeoPoint(2d, 2d)));
        drawingCircle.setColor(Color.RED);
        drawingCircle.setFillColor(Color.BLACK);
        drawingCircle.setRadius(2000);
        group.addItem(drawingCircle);

        mi = mapView.getMapItem("ellipse-1");
        if (mi != null) mi.removeFromGroup();
        DrawingEllipse drawingEllipse = new DrawingEllipse(mapView, "ellipse-1");

        drawingEllipse.setMetaBoolean("archive", false);
        drawingEllipse.setMetaBoolean("editable", false);
        drawingEllipse.setTitle("ellipse");
        drawingEllipse.setCenterPoint(GeoPointMetaData.wrap(new GeoPoint(1d, 2d)));
        drawingEllipse.setColor(Color.GREEN);
        drawingEllipse.setFillColor(Color.YELLOW);
        drawingEllipse.setEllipses(Collections.singletonList(new Ellipse("ellipse-1.1")));
        drawingEllipse.setLength(2000);
        drawingEllipse.setWidth(500);
        drawingEllipse.setAngle(0);
        group.addItem(drawingEllipse);

        mi = mapView.getMapItem("line1");
        if (mi != null) mi.removeFromGroup();
        DrawingShape line1 = new DrawingShape(mapView, "line1");
        line1.setMetaBoolean("archive", false);
        line1.setPoints(new GeoPoint[] { new GeoPoint(7,3, -100),
                new GeoPoint(7,4,-100) });
        line1.setAltitudeMode(Feature.AltitudeMode.Absolute);
        line1.setColor(Color.RED);
        group.addItem(line1);

        mi = mapView.getMapItem("line2");
        if (mi != null) mi.removeFromGroup();
        DrawingShape line2 = new DrawingShape(mapView, "line2");
        line2.setMetaBoolean("archive", false);
        line2.setPoints(new GeoPoint[] { new GeoPoint(7.2,3.2, -100),
                new GeoPoint(7.2,4.2,-100) });
        line2.setColor(Color.MAGENTA);
        line2.setAltitudeMode(Feature.AltitudeMode.ClampToGround);
        group.addItem(line2);


    }

    /**
     * For plugins to have custom radial menus, we need to set the "menu" metadata to
     * contain a well formed xml entry.   This only allows for reskinning of existing
     * radial menus with icons and actions that already exist in ATAK.
     * In order to perform a completely custom radia menu instalation. You need to
     * define the radial menu as below and then uuencode the sub elements such as
     * images or instructions.
     */
    private String getMenu() {
        return PluginMenuParser.getMenu(pluginContext, "menu.xml");
    }

    /**
     * This is an example of a completely custom xml definition for a menu.   It uses the
     * plaintext stringified version of the current menu language plus uuencoded images
     * and actions.
     */
    public String getMenu2() {
        return PluginMenuParser.getMenu(pluginContext, "menu2.xml");
    }

    /**
     * Add a plugin-specific contact to the contacts list
     * This contact fires an intent when a message is sent to it,
     * instead of using the default chat implementation
     *
     * @param name Contact display name
     * @return New plugin contact
     */
    public Contact addPluginContact(String name) {

        // Add handler for messages
        HelloWorldContactHandler contactHandler = new HelloWorldContactHandler(
                pluginContext);
        CotMapComponent.getInstance().getContactConnectorMgr()
                .addContactHandler(contactHandler);

        // Create new contact with name and random UID
        IndividualContact contact = new IndividualContact(
                name, UUID.randomUUID().toString());

        // Add plugin connector which points to the intent action
        // that is fired when a message is sent to this contact
        contact.addConnector(new PluginConnector(CHAT_HELLO_WORLD));

        // Add IP connector so the contact shows up when sending CoT or files
        contact.addConnector(new IpConnector(SEND_HELLO_WORLD));

        // Set default connector to plugin connector
        AtakPreferences prefs = new AtakPreferences(getMapView().getContext());
        prefs.set("contact.connector.default." + contact.getUID(),
                PluginConnector.CONNECTOR_TYPE);

        // Add new contact to master contacts list
        Contacts.getInstance().addContact(contact);

        return contact;
    }

    /**
     * Remove a contact from the master contacts list
     * This will remove it from the contacts list drop-down
     *
     * @param contact Contact object
     */
    public void removeContact(Contact contact) {
        Contacts.getInstance().removeContact(contact);
    }

    private void plotISSLocation() {
        double lat = Double.NaN, lon = Double.NaN;
        try {
            final java.io.InputStream input = new java.net.URL(
                    "http://api.open-notify.org/iss-now.json").openStream();
            final String returnJson = FileSystemUtils.copyStreamToString(input,
                    true, FileSystemUtils.UTF8_CHARSET);

            Log.d(TAG, "return json: " + returnJson);

            android.util.JsonReader jr = new android.util.JsonReader(
                    new java.io.StringReader(returnJson));
            jr.beginObject();
            while (jr.hasNext()) {
                String name = jr.nextName();
                switch (name) {
                    case "iss_position":
                        jr.beginObject();
                        while (jr.hasNext()) {
                            String n = jr.nextName();
                            switch (n) {
                                case "latitude":
                                    lat = jr.nextDouble();
                                    break;
                                case "longitude":
                                    lon = jr.nextDouble();
                                    break;
                                case "message":
                                    jr.skipValue();
                                    break;
                            }
                        }
                        jr.endObject();
                        break;
                    case "timestamp":
                        jr.skipValue();
                        break;
                    case "message":
                        jr.skipValue();
                        break;
                }
            }
            jr.endObject();
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
            final MapItem mi = getMapView().getMapItem("iss-unique-identifier");
            if (mi != null) {
                if (mi instanceof Marker) {
                    Marker marker = (Marker) mi;

                    GeoPoint newPoint = new GeoPoint(lat, lon);
                    GeoPoint lastPoint = ((Marker) mi).getPoint();
                    long currTime = SystemClock.elapsedRealtime();

                    double dist = lastPoint.distanceTo(newPoint);
                    double dir = lastPoint.bearingTo(newPoint);

                    double delta = currTime -
                            mi.getMetaLong("iss.lastUpdateTime", 0);

                    double speed = dist / (delta / 1000f);

                    marker.setTrack(dir, speed);

                    marker.setPoint(newPoint);
                    mi.setMetaLong("iss.lastUpdateTime",
                            SystemClock.elapsedRealtime());

                }
            } else {
                PlacePointTool.MarkerCreator mc = new PlacePointTool.MarkerCreator(
                        new GeoPoint(lat, lon));
                mc.setUid("iss-unique-identifier");
                mc.setCallsign("International Space Station");
                mc.setType("a-f-P-T");
                mc.showCotDetails(false);
                mc.setNeverPersist(true);
                Marker m = mc.placePoint();
                // don't forget to turn on the arrow so that we know where the ISS is going
                m.setStyle(Marker.STYLE_ROTATE_HEADING_MASK);
                //m.setMetaBoolean("editable", false);
                m.setMetaBoolean("movable", false);
                m.setMetaString("how", "m-g");
            }
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float[] values = event.values;
            // Movement
            float x = values[0];
            float y = values[1];
            float z = values[2];

            float asr = (x * x + y * y + z * z)
                    / (SensorManager.GRAVITY_EARTH
                            * SensorManager.GRAVITY_EARTH);
            if (Math.abs(x) > 6 || Math.abs(y) > 6 || Math.abs(z) > 8)
                Log.d(TAG, "gravity=" + SensorManager.GRAVITY_EARTH + " x=" + x
                        + " y=" + y + " z=" + z + " asr=" + asr);
            if (y > 7) {
                TextContainer.getTopInstance().displayPrompt("Tilt Right");
                Log.d(TAG, "tilt right");
            } else if (y < -7) {
                TextContainer.getTopInstance().displayPrompt("Tilt Left");
                Log.d(TAG, "tilt left");
            } else if (x > 7) {
                TextContainer.getTopInstance().displayPrompt("Tilt Up");
                Log.d(TAG, "tilt up");
            } else if (x < -7) {
                TextContainer.getTopInstance().displayPrompt("Tilt Down");
                Log.d(TAG, "tilt down");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.d(TAG, "accuracy for the accelerometer: " + accuracy);
        }
    }

    BroadcastReceiver fakePhoneCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "intent: " + intent.getAction() + " "
                    + intent.getStringExtra("mytime"));
            getMapView().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getMapView().getContext(),
                            "intent: " + intent.getAction() + " "
                                    + intent.getStringExtra("mytime"),
                            Toast.LENGTH_LONG).show();
                }
            });
            NotificationManager nm = (NotificationManager) getMapView()
                    .getContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getIntExtra("notificationId", 0);
            Log.d(TAG, "cancelling id: " + id);
            if (id > 0) {
                nm.cancel(id);
            }
        }
    };



    /**
     * Note - this will become a API offering in 4.5.1 and beyond.
     * @param context
     * @param drawableId
     * @return
     */
    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(),
                    drawableId);
        } else if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            Bitmap bitmap = Bitmap.createBitmap(
                    vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(),
                    canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    private int[] generateHeatMap() {
        int[] data = new int[] {
                1, 1, 2, 0, 0, 0, 0, 0,
                1, 4, 2, 0, 0, 0, 0, 0,
                2, 2, 0, 0, 0, 0, 0, 0,
                6, 6, 0, 3, 0, 0, 0, 3,
                6, 6, 5, 3, 1, 3, 3, 3,
                6, 6, 3, 3, 0, 3, 6, 6,
                3, 3, 3, 0, 0, 0, 6, 5,
                3, 0, 0, 0, 0, 0, 5, 5,
        };
        for (int i = 0; i < data.length; ++i) {
            if (data[i] != 0)
                data[i] = Color.BLACK / data[i];
        }
        return data;

    }

    /**
     * Brute Force calculation
     * @param points a listing of ordered points
     * @param distance the distance into the ordered points in meters
     * @returns the point that falls on the line described by the listing of ordered points with the
     * distance in
     */
    private GeoPoint findArbitraryPointOnLine(@NonNull List<GeoPoint> points, double distance) {
        for (int i = 1; i < points.size(); ++i) {
            GeoPoint b = points.get(i);
            GeoPoint a = points.get(i-1);

            final double dist = GeoCalculations.slantDistanceTo(a, b);
            distance-=dist;
            if (distance < 0) {
                final double azimuth = GeoCalculations.bearingTo(b, a);
                final double inclination;
                if (Double.isNaN(b.getAltitude()) || Double.isNaN(a.getAltitude())) {
                    inclination = 0;
                } else {
                    // compute the inclination
                    final double height = b.getAltitude() - a.getAltitude();
                    inclination = Math.asin(height / dist);

                }
                // back track on the route to based on the overage.
                return GeoCalculations.pointAtDistance(b, azimuth, Math.abs(distance), inclination);
            }
        }
        return points.get(points.size()-1);
    }

    private void exampleCreateMissionPackage() {
        // also known as a data package
        File f = new File("/sdcard/test.zip");
        Marker m = getMapView().getSelfMarker();
        Collection<MapItem> items = getMapView().getRootGroup().deepFindItems(m.getPoint(), 20000, null);
        MissionPackageManifest mpm = new MissionPackageManifest();
        for (MapItem item : items) {
            if (item instanceof Marker || item instanceof Shape) {
                CotEvent ce = CotEventFactory.createCotEvent(item);
                if (ce != null && ce.isValid()) {
                    if (item != getMapView().getSelfMarker())
                        mpm.addMapItem(item.getUID());
                }
            }
        }

        // sample code to show how to add a file to the data package
        //mpm.addFile(new File("/sdcard/sample.tif"), null);

        mpm.setName("items-around-me");
        mpm.setPath(f.getPath());

        // enable if you would like the data package deleted as soon as it is imported
        //mpm.getConfiguration().setParameter("onReceiveDelete", "true");

        MissionPackageBuilder mpb = new MissionPackageBuilder(null, mpm, getMapView().getRootGroup());
        String fName = mpb.build();

        ImportResolver importer = new ImportMissionV1PackageSort(
                getMapView().getContext(), true, true, true);

        Thread t = new Thread("import-thread") {
            public void run() {
                if (!importer.beginImport(f)) {
                    getMapView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getMapView().getContext(), "import failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        t.start();



        
    }
}
