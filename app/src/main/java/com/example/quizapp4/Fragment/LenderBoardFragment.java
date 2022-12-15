package com.example.quizapp4.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp4.MainActivity;
import com.example.quizapp4.Model.DbQuery;
import com.example.quizapp4.MyCompleteListener;
import com.example.quizapp4.R;


public class LenderBoardFragment extends Fragment {

    private TextView totalUsersTV, myImgTextTV, myScoreTV, myRankTV;
    private RecyclerView usersView;
    private RankAdapter adapter;

    public LenderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lender_board, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Lenderboard");

        initViews(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        usersView.setLayoutManager(layoutManager);

        adapter = new RankAdapter(DbQuery.g_usersList);

        usersView.setAdapter(adapter);

        DbQuery.getTopUsers(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                adapter.notifyDataSetChanged();

                if (DbQuery.myPerformance.getScore() != 0) {
                    if (!DbQuery.isMeOnTopList) {
                        calculateRank();
                    }

                    myScoreTV.setText("Score: " + DbQuery.myPerformance.getScore());
                    myRankTV.setText("Rank " + DbQuery.myPerformance.getRank());
                }

            }

            @Override
            public void onFailure() {

            }
        });

        totalUsersTV.setText("Total users: " + DbQuery.g_usersCount);
        myImgTextTV.setText(DbQuery.myPerformance.getName().toUpperCase().substring(0, 1));

        return view;
    }

    private void initViews(View view) {
        totalUsersTV = view.findViewById(R.id.total_users);
        myImgTextTV = view.findViewById(R.id.img_text);
        myScoreTV = view.findViewById(R.id.total_score);
        myRankTV = view.findViewById(R.id.rank);
        usersView = view.findViewById(R.id.users_view);

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