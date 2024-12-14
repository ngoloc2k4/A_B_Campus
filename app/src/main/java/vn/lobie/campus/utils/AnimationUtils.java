package vn.lobie.campus.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import vn.lobie.campus.R;

public class AnimationUtils {
    public static void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.animate()
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    public static void slideUp(View view) {
        Animation slideUp = AnimationUtils.loadAnimation(
            view.getContext(), 
            R.anim.slide_up
        );
        view.startAnimation(slideUp);
    }

    public static void animateRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAlpha(0f);
        recyclerView.animate()
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }
}
