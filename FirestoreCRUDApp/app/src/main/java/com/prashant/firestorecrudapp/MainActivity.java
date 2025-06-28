package com.prashant.firestorecrudapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreCrudApp";

    // UI Elements
    private EditText nameEditText, emailEditText;
    private Button addButton, updateButton, deleteButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewUsers;

    // Firestore instance
    private FirebaseFirestore db;

    // RecyclerView Adapter and List
    private UserAdapter userAdapter;
    private List<User> userList;

    // To store the currently selected user's ID for update/delete operations
    private String selectedUserId = null;

    // Listener for real-time updates
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        // Setup RecyclerView
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        // Set click listener for RecyclerView items to populate input fields for update/delete
        userAdapter.setOnItemClickListener(user -> {
            selectedUserId = user.getId();
            nameEditText.setText(user.getName());
            emailEditText.setText(user.getEmail());
            Toast.makeText(MainActivity.this, "Selected: " + user.getName(), Toast.LENGTH_SHORT).show();
        });


        // Set up button click listeners
        addButton.setOnClickListener(v -> addUser());
        updateButton.setOnClickListener(v -> updateUser());
        deleteButton.setOnClickListener(v -> deleteUser());

        // Start listening for real-time updates
        listenForUsers();

    }

    /**
     * Adds a new user to the Firestore database.
     * Uses a HashMap to represent the user data.
     */
    private void addUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter name and email", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();

        // Create a new user map
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "User added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        clearInputFields();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "Error adding user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * Listens for real-time updates to the "users" collection.
     * This is the "Read" operation that keeps the RecyclerView updated.
     */
    private void listenForUsers() {
        showProgressBar();
        firestoreListener = db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot snapshots,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        hideProgressBar();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            Toast.makeText(MainActivity.this, "Error fetching users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        userList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot doc : snapshots) {
                            // Convert each document to a User object
                            User user = doc.toObject(User.class);
                            user.setId(doc.getId()); // Set the document ID
                            userList.add(user);
                        }
                        userAdapter.setUsers(userList); // Update the adapter
                        Log.d(TAG, "Users updated: " + userList.size());
                    }
                });
    }

    /**
     * Updates an existing user in the Firestore database.
     * Requires a selectedUserId to know which document to update.
     */
    private void updateUser() {
        if (selectedUserId == null) {
            Toast.makeText(this, "Please select a user to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Please enter new name and email for update", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();

        DocumentReference userRef = db.collection("users").document(selectedUserId);

        userRef.update("name", newName, "email", newEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                        selectedUserId = null; // Deselect user after update
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "Error updating user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    /**
     * Deletes a user from the Firestore database.
     * Requires a selectedUserId to know which document to delete.
     */
    private void deleteUser() {
        if (selectedUserId == null) {
            Toast.makeText(this, "Please select a user to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();

        db.collection("users").document(selectedUserId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "User deleted successfully!", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                        selectedUserId = null; // Deselect user after deletion
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    /**
     * Clears the input fields (EditTexts) and deselects any user.
     */
    private void clearInputFields() {
        nameEditText.setText("");
        emailEditText.setText("");
        selectedUserId = null;
    }

    /**
     * Shows the progress bar.
     */
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the progress bar.
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detach the Firestore listener to prevent memory leaks
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}