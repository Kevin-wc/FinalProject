package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LeaderboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        TextView title = v.findViewById(R.id.questionText);

        ListView listView = v.findViewById(R.id.answersList);

        String[] players = new String[]{
                "1st  @Secretlyaccurtly",
                "2nd  @PopOdonnell",
                "3rd  @LiquidSnail",
                "4th  @DavyJones",
                "5th  @CoachNemesisa",
                "6th  @IceSatyr",
                "7th  @DirtySquash",
                "8th  @BlazingEvil",
                "9th  @DefeatedInsect"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.custom_list_item,
                R.id.leaderboardItem,
                players
        );
        listView.setAdapter(adapter);

        return v;
    }
}
