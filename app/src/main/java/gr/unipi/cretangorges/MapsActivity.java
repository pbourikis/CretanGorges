package gr.unipi.cretangorges;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.lang.Object;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.collections.GroundOverlayManager;
import com.google.maps.android.collections.MarkerManager;
import com.google.maps.android.collections.PolygonManager;
import com.google.maps.android.collections.PolylineManager;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Renderer;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;
import com.google.maps.android.data.Renderer;
import com.google.maps.android.data.kml.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import java.io.Serializable;

import gr.unipi.cretangorges.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // Cloud Storage
    FirebaseStorage storage;
    StorageReference storageGorges;

    public String location;
    public String url;

    public Gorge gorge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize storage
        storage = FirebaseStorage.getInstance();
        storageGorges = storage.getReference("/Gorges/");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent in = getIntent();
        gorge = (Gorge) in.getSerializableExtra("Gorge");
        System.out.println("Length " + gorge.length);
        System.out.println("Name " + gorge.name);
        System.out.println("Text " + gorge.text);
        System.out.println("Difficulty " + gorge.difficulty);
        System.out.println("Price " + gorge.price);
        System.out.println("Time " + gorge.time);
        System.out.println("ID " + gorge.id);
        location = gorge.id.concat("/").concat(gorge.id).concat(".kml");

        storageGorges.child(location).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
                retrieveFileFromUrl();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            mMap = googleMap;
            //retrieveFileFromResource();
            //retrieveFileFromUrl();
        } catch (Exception e) {
            Log.e("Exception caught", e.toString());
        }

    }

    private void retrieveFileFromResource() {
        new LoadLocalKmlFile(R.raw.gorge).execute();
    }

    private void moveCameraToKml(KmlLayer kmlLayer) {
        try {
            ArrayList<LatLng> points = new ArrayList<>();
            for (KmlContainer c : kmlLayer.getContainers()) {
                for (KmlPlacemark p : c.getPlacemarks()) {
                    if (p.getGeometry().getGeometryType().equals("LineString")) {
                        points.addAll((Collection<? extends LatLng>) p.getGeometry().getGeometryObject());
                    }
                }
            }
            LatLng gorge_start = new LatLng(points.get(0).latitude,points.get(0).longitude);
            int i = points.size()/2;
            LatLng gorge_cord = new LatLng(points.get(i).latitude,points.get(i).longitude);
            mMap.addMarker(new MarkerOptions().position(gorge_start).title("Αρχή " + gorge.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gorge_cord, 15));
            Toast.makeText(this, gorge.name, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // may fail depending on the KML being shown
            e.printStackTrace();
        }
    }

    private void addKmlToMap(KmlLayer kmlLayer) {
        if (kmlLayer != null) {
            try {
                kmlLayer.addLayerToMap();
            }catch (Exception exception){
                System.out.println(exception);
            }
            kmlLayer.setOnFeatureClickListener(feature -> Toast.makeText(MapsActivity.this,
                    gorge.name,
                    Toast.LENGTH_SHORT).show());
            moveCameraToKml(kmlLayer);
        }
    }

/************************LOCAL****************************/
    private class LoadLocalKmlFile extends AsyncTask<String, Void, KmlLayer> {
        private final int mResourceId;

        LoadLocalKmlFile(int resourceId) {
            mResourceId = resourceId;
        }

        @Override
        protected KmlLayer doInBackground(String... strings) {
            try {
                return new KmlLayer(mMap, mResourceId, MapsActivity.this);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(KmlLayer kmlLayer) {
            addKmlToMap(kmlLayer);
        }
    }
/**************************FROMURL************************/

private void retrieveFileFromUrl() {
    new DownloadKmlFile(url).execute();
}

    private Renderer.ImagesCache getImagesCache() {
        final RetainFragment retainFragment =
                RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        return retainFragment.mImagesCache;
    }

    /**
     * Fragment for retaining the bitmap cache between configuration changes.
     */
    public static class RetainFragment extends Fragment {
        private static final String TAG = RetainFragment.class.getName();
        Renderer.ImagesCache mImagesCache;

        static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }


    private class DownloadKmlFile extends AsyncTask<String, Void, KmlLayer> {
        private final String mUrl;

        DownloadKmlFile(String url) {
            mUrl = url;
        }

        protected KmlLayer doInBackground(String... params) {
            try {
                InputStream is =  new URL(mUrl).openStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                try {
                    return new KmlLayer(mMap,
                            new ByteArrayInputStream(buffer.toByteArray()),
                            MapsActivity.this,
                            new MarkerManager(mMap),
                            new PolygonManager(mMap),
                            new PolylineManager(mMap),
                            new GroundOverlayManager(mMap),
                            getImagesCache());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(KmlLayer kmlLayer) {
            addKmlToMap(kmlLayer);
        }
    }

}
