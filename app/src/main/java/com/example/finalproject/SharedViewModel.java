package com.example.finalproject;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    public LiveData<User> getUser() {
        return user;
    }

    public void fetchUserData(String userId) {
        if (userId == null) {
            user.setValue(null);
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        if (userListener != null) {
            userRef.removeEventListener(userListener);
        }

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                user.postValue(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("SharedViewModel", "loadUser:onCancelled", databaseError.toException());
                user.postValue(null);
            }
        };
        userRef.addValueEventListener(userListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up the listener when the ViewModel is destroyed to prevent memory leaks
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }
}
