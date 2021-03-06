package com.webmyne.paylabas.userapp.profile;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.views.ButtonRectangle;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.webmyne.paylabas.userapp.base.DatabaseWrapper;
import com.webmyne.paylabas.userapp.base.MyApplication;
import com.webmyne.paylabas.userapp.base.MyDrawerActivity;
import com.webmyne.paylabas.userapp.custom_components.CircleDialog;
import com.webmyne.paylabas.userapp.helpers.AppConstants;
import com.webmyne.paylabas.userapp.helpers.CallWebService;
import com.webmyne.paylabas.userapp.helpers.ComplexPreferences;
import com.webmyne.paylabas.userapp.home.MyAccountFragment;
import com.webmyne.paylabas.userapp.model.City;
import com.webmyne.paylabas.userapp.model.Country;
import com.webmyne.paylabas.userapp.model.LanguageStringUtil;
import com.webmyne.paylabas.userapp.model.State;
import com.webmyne.paylabas.userapp.model.User;
import com.webmyne.paylabas_user.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA =0;
    private static final int RESULT_OK=0;
    private Spinner spQuestion;
    private CircleDialog circleDialog;

    private String mParam1;
    private String mParam2;
    private ButtonRectangle btnupdateProfile;
    private EditText edFirstName;
    private EditText edLastName;
    private EditText edCountryCode;
    private EditText edEmail;
    private EditText edAddress;
    private EditText edZipcode;
    private EditText edMobileno;
    private EditText edBirthdate;
    private EditText edAnswer;
    private Spinner spCountry;
    private Spinner spState;
    private Spinner spCity;
    private static final int CAMERA_REQUEST = 500;
    private static final int GALLERY_REQUEST = 300;
    final CharSequence[] items = { "Take Photo", "Choose from Gallery" };
    private ImageView imgprofile;
    ArrayList<Country> countrylist;
    ArrayList<State> statelist;
    ArrayList<City> cityList;
    int temp_CountryID;
    int temp_CountryID1;
    int temp_StateID;
    int temp_CityID;
    private DatabaseWrapper db_wrapper;
    private static User user_prof;
    static int QuestionID;

    boolean NEW_PROFILE_IMAGE=false;
    int FLAG_STATE=0;
    int FLAG_CITY=0;
    static File ProfileImagePath;
    static String ProfileImageName;

        /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_profile, container, false);
        edFirstName = (EditText)convertView.findViewById(R.id.edFirstname);
        edLastName = (EditText)convertView.findViewById(R.id.edLastname);
        edEmail  = (EditText)convertView.findViewById(R.id.edEmail);
        edAddress = (EditText)convertView.findViewById(R.id.edAddress);
        edZipcode = (EditText)convertView.findViewById(R.id.edZipcode);
        edMobileno = (EditText)convertView.findViewById(R.id.edMobileno);
        edBirthdate = (EditText)convertView.findViewById(R.id.dgBirthdate);
        btnupdateProfile=(ButtonRectangle)convertView.findViewById(R.id.btnupdateProfile);
        imgprofile = (ImageView)convertView.findViewById(R.id.imgProfile);
        edCountryCode = (EditText)convertView.findViewById(R.id.edCountryCode);
        spQuestion = (Spinner)convertView.findViewById(R.id.spQuestion);
        spCountry = (Spinner)convertView.findViewById(R.id.spCountry);
        spState = (Spinner)convertView.findViewById(R.id.spState);
        spCity = (Spinner)convertView.findViewById(R.id.spCity);
        edAnswer = (EditText)convertView.findViewById(R.id.edanswer);

        intView();

        imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.code_UPDATEPHOTO));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                                                      Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, CAMERA_REQUEST);
                            Log.e("Camera ","exit");

                        } else if (items[item].equals("Choose from Gallery")) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(pickPhoto , GALLERY_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });

