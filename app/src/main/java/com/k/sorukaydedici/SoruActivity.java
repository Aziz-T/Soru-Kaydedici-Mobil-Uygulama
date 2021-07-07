package com.k.sorukaydedici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SoruActivity extends AppCompatActivity implements KlasorListener {

    private EditText editbaslik,editSoru,editCevap;
    private ImageButton imageFav;
    private TextView favtxt;

    private List<SoruModel> soruModelList;
    private List<SoruModel> soruModelFav;
    private List<KlasorModel> klasorModelList;
    private List<SoruModel> soruModelKlasor;

    private SoruModel soruModel;


    private String baslik, soru, cevap;
    private int position;
    private int durum;
    private String ata;
    private boolean drm;

    private BottomSheetDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soru);
        editbaslik=findViewById(R.id.editBaslik1);
        editSoru=findViewById(R.id.editSoru1);
        editCevap=findViewById(R.id.editCevap1);
        imageFav=findViewById(R.id.imageFav);
        favtxt=findViewById(R.id.favTxt);
        loadData();
        loadDataKlasor();


       baslik= getIntent().getStringExtra("baslik");
       soru= getIntent().getStringExtra("soru");
       cevap= getIntent().getStringExtra("cevap");
       position=getIntent().getIntExtra("position",0);
       drm=getIntent().getBooleanExtra("durum",false);

        if(drm){
            imageFav.setEnabled(false);
            imageFav.setAlpha(0f);
            favtxt.setEnabled(false);
            favtxt.setAlpha(0f);
        }

       editbaslik.setText(baslik);
       editSoru.setText(soru);
       editCevap.setText(cevap);
       soruModel = new SoruModel(baslik,soru,cevap);
        butonKontrol();
       imageFav.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               favori();
           }
       });


    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        saveData1();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("baslik", editbaslik.getText().toString());
        intent.putExtra("soru", editSoru.getText().toString());
        intent.putExtra("cevap", editCevap.getText().toString());
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.duzenle_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.silMenu){

        }if (item.getItemId()==R.id.kategori){
            dialogAc();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogAc() {
        dialogOlustur();
    }

    private void dialogOlustur() {
        dialog1 = new BottomSheetDialog(this);


        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(true);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog1.setContentView(R.layout.dialog_item);

        DialogAdapter adapter = new DialogAdapter(klasorModelList, (KlasorListener) this,"dialog",this);
        TextView olustur = (TextView) dialog1.findViewById(R.id.yeniOlustur);
        olustur.setText("Klasor Oluştur +");
        RecyclerView rec = (RecyclerView) dialog1.findViewById(R.id.klasorRec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rec.setLayoutManager(linearLayoutManager);
        rec.setAdapter(adapter);


        olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.cancel();
                ekleDialog();
            }
        });

        dialog1.show();
    }

    private void ekleDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        //final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.ekle_dialog_item);
        final EditText olustur = (EditText) dialog.findViewById(R.id.olusturEdit);
        Button cancel = (Button) dialog.findViewById(R.id.iptalButon);
        Button tamam = (Button) dialog.findViewById(R.id.ok_button);
        tamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BURAYA TAMAMA BASINCA KLASORE SORU MODEL EKLENECEK YAPILACAK
                klasorModelList.add(new KlasorModel(olustur.getText().toString()));
                ata=olustur.getText().toString();
                loadDataSoruKlasor();
                soruModelKlasor.add(soruModel);
                Toast.makeText(SoruActivity.this, "Soru "+ata+" klasörüne eklendi.", Toast.LENGTH_SHORT).show();
                saveDataSoruKlasor();
                saveDataKlasor();
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void favori() {
        loadData1();
        if(kontrol()){
            Toast.makeText(this, "Favorilerden kaldırılıdı.", Toast.LENGTH_SHORT).show();
            imageFav.setBackground(ContextCompat.getDrawable(this,R.drawable.fav_icon_bos));
            soruModelFav.remove(durum);
            saveData1();
        } else {
            Toast.makeText(this, "Favorilere eklendi.", Toast.LENGTH_SHORT).show();
            imageFav.setBackground(ContextCompat.getDrawable(this,R.drawable.fav_icon_dolu));
            soruModelFav.add(soruModel);
            saveData1();

        }
    }

    private void butonKontrol(){
        loadData1();
        if(kontrol()){
            imageFav.setBackground(ContextCompat.getDrawable(this,R.drawable.fav_icon_dolu));
        } else {

            imageFav.setBackground(ContextCompat.getDrawable(this,R.drawable.fav_icon_bos));
        }
    }

    private boolean kontrol() {
        int c;
        if(soruModelFav!=null){
            for(c=0 ; c<soruModelFav.size() ; c++ ){
                if(soruModelFav.get(c).getBaslik().equals(soruModel.getBaslik())
                        &&soruModelFav.get(c).getSoru().equals(soruModel.getSoru())
                                &&soruModelFav.get(c).getCevap().equals(soruModel.getCevap())){
                    durum=c;
                    return true;
                }
            }
        }

        return false;
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
    private void saveData1() {
        SharedPreferences sharedPreferences = getSharedPreferences("favori",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(soruModelFav);
        editor.putString("fav list",json);
        editor.apply();
    }
    private void loadData1() {
        SharedPreferences sharedPreferences = getSharedPreferences("favori",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("fav list",null);
        Type type= new TypeToken<ArrayList<SoruModel>>(){}.getType();
        soruModelFav=gson.fromJson(json,type);
        if (soruModelFav==null){
            soruModelFav=new ArrayList<>();
        }
    }


    @Override
    public void onKlasorListener(KlasorModel klasorModel, int position) {

        ata= klasorModel.getIsim();
        loadDataSoruKlasor();
        Toast.makeText(this,"Soru "+ ata +" klasörüne eklendi.", Toast.LENGTH_SHORT).show();
        soruModelKlasor.add(soruModel);
        saveDataSoruKlasor();

        dialog1.cancel();

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
    private void saveDataSoruKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("soruklasor",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(soruModelKlasor);
        editor.putString(ata,json);
        editor.apply();
    }
    private void loadDataSoruKlasor() {
        SharedPreferences sharedPreferences = getSharedPreferences("soruklasor",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ata,null);
        Type type= new TypeToken<ArrayList<SoruModel>>(){}.getType();
        soruModelKlasor=gson.fromJson(json,type);
        if (soruModelKlasor==null){
            soruModelKlasor=new ArrayList<>();
        }
    }
}
