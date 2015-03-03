package prohor33.redmadrobot.test.analytic;

import android.app.Activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import prohor33.redmadrobot.test.app.AppInternalSettings;
import prohor33.redmadrobot.test.app.CollageMakerApp;
import prohor33.redmadrobot.test.app.R;

/**
 * Created by prohor on 04/03/15.
 */
public class GoogleAnalyticsUtils {

    static String TAG = "GoogleAnalyticsUtils";

    public static void SendEvent(Activity activity, final int categoryId, final int actionId,
                                 final int labelId) {

        if (!AppInternalSettings.collectStatistics)
            return;

        // Get tracker.
        Tracker t = ((CollageMakerApp)activity.getApplication()).getAppTracker(activity);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(activity.getResources().getString(categoryId))
                .setAction(activity.getResources().getString(actionId))
                .setLabel(activity.getResources().getString(labelId))
                .build());

    }

    public static void SendEventWithValue(Activity activity, final int categoryId, final int actionId,
                                          final int labelId, final long value) {

        if (!AppInternalSettings.collectStatistics)
            return;

        // Get tracker.
        Tracker t = ((CollageMakerApp)activity.getApplication()).getAppTracker(activity);
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory(activity.getResources().getString(categoryId))
                .setAction(activity.getResources().getString(actionId))
                .setLabel(activity.getResources().getString(labelId))
                .setValue(value)
                .build());

    }

    // Tracking events =============================================================================
    public static void trackPushGimmeCollageButton(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_push_gimme_collage_btn,
                R.string.ga_event_action_push_gimme_collage_btn,
                R.string.ga_event_label_push_gimme_collage_btn);
    }
    public static void trackPushShareCollageButton(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_push_share_collage_btn,
                R.string.ga_event_action_push_share_collage_btn,
                R.string.ga_event_label_push_share_collage_btn);
    }
    public static void trackPushSaveCollageButton(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_push_save_collage_btn,
                R.string.ga_event_action_push_save_collage_btn,
                R.string.ga_event_label_push_save_collage_btn);
    }
    public static void trackPushClosePreviewButton(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_push_close_preview_btn,
                R.string.ga_event_action_push_close_preview_btn,
                R.string.ga_event_label_push_close_preview_btn);
    }
    public static void trackCancelLoading(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_cancel_loading,
                R.string.ga_event_action_cancel_loading,
                R.string.ga_event_label_cancel_loading);
    }
    public static void trackNotQuadImage(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_not_quad_image,
                R.string.ga_event_action_not_quad_image,
                R.string.ga_event_label_not_quad_image);
    }
    public static void trackPreviewGenerationFails(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_preview_generation_fails,
                R.string.ga_event_action_preview_generation_fails,
                R.string.ga_event_label_preview_generation_fails);
    }
    public static void trackShowPreview(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_sow_preview,
                R.string.ga_event_action_sow_preview,
                R.string.ga_event_label_sow_preview);
    }
    public static void trackUserHaveNoPublicImages(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_user_have_no_public_images,
                R.string.ga_event_action_user_have_no_public_images,
                R.string.ga_event_label_user_have_no_public_images);
    }
    public static void trackNoUserWithSuchNickname(Activity activity) {
        SendEvent(activity,
                R.string.ga_event_category_no_user_with_such_nickname,
                R.string.ga_event_action_no_user_with_such_nickname,
                R.string.ga_event_label_no_user_with_such_nickname);
    }
}
