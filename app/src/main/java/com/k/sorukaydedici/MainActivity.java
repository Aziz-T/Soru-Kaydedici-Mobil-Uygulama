package com.k.sorukaydedici;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SoruListener, KlasorListener  {

    public static final int REQUEST_CODE=0;

    private RecyclerView recyclerView;
    private FloatingActionButton ekleButton;
    private List<SoruModel> soruModelList;
    private SoruModel soruModel;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private List<KlasorModel> klasorModelList;
    private List<SoruModel> soruModelListForKlasor;
    private BottomSheetDialog dialog;

    private String ata;

    private TextView sonEk, hicYok;

    SoruAdapter soruAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.soruRecycle);
        ekleButton=findViewById(R.id.ekleButon);
        drawerLayout=findViewById(R.id.drawer);
        sonEk=findViewById(R.id.sonEk);
        hicYok=findViewById(R.id.hicYok);
        drawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.Ac,R.string.Kapa);
        drawerToggle.setDrawerIndicatorEnabled(true);


        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        loadData();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sonEk.setAlpha(0f);

        if(soruModelList.size()>0){
            sonEk.setAlpha(1f);
            hicYok.setAlpha(0f);
        }
        getSorular(soruModelList,soruModel,0);






        ekleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EkleActivity.class);
                startActivity(intent);
                finish();


            }
        });

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id== R.id.favoriler){
                    Intent intent = new Intent(MainActivity.this,FavoriActivity.class);
                    startActivity(intent);
                }if(id== R.id.kategoriler){
                    Intent intent = new Intent(MainActivity.this,KlasorActivity.class);
                    startActivity(intent);
                }if(id== R.id.nasil){
                    Intent intent = new Intent(MainActivity.this,NasilActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });




    }



    private void getSorular(List<SoruModel> soruModelList, SoruModel soruModel, int position) {
        soruAdapter = new SoruAdapter(soruModelList,this,MainActivity.this);
        if(soruModel!=null){
            soruModelList.get(position).setBaslik(soruModel.getBaslik());
            soruModelList.get(position).setSoru(soruModel.getSoru());
            soruModelList.get(position).setCevap(soruModel.getCevap());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null){
                    soruModel = new SoruModel(data.getStringExtra("baslik"),data.getStringExtra("soru"),data.getStringExtra("cevap"));
                    getSorular(soruModelList,soruModel,data.getIntExtra("position",0));
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.testYap){
            loadDataKlasor();
            testDialogOlustur();




        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void testDialogOlustur() {
       dialog = new BottomSheetDialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_item);
        dialog.setCancelable(true);

        DialogAdapter adapter = new DialogAdapter(klasorModelList,this,"dialog",MainActivity.this);
        TextView olustur = (TextView) dialog.findViewById(R.id.yeniOlustur);
        RecyclerView rec = (RecyclerView) dialog.findViewById(R.id.klasorRec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rec.setLayoutManager(linearLayoutManager);
        rec.setAdapter(adapter);
        if(klasorModelList.size()>0){
            olustur.setText("Test Yapılacak Klasoru Seçin");
            dialog.show();
        }else {
            Toast.makeText(this, "Önce Klasor Ekleyin!", Toast.LENGTH_SHORT).show();
        }



    }


    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("soru",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(soruModelList);
        editor.putString("soru list",json);
        editor.apply();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("soru",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("soru list",null);
        Type type= new TypeToken<ArrayList<SoruModel>>(){}.getType();
        soruModelList=gson.fromJson(json,type);
        if (soruModelList==null){
            soruModelList=new ArrayList<>();
        }
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
            ata = klasorModel.getIsim();
            loadDataSoruKlasor();
            if(soruModelListForKlasor.size()>4){
                dialog.cancel();
                Intent intent = new Intent(MainActivity.this,TestActivity.class);
                intent.putExtra("ata",ata);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Klasordeki Soru Sayisi En Az 5 Olmalıdır.", Toast.LENGTH_SHORT).show();
            }




    }
    private void saveDataSoruKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("soruklasor",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(soruModelListForKlasor);
        editor.putString(ata,json);
        editor.apply();
    }
    private void loadDataSoruKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("soruklasor",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ata,null);
        Type type= new TypeToken<ArrayList<SoruModel>>(){}.getType();
        soruModelListForKlasor=gson.fromJson(json,type);
        if (soruModelListForKlasor==null){
            soruModelListForKlasor=new ArrayList<>();
        }
    }
}
