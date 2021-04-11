package com.androsov.groupjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Student> studentArrayList;

    private OnMapReadyCallback callback = googleMap -> {

        ArrayList<String> markers = new ArrayList<>(studentArrayList.size());
        for (int i = 0; i < studentArrayList.size(); i++) {

            if (studentArrayList.get(i).latitude.isEmpty()) {
                studentArrayList.get(i).latitude = "0";
            }

            if (studentArrayList.get(i).longitude.isEmpty()) {
                studentArrayList.get(i).longitude = "0";
            }

            LatLng latLng = new LatLng(Double.parseDouble(studentArrayList.get(i).latitude), Double.parseDouble(studentArrayList.get(i).longitude));
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(studentArrayList.get(i).firstName + " " + studentArrayList.get(i).lastName));
            markers.add(marker.getId());
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(53.54, 27.34)));
        googleMap.setMinZoomPreference(8);

        googleMap.setOnMarkerClickListener(marker -> {
            if (markers.contains(marker.getId())) {
                StudentDetailsFragment elementFragment = new StudentDetailsFragment(studentArrayList.get(markers.indexOf(marker.getId())));
                TabBarActivity appCompatActivity = (TabBarActivity) getActivity();
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                appCompatActivity.setFragment(elementFragment, "");
                return true;
            }
            return false;
        });
    };


    public MapsFragment(){
        studentArrayList = new ArrayList<>();
        db.collection("group mates")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Student student = document.toObject(Student.class);
                            student.id = document.getId();
                            studentArrayList.add(student);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
}