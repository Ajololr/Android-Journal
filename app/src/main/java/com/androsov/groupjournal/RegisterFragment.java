package com.androsov.groupjournal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.androsov.groupjournal.MainActivity.imagesRef;
import static com.androsov.groupjournal.MainActivity.mAuth;
import static com.androsov.groupjournal.MainActivity.db;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private View v;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters

    final Calendar myCalendar = Calendar.getInstance();
    Uri studentImageUri = null;

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void clearFields() {
        ((EditText) v.findViewById(R.id.edit_text_birthday_create)).setText("");
        ((EditText) v.findViewById(R.id.edit_text_second_name_create)).setText("");
        ((EditText) v.findViewById(R.id.edit_text_last_name_create)).setText("");
        ((EditText) v.findViewById(R.id.edit_text_first_name_create)).setText("");
        ((EditText) v.findViewById(R.id.edit_text_email_create)).setText("");
        ((EditText) v.findViewById(R.id.edit_text_password_create)).setText("");
        ((ImageView) v.findViewById(R.id.student_avatar_picker)).setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_photo_camera_black_48dp));
        studentImageUri = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);;
        v = view;
        Button button = view.findViewById(R.id.create_new_btn);
        button.setOnClickListener(v -> btnCreateClick(v));

        EditText edittext= view.findViewById(R.id.edit_text_birthday_create);
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edittext.setText(sdf.format(myCalendar.getTime()));
        };

        edittext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ImageView imageView = view.findViewById(R.id.student_avatar_picker);
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
        return view;
    }

    public void btnCreateClick(View view) {
        EditText emailEditText = (EditText) getView().findViewById(R.id.edit_text_email_create);
        String email = emailEditText.getText().toString();
        String password = ((EditText) getView().findViewById(R.id.edit_text_password_create)).getText().toString();
        String firstName = ((EditText) getView().findViewById(R.id.edit_text_first_name_create)).getText().toString();
        String lastName = ((EditText) getView().findViewById(R.id.edit_text_last_name_create)).getText().toString();
        String secondName = ((EditText) getView().findViewById(R.id.edit_text_second_name_create)).getText().toString();
        String birthday = ((EditText) getView().findViewById(R.id.edit_text_birthday_create)).getText().toString();

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || secondName.isEmpty() || birthday.isEmpty() || studentImageUri == null) {
            Toast.makeText(getActivity(), "Please, fill all the fields.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        StorageReference avatarRef = imagesRef.child(UUID.randomUUID() + ".jpg");
        avatarRef.putFile(studentImageUri)
            .addOnSuccessListener(taskSnapshot -> {
                Map<String, Object> user = new HashMap<>();
                user.put("firstName", firstName);
                user.put("lastName", lastName);
                user.put("secondName", secondName);
                user.put("birthday", myCalendar.getTime());
                user.put("email", email);
                user.put("videoUrl", "");
                user.put("latitude", "");
                user.put("longitude", "");

                avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    ArrayList<String> images = new ArrayList<>();
                    images.add(uri.toString());
                    user.put("images", images);

                    db.collection("group mates")
                            .add(user)
                            .addOnSuccessListener(documentReference -> {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(getActivity(), task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Created new group mate",
                                                        Toast.LENGTH_LONG).show();
                                                clearFields();
                                            } else {
                                                System.out.println("createUserWithEmail:failure" + task.getException());
                                                Toast.makeText(getActivity(), task.getException().getLocalizedMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(),"Error adding document: " + e,
                                    Toast.LENGTH_SHORT).show());
                });
            })
            .addOnFailureListener(exception -> {
                Toast.makeText(getActivity(),exception.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            });


    }

}