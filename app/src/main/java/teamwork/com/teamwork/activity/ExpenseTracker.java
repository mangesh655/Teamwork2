package teamwork.com.teamwork.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseTracker extends AppCompatActivity {

    static final int CAM_REQUEST = 1;
    private Button button;
    private EditText editText;
    private TextView tvListOfFiles;
    public String fileName;
    private ImageView imageView;
    File img_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_tracker);
        bindXML();

        getSupportActionBar().setTitle("Expense Tracker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.ENGLISH);
                String datetime = dateformat.format(c.getTime());
                fileName = editText.getText().toString()+"("+datetime+")";
                if (editText.getText().toString().trim().length() != 0) {
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getFile();
                    camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    imageView.setBackgroundColor(Color.WHITE);
                    startActivityForResult(camera_intent, CAM_REQUEST);
                } else {
                    Toast.makeText(ExpenseTracker.this, "Enter the Image Name", Toast.LENGTH_SHORT).show();
                }

            }
        });
        updateListOfImages();

    }

    private void bindXML() {
        button = (Button) findViewById(R.id.btn_capture_image);
        imageView = (ImageView) findViewById(R.id.iv_captured_image);
        editText = (EditText) findViewById(R.id.et_fileName);
        tvListOfFiles = (TextView) findViewById(R.id.tvListOfFiles);
    }

    public File getFile() {
        File folder = new File("sdcard/complaint_manager");
        if (!folder.exists()) {
            folder.mkdir();
        }
        img_file = new File(folder, fileName + ".jpg");
        return img_file;
    }

    public void updateListOfImages() {
        try {
            File folder = new File("sdcard/complaint_manager");
            String[] listOfFiles = folder.list();
            tvListOfFiles.setText("List of Images:");
            for (String f : listOfFiles) {
                tvListOfFiles.append("\n" + f);
            }
        } catch (NullPointerException ns) {
            ns.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "/sdcard/complaint_manager/" + fileName + ".jpg";
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (getFile().exists()) {
                    Toast.makeText(ExpenseTracker.this, "Image was stored at " + getFile().getAbsolutePath(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ExpenseTracker.this, "There was error saving the file", Toast.LENGTH_LONG).show();
                }
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(ExpenseTracker.this, "Image capture was cancelled", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        Picasso.with(this).setIndicatorsEnabled(true);

        /* using Picasso */
        Picasso.with(this)
                .load(new File(path))
                .into(imageView);

        updateListOfImages();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

