package com.webmyne.paylabas.userapp.money_transfer;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.webmyne.paylabas.userapp.base.MyApplication;
import com.webmyne.paylabas.userapp.base.MyDrawerActivity;
import com.webmyne.paylabas.userapp.custom_components.CircleDialog;
import com.webmyne.paylabas.userapp.custom_components.OTPDialog;
import com.webmyne.paylabas.userapp.helpers.AppConstants;
import com.webmyne.paylabas.userapp.helpers.ComplexPreferences;
import com.webmyne.paylabas.userapp.home.MyAccountFragment;
import com.webmyne.paylabas.userapp.model.LanguageStringUtil;
import com.webmyne.paylabas.userapp.model.PickUpPoint;
import com.webmyne.paylabas.userapp.model.Receipient;
import com.webmyne.paylabas.userapp.model.User;
import com.webmyne.paylabas_user.R;

import org.json.JSONObject;

import java.util.ArrayList;


public class MoneyTransferFinalActivity extends ActionBarActivity {

    Toolbar toolbar_actionbar;
    public static TextView txtSelectRecipient;
    public static Receipient recObj;

    ButtonRectangle btnAddRecipient;
    Float FinalPayableAmount;
    User user;


    TextView txtSendAmount,txtFees,txtamountPayable,txtamountRecipientGET,txtExchangerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer_final);

        toolbar_actionbar = (Toolbar)findViewById(R.id.toolbar);
        /* setting up the toolbar starts*/
        if (toolbar_actionbar != null) {
            toolbar_actionbar.setTitle(getString(R.string.code_TITLEMONEYTRANSFER));
            toolbar_actionbar.setNavigationIcon(R.drawable.icon_back);
            toolbar_actionbar.setBackgroundColor(getResources().getColor(R.color.paylabas_green));

            setSupportActionBar(toolbar_actionbar);

        }
        toolbar_actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intView();

        fillDestails();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(recObj!=null){
            Log.e("object", "object not null");
            txtSelectRecipient.setText(recObj.FirstName.toString()+" "+recObj.LastName.toString());
        }
        else {
            Log.e("object", "object is null");
            txtSelectRecipient.setText(getString(R.string.code_SELECTRECIPEINT));
        }

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(MoneyTransferFinalActivity.this, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);

    }

    private void fillDestails(){

        float commision = MoneyTrtansferChildFragment.bankobj.ApproxComm;

        String percentage = MoneyTrtansferChildFragment.bankobj.Perccharge;
        String fixedprice = MoneyTrtansferChildFragment.bankobj.Fixedcharge;

        Float Percentage = Float.valueOf(percentage)/100;

        Float amount1 = MoneyTrtansferChildFragment.bankobj.Amount * Percentage ;
        Log.e("amount1",String.valueOf(amount1));

//        Float FinalFeesAmount = Float.valueOf(MoneyTrtansferChildFragment.bankobj.ApproxComm)+amount1+Float.valueOf(fixedprice);

      //  FinalPayableAmount = Float.valueOf(MoneyTrtansferChildFragment.bankobj.Amount)+FinalFeesAmount;

        FinalPayableAmount = Float.valueOf(MoneyTrtansferChildFragment.bankobj.PayableAmt);

        txtSendAmount.setText("€ "+LanguageStringUtil.languageString(MoneyTransferFinalActivity.this, String.valueOf(MoneyTrtansferChildFragment.bankobj.Amount)));

        Float Fees = FinalPayableAmount - MoneyTrtansferChildFragment.bankobj.Amount;

        txtFees.setText("Fees:  € "+ LanguageStringUtil.languageString(MoneyTransferFinalActivity.this, String.valueOf(Fees)));

        txtamountPayable.setText("Total Payable Amount: € "+LanguageStringUtil.languageString(MoneyTransferFinalActivity.this, String.valueOf(MoneyTrtansferChildFragment.bankobj.PayableAmt)));

        txtamountRecipientGET.setText("Recipient Gets: "+LanguageStringUtil.languageString(MoneyTransferFinalActivity.this, String.valueOf(MoneyTrtansferChildFragment.bankobj.RecipientGet))+" "+ MoneyTrtansferChildFragment.bankobj.Currencies);
        txtExchangerate.setText("Exchnage Rate:  € 1 = "+LanguageStringUtil.languageString(MoneyTransferFinalActivity.this, String.valueOf(MoneyTrtansferChildFragment.bankobj.ConvRate))+" "+ MoneyTrtansferChildFragment.bankobj.Currencies);

    }
    private void intView(){

    txtSelectRecipient = (TextView)findViewById(R.id.txtSelectRecipient);

      txtSendAmount = (TextView)findViewById(R.id.txtSendAmount);
      txtFees = (TextView)findViewById(R.id.txtFees);
      txtamountPayable = (TextView)findViewById(R.id.txtamountPayable);
      txtamountRecipientGET = (TextView)findViewById(R.id.txtamountRecipientGET);
      txtExchangerate     = (TextView)findViewById(R.id.txtExchangerate);

    btnAddRecipient = (ButtonRectangle)findViewById(R.id.btnAddRecipient);

    txtSelectRecipient.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MoneyTransferFinalActivity.this,MoneyTransferRecipientActivity.class);
            i.putExtra("cc",getIntent().getStringExtra("cc"));
            startActivity(i);
        }
    });


     btnAddRecipient.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(recObj==null){
                 SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this,getString(R.string.code_SELECTRECIPIENTFIRST));
                 bar.show();
             }
             else {
                 processOTP();
             }
         }
     });

}

    private void processOTP(){
        try{

            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(MoneyTransferFinalActivity.this, "user_pref", 0);
            User user = complexPreferences.getObject("current_user", User.class);

            JSONObject userObject = new JSONObject();

            userObject.put("Amount",String.valueOf(MoneyTrtansferChildFragment.bankobj.Amount));
            userObject.put("UserCountryCode",String.valueOf(user.MobileCountryCode));
            userObject.put("UserID",String.valueOf(user.UserID));
            userObject.put("UserMobileNo", user.MobileNo);
            userObject.put("Culture", LanguageStringUtil.CultureString(MoneyTransferFinalActivity.this));

            Log.e("otp object",userObject.toString());

            final CircleDialog circleDialog = new CircleDialog(MoneyTransferFinalActivity.this, 0);
            circleDialog.setCancelable(true);
            circleDialog.show();
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.SEND_OTP, userObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {
                    circleDialog.dismiss();
                    String response = jobj.toString();
                    Log.e("cash out  Response", "" + response);
                    OTP otpobj= new GsonBuilder().create().fromJson(jobj.toString(), OTP.class);

                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("ResponseCode").equalsIgnoreCase("1")){

                            OTPDialog otpDialog = new OTPDialog(MoneyTransferFinalActivity.this,0,otpobj.VerificationCode);
                            otpDialog.setOnConfirmListner(new OTPDialog.OnConfirmListner() {
                                @Override
                                public void onComplete(String response) {
                                    processMoney();
                                }
                            });


                        }

                        else {
                            SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this,obj.getString("ResponseMsg"));
                            bar.show();
                        }

                    } catch (Exception e) {
                        Log.e("error response recharge1: ", e.toString() + "");
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    circleDialog.dismiss();
                    Log.e("error response live curreency: ", error + "");
                    SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this,getString(R.string.code_NWW));
                    bar.show();
                }
            });


            req.setRetryPolicy(  new DefaultRetryPolicy(0,0,0));
            MyApplication.getInstance().addToRequestQueue(req);
        }catch(Exception e){
            Log.e("exception",e.toString());
        }

    }

