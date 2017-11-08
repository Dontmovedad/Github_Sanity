package com.example.tomdong.sanity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import Model.Budget;
import Model.BudgetModel;
import Model.Category;
import Model.CategoryModel;
import Model.Transaction;
import Model.TransactionModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    PieChart pieChart;
    ListView Budget_ListView;
    View myFragmentView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private TextView transDateText;
    private int transYear, transMonth, transDay;
    private Map<PieEntry, Long> pieMap = new HashMap<>();
    private FloatingActionButton scan;
    String mCurrentPhotoPath;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.activity_overview, container, false);
        // Budget_ListView = myFragmentView.findViewById(R.id.Budget_listview);
        final Calendar c = Calendar.getInstance();
        transDay = c.get(Calendar.DAY_OF_MONTH);
        transMonth = c.get(Calendar.MONTH);
        transYear = c.get(Calendar.YEAR);
        pieChart = (PieChart) myFragmentView.findViewById(R.id.overview_pie);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);
        //pieChart.getDescription().setText("Budgets OverView");

        ArrayList<PieEntry> yvalues = new ArrayList<>();
        Map<Long, Budget> budgetMap = BudgetModel.GetInstance().GetBudgetMap();
        for (Budget budget : budgetMap.values()) {
            PieEntry e = new PieEntry((float) budget.GetAmountLimit(), budget.getmName());
            yvalues.add(e);
            pieMap.put(e, budget.getmBudgetId());
        }
