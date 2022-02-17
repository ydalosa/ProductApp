package com.example.productapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdapterFilms extends FirestoreRecyclerAdapter<ModelFilms, AdapterFilms.FilmsViewHolder> {

    private static final String TAG = "Adapter Firestore";

       /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterFilms(@NonNull FirestoreRecyclerOptions<ModelFilms> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FilmsViewHolder holder, int position, @NonNull ModelFilms model) {
        String affiche = model.getAffiche();
        String titre = model.getTitre();
        String synopsis = model.getSynopsis();

        holder.tvTitre.setText(titre);
        holder.tvSynopsis.setText(synopsis);

        // Ajout de Glide pour l'affichage de l'affiche (image)
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher);

        Context context = holder.ivAffiche.getContext();
        Glide.with(context)
                .load(affiche)
                .apply(options)
                .fitCenter()
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivAffiche);
    }

    @NonNull
    @Override
    public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_film, parent, false);
        return new FilmsViewHolder(view);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.e(TAG, "Error Firebase : " + e.getMessage());
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivAffiche;
        public TextView tvTitre, tvSynopsis;

        public FilmsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAffiche = itemView.findViewById(R.id.ivAffiche);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 int position = getBindingAdapterPosition();
                 if(position != RecyclerView.NO_POSITION && filmClickListener != null){
                     DocumentSnapshot filmSnapshot = getSnapshots().getSnapshot(position);
                     filmClickListener.onItemClick(filmSnapshot, position);
                 }
                }
            });
        }
    }

    /** #1 Interface **/
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    private OnItemClickListener filmClickListener;

    public void setOnItemClickListener(OnItemClickListener filmClickListener){
        this.filmClickListener = filmClickListener;
    }
    }











































