package prohor33.redmadrobot.test.ads;

import android.app.Activity;
import android.content.res.Configuration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import prohor33.redmadrobot.test.R;
import prohor33.redmadrobot.test.app.Application;

/**
 * Created by prohor on 03/03/15.
 */
public class Ads {

    private static InterstitialAd mInterstitial = null;

    public static void showBanner(Activity activity) {
        if (!Application.showAds)
            return;

        AdView mAdView = new AdView(activity);
        mAdView.setAdUnitId(activity.getResources().getString(R.string.admob_banner_id));
        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdListener(new ToastAdListener(this));

        boolean orientation_portrait = activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;
        FrameLayout main_lr = (FrameLayout) activity.findViewById(orientation_portrait ?
                R.id.flMain : R.id.flBannerHolder);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        main_lr.addView(mAdView, params);

        mAdView.loadAd(new AdRequest.Builder().build());
    }

//    public static void LoadInterstitial(Activity activity) {
//        mInterstitial = new InterstitialAd(activity);
////        mInterstitial.setAdUnitId(activity.getResources().getString(R.string.admob_interstitial_graphic_id));
//        mInterstitial.setAdUnitId(activity.getResources().getString(R.string.admob_interstitial_video_id));
//        mInterstitial.loadAd(new AdRequest.Builder().build());
////        mInterstitial.setAdListener(new AdListener() {
////            @Override
////            public void onAdLoaded() {
////                super.onAdLoaded();
////                mInterstitial.show();
////            }
////        });
//    }

    public static boolean ShowInterstitial() {
        if (mInterstitial != null && mInterstitial.isLoaded()) {
            mInterstitial.show();
            return true;
        }
        return false;
    }
}