private void processMoney(){

    final CircleDialog circleDialog=new CircleDialog(MoneyTransferFinalActivity.this,0);
    circleDialog.setCancelable(true);
    circleDialog.show();

    /*"Amount":"String content",
            "BankID":"String content",

            "PayableAmt":"String content",

            "ReceiverAddress":"String content",
            "ReceiverCity":"String content",
            "ReceiverCountry":"String content",

            "ReceiverEmailAddress":"String content",
            "ReceiverFirstName":"String content",
            "ReceiverLastName":"String content",
            "ReceiverMobileNo":"String content",
            "ReceiverState":"String content",
            "ReceiverZip":"String content",
            "ShortCode":"String content",

            "UserID":9223372036854775807*/


    try{
        JSONObject userObject = new JSONObject();

        userObject.put("Amount",String.valueOf(MoneyTrtansferChildFragment.bankobj.Amount));
        userObject.put("BankID",String.valueOf(MoneyTrtansferChildFragment.bankobj.BankID));
        userObject.put("PayableAmt",String.valueOf(FinalPayableAmount));

        userObject.put("ReceiverAddress",String.valueOf(recObj.Address));
        userObject.put("ReceiverCity",String.valueOf(recObj.City));
        userObject.put("ReceiverCountry",String.valueOf(recObj.Country));

        userObject.put("ReceiverEmailAddress",String.valueOf(recObj.EmailId));
        userObject.put("ReceiverFirstName",String.valueOf(recObj.FirstName));
        userObject.put("ReceiverLastName",String.valueOf(recObj.LastName));

        userObject.put("ReceiverMobileNo",String.valueOf(recObj.MobileNo));
        userObject.put("ReceiverState",String.valueOf(recObj.State));
        userObject.put("ReceiverZip",String.valueOf(recObj.ZipCode));
        //userObject.put("ShortCode",String.valueOf(FinalPayableAmount));
        userObject.put("Culture", LanguageStringUtil.CultureString(MoneyTransferFinalActivity.this));
        userObject.put("UserID",user.UserID);


        Log.e("obj of mone transfer --",userObject.toString());
        JsonObjectRequest req = new JsonObjectRequest(com.android.volley.Request.Method.POST, AppConstants.MoONEY_CASH_PICKUP, userObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jobj) {

                circleDialog.dismiss();
                String response = jobj.toString();
                Log.e("Response money transfer : ", "" + response);
                try {

                    JSONObject obj = new JSONObject(response);
                    if(obj.getString("ResponseCode").equalsIgnoreCase("1")){

                        SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this,getString(R.string.code_MONEYTRANSFERDONE));
                        bar.show();
                        recObj = null;  // setting the object null
                    }

                    else {
                        SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this,getString(R.string.code_MONEYTRANSFERFAIL));
                        bar.show();
                        recObj = null;  // setting the object null

                    }

                    CountDownTimer countDownTimer;
                    countDownTimer = new MyCountDownTimer(3000, 1000); // 1000 = 1s
                    countDownTimer.start();


                } catch (Exception e) {
                    Log.e("error responsegg1: ", e.toString() + "");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                circleDialog.dismiss();
                Log.e("error responsegg:11 ", error + "");
                SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this, error.getMessage());
                bar.show();

            }
        });

        req.setRetryPolicy(  new DefaultRetryPolicy(0,0,0));
        MyApplication.getInstance().addToRequestQueue(req);

    }catch (Exception e){
        Log.e("Exception in money transfer1",e.toString());
        SnackBar bar = new SnackBar(MoneyTransferFinalActivity.this, e.getMessage());
        bar.show();
    }
}
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }
        @Override
        public void onFinish() {
            Log.e("counter","Time's up!");

            Intent i = new Intent(MoneyTransferFinalActivity.this,MyDrawerActivity.class);
            startActivity(i);
            finish();

           /* FragmentManager manager = MoneyTransferFinalActivity.this.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_container,new MyAccountFragment());
            ft.commit();*/
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

    }
    private class OTP{
        @SerializedName("VerificationCode")
        public String VerificationCode;
    }
//end of main class
}
