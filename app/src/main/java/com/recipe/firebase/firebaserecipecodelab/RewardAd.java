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

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class RewardAd {
    public static RewardedVideoAd sRewardedVideoAd;
    public static String adUnitID;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    public RewardAd(Activity activity) {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("task_completetion_ad_unit_id", "ca-app-pub-0977440612291676/4209413500");
        mFirebaseRemoteConfig.setDefaults(defaults);

        fetchAdUnitID(activity);
        String adUnitID = mFirebaseRemoteConfig.getString("task_completetion_ad_unit_id");

        this.adUnitID = adUnitID;
        sRewardedVideoAd = newRewardedVideoAd(activity);
        loadRewardedVideoAd();
    }

    public RewardedVideoAd newRewardedVideoAd(Activity activity) {
        RewardedVideoAd rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
        return rewardedVideoAd;
    }

    public RewardedVideoAd getAd(Activity activity) {
        if (sRewardedVideoAd != null && sRewardedVideoAd.isLoaded()) {
            return sRewardedVideoAd;
        } else {
            new RewardAd(activity);
            return sRewardedVideoAd;
        }
    }

    public void loadRewardedVideoAd() {
        sRewardedVideoAd.loadAd(this.adUnitID, new AdRequest.Builder().build());
    }

    private void fetchAdUnitID (Activity activity) {
        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) mFirebaseRemoteConfig.activateFetched();
            }
        };

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            // This forces Remote Config to fetch from server every time.
            mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(activity, onCompleteListener);
        } else {
            mFirebaseRemoteConfig.fetch().addOnCompleteListener(activity, onCompleteListener);
        }
    }
}

