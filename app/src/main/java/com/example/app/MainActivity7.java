package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity7 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Demander la fonctionnalité de fenêtre sans titre
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Définir l'activité en plein écran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Définir le layout de l'activité
        setContentView(R.layout.activity_main7); // Assurez-vous que le layout est bien "activity_main7"

        // Initialiser le bouton après avoir défini le layout
        Button letsgo = findViewById(R.id.buttonletsgo);
        // Assurez-vous que le bouton n'est pas null
        if (letsgo != null) {
            letsgo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Créer une intention pour naviguer vers MainActivity
                    Intent intent = new Intent(MainActivity7.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
