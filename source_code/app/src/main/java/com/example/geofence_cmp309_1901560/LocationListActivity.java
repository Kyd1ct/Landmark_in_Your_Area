package com.example.geofence_cmp309_1901560;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LocationListActivity extends AppCompatActivity implements EventListener<QuerySnapshot> {

    // Variables and objects
    private ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference locations = FirebaseFirestore.getInstance().collection(FirestoreLocation.COLLECTION_PATH);
    ArrayList<FirestoreLocation> data = new ArrayList<>();
    ArrayList<String> documentIDs = new ArrayList<>();
    LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        /* Get reference to list view */
        listView = findViewById(R.id.list_locations);

        // OnLongClick listener for data deletion
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Collection path and document ID based on list position
            DocumentReference docRef = db.document("Locations/" + documentIDs.get(position));

            // Deletion alert dialogue
            new AlertDialog.Builder(LocationListActivity.this)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")

                    // Listener forcing the dialog to take action before dismissing itself
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> docRef.delete().addOnSuccessListener(aVoid -> {

                        // Remove document and refresh activity (visual big within emulator)
                        documentIDs.remove(position);
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                        listView.invalidate();
                    }).addOnFailureListener(e -> Log.e(Utils.TAG, "Failed to delete file!", e)))

                    // Null listener closing the dialogue without actions
                    .setNegativeButton(android.R.string.no, null)
                    // Sets alert icons and shows the dialogue
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return false;
        });

        // Button for deleting all data
        Button btn_del = (Button) findViewById(R.id.btnDelete);

        // OnClickListener for the button
        btn_del.setOnClickListener(v -> new AlertDialog.Builder(LocationListActivity.this)
                .setTitle("Delete History")
                .setMessage("Are you sure you want to delete your entire history?")

                // Listener forcing the dialog to take action before dismissing itself
                .setPositiveButton(android.R.string.yes, (dialog, which) -> FirebaseFirestore.getInstance().collection("Locations")
                        // Get Collection
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            // Batch used to mass-delete all documents within the collection
                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot:snapshotList) {
                                batch.delete(snapshot.getReference());
                            }
                            // Commit delete action
                            batch.commit()
                                    .addOnSuccessListener(unused -> Log.d(Utils.TAG, "Documents Deleted"))
                                    .addOnFailureListener(e -> Log.e(Utils.TAG, "Failure to delete documents: ", e));
                            // Clear adapter and notify to clear the list view
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {

                        }))

                // Null listener closing the dialogue without actions
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());


        // Add a listener to the entire collection of contact which will notify of changes
        locations.addSnapshotListener(this);
        // Populate the location list from Firestore data
        populateList();

    }

    private void populateList(){
        // Get all location documents in collection
        locations.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get location as FirestoreLocation object

                            FirestoreLocation locData = document.toObject(FirestoreLocation.class);
                            // Set object's ID from document name (id)
                            locData.setID(document.getId());
                            // Add to list of locations
                            data.add(locData);
                            documentIDs.add(document.getId());
                        }
                        // Initialise the adapter with the list of locations
                        adapter = new LocationAdapter(LocationListActivity.this, data);
                        // Attach the adapter to our list view.
                        listView.setAdapter(adapter);
                    } else {
                        Log.w(Utils.TAG, "Error getting documents.", task.getException());
                    }

                });
    }

    // Process updates for any of the documents in the Firestore Locations collection
    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        // If exception occurs, don't try to do anything else, just display the error and return
        if (e != null) {
            Log.e(Utils.TAG, "Listen failed.", e);
            return;
        }

        // Update data from Firestore, then list via adapter if data changes
        if(data != null && adapter != null){ // Important checks to avoid crashes
            /* If there is a change these should not be null, it may be empty if
               all documents have been deleted form the collection. */
            if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()) {
                // Clears adapter and creates a new data list
                adapter.clear();
                data = new ArrayList<>();
                // Gets all documents in the affected collection
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Get the data for each item
                    FirestoreLocation loc = document.toObject(FirestoreLocation.class);
                    loc.setID(document.getId());
                    // Add location to the new list
                    data.add(loc);
                }
                // Add all data to adapter
                adapter.addAll(data);
                // Tell the adapter to redraw the listView with new dataset
                runOnUiThread(() -> adapter.notifyDataSetChanged());

            }
        }
    }



}
