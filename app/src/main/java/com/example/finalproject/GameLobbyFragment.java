package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameLobbyFragment extends Fragment {

    private static final String ARG_GENRE = "genre";
    private String genre;

    public static GameLobbyFragment newInstance(String genre) {
        GameLobbyFragment f = new GameLobbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genre);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genre = getArguments().getString(ARG_GENRE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_game_lobby, container, false);

        TextView genreTitle = v.findViewById(R.id.genreTitle);
        TextView lobbyCodeText = v.findViewById(R.id.lobbyCodeText);
        TextView statusText = v.findViewById(R.id.statusText);
        EditText joinCodeInput = v.findViewById(R.id.joinCodeInput);
        Button createBtn = v.findViewById(R.id.createLobbyBtn);
        Button joinBtn = v.findViewById(R.id.joinLobbyBtn);
        Button startBtn = v.findViewById(R.id.startGameBtn);

        genreTitle.setText(genre + " Quiz Battle");

        String fakeCode = "4821";
        lobbyCodeText.setText("Your lobby code: " + fakeCode);

        createBtn.setOnClickListener(view -> {
            statusText.setText("Waiting for friend to join...");
        });

        joinBtn.setOnClickListener(view -> {
            String entered = joinCodeInput.getText().toString().trim();
            if (entered.equals(fakeCode)) {
                statusText.setText("Joined lobby with friend!");
            } else {
                statusText.setText("Code not found. (Demo only)");
            }
        });

        startBtn.setOnClickListener(view -> {
            if (getActivity() instanceof HomeActivity) {
                //pass genre for different questions later
                ((HomeActivity) getActivity()).openQuizScreen(genre);
            }
        });

        return v;
    }
}