/*
        ArrayList<PieEntry> yvalues= new ArrayList<>();
        yvalues.add(new PieEntry(100f,"PartyA"));
        yvalues.add(new PieEntry(100f,"USA"));
        yvalues.add(new PieEntry(100f,"China"));
        yvalues.add(new PieEntry(100f,"Japan"));
        yvalues.add(new PieEntry(23f,"Russia"));
*/

        PieDataSet dataSet = new PieDataSet(yvalues, "Budgets");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);
        pieChart.animateY(1000);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                Log.i("VAL SELECTED",
//                        "Value: " + BudgetModel.GetInstance().getBudgetById( Long.valueOf(((PieEntry) e).getLabel()) ).getmName()+ ", index: " + h.getX()
//                                + ", DataSet index: " + h.getDataSetIndex());
                long bgtID = pieMap.get(e);
                //Log.d("Budget ID", Long.toString(bgtID));
                Intent i = new Intent(getContext(), BudgetViewActivity.class);
                i.putExtra("bgtID", bgtID);
                startActivity(i);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) myFragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
        scan=(FloatingActionButton)myFragmentView.findViewById(R.id.fab_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        return myFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void showInputDialog() {

        // get input_dialog.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext()).setView(promptView);

        final EditText transAmount = (EditText) promptView.findViewById(R.id.trans_amt);
        final EditText transNote = (EditText) promptView.findViewById((R.id.trans_note));
        Button transDateButton = (Button) promptView.findViewById(R.id.trans_date_button);
        transDateText = (TextView) promptView.findViewById(R.id.trans_date_text);
        transDateText.setText(transYear + "-" + (transMonth + 1) + "-" + transDay);
        transDateButton.setOnClickListener(this);
        final Spinner bgtSpinner = (Spinner) promptView.findViewById(R.id.bgt_spinner);
        final Spinner catSpinner = (Spinner) promptView.findViewById(R.id.cat_spinner);

        Map<Long, Budget> bgtMap = BudgetModel.GetInstance().GetBudgetMap();
        Map<Long, Category> catMap = CategoryModel.GetInstance().mIDToCategory;
        final Map<String, Long> bgtNameIdMap = new HashMap<>();
        final Map<String, Long> catNameIdMap = new HashMap<>();
        final ArrayList<String> bgts = new ArrayList<>();
        final ArrayList<String> cats = new ArrayList<>();

        for (Map.Entry<Long, Budget> entry : bgtMap.entrySet()) {
            Long bgtId = entry.getKey();
            Budget bgt = entry.getValue();
            bgtNameIdMap.put(bgt.getmName(), bgtId);
            bgts.add(bgt.getmName());
        }

        for (Map.Entry<Long, Category> entry : catMap.entrySet()) {
            Long catId = entry.getKey();
            Category cat = entry.getValue();
            catNameIdMap.put(cat.getmName(), catId);
        }

        ArrayAdapter<String> bgtAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, bgts);

        final ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cats);

        bgtSpinner.setAdapter(bgtAdapter);
        catSpinner.setAdapter(catAdapter);

        bgtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                cats.clear();
                Long selectedBgtID = bgtNameIdMap.get(bgtSpinner.getSelectedItem());
                List<Category> cList = BudgetModel.GetInstance().getCategoriesUnderBudget(selectedBgtID);
                for (Category c : cList) {
                    cats.add(c.getmName());
                    catAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });


        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (transAmount.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Amount cannot be empty! Please Try Again", Toast.LENGTH_SHORT).show();
                        } else {
                            TransactionModel.GetInstance().addTransaction(
                                    new Transaction(Double.parseDouble(transAmount.getText().toString()),
                                            catNameIdMap.get(catSpinner.getSelectedItem()).longValue(),
                                            transNote.getText().toString(),
                                            transYear,
                                            transMonth,
                                            transDay));
                            String trans = Double.parseDouble(transAmount.getText().toString()) + " " +
                                    catNameIdMap.get(catSpinner.getSelectedItem()).longValue() + " " +
                                    transNote.getText().toString() + " " +
                                    transYear + " " +
                                    transMonth + " " +
                                    transDay;
                            Toast.makeText(getContext(), "Add Transaction: " + trans, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                transDateText.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                transYear = year;
                transMonth = month;
                transDay = dayOfMonth;
            }
        }, transYear, transMonth, transDay);
        datePickerDialog.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;
    File photoFile=null;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

       }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Log.d("On Activity Result:**", "onActivityResult: ");
            new RestAsync().execute();
        }
    }

    public class RestAsync extends AsyncTask<String,String,String>
    {
        BufferedReader httpResponseReader;
        HttpsURLConnection urlConnection;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings){

            try{
                SSLContext sslContext = SSLContexts.createSystemDefault();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslContext,
                        SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
                CloseableHttpClient  httpclient= HttpClientBuilder.create()
                        .setSSLSocketFactory(sslsf)
                        .build();
                HttpPost httpPost = new HttpPost("https://api.taggun.io/api/receipt/v1/simple/encoded");
                String encodedFile=encodeFileToBase64Binary(photoFile);
                Log.d("My File to 64 encoded", encodedFile);
                String jsonString = new JSONObject()
                       // .put("image","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")
                        .put("image",encodedFile)
                        .put("filename", "example.jpg")
                        .put("contentType", "image/jpeg")
                        .put("refresh",true)
                        .put("incognito",false)
                        .put("ipAddress","219.88.232.1")
                        .put("language","en").toString();
                StringEntity entity = new StringEntity(jsonString);
                httpPost.setEntity(entity);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("apikey","c0080e90c34611e7a0ebfdc7a5da208a");

                CloseableHttpResponse response = httpclient.execute(httpPost);
                if(response.getStatusLine().getStatusCode()==200)
                {
                    Log.d("I fucking did it", "doInBackground: ");
                    InputStream ips=response.getEntity().getContent();
                    BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String s;
                    while(true )
                    {
                        s = buf.readLine();
                        if(s==null || s.length()==0)
                            break;
                        sb.append(s);

                    }
                    buf.close();
                    ips.close();
                   String result= sb.toString();
                    Log.d("The content is: ", result);
                }
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private String encodeFileToBase64Binary(File file)
            throws IOException {

        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);
        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }


}
