package com.androsov.groupjournal;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androsov.groupjournal.dummy.StudentsContent.Student;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Student}.
 * TODO: Replace the implementation with code for your data type.
 */
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
        holder.mImageView.setImageURI(Uri.parse(mValues.get(position).images.get(0)));
        holder.mNameText.setText(mValues.get(position).firstName + " " + mValues.get(position).lastName + " " + mValues.get(position).secondName);
        holder.mBirthdayText.setText((CharSequence) mValues.get(position).birthday);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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
            mImageView = (ImageView) view.findViewById(R.id.student_avatar);
            mNameText = (TextView) view.findViewById(R.id.student_name);
            mBirthdayText = (TextView) view.findViewById(R.id.student_birthday);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameText.getText() + "'";
        }
    }
}