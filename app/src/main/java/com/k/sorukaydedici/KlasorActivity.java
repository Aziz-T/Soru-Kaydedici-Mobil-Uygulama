package com.k.sorukaydedici;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class KlasorActivity extends AppCompatActivity implements KlasorListener {
    private List<KlasorModel> klasorModelList;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klasor);
        loadDataKlasor();

        recyclerView= findViewById(R.id.recycle);

        DialogAdapter dialogAdapter = new DialogAdapter(klasorModelList,this,"asil",this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dialogAdapter);



    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDataKlasor();
    }

    private void saveDataKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("klasor",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(klasorModelList);
        editor.putString("klasor list",json);
        editor.apply();
    }
    private void loadDataKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("klasor",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("klasor list",null);
        Type type= new TypeToken<ArrayList<KlasorModel>>(){}.getType();
        klasorModelList=gson.fromJson(json,type);
        if (klasorModelList==null){
            klasorModelList=new ArrayList<>();
        }
    }

    @Override
    public void onKlasorListener(KlasorModel klasorModel, int position) {
        Intent intent = new Intent(KlasorActivity.this,SorularActivity.class);
        intent.putExtra("ata",klasorModel.getIsim());
        startActivity(intent);
    }
}
