package com.k.sorukaydedici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SorularActivity extends AppCompatActivity implements SoruListener{
    public static final int REQUEST_CODE=0;
    private List<SoruModel> soruModelList;
    private RecyclerView recyclerView;
    private String ata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorular);
        ata= getIntent().getStringExtra("ata");
        recyclerView=findViewById(R.id.sorularRec);

        loadDataSoruKlasor();
        SoruAdapter soruAdapter = new SoruAdapter(soruModelList,(SorularActivity) this,this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(soruAdapter);


    }
    private void saveDataSoruKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("soruklasor",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(soruModelList);
        editor.putString(ata,json);
        editor.apply();
    }
    private void loadDataSoruKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("soruklasor",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ata,null);
        Type type= new TypeToken<ArrayList<SoruModel>>(){}.getType();
        soruModelList=gson.fromJson(json,type);
        if (soruModelList==null){
            soruModelList=new ArrayList<>();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDataSoruKlasor();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.testYap){
            if(soruModelList.size()>4){
                Intent intent = new Intent(SorularActivity.this,TestActivity.class);
                intent.putExtra("ata",ata);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Soru sayısı en az 5 olmalıdır", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSoruListener(SoruModel soruModel, int position) {
        Intent intent = new Intent(getApplicationContext(),SoruActivity.class);
        intent.putExtra("baslik",soruModel.getBaslik());
        intent.putExtra("soru",soruModel.getSoru());
        intent.putExtra("cevap",soruModel.getCevap());
        intent.putExtra("position",position);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
