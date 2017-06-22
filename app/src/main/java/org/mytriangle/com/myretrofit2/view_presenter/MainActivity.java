/*
 * Created by Shaon on 8/14/16 10:28 PM
 * Copyright (c) 2016. This is free to use in any software.
 * You must provide developer name on your project.
 */

package org.mytriangle.com.myretrofit2.view_presenter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mytriangle.com.myretrofit2.R;
import org.mytriangle.com.myretrofit2.model.MyModel;
import org.mytriangle.com.myretrofit2.model.People;
import org.mytriangle.com.myretrofit2.service.APIService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    EditText editName;
    TextView textDetails;
    Button btnGetData, btnInsertData;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textDetails = (TextView) findViewById(R.id.textDetails);
        btnGetData = (Button) findViewById(R.id.btnGetData);
        btnInsertData = (Button) findViewById(R.id.btnInsert);
        editName = (EditText) findViewById(R.id.editName);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPeopleDetails();
            }
        });

        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPeopleDetails();
            }
        });

    }

    private void getPeopleDetails() {

        showpDialog();
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    //http://192.168.1.32/ALJAM/POST.php
                    .baseUrl("http://192.168.1.32/").
                            addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService service = retrofit.create(APIService.class);

            Call<List<MyModel>> call = service.getPeopleDetails();

            call.enqueue(new Callback<List<MyModel>>() {
                @Override
                public void onResponse(Response<List<MyModel>> response, Retrofit retrofit) {
                    List<MyModel> peopleData = response.body();
                    String details = "";
                    for (int i = 0; i < peopleData.size(); i++) {
                        String id = peopleData.get(i).getMat();
                        String name = peopleData.get(i).getA();

                        details += "ID: " + id + "\n" +
                                "Name: " + name + "\n\n";


                    }
                    Toast.makeText(MainActivity.this, details, Toast.LENGTH_SHORT).show();
                    textDetails.setText(details);
                    hidepDialog();
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("onFailure", t.toString());
                    hidepDialog();
                }
            });
        } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
            hidepDialog();
        }
    }

    private void setPeopleDetails() {
        showpDialog();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.32/").
                        addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        People people = new People();

        people.setName(editName.getText().toString());

        Call<People> peopleCall = service.setPeopleDetails(people.getName());

        peopleCall.enqueue(new Callback<People>() {
            @Override
            public void onResponse(Response<People> response, Retrofit retrofit) {



                hidepDialog();
                Log.e("onResponse", "" + response.code() +
                        "  response body "  + response.body() +
                " responseError " + response.errorBody() + " responseMessage " +
                        response.message());
            }

            @Override
            public void onFailure(Throwable t) {
                hidepDialog();
                Log.d("onFailure", t.toString());
            }
        });

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
