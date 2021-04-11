package com.androsov.groupjournal;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import static com.androsov.groupjournal.MyStudentRecyclerViewAdapter.getImageBitmap;


public class MyGalleryRecyclerViewAdapter extends RecyclerView.Adapter<MyGalleryRecyclerViewAdapter.ViewHolder> {

    private final List<String> images;

    public MyGalleryRecyclerViewAdapter(List<String> items) {
        images = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.url = images.get(position);
        holder.mImageView.setImageBitmap(getImageBitmap(images.get(position)));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public String url;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.student_image);
            mImageView.setOnClickListener(v -> {
                TabBarActivity tabBarActivity = (TabBarActivity) view.getContext();
                ImageFullFragment imageFullFragment = new ImageFullFragment(url);
                tabBarActivity.setFragment(imageFullFragment, "");
                tabBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + url;
        }
    }
}