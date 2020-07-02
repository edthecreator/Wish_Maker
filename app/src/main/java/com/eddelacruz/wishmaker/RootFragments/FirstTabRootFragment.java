package com.eddelacruz.wishmaker.RootFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.R;
import com.eddelacruz.wishmaker.ViewPagerFragments.HomeFragment;


public class FirstTabRootFragment extends Fragment {
    private static String TAG = "Root Fragment Home";

   public FirstTabRootFragment() {

   }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_root,container,false);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.root_frame, new HomeFragment(), TransactionNameAndFragmentTag.HomeFragment);
        transaction.addToBackStack(TransactionNameAndFragmentTag.HomeFragment);
        transaction.commitAllowingStateLoss();
        return view;
    }
}
