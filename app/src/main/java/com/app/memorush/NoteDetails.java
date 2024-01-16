package com.app.memorush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetails extends AppCompatActivity {

    EditText eTextTitlenote, eTextContentnote;
    ImageButton BtnSave;
    TextView pageTitleTV;
    String title, content, docId;
    boolean isEditMode = false;
    TextView delNoteTVBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        eTextTitlenote = findViewById(R.id.notetitle);
        eTextContentnote = findViewById(R.id.notecontent);
        BtnSave = findViewById(R.id.savenotebtn);
        pageTitleTV = findViewById(R.id.pagetitle);
        delNoteTVBtn = findViewById(R.id.delnotebtn);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        eTextTitlenote.setText(title);
        eTextContentnote.setText(content);
        if (isEditMode){
            pageTitleTV.setText("Edit your note");
            delNoteTVBtn.setVisibility(View.VISIBLE);
        }

        BtnSave.setOnClickListener( (v) -> saveNote() );

        delNoteTVBtn.setOnClickListener( (v)-> deleteNoteFromFirebase() );

    }

    void saveNote() {
        String noteTitle = eTextTitlenote.getText().toString();
        String noteContent = eTextContentnote.getText().toString();
        if (noteTitle == null || noteTitle.isEmpty()) {
            eTextTitlenote.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetails.this, "Note added successfully");
                    finish();
                } else {
                    Utility.showToast(NoteDetails.this, "Failed while adding note");
                }
            }
        });

    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetails.this, "Note deleted successfully");
                    finish();
                } else {
                    Utility.showToast(NoteDetails.this, "Failed while deleting note");
                }
            }
        });
    }

}
