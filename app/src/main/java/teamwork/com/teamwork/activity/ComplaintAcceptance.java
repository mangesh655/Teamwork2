package teamwork.com.teamwork.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.teamwork.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import teamwork.com.teamwork.app.AppController;
import teamwork.com.teamwork.app.Config;
import teamwork.com.teamwork.helper.SQLiteHandler;
import teamwork.com.teamwork.helper.SessionManager;
import teamwork.com.teamwork.util.NotificationUtils;

public class ComplaintAcceptance extends AppCompatActivity {

    TextView tvCmpId, tvCmpType, tvCmpTitle, tvCmpDetails, tvCmpTime, tvCustId, tvCustName, tvCustAddress, tvCustPhone;
    Button btnAccept, btnPass;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        setContentView(R.layout.activity_complaint_acceptance);

        db = new SQLiteHandler(getApplicationContext());
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        final HashMap<String, String> user;
        user = db.getUserDetails();

        tvCmpId = (TextView) findViewById(R.id.cmp_id);
        tvCmpTitle = (TextView) findViewById(R.id.cmp_title);
        tvCmpDetails = (TextView) findViewById(R.id.cmp_details);
        tvCmpTime = (TextView) findViewById(R.id.cmp_time);
        tvCmpType = (TextView) findViewById(R.id.cmp_type);

        tvCustId = (TextView) findViewById(R.id.cust_id);
        tvCustName = (TextView) findViewById(R.id.cust_name);
        tvCustAddress = (TextView) findViewById(R.id.cust_address);
        tvCustPhone = (TextView) findViewById(R.id.cust_phone);

        btnAccept = (Button) findViewById(R.id.btn_accept);
        btnPass = (Button) findViewById(R.id.btn_pass);

        //Log.e("GET INTENT", getIntent().toString());
        //Log.e("GET INTENT", getIntent().toString());

        Intent intent = getIntent();
        final String complaint_id = intent.getStringExtra("complaint_id");
        String complaint_type = intent.getStringExtra("complaint_type");
        String complaint_title = intent.getStringExtra("complaint_title");
        String complaint_details = intent.getStringExtra("complaint_details");
        String complaint_launched_on = intent.getStringExtra("complaint_launched_date");

        String customer_id = intent.getStringExtra("customer_id");
        String customer_name = intent.getStringExtra("customer_name");
        String customer_contact = intent.getStringExtra("customer_contact");

        String customer_address = intent.getStringExtra("customer_address");
        final double customer_lat = intent.getDoubleExtra("customer_lat", 0.0d);
        final double customer_lng = intent.getDoubleExtra("customer_lng", 0.0d);

        tvCmpId.setText(complaint_id);
        tvCmpType.setText(complaint_type);
        tvCmpTitle.setText(complaint_title);
        tvCmpDetails.setText(complaint_details);
        tvCmpTime.setText(complaint_launched_on);

        tvCustId.setText(customer_id);
        tvCustName.setText(customer_name);
        tvCustAddress.setText(customer_address);
        tvCustPhone.setText(customer_contact);

        disableBtn(btnAccept, 20, complaint_id, user.get("uid"), customer_lat, customer_lng);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAccepted(complaint_id, "true", "", user.get("uid"), customer_lat, customer_lng);
                //disableBtn(btnAccept, 0, complaint_id, user.get("uid"), customer_lat, customer_lng);
            }
        });

        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAccepted(complaint_id, "true", "I am busy right now.", user.get("uid"), customer_lat, customer_lng);
            }
        });

       /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("NEW NOTIFICATION", intent.getExtras().toString());

                    // new push notification is received
                    Toast.makeText(getApplicationContext(), "New Notification", Toast.LENGTH_LONG).show();
                    Log.e("NEW NOTIFICATION", intent.getExtras().toString());

                }
        };*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    protected void disableBtn(final Button btn, final int second, final String complaint_id,
                              final String outhouse_id, final double customer_lat, final double customer_lng) {
        new CountDownTimer(second*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                btn.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                btn.setText("ACCEPT");
                btn.setEnabled(false);
                //isAccepted(complaint_id, "false", "I am busy right now.", outhouse_id, customer_lat, customer_lng);
            }
        }.start();

    }

    protected void isAccepted(final String cmp_id, final String isAccepted, final String msg,
                              final String outhouse_id , final double customer_lat, final double customer_lng) {

        // Tag used to cancel the request
        final String TAG = "COPLAINT ACCEPTED?";
        Log.d(TAG, "ACCEPTANCE: " + cmp_id+" "+ isAccepted);
        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_ALLOCATION,  new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "ACCEPTANCE: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                        isAccepted(cmp_id, isAccepted, msg, outhouse_id, customer_lat, customer_lng);
                    }
                    else{
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+customer_lat+","+customer_lng);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                            finish();
                        }
                    }
                        //showMap();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("outhouse_id", outhouse_id);
                params.put("cmp_id", cmp_id);
                params.put("isaccepted", isAccepted);
                params.put("msg", msg);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG);
    }

    public void showMap() {

    }
}
