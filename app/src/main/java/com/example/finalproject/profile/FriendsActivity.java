package com.example.finalproject.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class   FriendsActivity extends AppCompatActivity {
    EditText searchET;
    Button searchB;
    Button sendRequestB;
    ListView friendsList;
    ListView requestsList;

    String currentUid;
    String foundUserId = null; // result of a successful search
    // Lists for displaying friends + incoming requests
    ArrayList<String> friends = new ArrayList<>();
    ArrayList<String> incoming = new ArrayList<>();

    ArrayAdapter<String> friendsAdapter;
    ArrayAdapter<String> incomingAdapter;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);

        searchET = findViewById(R.id.searchET);
        searchB = findViewById(R.id.searchBtn);
        sendRequestB = findViewById(R.id.sendRequestBtn);
        friendsList = findViewById(R.id.friendsList);
        requestsList = findViewById(R.id.requestsList);
        // Hide the send-request button until a user is successfully found
        sendRequestB.setVisibility(View.GONE);
        // Get current logged-in user's UID
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Root reference to "Users" node in Firebase
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        // Adapters populate ListView with simple strings (usernames/UIDs)
        friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friends);
        incomingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, incoming);

        friendsList.setAdapter(friendsAdapter);
        requestsList.setAdapter(incomingAdapter);
        // Load user's current friends + pending requests into ListViews
        loadFriends();
        loadIncomingRequests();
        // Button to search for a username
        searchB.setOnClickListener(v -> searchUser());
        // Button to send a friend request to last searched user
        sendRequestB.setOnClickListener(v -> sendFriendRequest());
    }

    /**
     * Loads the list of confirmed friends in real-time.
     * Firebase automatically updates the ListView whenever the database changes.
     */
    private void loadFriends() {
        usersRef.child(currentUid).child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        friends.clear();
                        // Each child key is a friend's UID
                        for (DataSnapshot child : snapshot.getChildren()) {
                            friends.add(child.getKey());
                        }
                        // Refresh list view
                        friendsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }
    /**
     * Loads all incoming friend requests.
     * Clicking on an incoming request accepts it.
     */
    private void loadIncomingRequests() {
        usersRef.child(currentUid).child("friendRequestsReceived")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        incoming.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            incoming.add(child.getKey());
                        }
                        incomingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
        // Clicking an item in this list = accept request
        requestsList.setOnItemClickListener((adapter, view, position, id) -> {
            String requesterId = incoming.get(position);
            acceptRequest(requesterId);
        });
    }
    /**
     * Searches for a username in the Users table.
     * If a match is found, store that user's UID so the user can send a request.
     */
    private void searchUser() {
        String username = searchET.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        // Clear old search result
        foundUserId = null;
        // Read ALL users once
        usersRef.get().addOnSuccessListener(snapshot -> {
            boolean found = false;

            for (DataSnapshot child : snapshot.getChildren()) {
                // Retrieve username stored in that user's data
                String userNameMatch = child.child("userName").getValue(String.class);
                // Compare user input with stored username
                if (username.equalsIgnoreCase(userNameMatch)) {
                    found = true;
                    foundUserId = child.getKey();   // save UID
                    break;
                }
            }
            // Two checks:
            // 1. user exists
            // 2. user is not yourself
            if (foundUserId != null && !foundUserId.equals(currentUid)) {
                sendRequestB.setVisibility(View.VISIBLE);
                Toast.makeText(this, "User found!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                sendRequestB.setVisibility(View.GONE);
            }
        });
    }
    /**
     * Sends a friend request to the user found by search.
     */
    private void sendFriendRequest() {
        if (foundUserId == null) return;

        DatabaseReference receiverRef = usersRef.child(foundUserId)
                .child("friendRequestsReceived")
                .child(currentUid);

        DatabaseReference senderRef = usersRef.child(currentUid)
                .child("friendRequestsSent")
                .child(foundUserId);

        receiverRef.setValue(true);
        senderRef.setValue(true);

        Toast.makeText(this, "Friend request sent!", Toast.LENGTH_SHORT).show();
        // Hide button until another search happens
        sendRequestB.setVisibility(View.GONE);
    }
    /**
     * Accepts a friend request.
     * Steps:
     * 1. Add both users to each other's "friends" list
     * 2. Remove pending friend requests on both sides
     */
    private void acceptRequest(String requesterId) {

        // Add each other as friends
        usersRef.child(currentUid).child("friends").child(requesterId).setValue(true);
        usersRef.child(requesterId).child("friends").child(currentUid).setValue(true);
        // Remove request from both sender and receiver
        usersRef.child(currentUid).child("friendRequestsReceived").child(requesterId).removeValue();
        usersRef.child(requesterId).child("friendRequestsSent").child(currentUid).removeValue();

        Toast.makeText(this, "Friend added!", Toast.LENGTH_SHORT).show();
    }
}
