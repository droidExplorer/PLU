package com.webmyne.paylabas.userapp.money_transfer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webmyne.paylabas.userapp.base.DatabaseWrapper;
import com.webmyne.paylabas.userapp.custom_components.CircleDialog;
import com.webmyne.paylabas.userapp.helpers.AppConstants;
import com.webmyne.paylabas.userapp.helpers.CallWebService;
import com.webmyne.paylabas.userapp.helpers.ComplexPreferences;
import com.webmyne.paylabas.userapp.model.City;
import com.webmyne.paylabas.userapp.model.Country;
import com.webmyne.paylabas.userapp.model.Receipient;
import com.webmyne.paylabas.userapp.model.State;
import com.webmyne.paylabas.userapp.model.User;
import com.webmyne.paylabas_user.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MoneyTransferRecipientActivity extends ActionBarActivity {

    Toolbar toolbar_actionbar;
    TextView txtSelectRecipient;
    Spinner spinnerRecipientContact,spCountry,spState,spCity;
    EditText edFirstname,edLastname,edEmail,edAddress,edZipcode,edCountryCode,edMobileno;

    private ArrayList<Receipient> receipients;
    private User user;

    ArrayList<Country> countrylist;
    ArrayList<State> statelist;
    ArrayList<City> cityList;
    int temp_CountryID;
    int temp_CountryID1;
    int temp_StateID;
    int temp_CityID;
    int getCountryID;
    int RecipientId;
    private DatabaseWrapper db_wrapper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer_recipient);

        toolbar_actionbar = (Toolbar)findViewById(R.id.toolbar);
        /* setting up the toolbar starts*/
        if (toolbar_actionbar != null) {
            toolbar_actionbar.setTitle("Recipient");
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


    }

private void intView(){

    spinnerRecipientContact = (Spinner)findViewById(R.id.spinnerRecipient);
    edFirstname = (EditText)findViewById(R.id.edFirstname);
    edLastname = (EditText)findViewById(R.id.edLastname);
    edAddress = (EditText)findViewById(R.id.edAddress);
    edZipcode  = (EditText)findViewById(R.id.edZipcode);
    edCountryCode  = (EditText)findViewById(R.id.edCountryCode);
    edEmail = (EditText)findViewById(R.id.edEmail);
    edMobileno = (EditText)findViewById(R.id.edMobileno);

    spCountry = (Spinner)findViewById(R.id.spCountry);
    spState = (Spinner)findViewById(R.id.spState);
    spCity = (Spinner)findViewById(R.id.spCity);


    spinnerRecipientContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){

                    }else{
                        fillRecipientDetails(position);

                    }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });


    spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            fetchStateAndDisplay(position+1,spinnerRecipientContact.getSelectedItemPosition());
            temp_CountryID1=position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });

    spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            temp_CityID=position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });



}

    @Override
    protected void onResume() {
        super.onResume();
        fetchRecipientDisplay();
        fetchCountryAndDisplay();
    }



private void fillRecipientDetails(int pos){
edFirstname.setText(receipients.get(pos).FirstName);
edLastname.setText(receipients.get(pos).LastName);
edEmail.setText(receipients.get(pos).EmailId);
edMobileno.setText(receipients.get(pos).MobileNo);


}

private void fetchRecipientDisplay(){
    final CircleDialog circleDialog = new CircleDialog(MoneyTransferRecipientActivity.this, 0);
    circleDialog.setCancelable(true);
    circleDialog.show();

    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(MoneyTransferRecipientActivity.this, "user_pref", 0);
    user = complexPreferences.getObject("current_user", User.class);

    new CallWebService(AppConstants.GETRECEIPIENTS + user.UserID, CallWebService.TYPE_JSONARRAY) {

        @Override
        public void response(String response) {

            circleDialog.dismiss();
            Log.e("Receipients List", response);
            if (response == null) {

            } else {

                Type listType = new TypeToken<List<Receipient>>() {
                }.getType();

                receipients = new GsonBuilder().create().fromJson(response, listType);

                Receipient r1 = new Receipient();
                r1.FirstName = "Select";
                r1.LastName = "Recipient";
                receipients.add(0,r1);



                RecipientAdapter adapter = new RecipientAdapter(MoneyTransferRecipientActivity.this,
                        android.R.layout.simple_spinner_item,receipients);

                spinnerRecipientContact.setAdapter(adapter);

            }

        }

        @Override
        public void error(VolleyError error) {
            circleDialog.dismiss();
            SnackBar bar = new SnackBar(MoneyTransferRecipientActivity.this, "Sync Error. Please Try again");
            bar.show();
        }
    }.start();

}

