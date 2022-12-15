package com.example.quizapp4.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.quizapp4.LoginActivity;
import com.example.quizapp4.MainActivity;
import com.example.quizapp4.Model.DbQuery;
import com.example.quizapp4.MyCompleteListener;
import com.example.quizapp4.MyProfileActivity;
import com.example.quizapp4.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.Objects;

public class AccountFragment extends Fragment {

    private LinearLayout logoutB;
    private TextView profile_img_text, name, score, rank;
    private LinearLayout lenderB, profileB, bookmarksB;
    private BottomNavigationView bottomNavigationView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Account");

        String userName = DbQuery.myProfile.getName();
        profile_img_text.setText(userName.toUpperCase().substring(0, 1));

        name.setText(userName);
//        score.setText(String.valueOf(DbQuery.myPerformance.getScore()));

        if (DbQuery.g_usersList.size() == 0) {
            DbQuery.getTopUsers(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    if (DbQuery.myPerformance.getScore() != 0) {
                        if (!DbQuery.isMeOnTopList) {
                            calculateRank();
                        }

                        score.setText("Score: " + DbQuery.myPerformance.getScore());
                        rank.setText("Rank " + DbQuery.myPerformance.getRank());
                    }

                }

                @Override
                public void onFailure() {

                }
            });
        } else {
            score.setText("Score: " + DbQuery.myPerformance.getScore());

            if (DbQuery.myPerformance.getScore() != 0) {
                rank.setText("Rank " + DbQuery.myPerformance.getRank());
            }
        }

        logoutB.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleClient = GoogleSignIn.getClient(getContext(), gso);
            mGoogleClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            });
        });

        bookmarksB.setOnClickListener(view1 -> {

        });

        profileB.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), MyProfileActivity.class);
            startActivity(intent);
        });

        lenderB.setOnClickListener(view1 -> {
            bottomNavigationView.setSelectedItemId(R.id.navigation_lenderboard);
        });

        return view;
    }

    private void initViews(View view) {
        logoutB = view.findViewById(R.id.logoutB);
        profile_img_text = view.findViewById(R.id.profile_img_text);
        name = view.findViewById(R.id.name);
        score = view.findViewById(R.id.score);
        rank = view.findViewById(R.id.rank);
        lenderB = view.findViewById(R.id.lenderBFrag);
        bookmarksB = view.findViewById(R.id.bookmarkB);
        profileB = view.findViewById(R.id.profileB);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation_bar);
    }

    private void calculateRank() {
        int lowTopScore = DbQuery.g_usersList.get(DbQuery.g_usersList.size() - 1).getScore();
        int remaining_slots = DbQuery.g_usersCount - 20;
        int myslot = (DbQuery.myPerformance.getScore() * remaining_slots) / lowTopScore;
        int rank;

        if (lowTopScore != DbQuery.myPerformance.getScore()) {
            rank = DbQuery.g_usersCount - myslot;
        } else {
            rank = 21;
        }

        DbQuery.myPerformance.setRank(rank);
    }
}