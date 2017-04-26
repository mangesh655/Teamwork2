package teamwork.com.teamwork.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.teamwork.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EngineerLoginFragment extends Fragment {

    Button btLogin;
    EditText etUserId, etPassword;

    public EngineerLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customer_login, container, false);

        btLogin = (Button) v.findViewById(R.id.btLogin);
        etUserId = (EditText) v.findViewById(R.id.etUserId);
        etPassword = (EditText) v.findViewById(R.id.etPassword);

        btLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (etUserId.getText().toString().trim().equalsIgnoreCase("naushad")
                        && etPassword.getText().toString().trim().equalsIgnoreCase("123")) {
                    Intent intent = new Intent(getActivity(), EngineerControlCentre.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        return v;
    }
}
