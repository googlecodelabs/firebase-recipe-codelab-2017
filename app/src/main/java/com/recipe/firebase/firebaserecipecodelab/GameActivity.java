// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.recipe.firebase.firebaserecipecodelab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import static android.R.attr.animation;

public class GameActivity extends AppCompatActivity {
    // Initialize Firebase Components
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

         mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Manage hint mask
        Intent intent = getIntent();
        boolean hint = intent.getBooleanExtra("hint", false);
        float hint_weight = intent.getFloatExtra("hint_weight",1.0f);
        RelativeLayout hint_mask_view = (RelativeLayout) findViewById(R.id.hint_mask);
        hint_mask_view.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                hint_weight));
        if (!hint) {
            hint_mask_view.setVisibility(View.INVISIBLE);
        }

        // Change before_image's alpha
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView before_image = (ImageView) findViewById(R.id.before_image);
                AlphaAnimation alpha = new AlphaAnimation(1, 0);
                alpha.setFillAfter(true);
                alpha.setDuration(7000);
                before_image.startAnimation(alpha);
            }
        }, 5000);

        // Show start countdown
        final TextView countdown_text = (TextView) findViewById(R.id.countdown_text);
        final RelativeLayout background_before_start = (RelativeLayout) findViewById(R.id.background_before_start);
        countdown_text.setText("Ready?");
        ScaleAnimation scale_countdown = new ScaleAnimation(1.0f, 0.3f, 1.0f, 0.3f,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f);
        scale_countdown.setDuration(2000);
        scale_countdown.setFillAfter(true);
        countdown_text.startAnimation(scale_countdown);
        scale_countdown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                countdown_text.setText("GO!");
                countdown_text.setTextSize(240.0f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countdown_text.setText("");
                        background_before_start.setVisibility(View.INVISIBLE);
                        startGame();
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // Move to success result activity when player click proper area
        final Button clear_button = (Button) findViewById(R.id.clear_button);
        clear_button.setBackgroundColor(Color.TRANSPARENT);
        clear_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearStage();
            }
        });

        // Move to fail result activity when player click not proper area
        final Button fail_button = (Button) findViewById(R.id.fail_button);
        fail_button.setBackgroundColor(Color.TRANSPARENT);
        fail_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                failStage();
            }
        });
    }

    private void clearStage () {
        // Send log event to Firebase
        mFirebaseAnalytics.logEvent("stage_clear", null);

        // Move to clear activity.
        Intent intent = new Intent(GameActivity.this, SuccessActivity.class);
        startActivity(intent);
    }

    private void failStage () {
        // Send log event to Firebase
        mFirebaseAnalytics.logEvent("stage_failed", null);

        // Move to clear activity.
        Intent intent = new Intent(GameActivity.this, FailActivity.class);
        startActivity(intent);
    }

    private void startGame () {
        // Show countdown bar
        ImageView red_bar = (ImageView) findViewById(R.id.red_bar);
        ScaleAnimation scale = new ScaleAnimation(1,0,1,1);
        scale.setDuration(13000);
        scale.setFillAfter(true);
        red_bar.startAnimation(scale);
        scale.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                failStage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
