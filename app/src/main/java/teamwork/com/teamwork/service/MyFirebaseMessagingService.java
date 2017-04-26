package teamwork.com.teamwork.service;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import teamwork.com.teamwork.activity.ComplaintAcceptance;
import teamwork.com.teamwork.app.Config;
import teamwork.com.teamwork.util.NotificationUtils;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            String click_action = data.getString("click_action");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "click action: " + click_action);
            Log.e(TAG, "timestamp: " + timestamp);

                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(click_action);

                resultIntent.putExtra("complaint_id", payload.getJSONObject("complaint").getString("cmp_id"));
                resultIntent.putExtra("complaint_type", payload.getJSONObject("complaint").getString("cmp_type"));
                resultIntent.putExtra("complaint_title", payload.getJSONObject("complaint").getString("cmp_title"));
                resultIntent.putExtra("complaint_details", payload.getJSONObject("complaint").getString("cmp_details_inhouse"));
                resultIntent.putExtra("complaint_launched_date", payload.getJSONObject("complaint").getString("cmp_launch_date"));

                resultIntent.putExtra("customer_id", payload.getJSONObject("customer").getString("cust_id"));
                resultIntent.putExtra("customer_name", payload.getJSONObject("customer").getString("name"));
                resultIntent.putExtra("customer_contact", payload.getJSONObject("customer").getString("phone"));

                resultIntent.putExtra("customer_lat", payload.getJSONObject("address").getDouble("lat"));
                resultIntent.putExtra("customer_lng", payload.getJSONObject("address").getDouble("lng"));
                resultIntent.putExtra("customer_address", payload.getJSONObject("address").getString("flat_no") +
                        " "+ payload.getJSONObject("address").getString("street") +
                        " "+ payload.getJSONObject("address").getString("city") +
                        " "+ payload.getJSONObject("address").getString("state") +
                        " "+ payload.getJSONObject("address").getString("country") +
                        " "+ payload.getJSONObject("address").getString("pin_code"));

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}