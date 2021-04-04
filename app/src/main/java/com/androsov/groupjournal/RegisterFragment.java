package com.androsov.groupjournal;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.androsov.groupjournal.MainActivity.imagesRef;
import static com.androsov.groupjournal.MainActivity.mAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);;
        Button button = (Button) view.findViewById(R.id.create_new_btn);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnCreateClick(v);
            }
        });

        EditText edittext= (EditText) view.findViewById(R.id.edit_text_birthday_create);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                edittext.setText(sdf.format(myCalendar.getTime()));
            }

        };

        edittext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
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

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || secondName.isEmpty()) {
            Toast.makeText(getActivity(), "Please, fill all the fields.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("secondName", secondName);
        user.put("birthday", myCalendar.getTime());
        user.put("email", email);

//         Uri file = Uri.fromFile(new File());
//        imagesRef.putFile(file)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Get a URL to the uploaded content
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                })
//                .addOnFailureListener(exception -> {
//                    // Handle unsuccessful uploads
//                    // ...
//                });

//        db.collection("group mates")
//                .add(user)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getActivity(),"DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> Toast.makeText(getActivity(),"Error adding document" + e,
//                        Toast.LENGTH_SHORT).show());

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Created new group mate",
                                Toast.LENGTH_LONG).show();

                    } else {
                        System.out.println("createUserWithEmail:failure" + task.getException());
                        Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });


    }

}