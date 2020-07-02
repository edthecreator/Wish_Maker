package com.eddelacruz.wishmaker.LoadingFragments;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.eddelacruz.wishmaker.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.database.snapshot.DoubleNode;
import com.race604.drawable.wave.WaveDrawable;

public class LoadingFragment<doubleBounce> extends Fragment {
    private static String TAG = "Loading Fragment";

    ProgressBar progressBar;
    Sprite doubleBounce;


    public LoadingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_fragment,container,false);
        SetUp(view);
        return view;
    }

    private void SetUp(View rootView) {
        try {
            progressBar = (ProgressBar)rootView.findViewById(R.id.spin_kit);
            doubleBounce = new DoubleBounce();
            progressBar.setIndeterminateDrawable(doubleBounce);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
