package com.k.sorukaydedici;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriActivity extends AppCompatActivity implements SoruListener {
    public static final int REQUEST_CODE=0;
    private List<SoruModel> favModelList;
    private SoruAdapter soruAdapter;
    private RecyclerView recyclerView;
    private SoruModel soruModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favori);
        recyclerView=findViewById(R.id.favRecycle);
        loadData();
        getSorular(favModelList,soruModel,0);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("favori",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favModelList);
        editor.putString("fav list",json);
        editor.apply();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("favori",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("fav list",null);
        Type type= new TypeToken<ArrayList<SoruModel>>(){}.getType();
        favModelList=gson.fromJson(json,type);
        if (favModelList==null){
            favModelList=new ArrayList<>();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null){
                    soruModel = new SoruModel(data.getStringExtra("baslik"),data.getStringExtra("soru"),data.getStringExtra("cevap"));
                    getSorular(favModelList,soruModel,data.getIntExtra("position",0));
                }

            }
        }
    }
    private void getSorular(List<SoruModel> favModelList, SoruModel soruModel, int position) {

            soruAdapter = new SoruAdapter(favModelList,this,FavoriActivity.this);
            if(soruModel!=null){
                favModelList.get(position).setBaslik(soruModel.getBaslik());
                favModelList.get(position).setSoru(soruModel.getSoru());
                favModelList.get(position).setCevap(soruModel.getCevap());
                soruAdapter.notifyItemChanged(position);
            }
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(soruAdapter);



    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onSoruListener(SoruModel soruModel, int position) {
        Intent intent = new Intent(getApplicationContext(),SoruActivity.class);
        intent.putExtra("baslik",soruModel.getBaslik());
        intent.putExtra("soru",soruModel.getSoru());
        intent.putExtra("cevap",soruModel.getCevap());
        intent.putExtra("position",position);
        intent.putExtra("durum",true);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
