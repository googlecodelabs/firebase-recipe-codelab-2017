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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FailActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private RewardAd rewardAd;
    private RewardedVideoAd mAd;
    // Initialize Firebase Components
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain ad singleton
        rewardAd = new RewardAd(this);
        mAd = rewardAd.getAd(this);
        mAd.setRewardedVideoAdListener(this);

        // Show Rewarded Video ads then move to game when user clicks "show hint" button
        final Button hint_button = (Button) findViewById(R.id.hint_button);
        hint_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mAd.isLoaded()) {
                    mAd.show();
                }
            }
        });

        // Move to game when user clicks "try again" button
        final Button again_button = (Button) findViewById(R.id.again_button);
        again_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(FailActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        // Move to home screen when user clicks "back to home" button
        final Button home_button = (Button) findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(FailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Intent intent = new Intent(FailActivity.this, GameActivity.class);
        intent.putExtra("hint", true);
        intent.putExtra("hint_weight", (float)reward.getAmount());
        mFirebaseAnalytics.setUserProperty("RewardAmount", new Integer(reward.getAmount()).toString());
        startActivity(intent);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }
}
