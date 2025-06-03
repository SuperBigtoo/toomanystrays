package com.example.toomanystrays.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toomanystrays.R;
import com.example.toomanystrays.models.Comment;
import com.example.toomanystrays.viewholder.ViewHolderComment;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<ViewHolderComment>{

    Context context;
    ArrayList<Comment> comments;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderComment(LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderComment holder, int position) {
        holder.commentUsername.setText(comments.get(position).getUsername());
        holder.commentDetails.setText(comments.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
