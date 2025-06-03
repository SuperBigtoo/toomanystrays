package com.example.toomanystrays.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toomanystrays.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ViewHolderStray extends RecyclerView.ViewHolder {

    public TextView strayName, strayDetails;
    public ShapeableImageView imageStray;

    public ViewHolderStray(@NonNull View itemView) {
        super(itemView);
        strayName = itemView.findViewById(R.id.strayName);
        strayDetails = itemView.findViewById(R.id.strayDetail);
        imageStray = itemView.findViewById(R.id.imageStray);
    }
}
