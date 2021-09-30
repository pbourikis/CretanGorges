package gr.unipi.cretangorges;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Cloud Storage
    FirebaseStorage storage;
    StorageReference storageGorges;

    // Database
    FirebaseDatabase database;
    DatabaseReference GorgesDB;

    private RecyclerView GorgeCard;

    //Gorges
    public ArrayList<Gorge> gorges = new ArrayList<Gorge>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GorgeCard = findViewById(R.id.Gorge);

        //Initialize databases
        database = FirebaseDatabase.getInstance();
        GorgesDB = database.getReference("Gorges");
        //Initialize storage
        storage = FirebaseStorage.getInstance();
        storageGorges = storage.getReference("/Gorges/");

        //Layouts
        //btn = findViewById(R.id.btn);

        //Loop through Gorges
        GorgesDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Gorge gorge = snapshot.child(String.valueOf(i)).getValue(Gorge.class);
                    System.out.println(gorge.length);
                    gorges.add(gorge);
                    i++;
                }
                createCards();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void createCards(){
        // we are initializing our adapter class and passing our arraylist to it.
        GorgeAdapter gorgeAdapter = new GorgeAdapter(MainActivity.this, gorges);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        GorgeCard.setLayoutManager(linearLayoutManager);
        GorgeCard.setAdapter(gorgeAdapter);
    }

}