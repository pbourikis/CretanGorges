package gr.unipi.cretangorges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InfoActivity extends AppCompatActivity {

    // Cloud Storage
    FirebaseStorage storage;
    StorageReference storageGorges;

    public TextView infoText;
    public TextView gorgeTitle;
    public Button map_btn;

    public Gorge gorge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Initialize storage
        storage = FirebaseStorage.getInstance();
        storageGorges = storage.getReference("/Gorges/");

        Intent in = getIntent();
        gorge = (Gorge) in.getSerializableExtra("Gorge");
        System.out.println("Length " + gorge.length);
        System.out.println("Name " + gorge.name);
        System.out.println("Text " + gorge.text);
        System.out.println("Difficulty " + gorge.difficulty);
        System.out.println("Price " + gorge.price);
        System.out.println("Time " + gorge.time);
        System.out.println("ID " + gorge.id);

        infoText = findViewById(R.id.info_txt);
        gorgeTitle = findViewById(R.id.gorge_title);
        map_btn = findViewById(R.id.map_btn);
        infoText.setText(gorge.text);
        gorgeTitle.setText(gorge.name);

        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMaps(gorge);
            }
        });


    }
    public void goToMaps(Gorge gorge){
        Intent intent = new Intent(this , MapsActivity.class);
        intent.putExtra("Gorge", gorge);
        startActivity(intent);
    }
}