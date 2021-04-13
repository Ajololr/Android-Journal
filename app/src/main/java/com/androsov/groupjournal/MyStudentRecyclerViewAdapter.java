package com.androsov.groupjournal;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.androsov.groupjournal.OptionsFragment.fontSize;
import static com.androsov.groupjournal.StudentFragmentList.setData;

public class MyStudentRecyclerViewAdapter extends RecyclerView.Adapter<MyStudentRecyclerViewAdapter.ViewHolder> implements Filterable {

    private final List<Student> mValues;
    private static List<Student> studentsFiltered = new ArrayList<>();
    public static boolean isFiltering = false;

    public MyStudentRecyclerViewAdapter(List<Student> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_student, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (isFiltering || !studentsFiltered.isEmpty()) {
            holder.mItem = studentsFiltered.get(position);
            holder.mImageView.setImageBitmap(getImageBitmap(studentsFiltered.get(position).images.get(0)));
            holder.mNameText.setText(studentsFiltered.get(position).firstName + " " + studentsFiltered.get(position).lastName + " " + studentsFiltered.get(position).secondName);
            holder.mBirthdayText.setText(new SimpleDateFormat("yyyy-mm-dd").format(studentsFiltered.get(position).birthday));
        } else {
            holder.mItem = mValues.get(position);
            holder.mImageView.setImageBitmap(getImageBitmap(mValues.get(position).images.get(0)));
            holder.mNameText.setText(mValues.get(position).firstName + " " + mValues.get(position).lastName + " " + mValues.get(position).secondName);
            holder.mBirthdayText.setText(new SimpleDateFormat("yyyy-mm-dd").format(mValues.get(position).birthday));
        }
    }

    @Override
    public int getItemCount() {
        return isFiltering ? studentsFiltered.size() : mValues.size();
    }

    public static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
        }
        return bm;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    isFiltering = false;
                    studentsFiltered = mValues;
                } else {
                    List<Student> filteredList = new ArrayList<>();
                    for (Student row : mValues) {
                        if ((row.lastName + row.firstName + row.secondName).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    studentsFiltered = filteredList;
                    isFiltering = true;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = studentsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                isFiltering = true;
                studentsFiltered = (ArrayList<Student>) filterResults.values;
                setData();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNameText;
        public final TextView mBirthdayText;
        public Student mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.student_avatar);
            mNameText = view.findViewById(R.id.student_name);
            mBirthdayText = view.findViewById(R.id.student_birthday);

            mBirthdayText.setTextSize(fontSize);
            mNameText.setTextSize(fontSize);

            mView.setOnClickListener(v -> {
                TabBarActivity tabBarActivity = (TabBarActivity) view.getContext();
                StudentDetailsFragment studentDetailsFragment = new StudentDetailsFragment(mItem);
                tabBarActivity.setFragment(studentDetailsFragment, mItem.lastName);
                tabBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameText.getText() + "'";
        }
    }
}