package com.androsov.groupjournal;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

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
        return view;
    }

    public void btnCreateClick(View view) {
        EditText emailEditText = (EditText) getView().findViewById(R.id.edit_text_email_create);
        String email = emailEditText.getText().toString();
        String password = ((EditText) getView().findViewById(R.id.edit_text_password_create)).getText().toString();


        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//        imagesRef.putFile(file)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Get a URL to the uploaded content
////                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
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
                        System.out.println("createUserWithEmail");
                        Toast.makeText(getActivity(), "Created new group mate",
                                Toast.LENGTH_LONG).show();
                        FirebaseUser user1 = mAuth.getCurrentUser();

                    } else {
                        System.out.println("createUserWithEmail:failure" + task.getException());
                        Toast.makeText(getActivity(), "Authentication failed: " + task.getException(),
                                Toast.LENGTH_LONG).show();
                    }
                });


    }

}