// Update profile button is Clicked.....
       btnupdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmptyField(edFirstName)) {
                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_PLZENTERFNAME));
                    bar.show();
                } else if (isEmptyField(edLastName)) {
                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_ENTELASTNAEM));
                    bar.show();
                } else if (isEmptyField(edBirthdate)) {
                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_BIRTHDAY));
                    bar.show();
                } else if (isEmptyField(edAddress)) {

                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_ADD));
                    bar.show();
                } else if (isEmptyField(edZipcode)) {

                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_ZIPCODE));
                    bar.show();

                } else if (!isZipcodeMatch(edZipcode)) {

                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_VALIDZIPCODE));
                    bar.show();
                } else if (!checkvalidquestion()) {
                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_SELQUESTION));
                    bar.show();
                }
                else if (isEmptyField(edAnswer)) {
                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_ENTERANSWER));
                    bar.show();
                } else {

                        Log.e("image name -->",String.valueOf(ProfileImageName));
                        Log.e("image path -->",String.valueOf(ProfileImagePath));

                        if(NEW_PROFILE_IMAGE){
                         uploadFile(ProfileImagePath);
                        }
                         process_UpdateProfile();
                }
            }
        });

        return convertView;
    }

    // User ProfileUpdate process
    private void process_UpdateProfile() {
        try {

            JSONObject userObject = new JSONObject();
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
            User user = complexPreferences.getObject("current_user", User.class);

            userObject.put("FName", edFirstName.getText().toString().trim());
            userObject.put("LName", edLastName.getText().toString().trim());
            userObject.put("DOBString", edBirthdate.getText().toString().trim());
            userObject.put("EmailID", edEmail.getText().toString().trim());
            userObject.put("Address", edAddress.getText().toString().trim());

            userObject.put("Country",countrylist.get(spCountry.getSelectedItemPosition()).CountryID);
            userObject.put("State", statelist.get(spState.getSelectedItemPosition()).StateID);
            userObject.put("City", cityList.get(spCity.getSelectedItemPosition()).CityID);

            userObject.put("Zip", edZipcode.getText().toString().trim());
            userObject.put("MobileNo", edMobileno.getText().toString().trim());


            userObject.put("Culture", LanguageStringUtil.CultureString(getActivity()));
            userObject.put("Answer",edAnswer.getText().toString().trim());

            //   userObject.put("DeviceType", "Android");
            //   userObject.put("Gender", "Male");
            if(NEW_PROFILE_IMAGE){
                userObject.put("Image", ProfileImageName);
            }
           else
            {
                userObject.put("Image", user.Image);
            }
            // userObject.put("IsDeleted", false);
              userObject.put("IsRegistered", true);
            //  userObject.put("IsSuperAdmin", false);
            //    userObject.put("LastTryDate", null);
            //    userObject.put("LastTryDateLogin", null);
            //   userObject.put("LemonwayBal", "lemon way amount");
            userObject.put("MobileCountryCode", edCountryCode.getText().toString().trim());
            //    userObject.put("NotificationID", "notification");
            //    userObject.put("PassportNo", "paspport");
            //    userObject.put("PaylabasMerchantID", "palabs merchant id");
             userObject.put("QuestionId", spQuestion.getSelectedItemPosition());
            //userObject.put("ResponseCode", "response code");
            // userObject.put("ResponseMsg", "response msg");
            //   userObject.put("RoleId", 2147483647);
            //   userObject.put("Status", true);
            //   userObject.put("StatusMsg", "status msg");
            //   userObject.put("TryCount", 2147483647);
            //    userObject.put("TryCountLogin", 2147483647);
            //  userObject.put("UpdateDate", null);
            //   userObject.put("UpdateDateInt", 2147483647);
            userObject.put("UserID",user.UserID );
            // userObject.put("UserName", "user1");
            //   userObject.put("VerificationCode", "verficatino code");
           userObject.put("isVerified", true);


            Log.e("json obj",userObject.toString());
            final CircleDialog circleDialog = new CircleDialog(getActivity(), 0);
            circleDialog.setCancelable(true);
            circleDialog.show();

                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.USER_REGISTRATION, userObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {
                    circleDialog.dismiss();
                    String response = jobj.toString();
                    Log.e("Profile Response222 : ", "" + response);


                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("ResponseCode").equalsIgnoreCase("1")){


                            User currentUser = new GsonBuilder().create().fromJson(response,User.class);
                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref",0);
                            complexPreferences.putObject("current_user", currentUser);
                            complexPreferences.commit();


                            SnackBar bar112 = new SnackBar(getActivity(), getString(R.string.code_PROFILEUPDATE));
                            bar112.show();

                            CountDownTimer countDownTimer;
                            countDownTimer = new MyCountDownTimer(3000, 1000); // 1000 = 1s
                            countDownTimer.start();




                        }

                        else {

                            SnackBar bar112 = new SnackBar(getActivity(), obj.getString("ResponseMsg"));
                            bar112.show();
                        }

                    } catch (Exception e) {

                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    circleDialog.dismiss();
                    Log.e("error responsPROFILE: ", error + "");
                    SnackBar bar = new SnackBar(getActivity(), error.getMessage());
                    bar.show();

                }
            });
            MyApplication.getInstance().addToRequestQueue(req);
        } catch (Exception e) {
            Log.e("error responsPROFILE: ", e.toString() + "");
        }
    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }
        @Override
        public void onFinish() {
            Log.e("counter","Time's up!");

            Intent iCOnfirmSignUp = new Intent( getActivity() ,MyDrawerActivity.class );
            startActivity(iCOnfirmSignUp);
            getActivity().finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

    }


    public void uploadFile(final File fileName){
        Log.e("filename--->",fileName+"");
        final FTPClient client = new FTPClient();
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    client.connect(AppConstants.ftpPath,121);
                    client.login(AppConstants.ftpUsername, AppConstants.ftpPassword);
                    client.setType(FTPClient.TYPE_AUTO);
                    client.changeDirectory("/Images/");

                    client.upload(fileName, new MyTransferListener());
                    Log.e("filename",fileName+"");
                } catch (Exception e) {
                    Log.e("err try1 ", e.toString());

                    try {
                        client.disconnect(true);
                    } catch (Exception e2) {
                        Log.e("err try2 ", e2.toString());
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            Log.e("filename","Upload Started ");
            // Transfer started
//                Toast.makeText(getActivity(), " Upload Started ...", Toast.LENGTH_SHORT).show();
            System.out.println(" Upload Started ...");
        }

        public void transferred(int length) {
            System.out.println(" transferred ..." );
            Log.e("filename","transferred");
        }

        public void completed() {
            // Transfer completed
            System.out.println(" completed ..." );
            Log.e("filename", "upload completed");
            circleDialog.dismiss();

          /*  getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SnackBar bar = new SnackBar(getActivity(),"Profile Image Update Sucessfully...");
                    bar.show();

                }
            });*/

        }

        public void aborted() {
            // Transfer aborted
            System.out.println(" transfer aborted ,please try again..." );
//                Toast.makeText(getActivity()," transfer aborted ,please try again...", Toast.LENGTH_SHORT).show();
        }

        public void failed() {
            // Transfer failed
            System.out.println(" failed ..." );
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Titlekris", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("inside on acitivyty","inside on activity");
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();


                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP

                Uri tempUri = getImageUri(getActivity(), bmp);
                ProfileImageName = String.valueOf(tempUri);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                ProfileImagePath = new File(getRealPathFromURI(tempUri));

                // Convert ByteArray to Bitmap::
                final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imgprofile.setBackground(null);
                imgprofile.setImageBitmap(bitmap);
                NEW_PROFILE_IMAGE=true;
            }
            else{
                SnackBar bar = new SnackBar(getActivity(),getString(R.string.code_ERRORTOLOADFROMCAMERA));
                bar.show();
            }

        }
        else  if (requestCode == GALLERY_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                final Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                File file = new File(picturePath);
                ProfileImagePath = file;
                ProfileImageName= file.getName();
                imgprofile.setBackground(null);
                imgprofile.setImageBitmap(thumbnail);
                NEW_PROFILE_IMAGE=true;

              }
            else{
                SnackBar bar = new SnackBar(getActivity(),getString(R.string.code_ERRORTOLOADFROMGALLERY));
                bar.show();
            }
        }
    }
    public void intView(){

           fetchCountryAndDisplay();

           spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = countrylist.get(position).CountryID;
                fetchStateAndDisplay(pos);
                temp_CountryID1=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FLAG_CITY=1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuestionID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public boolean checkvalidquestion()
    {
        boolean isquestionvalid = false;
        if(QuestionID==0){

            isquestionvalid=false;
        }
        else
        {
            isquestionvalid=true;
        }

        return isquestionvalid;
    }

public boolean isZipcodeMatch(EditText param1){
        boolean isMatch = false;
        if(param1.getText().toString().matches("[a-zA-Z0-9]*")){
            isMatch = true;
        }
        return isMatch;
    }
public boolean isEmptyField(EditText param1){

        boolean isEmpty = false;
        if(param1.getText() == null || param1.getText().toString().equalsIgnoreCase("")){
            isEmpty = true;
        }
        return isEmpty;
    }

public void  fetchCountryAndDisplay(){
    new AsyncTask<Void,Void,Void>() {
        @Override
        protected Void doInBackground(Void... voids) {

            db_wrapper = new DatabaseWrapper(getActivity());
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

            try{


                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                User user = complexPreferences.getObject("current_user", User.class);

                CountryAdapter countryAdapter = new CountryAdapter(getActivity(),R.layout.spinner_country, countrylist);
                spCountry.setAdapter(countryAdapter);

                circleDialog =new CircleDialog(getActivity(),0);
                circleDialog.setCancelable(true);
                circleDialog.show();

                new CallWebService(AppConstants.GET_USER_PROFILE +user.UserID+"/"+LanguageStringUtil.CultureString(getActivity()),CallWebService.TYPE_JSONOBJECT) {
                    @Override
                    public void response(String response) {

                        Log.e("Profile get",response);
                        User currentUser_Profile = new GsonBuilder().create().fromJson(response,User.class);

                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                        complexPreferences.putObject("current_user", currentUser_Profile);
                        complexPreferences.commit();

                        ComplexPreferences complexPreferences2 = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                        user_prof = complexPreferences2.getObject("current_user", User.class);

                        String dateTime = user_prof.DOBString.toString();
                        String date = dateTime.split(" ")[0];

                        edFirstName.setText(user_prof.FName.toString());
                        edLastName.setText(user_prof.LName.toString());
                        edBirthdate.setText(date);
                        edEmail.setText(user_prof.EmailID.toString());
                        edAddress.setText(user_prof.Address.toString());
                        edZipcode.setText(user_prof.Zip.toString());
                        edCountryCode.setText(user_prof.MobileCountryCode.toString());
                        edMobileno.setText(user_prof.MobileNo.toString());
                        spQuestion.setSelection((int) user_prof.QuestionId);
                        edAnswer.setText(user_prof.Answer);

                        // for image
                        if(user_prof.Image !=null){
                            processCheckImage(user_prof.Image.toString());
                        }

                        int posCountry = 0;
                        try {
                            for (int i = 0; i < countrylist.size(); i++) {
                                if (countrylist.get(i).CountryID == user_prof.Country) {
                                    posCountry = i;
                                    break;
                                }
                            }
                        }catch (Exception e){
                            Log.e("error ","recipient-prof is not loaded");
                        }
                        spCountry.setSelection(posCountry);
                       // spCountry.setSelection((int)user_prof.Country-1);

                        //todo remain
                        //fetchStateAndDisplay((int)user_prof.Country);

                        circleDialog.dismiss();
                    }
                    @Override
                    public void error(VolleyError error) {
                        Log.e("volly er",error.toString());
                        circleDialog.dismiss();
                    }
                }.start();



            }catch(Exception e){
            }


                  }

    }.execute();


}

private void processCheckImage(String imgName) {
      try {
            Log.e("full path",String.valueOf(AppConstants.fileDownloadPath+imgName));
            Picasso.with(getActivity().getBaseContext()).load(AppConstants.fileDownloadPath+imgName).into(imgprofile);

        }
        catch(Exception e){
            Log.e("Execpetion occurs ",e.toString());
        }

    }

private void fetchStateAndDisplay(int CountryID) {

        statelist = new ArrayList<State>();

        temp_CountryID=CountryID;

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                db_wrapper = new DatabaseWrapper(getActivity());
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
                StateAdapter stateAdapter = new StateAdapter(getActivity(),R.layout.spinner_state, statelist);
                spState.setAdapter(stateAdapter);

                int posState = 0;
                try {
                    for (int i = 0; i < statelist.size(); i++) {
                        if (statelist.get(i).StateID == user_prof.State) {
                            posState = i;
                            break;
                        }
                    }
                }catch (Exception e){
                    Log.e("error fetchStateblock","user-prof is not loaded");
                }
                spState.setSelection(posState);





               // spState.setSelection(0);

                spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        temp_StateID=position;
                        try {
                            fetchAndDisplayCity(statelist.get(position).StateID);
                        }
                        catch(Exception e){
                            Log.e("err in fetchStateblock","on seleetced statelist not ready");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }
        }.execute();
    }

