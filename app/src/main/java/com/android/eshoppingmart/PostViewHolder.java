package com.android.eshoppingmart;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {
    TextView textViewTitle;
    TextView textViewBody;
    TextView textviewPrice;
    CardView cardView;
    ImageView Img;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.item_card);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewBody = itemView.findViewById(R.id.textViewBody);
        textviewPrice = itemView.findViewById(R.id.textViewPrice);
        Img= itemView.findViewById(R.id.imageView2);
    }

    public void setItem(final Post post, View.OnLongClickListener onLongClickListener){
        textViewTitle.setText(post.title);
        textViewBody.setText(post.body);
        textviewPrice.setText("Rs."+post.price);

        Picasso.get().load(post.imageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(Img, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(post.imageUrl).into(Img);
            }
        });
        cardView.setOnLongClickListener(onLongClickListener);


    }


}