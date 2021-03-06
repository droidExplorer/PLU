package com.webmyne.paylabas.userapp.giftcode;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webmyne.paylabas.userapp.base.MyApplication;
import com.webmyne.paylabas.userapp.custom_components.CircleDialog;
import com.webmyne.paylabas.userapp.helpers.AppConstants;
import com.webmyne.paylabas.userapp.helpers.CallWebService;
import com.webmyne.paylabas.userapp.helpers.ComplexPreferences;
import com.webmyne.paylabas.userapp.home.MyAccountFragment;
import com.webmyne.paylabas.userapp.model.Country;
import com.webmyne.paylabas.userapp.model.GCCountry;
import com.webmyne.paylabas.userapp.model.LanguageStringUtil;
import com.webmyne.paylabas.userapp.model.User;
import com.webmyne.paylabas_user.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CombineGCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CombineGCFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private LinearLayout linearCombineGiftCode;
    private ButtonRectangle btnAddCombineGiftCode;
    private ButtonRectangle btnCombineGcCombineGc;

    private User user;
    private ArrayList<String> combine_giftcode_list;
    private ArrayList<GCCountry> countryList;
    private Spinner spGCCountry;
    private JSONObject responseObject;
    private GCCountryAdapter gcCountryAdapter;
    private double localOldtextValue;

    public static CombineGCFragment newInstance(String param1, String param2) {
        CombineGCFragment fragment = new CombineGCFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CombineGCFragment() {

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

        View convertView = inflater.inflate(R.layout.fragment_combine_gc, container, false);
        init(convertView);
        return convertView;
    }

    private void init(View convertView) {
        linearCombineGiftCode = (LinearLayout) convertView.findViewById(R.id.linearCombineGiftCode);
        btnAddCombineGiftCode = (ButtonRectangle) convertView.findViewById(R.id.btnAddCombineGiftCode);
        btnAddCombineGiftCode.setOnClickListener(this);
        btnCombineGcCombineGc = (ButtonRectangle) convertView.findViewById(R.id.btnCombineGcCombineGc);
        btnCombineGcCombineGc.setOnClickListener(this);
        spGCCountry = (Spinner) convertView.findViewById(R.id.spGCCountry);

        spGCCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                processCountrySelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void processCountrySelection(int position) {

        for (int i = 0; i < linearCombineGiftCode.getChildCount(); i++) {

            LinearLayout layout = (LinearLayout) linearCombineGiftCode.getChildAt(i);
            EditText ed = (EditText) layout.findViewById(R.id.entergiftcode_combinegiftcode);
            TextView oldText = (TextView) layout.findViewById(R.id.txtAmountGCCombineGC);
            TextView newText = (TextView) layout.findViewById(R.id.txtNewAmountGCCombineGC);

            try {
                double oldValue = Double.parseDouble(oldText.getText().toString().split(" ")[0]);


//                double newValue = oldValue * countryList.get(position).LiveRate;
                DecimalFormat df = new DecimalFormat("#.##");
                GCCountry selectedCountry = countryList.get(spGCCountry.getSelectedItemPosition());
                double newValue=0.0d;
                if(selectedCountry.CurrencyName.toString().trim().equalsIgnoreCase(responseObject.getString("LocalValueReceivedCurrancy").trim())){
                    newValue = localOldtextValue;
                } else {
                    newValue = oldValue * selectedCountry.LiveRate;
                }
                newValue = Double.valueOf(df.format(newValue));
                newText.setText("" + LanguageStringUtil.languageString(getActivity(), String.valueOf(newValue)) + " " + countryList.get(position).CurrencyName);

            } catch (Exception e) {

            }
           /*if(TextUtils.isDigitsOnly(oldText.getText().toString().split(" ")[0])){

               double oldValue = Double.parseDouble(oldText.getText().toString().split(" ")[0]);
               double newValue = oldValue * countryList.get(position).LiveRate;
               DecimalFormat df = new DecimalFormat("#.##");
               newValue = Double.valueOf(df.format(newValue));
               newText.setText(""+newValue+" "+countryList.get(position).CurrencyName);


           }
*/

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        getGCCountries();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        try {
            linearCombineGiftCode.removeAllViews();
        } catch (Exception e) {

        }


        addCombineStrip(false);
        addCombineStrip(false);

    }

    private void getGCCountries() {
        final CircleDialog d = new CircleDialog(getActivity(), 0);
        d.setCancelable(true);
        d.show();

        new CallWebService(AppConstants.GET_GC_COUNTRY, CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {

                d.dismiss();

                Type listType = new TypeToken<List<GCCountry>>() {
                }.getType();
                countryList = new GsonBuilder().create().fromJson(response, listType);

                for (int i = 0; i < countryList.size(); i++) {
                    Log.e("", countryList.get(i).CountryName + "");
                }

                gcCountryAdapter = new GCCountryAdapter(getActivity(), R.layout.spinner_country, countryList);
                spGCCountry.setAdapter(gcCountryAdapter);

            }

            @Override
            public void error(VolleyError error) {

                d.dismiss();
            }
        }.start();
    }

    private void addCombineStrip(boolean isDeleteVisible) {

        View vStrip = getActivity().getLayoutInflater().inflate(R.layout.item_combinegiftcode, null);
        vStrip.setTag(linearCombineGiftCode.getChildCount());
        TextView txtDelete = (TextView) vStrip.findViewById(R.id.btnDeleteCombineGiftCode);
        if (isDeleteVisible == false) {
            txtDelete.setVisibility(View.INVISIBLE);
        } else {
            txtDelete.setVisibility(View.VISIBLE);
        }

        txtDelete.setOnClickListener(deleteListner);
        final EditText edEnterGiftCode = (EditText) vStrip.findViewById(R.id.entergiftcode_combinegiftcode);
        edEnterGiftCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 9) {

                    LinearLayout first = (LinearLayout) edEnterGiftCode.getParent().getParent();
                    TextView ed = (TextView) first.findViewById(R.id.txtAmountGCCombineGC);
                    TextView txtNewAmountGCCombineGC = (TextView) first.findViewById(R.id.txtNewAmountGCCombineGC);
                    processFetchValue(edEnterGiftCode.getText().toString(), ed, edEnterGiftCode, txtNewAmountGCCombineGC);


                }
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearCombineGiftCode.addView(vStrip, params);
        linearCombineGiftCode.invalidate();

    }

    boolean duplicates(final ArrayList<String> arr) {
        Set<String> lump = new HashSet<String>();
        for (String i : arr) {
            if (lump.contains(i)) return true;
            lump.add(i);
        }
        return false;
    }

    private void processFetchValue(String code, final TextView index, final EditText ed, final TextView newText) {

        try {

            JSONObject generateObject = new JSONObject();
            generateObject.put("GCText", code);
            generateObject.put("SenderID", user.UserID);
            generateObject.put("Culture", LanguageStringUtil.CultureString(getActivity()));

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.GETGCDETAIL, generateObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {

                    responseObject=jobj;
                    String response = jobj.toString();

                    Log.e("Response FetchGC detail GC: ", "" + response);

                    try {

                        // todo
                        JSONObject obj = new JSONObject(response);

                        String responseCode = obj.getString("ResponseCode");

                        if (responseCode.equalsIgnoreCase("1")) {

                            index.setText(LanguageStringUtil.languageString(getActivity(),jobj.getString("LocalValueReceived") )+ " " + jobj.getString("LocalValueReceivedCurrancy"));

                            GCCountry selectedCountry = countryList.get(spGCCountry.getSelectedItemPosition());
                            double oldValue = Double.parseDouble(jobj.getString("GCAmount"));
                            double localoldvalue =  Double.parseDouble(jobj.getString("LocalValueReceived"));
                            localOldtextValue=localoldvalue;
                            double newValue=0.0d;
                            Log.e("selectedCountry.CurrencyName.toString()",selectedCountry.CurrencyName.toString()+"");
                            if(selectedCountry.CurrencyName.toString().trim().equalsIgnoreCase(jobj.getString("LocalValueReceivedCurrancy").trim())){
                                newValue = localoldvalue ;

                                Log.e("new value","same");
                            } else {
                                Log.e("new value","not same");
                                newValue = oldValue * selectedCountry.LiveRate;
                            }

                            DecimalFormat df = new DecimalFormat("#.##");
                            newValue = Double.valueOf(df.format(newValue));
                            newText.setText("" + LanguageStringUtil.languageString(getActivity(),String.valueOf(newValue)) + " " + selectedCountry.CurrencyName);


                        } else {

                            ed.setText("");
                            ed.setError(jobj.getString("ResponseMsg"));
                          /*  SnackBar bar = new SnackBar(getActivity(),jobj.getString("ResponseMsg"));
                            bar.show();*/

                        }

                    } catch (Exception e) {

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {


                    SnackBar bar = new SnackBar(getActivity(), error.getMessage());
                    bar.show();

                }
            });

            req.setRetryPolicy(
                    new DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(req);

        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAddCombineGiftCode:
                processAddCombineStrips();
                break;
            case R.id.btnCombineGcCombineGc:
                processCombine();
                break;

        }
    }

    private void processCombine() {
        if (isPassedFromValidationProcess()) {

            try {

                JSONObject jMain = new JSONObject();
                JSONArray arr = new JSONArray();
                double newLocalValue= 0.0d;

                for (int i = 0; i < linearCombineGiftCode.getChildCount(); i++) {
                    LinearLayout layout = (LinearLayout) linearCombineGiftCode.getChildAt(i);
                    EditText ed = (EditText) layout.findViewById(R.id.entergiftcode_combinegiftcode);
                    TextView newText = (TextView) layout.findViewById(R.id.txtNewAmountGCCombineGC);

                    String newvalue1= String.valueOf(newText.getText().toString().split(" ")[0]);
                    newvalue1 = newvalue1.replaceAll("\\,", ".");

                    newLocalValue = newLocalValue + Double.parseDouble(newvalue1);

                    JSONObject jobj = new JSONObject();
                    jobj.put("GiftCode", ed.getText().toString());
                    arr.put(jobj);
                }

                //todo change service and values
                jMain.put("GiftCode", arr);
                jMain.put("SenderID", user.UserID);
                DecimalFormat df = new DecimalFormat("#.##");
                newLocalValue = Double.valueOf(df.format(newLocalValue));

                String newvalue= String.valueOf(newLocalValue);
                newvalue = newvalue.replaceAll("\\,", ".");

                jMain.put("NewLocalValueReceived", newvalue + "");
                jMain.put("Culture", LanguageStringUtil.CultureString(getActivity()));

                jMain.put("NewLocalValueReceivedCurrancy", countryList.get(spGCCountry.getSelectedItemPosition()).CurrencyName + "");
                Log.e("------- jMAIN  obj", "" + jMain.toString());

                try {

                    final CircleDialog d = new CircleDialog(getActivity(), 0);
                    d.setCancelable(true);
                    d.show();

                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, AppConstants.COMBINE_GC, jMain, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jobj) {

                            try {
                                String response = jobj.toString();
                                Log.e("Response : ", "" + response);
                                if (jobj.getString("ResponseCode").equalsIgnoreCase("1")) {
                                    SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_GIFTCODECOMBINED));
                                    bar.show();
                                    clearAll();

                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = manager.beginTransaction();
                                    ft.replace(R.id.main_container, new MyAccountFragment());
                                    ft.commit();

                                } else {
                                    SnackBar bar = new SnackBar(getActivity(), jobj.getString("ResponseMsg"));
                                    bar.show();
                                }

                                d.dismiss();

                            } catch (Exception e) {

                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            d.dismiss();
                            Log.e("error responsegg: ", error + "");
                            SnackBar bar = new SnackBar(getActivity(), error.getMessage());
                            bar.show();

                        }
                    });
                    req.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));

                    MyApplication.getInstance().addToRequestQueue(req);

                } catch (Exception e) {
                    Log.e("exc1",e.toString());
                }


            } catch (Exception e) {
                Log.e("exc2",e.toString());
            }

        }
    }

    public boolean isPassedFromValidationProcess() {

        boolean isPassed = false;
        ArrayList<String> gcs = new ArrayList<String>();

        for (int i = 0; i < linearCombineGiftCode.getChildCount(); i++) {

            LinearLayout layout = (LinearLayout) linearCombineGiftCode.getChildAt(i);
            EditText ed = (EditText) layout.findViewById(R.id.entergiftcode_combinegiftcode);
            gcs.add(ed.getText().toString());

            if (ed.getText().toString().equalsIgnoreCase("")) {
                ed.setError(getString(R.string.code_ENTERGC));
                isPassed = false;
                return isPassed;

            } else if (duplicates(gcs) == true) {

                SnackBar bar = new SnackBar(getActivity(), getString(R.string.code_CANNOTCOMBINEGIFTCODE));
                bar.show();
                isPassed = false;
                return isPassed;

            } else {

                if (ed.getText().toString().length() == 9) {
                    isPassed = true;
                    continue;

                } else {
                    ed.setError(getString(R.string.code_ENTERVALIDGC));
                    isPassed = false;
                    return isPassed;
                }
            }
        }

        return isPassed;
    }

    private boolean matchPreviousGCS(String s, ArrayList<String> gcs) {

        boolean isMatch = false;

        for (String sgc : gcs) {

            if (s.equals(sgc)) {

                isMatch = true;
                return isMatch;
            }
        }

        return isMatch;
    }

    private void clearAll() {


        for (int i = 0; i < linearCombineGiftCode.getChildCount(); i++) {

            LinearLayout layout = (LinearLayout) linearCombineGiftCode.getChildAt(i);
            EditText ed = (EditText) layout.findViewById(R.id.entergiftcode_combinegiftcode);
            ed.setText("");
        }
    }

    private View.OnClickListener deleteListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LinearLayout fp = (LinearLayout) v.getParent();
            LinearLayout second = (LinearLayout) fp.getParent();
            LinearLayout first = (LinearLayout) second.getParent();
            LinearLayout third = (LinearLayout) first.getParent();
            linearCombineGiftCode.removeViewAt(linearCombineGiftCode.indexOfChild(third));
            linearCombineGiftCode.invalidate();
        }
    };

    private void processAddCombineStrips() {

        if (linearCombineGiftCode.getChildCount() == 5) {

        } else {
            addCombineStrip(true);
        }


    }

   /* public class GCCountryAdapter extends ArrayAdapter<GCCountry> {
        Context context;
        int layoutResourceId;
        ArrayList<GCCountry> values = new ArrayList<GCCountry>();

        // int android.R.Layout.
        public GCCountryAdapter(Context context, int resource, ArrayList<GCCountry> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values.clear();
            this.values.addAll(objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(getActivity());
            txt.setPadding(16, 16, 16, 16);
            txt.setGravity(Gravity.CENTER_VERTICAL);

            txt.setText(values.get(position).CountryName + "");

            return txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(getActivity());
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16, 16, 16, 16);

            txt.setText(values.get(position).CountryName + "");


            return txt;
        }
    }*/

    public class GCCountryAdapter extends ArrayAdapter<GCCountry> {
        Context context;
        int layoutResourceId;
        ArrayList<GCCountry> values;
        // int android.R.Layout.
        public GCCountryAdapter(Context context, int resource, ArrayList<GCCountry> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(getActivity());
            txt.setPadding(16,16,16,16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(" "+values.get(position).CountryName);

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 16;

            int w= (int)getResources().getDimension(R.dimen.flag_width1);
            int h= (int)getResources().getDimension(R.dimen.flag_height1);
            LinearLayout.LayoutParams params_image = new LinearLayout.LayoutParams(dpToPx(w),dpToPx(h));

            ImageView img = new ImageView(context);
            if (values.get(position).ShortCode == null || values.get(position).ShortCode.equalsIgnoreCase("") || values.get(position).ShortCode.equalsIgnoreCase("NULL")) {
            } else {
                try {
                    img.setImageBitmap(getBitmapFromAsset(values.get(position).ShortCode.toString().trim().toLowerCase()+".png"));

                } catch (Exception e) {
                    Log.e("MyTag dro down", "Failure to get drawable id.", e);
                }


            }

            layout.addView(img, params_image);
            layout.addView(txt, params);
            return  layout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(getActivity());

            txt.setPadding(16, 16, 16, 16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(" "+values.get(position).CountryName);
            LinearLayout.LayoutParams main_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout layout = new LinearLayout(context);

            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setLayoutParams(main_params);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;

            int w= (int)getResources().getDimension(R.dimen.flag_width1);
            int h= (int)getResources().getDimension(R.dimen.flag_height1);


            LinearLayout.LayoutParams params_image = new LinearLayout.LayoutParams(dpToPx(w),dpToPx(h));
            ImageView img = new ImageView(context);


            if (values.get(position).ShortCode == null || values.get(position).ShortCode.equalsIgnoreCase("") || values.get(position).ShortCode.equalsIgnoreCase("NULL")) {
            } else {
                try {
                    img.setImageBitmap(getBitmapFromAsset(values.get(position).ShortCode.toString().trim().toLowerCase()+".png"));

                } catch (Exception e) {
                    Log.e("MyTag", "Failure to get drawable id.", e);
                }


            }

            layout.addView(img, params_image);
            layout.addView(txt, params);
            return  layout;
        }
    }



    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


//    public class GCCountryAdapter extends ArrayAdapter<GCCountry> {
//        Context context;
//        int layoutResourceId;
//        ArrayList<GCCountry> values;
//
//        // int android.R.Layout.
//        public GCCountryAdapter(Context context, int resource, ArrayList<GCCountry> objects) {
//            super(context, resource, objects);
//            this.context = context;
//            this.values = objects;
//        }
//
//        @Override
//        public View getDropDownView(int position, View convertView, ViewGroup parent) {
//
//            TextView txt = new TextView(getActivity());
//            txt.setPadding(16, 16, 16, 16);
//            txt.setGravity(Gravity.CENTER_VERTICAL);
//            txt.setText(values.get(position).CountryName);
//
//            LinearLayout layout = new LinearLayout(context);
//            layout.setOrientation(LinearLayout.HORIZONTAL);
//            layout.setGravity(Gravity.CENTER_VERTICAL);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.leftMargin = 16;
//            LinearLayout.LayoutParams params_image = new LinearLayout.LayoutParams(56, 32);
//
//            ImageView img = new ImageView(context);
//            try {
//                img.setImageBitmap(getBitmapFromAsset(values.get(position).CountryName.toString().trim() + "-flag.png"));
//            } catch (Exception e) {
//
//            }
//            img.setImageBitmap(getBitmapFromAsset(values.get(position).CountryName.toString().trim() + "-flag.png"));
//
//            layout.addView(img, params_image);
//            layout.addView(txt, params);
//
//
//            return layout;
//        }
//
//
//    }


    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getActivity().getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }
}

