package teamwork.com.teamwork.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.teamwork.R;

public class EngineerControlCentre extends AppCompatActivity {

    Button btExpenseTracker,btComplaintHistory, btEngineerProfile, btLogOut;
    private Intent drawerintent = null;
    public Drawer result = null;
    private AccountHeader headerResult = null;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    private ProfileDrawerItem mainProfile;
    Toolbar tbListingMenu;
    Toast ExitToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer_control_centre);
        tbListingMenu = (Toolbar) findViewById(R.id.tbListingMenu);
        setSupportActionBar(tbListingMenu);
        getSupportActionBar().setTitle("Engineer Control Centre");
        btExpenseTracker = (Button) findViewById(R.id.btExpenseTracker);
        btComplaintHistory=(Button) findViewById(R.id.btComplaintHistory);
        btEngineerProfile = (Button) findViewById(R.id.btEngineerProfile);
        btLogOut = (Button) findViewById(R.id.btLogOut);
        btExpenseTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EngineerControlCentre.this, ExpenseTracker.class));
            }
        });
        btComplaintHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EngineerControlCentre.this, complaint_history.class));
            }
        });
        btEngineerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EngineerControlCentre.this, engineer_profile.class));
            }
        });
        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EngineerControlCentre.this, MainActivity.class));
                finish();
                Toast.makeText(getBaseContext(), "Logged Out!", Toast.LENGTH_SHORT).show();
            }
        });

        ExitToast = Toast.makeText(getBaseContext(), "Tap Again to Exit", Toast.LENGTH_SHORT);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String user_name = getPrefs.getString("user_name", "Naushad Shukoor");
        String user_email = getPrefs.getString("user_email", "naushadshukoor@gmail.com");


        //Account Header for Material Drawer
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(
                        mainProfile = new ProfileDrawerItem()
                                .withName(user_name)
                                .withEmail(user_email)
                                .withIcon(getResources().getDrawable(R.drawable.profile3))
                )
                .withSelectionListEnabled(false)
                .withProfileImagesClickable(false)
                .build();


        final PrimaryDrawerItem expense_tracker = new PrimaryDrawerItem()
                .withIcon(MaterialDesignIconic.Icon.gmi_reorder)
                .withName("Expense Tracker")
                .withDescription("Track all your expenses")
                .withIdentifier(1)
                //.withBadge("2")
                //.withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.Black))
                .withSelectable(false);
        final PrimaryDrawerItem complaint_history = new PrimaryDrawerItem()
                .withIcon(MaterialDesignIconic.Icon.gmi_trending_up)
                .withName("Complaint history")
                .withDescription("View history of your complaints")
                .withIdentifier(2)
                //.withBadge("1")
                //.withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.Black))
                .withSelectable(false);

        final PrimaryDrawerItem engineerProfile = new PrimaryDrawerItem()
                .withIcon(MaterialDesignIconic.Icon.gmi_info)
                .withName("Engineer Profile")
                .withDescription("View your Profile!")
                .withIdentifier(3)
                //.withBadge("2")
                //.withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.Black))
                .withSelectable(false);

        final PrimaryDrawerItem logout = new PrimaryDrawerItem()
                .withIcon(MaterialDesignIconic.Icon.gmi_sign_in)
                .withName("Logout")
                .withIdentifier(4)
                //.withBadge("2")
                //.withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.Black))
                .withSelectable(false);

        final DividerDrawerItem divider = new DividerDrawerItem();

        //Material Drawer for listing menu activity
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(tbListingMenu)
                .withTranslucentStatusBar(true)
                .withTranslucentNavigationBar(false)
                .withFullscreen(false)
                //.withSliderBackgroundColor(getResources().getColor(R.color.material_drawer_dark_background))
                .withSelectedItem(-1)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(expense_tracker, complaint_history, engineerProfile,divider,logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            drawerintent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                drawerintent = new Intent(EngineerControlCentre.this, ExpenseTracker.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                drawerintent = new Intent(EngineerControlCentre.this, complaint_history.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                drawerintent = new Intent(EngineerControlCentre.this, engineer_profile.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                startActivity(new Intent(EngineerControlCentre.this, MainActivity.class));
                                finish();
                                Toast.makeText(getBaseContext(), "Logged Out!", Toast.LENGTH_SHORT).show();
                            } else {
                                drawerintent = null;
                            }
                        }
                        if (drawerintent != null) {
                            startActivity(drawerintent);
                        }
                        return false;
                    }
                }).build();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                ExitToast.cancel();
                super.onBackPressed();
                return;

            } else {
                ExitToast.show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }
}
