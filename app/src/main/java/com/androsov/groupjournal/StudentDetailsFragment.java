package com.androsov.groupjournal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.androsov.groupjournal.MainActivity.db;
import static com.androsov.groupjournal.MainActivity.imagesRef;
import static com.androsov.groupjournal.MainActivity.mAuth;
import static com.androsov.groupjournal.OptionsFragment.currentColor;
import static com.androsov.groupjournal.StudentFragmentList.loadData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentDetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "studentData";

    final Calendar myCalendar = Calendar.getInstance();
    Uri studentImageUri = null;
    Uri studentVideoUri = null;
    View v;

    private Student studentData;

    public StudentDetailsFragment() {
    }

    public StudentDetailsFragment(Student student) {
        studentData = student;
    }

    public static StudentDetailsFragment newInstance(Student studentData) {
        StudentDetailsFragment fragment = new StudentDetailsFragment(studentData);
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, studentData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentData = (Student) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Student student = studentData;
        myCalendar.setTime(student.birthday);
        EditText birthdayEdit = view.findViewById(R.id.birthday_edit);
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthdayEdit.setText(sdf.format(myCalendar.getTime()));
        ((EditText) view.findViewById(R.id.first_name_edit)).setText(student.firstName);
        ((EditText) view.findViewById(R.id.last_name_edit)).setText(student.lastName);
        ((EditText) view.findViewById(R.id.second_name_edit)).setText(student.secondName);
        ((EditText) view.findViewById(R.id.latitude_edit)).setText(student.latitude);
        ((EditText) view.findViewById(R.id.longitude_edit)).setText(student.longitude);
        VideoView videoView = view.findViewById(R.id.videoView);

        if (!student.videoUrl.isEmpty()) {
            videoView.setVideoURI(Uri.parse(student.videoUrl));
            videoView.start();
        }

        studentImageUri = Uri.parse(student.images.get(0));
    }

    public void btnSaveClick(View view) {
        String firstName = ((EditText) getView().findViewById(R.id.first_name_edit)).getText().toString();
        String lastName = ((EditText) getView().findViewById(R.id.last_name_edit)).getText().toString();
        String secondName = ((EditText) getView().findViewById(R.id.second_name_edit)).getText().toString();
        String birthday = ((EditText) getView().findViewById(R.id.birthday_edit)).getText().toString();
        String latitude = ((EditText) getView().findViewById(R.id.latitude_edit)).getText().toString();
        String longitude = ((EditText) getView().findViewById(R.id.longitude_edit)).getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || secondName.isEmpty() || birthday.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(getActivity(), "Please, fill all the fields.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("secondName", secondName);
        user.put("birthday", myCalendar.getTime());
        user.put("latitude", latitude);
        user.put("longitude", longitude);

        db.collection("group mates")
            .document(studentData.id)
            .update(user)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(getActivity(), "Updated group mate",
                        Toast.LENGTH_LONG).show();

                loadData();
            })
            .addOnFailureListener(e -> Toast.makeText(getActivity(),"Error adding document: " + e,
                    Toast.LENGTH_SHORT).show());
    }

    private void loadVideo(View view) {
        try {
            Intent videoPicker = new Intent(Intent.ACTION_PICK);
            videoPicker.setType("video/*");
            startActivityForResult(videoPicker, 2);
        } catch (Exception exception) {
            Toast.makeText(getActivity(),exception.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    private void showGallery(View view) {
        TabBarActivity tabBarActivity = (TabBarActivity) view.getContext();
        GalleryFragment galleryFragment = new GalleryFragment(studentData.images);
        tabBarActivity.setFragment(galleryFragment, "");
        tabBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 2 : {
                if (resultCode == RESULT_OK){
                    final Uri videoUri = data.getData();

                    String videoName = UUID.randomUUID().toString();
                    StorageReference videoRef = MainActivity.videosRef.child(videoName);

                    UploadTask uploadTask = videoRef.putFile(videoUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return videoRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            studentVideoUri =  task.getResult();
                        Map<String, Object> user = new HashMap<>();
                        user.put("videoUrl", String.valueOf(task.getResult()));

                        db.collection("group mates")
                                .document(studentData.id)
                                .update(user)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getActivity(), "Updated group mate",
                                            Toast.LENGTH_LONG).show();

                                    VideoView videoView = v.findViewById(R.id.videoView);

                                        videoView.setVideoURI(studentVideoUri);
                                        videoView.start();

                                    loadData();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getActivity(),"Error adding document: " + e,
                                        Toast.LENGTH_SHORT).show());
                        }
                    });
                }
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_details, container, false);
        v = view;

        EditText edittext= view.findViewById(R.id.birthday_edit);
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edittext.setText(sdf.format(myCalendar.getTime()));
        };

        edittext.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        view.findViewById(R.id.save_btn).setOnClickListener(this::btnSaveClick);
        view.findViewById(R.id.load_video_btn).setOnClickListener(this::loadVideo);
        view.findViewById(R.id.gallery_btn).setOnClickListener(this::showGallery);


        if (currentColor != 0) {
            view.findViewById(R.id.save_btn).setBackgroundColor(currentColor);
            view.findViewById(R.id.load_video_btn).setBackgroundColor(currentColor);
            view.findViewById(R.id.gallery_btn).setBackgroundColor(currentColor);
        }

        return view;
    }
}