package com.example.app;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity4 extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go back
        Button backButton = findViewById(R.id.back_b);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button to send email
        Button sendBtn = findViewById(R.id.login_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    sendEmail();
                } else {
                    Toast.makeText(MainActivity4.this, "Veuillez remplir tous les champs correctement.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFields() {
        EditText usernomForget = findViewById(R.id.usernom_forget);
        EditText nomForget = findViewById(R.id.nom_forget);
        EditText prenomForget = findViewById(R.id.prenom_forget);
        EditText emailInput = findViewById(R.id.email_input);
        Spinner specialiteSpinner = findViewById(R.id.specialite_spinner);

        String usernom = usernomForget.getText().toString().trim();
        String nom = nomForget.getText().toString().trim();
        String prenom = prenomForget.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String specialite = specialiteSpinner.getSelectedItem().toString();

        if (usernom.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || specialite.equals("Select a Spécialité:")) {
            return false; // Certains champs sont vides
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false; // L'adresse e-mail n'est pas valide
        }

        return true; // Tous les champs sont remplis correctement
    }

    private void sendEmail() {
        String email = "ghouatmounib@gmail.com"; // Adresse e-mail du destinataire
        String subject = "Demande de récupération de compte";
        String usernom = ((EditText) findViewById(R.id.usernom_forget)).getText().toString().trim();
        String nom = ((EditText) findViewById(R.id.nom_forget)).getText().toString().trim();
        String prenom = ((EditText) findViewById(R.id.prenom_forget)).getText().toString().trim();
        String emailFrom = ((EditText) findViewById(R.id.email_input)).getText().toString().trim();
        String specialite = ((Spinner) findViewById(R.id.specialite_spinner)).getSelectedItem().toString();

        String message = "Bonjour,\n\nJe souhaite récupérer mon compte. Voici mes informations :\n\n" +
                "User Name: " + usernom + "\n" +
                "Family Name: " + nom + "\n" +
                "Name: " + prenom + "\n" +
                "Email: " + emailFrom + "\n" +
                "Specialité: " + specialite;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // Type MIME pour les e-mails

        // Ajoute l'adresse e-mail du destinataire, le sujet et le corps du message
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        emailIntent.setPackage("com.google.android.gm");
        startActivity(emailIntent);


        Toast.makeText(MainActivity4.this, "Email envoyé.", Toast.LENGTH_SHORT).show();
    }
}
