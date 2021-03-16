package com.example.contactdetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText name, number, address;
    Button save;
    FirebaseFirestore firebaseFirestore;
    public  ProgressDialog pd;
    List<DetailsModel> detailsModels;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView mFirestoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        pd = new ProgressDialog(this);
        save = findViewById(R.id.save);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.firestore_list);
        detailsModels = new ArrayList<>();

        Query query = firebaseFirestore.collection("Details");
        FirestoreRecyclerOptions<DetailsModel> options = new FirestoreRecyclerOptions.Builder<DetailsModel>()
                .setQuery(query, DetailsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DetailsModel, DetailsViewHolder>(options) {
            @NonNull
            @Override
            public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                return new DetailsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DetailsViewHolder holder, int position, @NonNull DetailsModel model) {

                holder.list_name.setText(model.getName());
                holder.list_number.setText(model.getNumber());
                holder.list_address.setText(model.getAddress());

            }
            public void deleteItem(int position){

            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sname = name.getText().toString();
                String snumber = number.getText().toString();
                String saddress = address.getText().toString();


                Map<String,Object> details = new HashMap<>();
                details.put("name", sname);
                details.put("number", snumber);
                details.put("address", saddress);

                firebaseFirestore.collection("Details")
                        .add(details).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(MainActivity.this, "data saved",Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

                name.setText("");
                number.setText("");
                address.setText("");

            }

        });

//        fetchData = new ArrayList<>();
//        db.collection("Details").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//
//                                FetchData data = (FetchData) document.getData();
//                                fetchData.add(data);
//                                Log.d("TAG", document.getId() + " => " + document.getData());
//                            }
//                            helperAdapter = new HelperAdapter(fetchData);
//                            recyclerView.setAdapter(helperAdapter);
//                        } else {
//                            Log.w("TAG", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
    }


    private class DetailsViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_number, list_address;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.list_name);
            list_number = itemView.findViewById(R.id.list_number);
            list_address = itemView.findViewById(R.id.list_address);

        }

        public void onItemLongClick(View view, final int position){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            String[] options = {"Delete"};
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0){
                        deleteData(position);
                    }
                }
            }).create().show();
        }

        private void deleteData(int index){

            pd.setTitle("Loading Data....");
            pd.show();

            firebaseFirestore.collection("Details").document(detailsModels.get(index).getName())
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            // Toast.makeText(MainActivity.this, "Deleted...", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}