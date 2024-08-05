package com.newmview.wifi.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.ui.IconGenerator;
import com.mview.airtel.R;
import com.newmview.wifi.other.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class HeatMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    private View view;
    private ProgressBar pd;
    private Bundle bundle;
    private ArrayList<ArrayList<HashMap<String, String>>> graphFinalList;
    private ArrayList<HashMap<String, String>> graphList;
    private HashMap<String, String> selectedVal;
    private ArrayList<WeightedLatLng> list;
    private Marker marker;
    private IconGenerator iconFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mapfragment, container, false);
        init();


        return view;
    }

    private void init() {
        pd = view.findViewById(R.id.progress);
         iconFactory=new IconGenerator(getActivity());

        bundle=getArguments();
        if(getArguments()!=null)
        {
            //columnWiseList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("columnWiseList");
            graphFinalList= (ArrayList<ArrayList<HashMap<String, String>>>) bundle.getSerializable("graphFinalList");
            graphList= (ArrayList<HashMap<String, String>>) bundle.getSerializable("graphList");
            selectedVal=(HashMap<String,String>)bundle.getSerializable("selectedVal");


        }
        getLatAngLongList();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    private void getLatAngLongList() {
        list=new ArrayList<WeightedLatLng>();

        if (graphFinalList != null && graphFinalList.size() > 0) {
            for (int i = 0; i < graphFinalList.size(); i++) {
                for (int j = 0; j < graphFinalList.get(i).size(); j++) {
                    double lat = Double.parseDouble(graphFinalList.get(i).get(j).get("lat"));
                    double lng=Double.parseDouble(graphFinalList.get(i).get(j).get("long"));
                    list.add(new WeightedLatLng(new LatLng(lat, lng),Integer.parseInt(graphFinalList.get(i).get(j).get("y"))));
                }
            }
        }
    }

    public Marker addText(final Context context, final GoogleMap map,
                          final LatLng location, final String text, final int padding,
                          final int fontSize) {
        if(marker!=null)
            marker.remove();


        if (context == null || map == null || location == null || text == null
                || fontSize <= 0) {
            return marker;
        }

        final TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(fontSize);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(text, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * padding, boundsText.height() + 2 * padding, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(text, canvasText.getWidth() / 2,
                canvasText.getHeight() - padding - boundsText.bottom, paintText);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text)))
                .anchor(0.5f, 1);


        marker = map.addMarker(markerOptions);




        return marker;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        LatLng bangladeshLatLng = new LatLng(23.6850, 90.3563);
        if (bangladeshLatLng != null) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladeshLatLng, 6.5F));
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            //mMap = googleMap;

            // Create a heat map tile provider, passing it the latlngs of the police stations.
            if(list!=null && list.size()>0) {
                HeatmapTileProvider provider = new HeatmapTileProvider.Builder().weightedData(list).build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            }

        }


    }


    @Override
    public void onMapClick(LatLng latLng) {
        Utils.showToast(getActivity(),"lat long "+latLng);
        getIndex(graphFinalList,latLng.latitude,latLng.longitude);
       // addText(getActivity(),mMap,latLng,graphFinalList.)
    }

    private int getIndex(ArrayList<ArrayList<HashMap<String, String>>> list, double latitude, double longitude) {


            int i;
            for (i = 0; i < list.size(); i++) {

                for(int j=0;j<list.get(i).size();j++)
                {
                    if((Double.parseDouble(list.get(i).get(j).get("lat"))==latitude) && (Double.parseDouble(list.get(i).get(j).get("long"))==longitude))
                    {

                        return j;
                    }
                }
            }
            return -1;



    }


}
