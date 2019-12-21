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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static java.lang.Integer.parseInt;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    private EditText titleEt, descriptionEt, priorityEt;
    private Button loadBtn, addBtn;
    private TextView allText;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        loadBtn = findViewById(R.id.loadBtn);
        allText = findViewById(R.id.allText);
        addBtn = findViewById(R.id.addBtn);
        priorityEt = findViewById(R.id.priorityEt);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loading notes
                loadNote();

                //loading notes running compound Queries
                //loadNotePriorityNot2();
            }
        });
    }


    private void loadNotePriorityNot2() {

        Task task1 = notebookRef.whereLessThan("priority",2)
                .orderBy("priority")
                .get();

        Task task2 = notebookRef.whereGreaterThan("priority",2)
                .orderBy("priority")
                .get();

        Task<List<QuerySnapshot>> allTask = Tasks.whenAllSuccess(task1,task2);

        allTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {

                String data = "";

                for(QuerySnapshot queryDocumentSnapshots : querySnapshots){
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());

                        String documentId = note.getDocumentId();
                        String title = note.getTitle();
                        String description = note.getDescription();
                        int priority = note.getPriority();

                        data += "Priority: "+priority+" Title : " + title + "\nDescription: " + description + " Id: " + documentId+
                                "\n\n";

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.toString());
            }
        });
    }


    private void addNote() {
        String title = titleEt.getText().toString();
        String description = descriptionEt.getText().toString();

        if(priorityEt.length() == 0){
            priorityEt.setText("0");
        }
        int priority = parseInt(priorityEt.getText().toString());

        Note note = new Note(title, description,priority);
        notebookRef.add(note);
    }

    public void loadNote() {
        notebookRef.orderBy("priority")
                .startAt(2)
                .limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String data = "";

                for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {
                    Note note = documentSnapshots.toObject(Note.class);
                    note.setDocumentId(documentSnapshots.getId());

                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String desscription = note.getDescription();
                    int priority = note.getPriority();

                    data += "Priority: "+priority+" Title : " + title + "\nDescription: " + desscription + " Id: " + documentId+
                    "\n\n";
                }

                allText.setText(data);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.toString());
            }
        });
    }
}
