package com.example.scanner;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.transitionseverywhere.TransitionManager;

public class FloatingButtonActivity extends AppCompatActivity {
    FloatingActionButton fab1, btn_setting, btn_logout,btn_close;
    CoordinatorLayout transitionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_button);

        transitionsContainer = findViewById(R.id.myLayout);
        fab1 = transitionsContainer.findViewById(R.id.floatingActionButton);
        btn_close = transitionsContainer.findViewById(R.id.fab_close);
        btn_setting = transitionsContainer.findViewById(R.id.Btn_setting);
        btn_logout = transitionsContainer.findViewById(R.id.btn_logout);


        fab1.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(transitionsContainer);
            btn_setting.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.VISIBLE);
            btn_close.setVisibility(View.VISIBLE);
            fab1.setVisibility(View.GONE);

        });

        btn_close.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(transitionsContainer);
            fab1.setVisibility(View.VISIBLE);
            btn_setting.setVisibility(View.GONE);
            btn_logout.setVisibility(View.GONE);
            btn_close.setVisibility(View.GONE);

        });


        transitionsContainer.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(transitionsContainer);
            fab1.setVisibility(View.VISIBLE);
            btn_setting.setVisibility(View.GONE);
            btn_logout.setVisibility(View.GONE);
            btn_close.setVisibility(View.GONE);
        });



    }


}
