package com.androsov.groupjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import static com.androsov.groupjournal.MainActivity.mAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {
    View v;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
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
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);;
        v = view;

        TextView button = (TextView) view.findViewById(R.id.addNewGroupMateLabel);
        button.setOnClickListener(v -> newMateClick());

        Button loginBtn = (Button) view.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(v -> btnLoginClick());


        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int appColor = app_preferences.getInt("color", 0);

        if (appColor != 0) {
            loginBtn.setBackgroundColor(appColor);
        }


        return view;
    }

    public void btnLoginClick() {
        String email = ((EditText) v.findViewById(R.id.editTextTextEmailAddress)).getText().toString();
        String password = ((EditText) v.findViewById(R.id.edit_text_password_login)).getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please, fill all fields",
                    Toast.LENGTH_SHORT).show();
        } else  {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), TabBarActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    public void newMateClick() {
        NavDirections action =
                SignInFragmentDirections
                        .actionSignInFragmentToRegisterFragment();
        Navigation.findNavController(v).navigate(action);
    }
}