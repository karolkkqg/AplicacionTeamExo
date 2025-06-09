package com.example.aplicacionteamexo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> listaPosts;
    private boolean esModerador;
    private OnEliminarClickListener listener;

    public interface OnEliminarClickListener {
        void onEliminar(Post post);
    }

    public PostAdapter(List<Post> listaPosts, boolean esModerador, OnEliminarClickListener listener) {
        this.listaPosts = listaPosts;
        this.esModerador = esModerador;
        this.listener = listener;
    }



    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMultimedia;
        ImageButton btnLike, btnEmojiPos, btnDislike, btnEmojiNeg, btnEliminar;
        EditText etComentario;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMultimedia = itemView.findViewById(R.id.img_multimedia);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
            btnEmojiPos = itemView.findViewById(R.id.btn_emoji_heart);
            btnEmojiNeg = itemView.findViewById(R.id.btn_emoji_broken);
            etComentario = itemView.findViewById(R.id.et_comentario);
            btnEliminar = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Post post) {
            // Aquí puedes cargar imagenes si usas Glide o Picasso, etc.

            if (esModerador) {
                btnEliminar.setVisibility(View.VISIBLE);
                btnEliminar.setOnClickListener(v -> listener.onEliminar(post));
            } else {
                btnEliminar.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(listaPosts.get(position));
    }

    @Override
    public int getItemCount() {
        return listaPosts.size();
    }
}

