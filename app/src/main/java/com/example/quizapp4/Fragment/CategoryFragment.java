package com.example.quizapp4.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.example.quizapp4.Model.DbQuery;
import com.example.quizapp4.R;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    private GridView catView;
//    public static List<CategoryModel> catList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        catView = view.findViewById(R.id.cat_Grid);

//        loadCategories();
        CategoryAdapter adapter = new CategoryAdapter(DbQuery.g_catlist);
        catView.setAdapter(adapter);

        return view;
    }

//    private void loadCategories() {
//
//    }
}