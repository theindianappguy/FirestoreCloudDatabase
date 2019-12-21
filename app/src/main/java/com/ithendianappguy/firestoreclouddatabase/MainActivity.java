package com.ithendianappguy.firestoreclouddatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText titleEt, descEt;
    private Button saveBtn, loadbtn, updateDescription, deleteDescriptionBtn, deleteNoteBtn;
    private TextView myNotebookTv;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.collection("Notebook").document("My First Note");
    // or DocumentReference
    // there is something called collection reference as well so that we can use the way we want

    private ListenerRegistration noteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEt = findViewById(R.id.title);
        descEt = findViewById(R.id.desc);

        loadbtn = findViewById(R.id.loadBtn);
        myNotebookTv = findViewById(R.id.myNotebookTv);
        updateDescription = findViewById(R.id.updateDescription);
        deleteNoteBtn = findViewById(R.id.deleteNoteBtn);
        deleteDescriptionBtn = findViewById(R.id.deleteDescriptionBtn);

        deleteNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteRef.delete();
            }
        });

        deleteDescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDescription();
            }
        });

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteOnline();

            }
        });

        loadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNoteData();
            }
        });

        updateDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDesc();
            }
        });

    }

    private void deleteDescription() {

        //Map<String, Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, FieldValue.delete());

        //noteRef.set(note);
        //OR
        noteRef.update(KEY_DESCRIPTION, FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void updateDesc() {
        String description = descEt.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION, description);

        //this will overwrite the document removing the title
        //noteRef.set(note);

        //this will update what it have basically getting merged with already existing document
        //noteRef.set(note, SetOptions.merge());

        //if we want to update one KEY PAIR in document we can also use
        noteRef.update(KEY_DESCRIPTION, description);

        //the difference between using .set and .update is that .update will only update if the file dont exits nothing happens
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*noteListener = */
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error While Loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {
                    String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);

                    myNotebookTv.setText("Title: " + title + "\n" + "Description: " + description);

                } else {
                    myNotebookTv.setText("");
                }
            }
        });
    }

  /*  @Override
    protected void onStop() {
        super.onStop();
        // its important to detach this otherwise we will use bandwidth when even app is not in use
        noteListener.remove();
    }*/

    private void loadNoteData() {

        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                   /* String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);
                    //OR
                    //Map<String,Object> note = documentSnapshot.getData();*/

                    Note note = documentSnapshot.toObject(Note.class);
                    String title = note.getTitle();
                    String description  = note.getDescription();

                    myNotebookTv.setText("Title: " + title + "\n" + "Description: " + description);

                } else {
                    Toast.makeText(MainActivity.this, "Document Does not Exists", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + e.toString());

            }
        });
    }

    private void saveNoteOnline() {

        String title = titleEt.getText().toString();
        String description = descEt.getText().toString();

        /*Map<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE,title);
        note.put(KEY_DESCRIPTION,description);*/

        Note note = new Note();
        note.setTitle(title);
        note.setDescription(description);

        db.collection("Notebook").document("My First Note")
                .set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Saved Succesfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }
}
