package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ArticleFragment extends Fragment {

    private static final String ARG_ARTICLE_ID = "article_id";
    private String articleId;

    public static ArticleFragment newInstance(String articleId) {
        ArticleFragment f = new ArticleFragment();
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

        View v = inflater.inflate(R.layout.fragment_article, container, false);

        TextView titleTV = v.findViewById(R.id.articleTitle);
        TextView bodyTV = v.findViewById(R.id.articleBody);
        Button quizBtn = v.findViewById(R.id.takeQuizBtn);

        // later you can switch on articleId; for now just this one
        titleTV.setText("Exploring Options: Alternatives to Vaping");
        bodyTV.setText(
                "Vaping may seem like a safer choice than smoking, but it still " +
                        "carries health risks and can create strong nicotine dependence.\n\n" +
                        "This article explores practical alternatives such as exercise, " +
                        "breathing exercises, social support, and hobbies that can help " +
                        "manage stress and cravings without nicotine."
        );

        quizBtn.setOnClickListener(view -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity())
                        .openQuizScreen(articleId);
            }
        });

        return v;
    }
}
