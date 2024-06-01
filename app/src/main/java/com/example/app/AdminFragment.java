package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class AdminFragment extends Fragment {

    private ImageButton imageButton, imageButton1;
    private Bitmap selectedImage1, selectedImage2;
    private EditText nomInput, prenomInput, numerotelInput, mInput, mcInput, addInput, emailInput, user_name, password;
    private Spinner spenner;
    private int imageButtonClicked;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        // Recherche des vues par leur ID
        imageButton = rootView.findViewById(R.id.image_btn);
        imageButton1 = rootView.findViewById(R.id.image_btn1);
        nomInput = rootView.findViewById(R.id.nom_input);
        prenomInput = rootView.findViewById(R.id.prenom_input);
        numerotelInput = rootView.findViewById(R.id.numerotel_input);
        mInput = rootView.findViewById(R.id.m_input);
        mcInput = rootView.findViewById(R.id.mc_input);
        addInput = rootView.findViewById(R.id.add_input);
        emailInput = rootView.findViewById(R.id.email_input);

        spenner = rootView.findViewById(R.id.s_spinner);
        user_name = rootView.findViewById(R.id.username);
        password = rootView.findViewById(R.id.password_input);

        // Ajout d'un écouteur d'événements à l'ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonClicked = R.id.image_btn;
                selectImage();
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonClicked = R.id.image_btn1;
                selectImage();
            }
        });

        // Ajout d'un écouteur d'événements au bouton d'ajout d'utilisateur
        Button addUserBtn = rootView.findViewById(R.id.login_btn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ajouter l'utilisateur après vérification des images
                addUser();
            }
        });

        return rootView;
    }

    private void selectImage() {
        // Ouvrir la galerie pour sélectionner une image
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            // Image sélectionnée depuis la galerie
            Uri imageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                if (imageButtonClicked == R.id.image_btn) {
                    selectedImage1 = selectedImage;
                    setImage(imageButton, selectedImage1);
                } else if (imageButtonClicked == R.id.image_btn1) {
                    selectedImage2 = selectedImage;
                    setImage(imageButton1, selectedImage2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setImage(ImageButton button, Bitmap bitmap) {
        if (bitmap != null) {
            // Set the image bitmap to the ImageButton and make it scale to fit the button size
            button.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            button.setImageBitmap(bitmap);
        }
    }

    private void addUser() {
        if (nomInput.getText().toString().isEmpty() ||
                prenomInput.getText().toString().isEmpty() ||
                numerotelInput.getText().toString().isEmpty() ||
                mInput.getText().toString().isEmpty() ||
                mcInput.getText().toString().isEmpty() ||
                addInput.getText().toString().isEmpty() ||
                emailInput.getText().toString().isEmpty() ||
                spenner.getSelectedItem().toString().equals("Select a Blood type:") ||
                user_name.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty()) {

            // عرض رسالة خطأ إذا كان هناك حقل فارغ
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // التحقق من وجود الصور
        if (selectedImage1 == null || selectedImage2 == null) {
            Toast.makeText(getContext(), "Please select both images", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupérer une référence à votre base de données Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users n");

        // التحقق مما إذا كان المستخدم موجودًا بالفعل
        String enteredUsername = user_name.getText().toString();
        usersRef.orderByChild("username").equalTo(enteredUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // المستخدم موجود بالفعل
                    Toast.makeText(getContext(), "This user already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // المستخدم غير موجود، يمكننا إضافته
                    User user = new User(
                            nomInput.getText().toString(),
                            prenomInput.getText().toString(),
                            numerotelInput.getText().toString(),
                            mInput.getText().toString(),
                            mcInput.getText().toString(),
                            addInput.getText().toString(),
                            emailInput.getText().toString(),
                            spenner.getSelectedItem().toString(),
                            user_name.getText().toString(),
                            password.getText().toString()
                    );

                    // Ajouter l'utilisateur à la base de données
                    usersRef.push().setValue(user);

                    // Afficher un message de succès
                    Toast.makeText(getContext(), "User added successfully", Toast.LENGTH_SHORT).show();

                    // Réinitialiser les champs
                    resetFields();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // التعامل مع الخطأ
                Toast.makeText(getContext(), "Error checking user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetFields() {
        nomInput.setText("");
        prenomInput.setText("");
        numerotelInput.setText("");
        mInput.setText("");
        mcInput.setText("");
        addInput.setText("");
        emailInput.setText("");
        user_name.setText("");
        password.setText("");
        spenner.setSelection(0); // Assuming 0 is the default position for the prompt item

        selectedImage1 = null;
        selectedImage2 = null;
        imageButton.setImageResource(R.drawable.default_image); // Assuming you have a default image resource
        imageButton1.setImageResource(R.drawable.default_image); // Assuming you have a default image resource
    }

    // Classe User pour représenter les données de l'utilisateur

}

