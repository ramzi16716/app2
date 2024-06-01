package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> usersList;

    public UsersAdapter(List<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.nom.setText(user.nom);
        holder.prenom.setText(user.prenom);
        holder.numerotel.setText(user.numerotel);
        holder.email.setText(user.email);
        holder.specialite.setText(user.specialite);
        holder.username.setText(user.username);
        holder.password.setText(user.password);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView nom, prenom, numerotel, email, specialite, username,password;

        public UserViewHolder(View view) {
            super(view);
            nom = view.findViewById(R.id.nom);
            prenom = view.findViewById(R.id.prenom);
            numerotel = view.findViewById(R.id.numerotel);
            email = view.findViewById(R.id.email);
            specialite = view.findViewById(R.id.specialite);
            username = view.findViewById(R.id.username);
            password=view.findViewById(R.id.password);
        }
    }
}
class User {
    public String nom;
    public String prenom;
    public String numerotel;
    public String m;
    public String mc;
    public String add;
    public String email;
    public String specialite;
    public String username;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String nom, String prenom, String numerotel, String m, String mc, String add, String email, String specialite, String username, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.numerotel = numerotel;
        this.m = m;
        this.mc = mc;
        this.add = add;
        this.email = email;
        this.specialite = specialite;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters (if needed)
}
