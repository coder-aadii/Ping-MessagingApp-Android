package com.messagingapp.ping;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity {

    private ProgressBar progressBar; // Loading indicator

    // Firebase Database Reference
    private DatabaseReference usersRef;

    // Adapter and data list for RecyclerView
    private UserAdapter userAdapter;
    private ArrayList<Users> usersArrayList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile); // Set the layout file for the activity

        // Initialize UI Components
        // UI Components
        SearchView searchUserView = findViewById(R.id.search_user_view); // Assume you added SearchView in your layout
        RecyclerView userSearchResults = findViewById(R.id.user_search_results); // RecyclerView ID from your layout
        progressBar = findViewById(R.id.progress_bar); // ProgressBar ID from your layout

        // Firebase Database Reference to "Users" node
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize RecyclerView with an empty list
        usersArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(this, usersArrayList);
        userSearchResults.setLayoutManager(new LinearLayoutManager(this));
        userSearchResults.setAdapter(userAdapter);

        // Set up SearchView listener for user search
        searchUserView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search when user presses search button
                if (!TextUtils.isEmpty(query)) {
                    searchUsers(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Search as user types
                if (!TextUtils.isEmpty(newText)) {
                    searchUsers(newText);
                } else {
                    usersArrayList.clear(); // Clear the list if search is empty
                    userAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    // Method to search users in Firebase database
    private void searchUsers(String searchText) {
        progressBar.setVisibility(View.VISIBLE); // Show loading indicator
        // Create a query that searches for both full name and email
        usersRef.orderByChild("fullName").startAt(searchText).endAt(searchText + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usersArrayList.clear(); // Clear previous search results

                        // Loop through full name results
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Users user = snapshot.getValue(Users.class);
                            if (user != null) {
                                usersArrayList.add(user); // Add matching user
                            }
                        }

                        // Now search by email as well
                        usersRef.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotEmail) {
                                        for (DataSnapshot snapshot : dataSnapshotEmail.getChildren()) {
                                            Users user = snapshot.getValue(Users.class);
                                            if (user != null && !usersArrayList.contains(user)) { // Check for null user and avoid duplicates
                                                usersArrayList.add(user); // Add matching user
                                            }
                                        }
                                        userAdapter.notifyDataSetChanged(); // Notify adapter to update RecyclerView
                                        progressBar.setVisibility(View.GONE); // Hide loading indicator
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressBar.setVisibility(View.GONE); // Hide loading indicator
                                        // Optionally show a toast or log the error
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE); // Hide loading indicator
                        // Optionally show a toast or log the error
                    }
                });
    }

}
