package com.example.tomipc.foodzye.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Review;

import java.util.List;


    public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

        private List<Review> reviewList;
        private Context c;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView comment, username, date;
            public ImageView userImage;
            public AppCompatRatingBar rate;

            public MyViewHolder(View view) {
                super(view);
                comment = (TextView) view.findViewById(R.id.review_review_text);
                username = (TextView) view.findViewById(R.id.review_name_user);
                userImage = (ImageView) view.findViewById(R.id.review_image_user);
                rate = (AppCompatRatingBar) view.findViewById(R.id.review_food_rate);
                date = (TextView) view.findViewById(R.id.review_food_date);
            }
        }


        public ReviewAdapter(List<Review> reviewList, Context c) {
            this.reviewList = reviewList;
            this.c = c;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Review review = reviewList.get(position);

            holder.comment.setText(review.getComment());

            if(review.getUserPicture().equals(""))
                holder.userImage.setImageResource(R.drawable.food_def);
            else
                Glide.with(c).load(Database.URL + review.getUserPicture()).thumbnail(0.3f).into(holder.userImage);

            holder.rate.setRating((float) review.getRate());
            holder.username.setText(review.getUsername());

            if(!(review.getDateUpdated().equals("")))
                holder.date.setText(review.getDateUpdated());
            else
                holder.date.setText(review.getDateCreated());
        }

        @Override
        public int getItemCount() {

            return reviewList.size();
        }
}
