package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizFragment extends Fragment {

    private static final String ARG_ARTICLE_ID = "article_id";
    private String articleId;
    private boolean hasSubmitted = false;

    public static QuizFragment newInstance(String articleId) {
        QuizFragment f = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getString(ARG_ARTICLE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        RadioGroup q1Group = v.findViewById(R.id.q1_group);
        RadioGroup q2Group = v.findViewById(R.id.q2_group);
        RadioGroup q3Group = v.findViewById(R.id.q3_group);
        Button submitBtn = v.findViewById(R.id.submitQuizBtn);

        submitBtn.setOnClickListener(view -> {

            if (hasSubmitted) {
                Toast.makeText(getContext(),
                        "You already submitted this quiz, bozo.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int score = 0;

            if (q1Group.getCheckedRadioButtonId() == R.id.q1_opt2) score++;
            if (q2Group.getCheckedRadioButtonId() == R.id.q2_opt3) score++;
            if (q3Group.getCheckedRadioButtonId() == R.id.q3_opt1) score++;

            Toast.makeText(getContext(),
                    "You scored " + score + " / 3", //maybe make this even more dynamic (the 3)
                    Toast.LENGTH_SHORT).show();

            if (score > 0) {
                addPointsToCurrentUser(score);
            }

            hasSubmitted = true;
            submitBtn.setEnabled(false);

        });

        return v;
    }

    private void addPointsToCurrentUser(int pointsToAdd) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { //just in case, you never know.
            Toast.makeText(getContext(),
                    "You must be logged in to earn points.",
                    Toast.LENGTH_SHORT).show();
            return;
        } //really just to show we can do it - get some points on the code

        DatabaseReference pointsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(user.getUid())
                .child("points");

        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer current = snapshot.getValue(Integer.class);
                if (current == null) current = 0;
                int updated = current + pointsToAdd;
                pointsRef.setValue(updated);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
