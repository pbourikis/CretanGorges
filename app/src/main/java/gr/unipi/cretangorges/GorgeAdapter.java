package gr.unipi.cretangorges;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class GorgeAdapter extends RecyclerView.Adapter<GorgeAdapter.Viewholder> {

    StorageReference storageReference = FirebaseStorage.getInstance().getReference("/Gorges/");
    StorageReference photoRef;

    private Context context;
    private ArrayList<Gorge> gorgeArrayList;

    // Constructor
    public GorgeAdapter(Context context, ArrayList<Gorge> gorgeList) {
        this.context = context;
        this.gorgeArrayList = gorgeList;
    }

    @NonNull
    @Override
    public GorgeAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GorgeAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        Gorge gorge = gorgeArrayList.get(position);
        photoRef = storageReference.child(gorge.id.concat("/").concat(gorge.id).concat("1.jpg"));
        holder.gorgeName.setText(gorge.getName());
        holder.gorgeLength.setText("Μήκος : " + gorge.getLength()+ "χμ");
        holder.gorgeDifficulty.setText("Δυσκολία : " + gorge.getDifficulty());
        holder.gor = gorge;
        final long TWO_MEGABYTE = 3072 * 1024;
        photoRef.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.gorgePhoto.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.gorgePhoto.setImageResource(R.mipmap.gorge);
            }
        });
        //holder.gorgePhoto.setImageResource(gorge.getId());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return gorgeArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView gorgePhoto;
        private TextView gorgeName, gorgeLength,gorgeDifficulty;
        private Gorge gor;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            gorgePhoto = itemView.findViewById(R.id.idIVCourseImage);
            gorgeName = itemView.findViewById(R.id.idTVCourseName);
            gorgeLength = itemView.findViewById(R.id.idTVCourseRating);
            gorgeDifficulty = itemView.findViewById(R.id.idTVCourseRating2);

            gorgePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMaps(gor);
                }
            });
            gorgeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMaps(gor);
                }
            });
            gorgeLength.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMaps(gor);
                }
            });
        }
    }

    public void goToMaps(Gorge gorge){
        Intent intent = new Intent(context , InfoActivity.class);
        intent.putExtra("Gorge", gorge);
        context.startActivity(intent);
    }
}