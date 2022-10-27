package com.bluecode.weledger;

import static com.bluecode.weledger.Constants.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bluecode.weledger.BookWriterAdminDashboard;
import com.bluecode.weledger.Constants;
import com.bluecode.weledger.MainActivity;
import com.bluecode.weledger.R;
import com.bluecode.weledger.utils.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookwriterNewMemberActivity extends AppCompatActivity {
    RadioGroup radio_single_fcg,radio_member_role,radio_gender;
    RadioButton radio_male,radio_female,radio_ordinary,radio_no,radio_yes;
    Toolbar toolbar;
    RequestQueue mRequestQueue;

    //radio_gender = findViewById(R.id.radio_gender);
    TextView save_member_details,groupName,groupID;
    EditText firstName,lastName,userName,passWord,admissionDate,gender,ecap_hh_ID,phoneNumber,userRole,singleFSW;
    Spinner spinner_singleFSW,spinner_gender,spinner_userRole;
    String submit_member_url=BASE_URL+"submit_member.php";
    String group_name;
    String group_id;
    DatePicker admission_date;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        userName = findViewById(R.id.user_name);
        passWord = findViewById(R.id.user_password);
        groupName = findViewById(R.id.group_name);
        groupID = findViewById(R.id.group_id);
        admissionDate = findViewById(R.id.admission_date);
        spinner_gender = findViewById(R.id.gender);
        ecap_hh_ID = findViewById(R.id.ecap_hh_id);
        phoneNumber = findViewById(R.id.phone_number);
        spinner_userRole = findViewById(R.id.user_role);
        spinner_singleFSW = findViewById(R.id.single_fsw);
        save_member_details = findViewById(R.id.save_member_details);



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        group_name = preferences.getString("group_name", "");
        group_id = preferences.getString("group_id", "");

        groupName.setText(group_name);
        groupName.setEnabled(false);
        groupName.setTextColor(Color.BLACK);

        groupID.setText(group_id);
        groupID.setEnabled(false);
        groupID.setTextColor(Color.BLACK);

        mRequestQueue = Connectivity.getInstance(this).getRequestQueue();
        save_member_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_group_name = groupName.getText().toString();
                String str_group_id = groupID.getText().toString();
                String str_firstName = firstName.getText().toString();
                String str_lastName = lastName.getText().toString();
                String str_userName = userName.getText().toString();
                String str_passWord = passWord.getText().toString();
                String str_admissionDate = admissionDate.getText().toString();
                String str_gender = spinner_gender.getSelectedItem().toString();
                String str_ecap_hh_ID = ecap_hh_ID.getText().toString();
                String str_phoneNumber = phoneNumber.getText().toString();
                String str_userRole = spinner_userRole.getSelectedItem().toString();
                String str_singleFSW = spinner_singleFSW.getSelectedItem().toString();


                if(userName.getText().toString().isEmpty()) {
                    errorDialog("Username Cannot Be Empty");
                }
                else startSubmission(
                        str_group_name,
                        str_group_id,
                        str_firstName,
                        str_lastName,
                        str_userName,
                        str_passWord,
                        str_admissionDate,
                        str_gender,
                        str_ecap_hh_ID,
                        str_phoneNumber,
                        str_userRole,
                        str_singleFSW
                );
            }
        });

    }
    private void startSubmission(final String groupname,
                                 final String groupid,
                                 final String firstname,
                                 final String surname,
                                 final String username,
                                 final String password,
                                 final String admissiondate,
                                 final String gender,
                                 final String ecaphh_id,
                                 final String phonenumber,
                                 final String userrole,
                                 final String singlefsw
    )
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String str_a = preferences.getString("a", "");
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.loading_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog reportsAlert = builder.create();
        if (reportsAlert.getWindow() != null)
            reportsAlert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        reportsAlert.setCancelable(true);
        reportsAlert.setCanceledOnTouchOutside(true);
        reportsAlert.show();
        reportsAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, submit_member_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("member_url", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {

                        String received_msg = object.getString("msg");

                        reportsAlert.dismiss();
                        errorDialog(object.getString("msg"));
                        finish();
                        Intent intent = new Intent(getApplicationContext(), BookWriterAdminDashboard.class);
                        startActivity(intent);
                    } else if (object.getString("status").equals("failed")) {
                        reportsAlert.dismiss();
                        errorDialog(object.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("group_name", groupname);
                parms.put("group_id",groupid);
                parms.put("firstname", firstname);
                parms.put("lastname", surname);
                parms.put("nrc", username);
                parms.put("password", password);
                parms.put("admission_date", admissiondate);
                parms.put("ecap_hh_id", ecaphh_id);
                parms.put("phone_number", phonenumber);
                parms.put("gender", gender);
                parms.put("user_role", userrole);
                parms.put("single_female_caregiver", singlefsw);
                parms.put("a", str_a);
                return parms;
            }
        };
        stringRequest.setShouldCache(false);
        mRequestQueue.add(stringRequest);
    }
    public void errorDialog(String error_text) {

        ViewGroup viewGroup = findViewById(android.R.id.content);
        final TextView main_text;
        final Button btn_ok;
        final LinearLayout linear_buttons;
        final CardView card_ok;
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_error, viewGroup, false);

        btn_ok = dialogView.findViewById(R.id.btn_ok);
//        card_ok = dialogView.findViewById(R.id.card_ok);
        main_text = dialogView.findViewById(R.id.main_text);
        linear_buttons = dialogView.findViewById(R.id.linear_buttons);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        final AlertDialog reportsAlert = builder.create();
        // Let's start with animation work. We just need to create a style and use it here as follow.
        if (reportsAlert.getWindow() != null)
            reportsAlert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        reportsAlert.show();
        reportsAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btn_ok.setVisibility(View.VISIBLE);
        linear_buttons.setVisibility(View.GONE);
//        To prevent dialog box from getting dismissed on back key pressed use this
        reportsAlert.setCancelable(false);

//        And to prevent dialog box from getting dismissed on outside touch use this
        reportsAlert.setCanceledOnTouchOutside(false);
        main_text.setText(error_text);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportsAlert.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BookWriterAdminDashboard.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.GROUP_NAME, group_name);
        bundle.putString(Constants.GROUP_ID,group_id);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
