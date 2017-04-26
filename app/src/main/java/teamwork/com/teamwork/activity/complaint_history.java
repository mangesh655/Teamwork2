package teamwork.com.teamwork.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

public class complaint_history extends AppCompatActivity {

    Spinner spinnerComplaintHistoryRange;
    TableLayout tbComplaints;
    TableRow tr4,tr5,tr6;
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Complaint History");

        tbComplaints=(TableLayout) findViewById(R.id.tbComplaints);
        tr4=(TableRow) findViewById(R.id.tr4);
        tr5=(TableRow) findViewById(R.id.tr5);
        tr6=(TableRow) findViewById(R.id.tr6);

        spinnerComplaintHistoryRange = (Spinner) findViewById(R.id.spinnerComplaintHistoryRange);
        adapter= ArrayAdapter.createFromResource(this,R.array.ComplaintHistoryRange,R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComplaintHistoryRange.setAdapter(adapter);
        spinnerComplaintHistoryRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    if(tr6.getVisibility()==View.VISIBLE) {
                        tr6.setVisibility(View.GONE);
                    }
                    if(tr4.getVisibility()==View.VISIBLE && tr5.getVisibility()==View.VISIBLE) {
                        tr4.setVisibility(View.GONE);
                        tr5.setVisibility(View.GONE);
                    }
            }else if(position==1){
                    if(tr4.getVisibility()==View.GONE && tr5.getVisibility()==View.GONE) {
                        tr4.setVisibility(View.VISIBLE);
                        tr5.setVisibility(View.VISIBLE);
                    }
                    if(tr6.getVisibility()==View.VISIBLE) {
                        tr6.setVisibility(View.GONE);
                    }
                }else if(position==2){
                    if(tr6.getVisibility()==View.GONE) {
                        tr6.setVisibility(View.VISIBLE);
                    }
                    if(tr4.getVisibility()==View.GONE && tr5.getVisibility()==View.GONE) {
                        tr4.setVisibility(View.VISIBLE);
                        tr5.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
