package com.example.munchkin;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AdminFragment extends Fragment {

    ImageView mBackBtn;
    CardView mAddBookBtn, mEditBookBtn, mRemoveBookBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        mBackBtn = view.findViewById(R.id.AdminFragment_backImageView);

        mAddBookBtn = view.findViewById(R.id.AdminFragment_addBookCard);
        mEditBookBtn = view.findViewById(R.id.AdminFragment_editBookCard);
        mRemoveBookBtn = view.findViewById(R.id.AdminFragment_removeBookCard);


        mBackBtn.setOnClickListener(view1 -> getActivity().finish());

        mAddBookBtn.setOnClickListener(view12 -> openAddBookActivity());

        mEditBookBtn.setOnClickListener(view13 -> openEditBookActivity());

        mRemoveBookBtn.setOnClickListener(view14 -> openRemoveBookActivity());

        return view;
    }

    public void openAddBookActivity() {
        Intent intent = new Intent(getActivity(), AddBook.class);
        startActivity(intent);
    }

    public void openEditBookActivity() {
        Intent intent = new Intent(getActivity(), EditList.class);
        startActivity(intent);
    }

    public void openRemoveBookActivity() {
        Intent intent = new Intent(getActivity(), RemoveBook.class);
        startActivity(intent);
    }
}