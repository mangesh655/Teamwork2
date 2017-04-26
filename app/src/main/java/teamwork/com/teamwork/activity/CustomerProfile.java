package teamwork.com.teamwork.activity;

/**
 * Created by Mangesh on 26-04-2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamwork.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import teamwork.com.teamwork.helper.SQLiteHandler;
import teamwork.com.teamwork.helper.SessionManager;

public class customerProfile extends AppCompatActivity {

    ImageView ivCustomerProfileImage;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Profile");

        ivCustomerProfileImage=(ImageView) findViewById(R.id.ivCustomerProfileImage);
        Picasso.with(this)
                .load(R.drawable.profile3)
                .resize(350, 350)
                .transform(new CircleTransform())
                .centerCrop()
                .into(ivCustomerProfileImage);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String user_name = user.get("name");
        String user_email = user.get("email");
        String user_created_at=user.get("created_at");
        String phone=user.get("phone");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date originalDate = new Date();
        try {
            originalDate = sdf.parse(user_created_at);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM ,yyyy", Locale.US);
        String convertedDate=newDateFormat.format(originalDate);

        TextView tvCustomerName=(TextView) findViewById(R.id.tvCustomerName);
        tvCustomerName.setText(user_name);
        TextView tvCustomerEmail=(TextView) findViewById(R.id.tvCustomerEmail);
        tvCustomerEmail.setText(user_email);
        TextView tvUserSince=(TextView) findViewById(R.id.tvUserSince);
        tvUserSince.append(convertedDate);
        TextView tvCustomerContactNo=(TextView) findViewById(R.id.tvCustomerContactNo);
        try {
            tvCustomerContactNo.append("9876543210");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(customer_profile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}