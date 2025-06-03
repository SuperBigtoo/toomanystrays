package com.example.toomanystrays.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toomanystrays.R;
import com.example.toomanystrays.models.StrayAnimal;
import com.example.toomanystrays.viewholder.ViewHolderStray;

import java.util.ArrayList;

public class StrayAdapter extends RecyclerView.Adapter<ViewHolderStray> {

    Context context;
    ArrayList<StrayAnimal> strayAnimals;

    public StrayAdapter(Context context, ArrayList<StrayAnimal> strayAnimals) {
        this.context = context;
        this.strayAnimals = strayAnimals;
    }

    @NonNull
    @Override
    public ViewHolderStray onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderStray(LayoutInflater.from(context).inflate(R.layout.stray_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderStray holder, int position) {
        holder.strayName.setText(strayAnimals.get(position).getStray_name());
        holder.strayDetails.setText(strayAnimals.get(position).getDetails());

        if (strayAnimals.get(position).getImage_url() != null) {
            String url = strayAnimals.get(position).getImage_url();
            byte[] bytes = Base64.decode(url, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.imageStray.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return strayAnimals.size();
    }
}
