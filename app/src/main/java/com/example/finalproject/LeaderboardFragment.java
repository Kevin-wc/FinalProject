package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        TextView title = v.findViewById(R.id.questionText);
        title.setText("Leader Board");

        ListView listView = v.findViewById(R.id.answersList);
        adapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_leaderboard, //changed from simple_list_item_1
                R.id.leaderText,
                displayList);
        listView.setAdapter(adapter);

        loadLeaderboard();

        return v;
    }

    private void loadLeaderboard() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance()
                .getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User u = child.getValue(User.class);
                    if (u != null) {
                        users.add(u);
                    }
                }

                //sort by points descending
                Collections.sort(users, (u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));

                displayList.clear();
                int rank = 1;
                for (User u : users) {
                    displayList.add(rank + "th  @" + u.getUserName() + "  (" + u.getPoints() + " pts)");
                    rank++;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //in case database request fails
                Toast.makeText(getContext(),
                        "Could not update score: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
