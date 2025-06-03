package com.example.toomanystrays.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toomanystrays.R;

public class ViewHolderComment extends RecyclerView.ViewHolder {

    public TextView commentUsername, commentDetails;
    public ViewHolderComment(@NonNull View itemView) {
        super(itemView);
        commentUsername = itemView.findViewById(R.id.commentUsername);
        commentDetails = itemView.findViewById(R.id.commentDetail);
    }
}
