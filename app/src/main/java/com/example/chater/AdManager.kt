package com.example.chater

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val context: Context){
    private var rewardedAd: RewardedAd? = null
    var isLoaded: Boolean = false

    fun loadRewardedAd(adUnit: String, onAdLoaded: () -> Unit = {}){
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context,adUnit,adRequest, object: RewardedAdLoadCallback(){
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                isLoaded = true
                onAdLoaded()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                isLoaded = false
            }
        })
    }

    fun showRewardedAd(
        onRewardEarner: (RewardItem) -> Unit = {},
        onAdClosed: () -> Unit = {},
        onAdFailed: () -> Unit = {}
    ){
        val activity = context as? Activity ?: return
        rewardedAd?.show(activity){rewardItem->
            onRewardEarner(rewardItem)
            onAdClosed()
        }?: run{
           onAdFailed
        }
    }
}