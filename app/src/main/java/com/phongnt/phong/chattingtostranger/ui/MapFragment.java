package com.phongnt.phong.chattingtostranger.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.phongnt.phong.chattingtostranger.R;
import com.phongnt.phong.chattingtostranger.data.DatabaseHandler;
import com.phongnt.phong.chattingtostranger.services.ChatService;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBEnvironment;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.location.request.SortField;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





/**
 * C
 */
public class MapFragment extends Fragment
        implements GoogleMap.OnMarkerClickListener,LocationListener {

    // ...

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    String querySearch = "food";
    String name ;
    MapView mapView;
    private LocationManager locationManager;
    private String provider;
    private GoogleMap map;
    DatabaseHandler databaseHandler ;
    private Map<String, QBUser> markerMapInfo = new HashMap<>();

    JSONArray arrayCategory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mapview, null, false);


        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        ChatService.initIfNeed(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, false);
        final Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null)

        {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            final QBLocation Qblocation = new QBLocation(location.getLatitude(),
                    location.getLongitude(), databaseHandler.getUser().getUser() + " is here");

            QBLocations.createLocation(Qblocation, new QBEntityCallbackImpl<QBLocation>() {
                @Override
                public void onSuccess(QBLocation qbLocation, Bundle args) {

                }

                @Override
                public void onError(List<String> errors) {

                }
            }, "Your friend is near!", QBEnvironment.DEVELOPMENT, 1000);
        }

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //TODO: Any custom actions
                boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gps_enabled) {
                    showSettingsAlert();

                }

                return false;
            }
        });

        QBLocationRequestBuilder getLocationsBuilder = new QBLocationRequestBuilder();
        getLocationsBuilder.setPerPage(100);
        getLocationsBuilder.setLastOnly();
        //getLocationsBuilder.setHasStatus();
        getLocationsBuilder.setSort(SortField.CREATED_AT);
        QBLocations.getLocations(getLocationsBuilder, new QBEntityCallbackImpl<ArrayList<QBLocation>>() {
            @Override
            public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {
                Log.e("found", String.valueOf(locations.size()));
                Toast.makeText(getActivity(),"Found "+ locations.size() +" people on map",Toast.LENGTH_SHORT).show();

                for (int i = 0; i < locations.size(); i++) {
                    QBLocation friendLocation = locations.get(i);
                    QBUser friendUser = friendLocation.getUser();

                    if (friendUser.getLogin() == null) name = friendUser.getEmail();
                    else name = friendUser.getLogin();
                    LatLng latLng = new LatLng(friendLocation.getLatitude(), friendLocation.getLongitude());


                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(name + " is here"));
                    if(i==locations.size() - 1 ) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
                    }
                    markerMapInfo.put(marker.getId(), friendUser);

                }


            }

            @Override
            public void onError(List<String> errors) {

            }
        });



        // get the last know location from your location manager.
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.e("market click", marker.getId());
                QBUser info;
                if (markerMapInfo.get(marker.getId()) != null) {
                    info = markerMapInfo.get(marker.getId());
                    List<QBUser> qbUserList = new ArrayList<>();
                    qbUserList.add(info);
                    ChatService.getInstance().addDialogsUsers(qbUserList);
                    QBDialog dialogToCreate = new QBDialog();
                    dialogToCreate.setName(info.getEmail() == null ? info.getLogin() : info.getEmail());
                    dialogToCreate.setType(QBDialogType.PRIVATE);
                    ArrayList<Integer> qbUserId = new ArrayList<>();
                    qbUserId.add(info.getId());
                    dialogToCreate.setOccupantsIds(qbUserId);
                    QBChatService.getInstance().getGroupChatManager().
                            createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                                @Override
                                public void onSuccess(QBDialog result, Bundle params) {
                                    super.onSuccess(result, params);
                                    startSingleChat(result);
                                }

                                @Override
                                public void onError(List<String> errors) {
                                    super.onError(errors);
                                    AlertDialog.Builder errordialog = new AlertDialog.Builder(getActivity());
                                    errordialog.setMessage("dialog creation errors: " + errors).create().show();
                                }
                            });

                }


            }
        });


       /* map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                //View v = getLayoutInflater(savedInstanceState).inflate(R.layout.map_window_info, null);
                //return v;
                return null;


            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
*/
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void startSingleChat(QBDialog dialog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

        ChatActivity.start(getActivity(), bundle);
        getActivity().finish();
    }



    @Override
    public boolean onMarkerClick(Marker marker) {



        return false;
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.e("on location change",location.toString());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e("on status",s);

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e("on provide",s);

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
