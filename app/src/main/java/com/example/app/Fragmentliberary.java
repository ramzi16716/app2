// Inside Fragmentliberary.java
package com.example.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fragmentliberary extends Fragment {
    private TextView userNameTextView;
    private ImageView image1;
    private TextView prenom_input;
    private TextView numerotel_input;
    private TextView m_input;
    private TextView mc_input;
    private TextView add_input;
    private TextView email_input;
    private TextView autre_input;
    private TextView blood_input;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public Fragmentliberary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liberary, container, false);
        userNameTextView = view.findViewById(R.id.nom_input);
        image1 = view.findViewById(R.id.image1);
        prenom_input = view.findViewById(R.id.prenom_input);
        numerotel_input = view.findViewById(R.id.numerotel_input);
        m_input = view.findViewById(R.id.m_input);
        mc_input = view.findViewById(R.id.mc_input);
        add_input = view.findViewById(R.id.add_input);
        email_input = view.findViewById(R.id.email_input);
        autre_input = view.findViewById(R.id.autre_input);
        blood_input = view.findViewById(R.id.blood_input);
        return view;
    }
    public String getNomInput() {
        return userNameTextView.getText().toString().trim();
    }

    public String getBloodInput() {
        return blood_input.getText().toString().trim();
}
    public String getprenom() {
        return prenom_input.getText().toString().trim();
    }
    public String getnumerotel() {
        return numerotel_input.getText().toString().trim();
    }


}
