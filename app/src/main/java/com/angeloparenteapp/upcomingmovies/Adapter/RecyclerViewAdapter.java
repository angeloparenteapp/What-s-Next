package com.angeloparenteapp.upcomingmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.angeloparenteapp.upcomingmovies.MainPoster;
import com.angeloparenteapp.upcomingmovies.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Adapter for the RecyclerView
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<MainPoster> listOfElements;
    private Context mContext;

    private String url = "https://image.tmdb.org/t/p/w500";

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView posterImage;
        public TextView posterTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.image_element);
            posterTitle = itemView.findViewById(R.id.title_element);
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<MainPoster> listOfElements) {
        this.mContext = context;
        this.listOfElements = listOfElements;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        final MainPoster currentElement = listOfElements.get(position);

        Glide.with(mContext).load(url + currentElement.getPosterImage()).into(holder.posterImage);
        holder.posterTitle.setText(currentElement.getPosterTitle());

        holder.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "You selected: " + currentElement.getPosterTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfElements.size();
    }
}