private void fetchCountryAndDisplay() {

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                db_wrapper = new DatabaseWrapper(MoneyTransferRecipientActivity.this);
                try {
                    db_wrapper.openDataBase();
                    countrylist= db_wrapper.getCountryData();
                    db_wrapper.close();
                }catch(Exception e){e.printStackTrace();}

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                CountryAdapter countryAdapter = new CountryAdapter(MoneyTransferRecipientActivity.this,R.layout.spinner_country, countrylist);
                spCountry.setAdapter(countryAdapter);

               // getCountryID=re

                spCountry.setSelection(getCountryID-1);
            }
        }.execute();

    }
    private void fetchStateAndDisplay(int CountryID,final int pos) {

        statelist = new ArrayList<State>();
        edCountryCode.setText(String.valueOf(countrylist.get(spCountry.getSelectedItemPosition()).CountryCode));
        temp_CountryID=CountryID;

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                db_wrapper = new DatabaseWrapper(MoneyTransferRecipientActivity.this);
                try {
                    db_wrapper.openDataBase();
                    statelist= db_wrapper.getStateData(temp_CountryID);
                    db_wrapper.close();
                }catch(Exception e){e.printStackTrace();}

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                StateAdapter stateAdapter = new StateAdapter(MoneyTransferRecipientActivity.this,R.layout.spinner_state, statelist);
                spState.setAdapter(stateAdapter);

                int posState = 0;
                try {
                    for (int i = 0; i < statelist.size(); i++) {
                        if (statelist.get(i).StateID == receipients.get(pos).State) {
                            posState = i;
                            break;
                        }
                    }
                }catch (Exception e){
                    Log.e("error ","recipient-prof is not loaded");
                }

                spState.setSelection(posState);


                spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        temp_StateID=position;
                        fetchAndDisplayCity(statelist.get(position).StateID,spinnerRecipientContact.getSelectedItemPosition());

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }
        }.execute();
    }

    private void fetchAndDisplayCity(final int stateID,final int pos) {
        cityList = new ArrayList<City>();
        boolean isAlreadyThere = false;
        db_wrapper = new DatabaseWrapper(MoneyTransferRecipientActivity.this);
        try {
            db_wrapper.openDataBase();
            isAlreadyThere = db_wrapper.isAlreadyInDatabase(stateID);
            db_wrapper.close();
        }catch(Exception e){e.printStackTrace();}

        if(isAlreadyThere == true){

            System.out.println("Cities are already in database");
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    db_wrapper = new DatabaseWrapper(MoneyTransferRecipientActivity.this);
                    try {
                        db_wrapper.openDataBase();
                        cityList = db_wrapper.getCityData(stateID);
                        db_wrapper.close();
                    }catch(Exception e){e.printStackTrace();}
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    CityAdapter cityAdapter = new CityAdapter(MoneyTransferRecipientActivity.this,R.layout.spinner_country, cityList);
                    spCity.setAdapter(cityAdapter);

                    int posCity = 0;
                    for(int i=0;i<cityList.size();i++){
                        if(cityList.get(i).CityID == receipients.get(pos).City){
                            posCity = i;
                            break;
                        }
                    }
                    spCity.setSelection(posCity);

                }
            }.execute();


        }else{

            final CircleDialog circleDialog=new CircleDialog(MoneyTransferRecipientActivity.this,0);
            circleDialog.setCancelable(true);
            circleDialog.show();


            System.out.println("Cities are not there");
            new CallWebService(AppConstants.GETCITIES +stateID,CallWebService.TYPE_JSONARRAY) {

                @Override
                public void response(String response) {

                    circleDialog.dismiss();
                    Type listType=new TypeToken<List<City>>(){
                    }.getType();
                    cityList =  new GsonBuilder().create().fromJson(response, listType);
                    CityAdapter cityAdapter = new CityAdapter(MoneyTransferRecipientActivity.this,R.layout.spinner_country, cityList);
                    spCity.setAdapter(cityAdapter);

                    db_wrapper = new DatabaseWrapper(MoneyTransferRecipientActivity.this);
                    try {
                        db_wrapper.openDataBase();
                        db_wrapper.insertCities(cityList);
                        db_wrapper.close();
                    }catch(Exception e){e.printStackTrace();}

                    int posCity = 0;
                    for(int i=0;i<cityList.size();i++){
                        if(cityList.get(i).CityID == receipients.get(pos).City){
                            posCity = i;
                            break;
                        }
                    }

                    spCity.setSelection(posCity);
                }

                @Override
                public void error(VolleyError error) {

                    circleDialog.dismiss();
                }
            }.start();

        }

    }


    public class CityAdapter extends ArrayAdapter<City> {

        Context context;
        int layoutResourceId;
        ArrayList<City> values;
        // int android.R.Layout.

        public CityAdapter(Context context, int resource, ArrayList<City> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).CityName);
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setText(values.get(position).CityName);
            return  txt;
        }
    }
    public class StateAdapter extends ArrayAdapter<State>{

        Context context;
        int layoutResourceId;
        ArrayList<State> values;
        // int android.R.Layout.

        public StateAdapter(Context context, int resource, ArrayList<State> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).StateName);
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setText(values.get(position).StateName);
            return  txt;
        }
    }
    public class CountryAdapter extends ArrayAdapter<Country> {
        Context context;
        int layoutResourceId;
        ArrayList<Country> values;

        // int android.R.Layout.
        public CountryAdapter(Context context, int resource, ArrayList<Country> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).CountryName);
            return txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16, 16, 16, 16);
            txt.setText(values.get(position).CountryName);
            return txt;
        }
    }

    public class RecipientAdapter extends ArrayAdapter<Receipient> {

        Context context;
        int layoutResourceId;
        ArrayList<Receipient> values;
        // int android.R.Layout.

        public RecipientAdapter(Context context, int resource, ArrayList<Receipient> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).FirstName+" "+values.get(position).LastName);
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MoneyTransferRecipientActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setText(values.get(position).FirstName+" "+values.get(position).LastName);
            return  txt;
        }
    }



    //end of main class
}
