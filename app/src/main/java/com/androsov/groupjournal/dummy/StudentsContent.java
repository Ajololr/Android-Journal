package com.androsov.groupjournal.dummy;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androsov.groupjournal.MyStudentRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.androsov.groupjournal.MainActivity.db;

public class StudentsContent {

    public static final List<Student> STUDENTS = new ArrayList<>();

    public static final Map<String, Student> ITEM_MAP = new HashMap<>();


    private static void addItem(Student item) {
        STUDENTS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void refresh(MyStudentRecyclerViewAdapter mAdapter) throws Error{
        STUDENTS.clear();
        ITEM_MAP.clear();

        db.collection("group mates")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> documentData = document.getData();
                            addItem(new Student(document.getId(),(String) documentData.get("firstName"),
                                    (String) documentData.get("lastName"),
                                    (String) documentData.get("secondName"),
                                    (String) documentData.get("latitude"),
                                    (String) documentData.get("longitude"),
                                    (ArrayList<String>) documentData.get("images"),
                                    ((Timestamp) documentData.get("birthday")).toDate(),
                                    (String) documentData.get("videoUrl")));
                        }
                    } else {
                        System.out.println(task.getException());
                        throw new Error( "Error getting documents: " + task.getException());
                    }
                    mAdapter.notifyDataSetChanged();
                });

    }

    public static class Student implements Serializable {
        public String id;
        public String firstName;
        public String lastName;
        public String secondName;
        public String latitude;
        public String longitude;
        public ArrayList<String> images;
        public Date birthday;
        public String videoUrl;

        public Student(String id, String firstName, String lastName, String secondName, String latitude, String longitude, ArrayList<String> images, Date birthday, String videoUrl) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.secondName = secondName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.images = images;
            this.birthday = birthday;
            this.videoUrl = videoUrl;
        }

        public Student() {}

        @Override
        public String toString() {
            return id;
        }
    }
}