private void fetchAndDisplayCity(final int stateID) {
        cityList = new ArrayList<City>();
        boolean isAlreadyThere = false;
        db_wrapper = new DatabaseWrapper(getActivity());
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
                    db_wrapper = new DatabaseWrapper(getActivity());
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

                    CityAdapter cityAdapter = new CityAdapter(getActivity(),R.layout.spinner_country, cityList);
                    spCity.setAdapter(cityAdapter);

                    int posState = 0;
                    try {
                    for(int i=0;i<cityList.size();i++){
                        if(cityList.get(i).CityID == user_prof.City){
                            posState = i;
                            break;
                        }
                    }
                    }catch (Exception e){
                        Log.e("error fetchCityblock","user-prof is not loaded");
                    }
                    spCity.setSelection(posState);


                }
            }.execute();


        }else{

            final CircleDialog circleDialog=new CircleDialog(getActivity(),0);
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
                    CityAdapter cityAdapter = new CityAdapter(getActivity(),R.layout.spinner_country, cityList);
                    spCity.setAdapter(cityAdapter);

                    db_wrapper = new DatabaseWrapper(getActivity());
                    try {
                        db_wrapper.openDataBase();
                        db_wrapper.insertCities(cityList);
                        db_wrapper.close();
                    }catch(Exception e){e.printStackTrace();}

                    int posState = 0;
                    try {
                    for(int i=0;i<cityList.size();i++){
                        if(cityList.get(i).CityID == user_prof.City){
                            posState = i;
                            break;
                        }
                    }
                    }catch (Exception e){
                        Log.e("error fetchCityblock","user-prof is not loaded");
                    }
                    spCity.setSelection(posState);


                }

                @Override
                public void error(VolleyError error) {

                    circleDialog.dismiss();
                }
            }.start();

        }

    }

    public class CityAdapter extends ArrayAdapter<City>{

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

            TextView txt = new TextView(getActivity());
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).CityName);
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(getActivity());
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

            TextView txt = new TextView(getActivity());
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).StateName);
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(getActivity());
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
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(getActivity());
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).CountryName);
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(getActivity());
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setText(values.get(position).CountryName);
            return  txt;
        }
    }
}
