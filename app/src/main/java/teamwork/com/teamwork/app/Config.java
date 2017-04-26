package teamwork.com.teamwork.app;

/**
 * Created by Mangesh on 23-03-2017.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String URL_SHOW_ADDRESS_BOOK = "http://192.168.0.7/ComplaintTracking/customer/showaddressbook.php";
    public static final String SUBMIT_COMPLAINT = "http://192.168.0.7/ComplaintTracking/customer/submitcomplaint.php";

    // Server user login url
    public static String URL_LOGIN = "http://192.168.0.7/ComplaintTracking/inhouse/login.php";

    // Server user logout url
    public static String URL_LOGOUT = "http://192.168.0.7/ComplaintTracking/inhouse/logout.php";

    // Server user register url
    public static String URL_REG_FIREBASE_ID = "http://192.168.0.7/ComplaintTracking/inhouse/registerfirebaseid.php";

    // Server user register url
    public static String URL_ALLOCATION = "http://192.168.0.7/ComplaintTracking/inhouse/allocation.php";

}