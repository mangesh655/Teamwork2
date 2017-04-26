package teamwork.com.teamwork.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.teamwork.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teamwork.com.teamwork.app.Address;
import teamwork.com.teamwork.app.AppController;
import teamwork.com.teamwork.app.Config;
import teamwork.com.teamwork.app.GeoCodingLocation;
import teamwork.com.teamwork.app.PlaceArrayAdapter;
import teamwork.com.teamwork.helper.SQLiteHandler;

/**
 * Created by Naushad on 23/01/2017.
 */

public class ComplaintRegistration extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private static final String TAG = ComplaintRegistration.class.getSimpleName();
    private static final int GOOGLE_API_CLIENT_ID = 0;

    EditText etFlatNo, etCmpTitle, etCmpDetails;
    AutoCompleteTextView etAddress;
    LinearLayout llAddressBook;
    Button btnSubmit;
    ArrayList<Address> addressList = new ArrayList<>();
    View.OnClickListener onClickListener;
    private SQLiteHandler db;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(9.726384, 67.007825), new LatLng(38.726662, 94.851569));

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaintregistration);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etCmpTitle = (EditText) findViewById(R.id.et_complaint_title);
        etCmpDetails = (EditText) findViewById(R.id.et_complaint_details);
        etAddress = (AutoCompleteTextView) findViewById(R.id.et_address);
        etFlatNo = (EditText) findViewById(R.id.et_flat_no);
        llAddressBook = (LinearLayout) findViewById(R.id.ll_address_book);
        btnSubmit = (Button) findViewById(R.id.btn_submit_complaint);

        db = new SQLiteHandler(getApplicationContext());
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        final HashMap<String, String> user = db.getUserDetails();

        mGoogleApiClient = new GoogleApiClient.Builder(ComplaintRegistration.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        etAddress.setThreshold(3);
        etAddress.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_INDIA, null);
        etAddress.setAdapter(mPlaceArrayAdapter);

        showAddressBook(user.get("uid"));

        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int id = view.getId();

                etFlatNo.setText(addressList.get(id).getFlatNo());
                etAddress.setText(addressList.get(id).getStreet()
                        + addressList.get(id).getCity()
                        + addressList.get(id).getState()
                        + addressList.get(id).getCountry()
                        + addressList.get(id).getPinCode());
            }
        };

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String custId= "", flatNo="", locationAddress="", street="", city="", state="",
                        country="", postalCode="", lat="", lng="", cmpTitle="", cmpDetails="";

                locationAddress = etAddress.getText().toString();
                GeoCodingLocation geoCodingLocation = new GeoCodingLocation();
                try {
                    List<android.location.Address>addresses = geoCodingLocation.getAddressDetails(locationAddress, ComplaintRegistration.this);

                    street = addresses.get(0).getAdminArea();
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    lat = Double.toString(addresses.get(0).getLatitude());
                    lng = Double.toString(addresses.get(0).getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                flatNo = etFlatNo.getText().toString();
                cmpDetails = etCmpDetails.getText().toString();
                cmpTitle = etCmpTitle.getText().toString();
                custId = user.get("uid");

                if(custId != "" && cmpTitle != "" && cmpDetails != "" && lat!="" && lng!= "" && locationAddress != "") {
                    submitComplaint(custId, lat, lng, flatNo, street, city, state, country, postalCode,
                    cmpTitle, cmpDetails, "Unattended");
                }
                else
                    Toast.makeText(ComplaintRegistration.this, "Please Complete all the details.", Toast.LENGTH_LONG);
            }
        });

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            etAddress.setText(Html.fromHtml(place.getAddress() + ""));
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAddressBook(final String custId) {

        // Tag used to cancel the request
        String tag_string_req = "req_show_address_book";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_SHOW_ADDRESS_BOOK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Address Book Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray addresses = jObj.getJSONArray("addresses");

                        int length = addresses.length();

                        for(int i=0;i<length;i++) {

                            String flatNo = addresses.getJSONObject(i).getString("flat_no") != "" ? addresses.getJSONObject(i).getString("flat_no")+" " : "";
                            String street = addresses.getJSONObject(i).getString("street") != "" ? addresses.getJSONObject(i).getString("street")+" " : "";
                            String city = addresses.getJSONObject(i).getString("city") != "" ? addresses.getJSONObject(i).getString("city")+" " : "";
                            String state = addresses.getJSONObject(i).getString("state") != "" ? addresses.getJSONObject(i).getString("state")+" " : "";
                            String country = addresses.getJSONObject(i).getString("country") != "" ? addresses.getJSONObject(i).getString("country")+" " : "";
                            String pinCode = addresses.getJSONObject(i).getString("pin_code") != "" ? addresses.getJSONObject(i).getString("pin_code")+" " : "";

                            Address address = new Address(flatNo, street, city, state, country, pinCode);
                            addressList.add(address);

                            TextView tvAddress = new TextView(ComplaintRegistration.this);
                            tvAddress.setId(i);
                            tvAddress.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            tvAddress.setText(flatNo+street+city+state+country+pinCode);
                            tvAddress.setOnClickListener(onClickListener);

                            llAddressBook.addView(tvAddress);
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, errorMsg);
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Complaint Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("custId", "58eb00");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void submitComplaint(final String custId, final String lat, final String lng,
                                 final String flatNo, final String street, final String city,
                                 final String state, final String country, final String postalCode,
                                 final String cmpTitle, final String cmpDetails,
                                 final String cmpStatus) {

        // Tag used to cancel the request
        String tag_string_req = "Submit Complaint";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.SUBMIT_COMPLAINT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Complaint Submission Response: " + response);


               /* try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        String cmp_id = jObj.getString("cmp_id");
                        Toast.makeText(getApplicationContext(),
                                "Your complaint number is : "+cmp_id+" Please note it for future reference.",
                                Toast.LENGTH_LONG);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }*/

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Complaint Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("custId", "58eb00");
                params.put("lat",lat);
                params.put("lng", lng);
                params.put("house_no", flatNo);
                params.put("street", street);
                params.put("city", city);
                params.put("state", state);
                params.put("country", country);
                params.put("postal_code", postalCode);
                params.put("title", cmpTitle);
                params.put("details", cmpDetails);
                params.put("status", cmpStatus);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}
