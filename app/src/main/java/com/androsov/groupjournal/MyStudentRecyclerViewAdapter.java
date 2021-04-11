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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.androsov.groupjournal.OptionsFragment.fontSize;

public class MyStudentRecyclerViewAdapter extends RecyclerView.Adapter<MyStudentRecyclerViewAdapter.ViewHolder> {

    private final List<Student> mValues;

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
        holder.mItem = mValues.get(position);
        holder.mImageView.setImageBitmap(getImageBitmap(mValues.get(position).images.get(0)));
        holder.mNameText.setText(mValues.get(position).firstName + " " + mValues.get(position).lastName + " " + mValues.get(position).secondName);
        holder.mBirthdayText.setText(new SimpleDateFormat("yyyy-mm-dd").format(mValues.get(position).birthday));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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