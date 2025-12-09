package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

public class HomeNavFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_nav, container, false);

        View card = v.findViewById(R.id.cardExploringOptions);
        View socialCard = v.findViewById(R.id.cardGenreSocial);
        View financialCard = v.findViewById(R.id.cardGenreFinancial);


        card.setOnClickListener(view -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity())
                        .openArticleScreen("exploring_options");
            }
        });

        socialCard.setOnClickListener(view -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).openGameLobby("Social");
            }
        });

        financialCard.setOnClickListener(view -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).openGameLobby("Financial");
            }
        });

        return v;

}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView homeSubtitle = view.findViewById(R.id.homeSubtitle);
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getUser().observe(getViewLifecycleOwner(), user -> {
           if(user != null) {
               homeSubtitle.setText("Hey " + user.getUserName() + ", welcome back!");
           }else{
               homeSubtitle.setText("Hey Bob, welcome back!");
           }
        });


    }
}
