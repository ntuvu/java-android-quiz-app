package com.example.quizapp4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    public CategoryFragment() {
        // Required empty public constructor
    }

    private GridView catView;
    private List<CategoryModel> catList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        catView = view.findViewById(R.id.cat_Grid);

        loadCategories();
        CategoryAdapter adapter = new CategoryAdapter(catList);
        catView.setAdapter(adapter);

        return view;
    }

    private void loadCategories() {
        catList.clear();
        catList.add(new CategoryModel("1", "GK", 20));
        catList.add(new CategoryModel("2", "ENGLISH", 30));
        catList.add(new CategoryModel("3", "MATH", 40));
        catList.add(new CategoryModel("4", "GEOMETRY", 50));
        catList.add(new CategoryModel("5", "JAV", 60));
    }
}