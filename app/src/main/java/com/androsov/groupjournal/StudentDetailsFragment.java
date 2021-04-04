package com.androsov.groupjournal;

import android.app.Activity;
import android.app.DatePickerDialog;
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

import com.androsov.groupjournal.dummy.StudentsContent;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.androsov.groupjournal.MainActivity.db;
import static com.androsov.groupjournal.MainActivity.imagesRef;
import static com.androsov.groupjournal.MainActivity.mAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentDetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "studentData";

    final Calendar myCalendar = Calendar.getInstance();
    Uri studentImageUri = null;

    private StudentsContent.Student studentData;

    public StudentDetailsFragment() {
        // Required empty public constructor
    }

    public static StudentDetailsFragment newInstance(StudentsContent.Student studentData) {
        StudentDetailsFragment fragment = new StudentDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, studentData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentData = (StudentsContent.Student) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        StudentsContent.Student student = StudentDetailsFragmentArgs.fromBundle(getArguments()).getStudentData();

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
        ((ImageView) view.findViewById(R.id.student_avatar_picker_edit)).setImageURI( Uri.parse(student.images.get(0)));
        ((Button) view.findViewById(R.id.watch_video_btn)).setEnabled(!student.videoUrl.isEmpty());

        studentImageUri = Uri.parse(student.images.get(0));
    }

    public void btnSaveClick(View view) {
        String firstName = ((EditText) getView().findViewById(R.id.first_name_edit)).getText().toString();
        String lastName = ((EditText) getView().findViewById(R.id.last_name_edit)).getText().toString();
        String secondName = ((EditText) getView().findViewById(R.id.second_name_edit)).getText().toString();
        String birthday = ((EditText) getView().findViewById(R.id.birthday_edit)).getText().toString();
        String latitude = ((EditText) getView().findViewById(R.id.latitude_edit)).getText().toString();
        String longitude = ((EditText) getView().findViewById(R.id.longitude_edit)).getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || secondName.isEmpty() || birthday.isEmpty() || studentImageUri == null) {
            Toast.makeText(getActivity(), "Please, fill all the fields.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        StorageReference avatarRef = imagesRef.child(UUID.randomUUID() + ".jpg");
        avatarRef.putFile(studentImageUri)
                .addOnCompleteListener(taskSnapshot -> {
                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", firstName);
                    user.put("lastName", lastName);
                    user.put("secondName", secondName);
                    user.put("birthday", myCalendar.getTime());
                    user.put("videoUrl", "");
                    user.put("latitude", latitude);
                    user.put("longitude", longitude);
                    try {
                        avatarRef.getDownloadUrl().wait();
                    } catch(Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    studentData.images.set(0, avatarRef.getDownloadUrl().getResult().toString());
                    String[] images = (String[]) studentData.images.toArray();
                    user.put("images", images);

                    db.collection("group mates")
                            .document(studentData.id)
                            .set(user)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getActivity(), "Updated group mate",
                                        Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(),"Error adding document: " + e,
                                    Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getActivity(),exception.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                });


    }

    private void playVideo(View view) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(studentData.videoUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception exception) {
            Toast.makeText(getActivity(),exception.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    private void loadVideo(View view) {
        try {
            ((Button) view.findViewById(R.id.watch_video_btn)).setEnabled(true);
        } catch (Exception exception) {
            Toast.makeText(getActivity(),exception.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_details, container, false);

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

        ImageView imageView = view.findViewById(R.id.student_avatar_picker_edit);
        imageView.setOnClickListener(v -> {
            ImagePicker.Companion.with(this)
                    .cropSquare()
                    .start((resultCode, data) -> {
                        if (resultCode == Activity.RESULT_OK) {
                            //Image Uri will not be null for RESULT_OK
                            Uri fileUri = data.getData();
                            imageView.setImageURI(fileUri);
                            studentImageUri = fileUri;
                        } else if (resultCode == ImagePicker.RESULT_ERROR) {
                            Toast.makeText(getContext(), "Image picker error", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    });
        });

        view.findViewById(R.id.watch_video_btn).setOnClickListener(this::playVideo);
        view.findViewById(R.id.save_btn).setOnClickListener(this::btnSaveClick);
        view.findViewById(R.id.load_video_btn).setOnClickListener(this::loadVideo);

        return view;
    }
}