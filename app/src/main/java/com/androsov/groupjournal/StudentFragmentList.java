package com.androsov.groupjournal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import static com.androsov.groupjournal.MainActivity.db;


public class StudentFragmentList extends Fragment {
    static public MyStudentRecyclerViewAdapter recyclerViewAdapter;
    static View view;
    private SearchView searchView;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static int mColumnCount = 2;
    private static ArrayList<Student> studentArrayList;


    public static void loadData() {
        studentArrayList = new ArrayList<>();
        db.collection("group mates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student student = (Student) document.toObject(Student.class);
                                student.id = document.getId();
                                studentArrayList.add(student);
                            }
                        } else {

                        };
                        if (view != null) {
                            setData();
                        }
                    }
                });
    }

     public StudentFragmentList() {
         loadData();
    }

    public static void setData() {
        // Set the adapter
        View rv = view.findViewById(R.id.list);

        if (rv instanceof RecyclerView) {
            Context context = rv.getContext();
            RecyclerView recyclerView = (RecyclerView) rv;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerViewAdapter = new MyStudentRecyclerViewAdapter(studentArrayList);

            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public static StudentFragmentList newInstance(int columnCount) {
        StudentFragmentList fragment = new StudentFragmentList();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_student_list, container, false);

        setData();

        searchView = view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                recyclerViewAdapter.getFilter().filter(query);
                return false;
            }
        });

        return view;
    }
}