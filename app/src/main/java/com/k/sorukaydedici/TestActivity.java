package com.k.sorukaydedici;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class TestActivity extends AppCompatActivity {
    private List<SoruModel> soruModelList;
    private String ata;
    private Button buton1, buton2, buton3, buton4, nextButon;
    private TextView textView;
    private LinearLayout linearLayout;
    private int position = 0;
    private int doru=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ata = getIntent().getStringExtra("ata");
        loadDataSoruKlasor();

        textView = findViewById(R.id.soruText);
        buton1 = findViewById(R.id.buton1);
        buton2 = findViewById(R.id.buton2);
        buton3 = findViewById(R.id.buton3);
        buton4 = findViewById(R.id.buton4);
        nextButon = findViewById(R.id.ileriButon);
        linearLayout = findViewById(R.id.lineer);

        getKelime(position);
        calistir();

        nextButon.setEnabled(false);
        nextButon.setAlpha(0.7f);


        nextButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;
                if (position == soruModelList.size()) {
                    Intent intent = new Intent(TestActivity.this, SkorActivity.class);
                    intent.putExtra("doru",doru);
                    intent.putExtra("sayi",soruModelList.size());
                    startActivity(intent);
                    finish();
                    //BURADA KALDIK EN SON
                } else {
                    getKelime(position);
                    butondegistir(false);
                }

            }
        });


    }

    private void butondegistir(Boolean durum) {

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (durum) {
                linearLayout.getChildAt(i).setEnabled(false);
                nextButon.setAlpha(1);
                nextButon.setEnabled(true);
            } else {
                linearLayout.getChildAt(i).setEnabled(true);
                linearLayout.getChildAt(i).setBackground(ContextCompat.getDrawable(this, R.drawable.buton_back_iki));
                nextButon.setAlpha(0.7f);
                nextButon.setEnabled(false);
            }
        }


    }

    private void calistir() {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            linearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((Button) view).getText().toString().equals(soruModelList.get(position).getCevap())) {
                        ((Button) view).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buton_back_doru));
                        butondegistir(true);
                        doru++;
                    } else {
                        ((Button) view).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buton_back_yanlis));
                        butondegistir(true);
                    }
                }
            });
        }
    }


    private void getKelime(int position) {

        textView.setText(soruModelList.get(position).getSoru());
        int cevap, durum, c = 0, i = 0;
        Random rnd = new Random();
        cevap=rnd.nextInt(4);

        while (i < 4) {
            Random random = new Random();
            durum = random.nextInt(soruModelList.size());
            if (durum != c) {
                ((Button) linearLayout.getChildAt(i)).setText(soruModelList.get(durum).getCevap());
               // Toast.makeText(this, soruModelList.get(durum).getCevap(), Toast.LENGTH_SHORT).show();
                i++;
            }
            c = durum;

        }
        ((Button)linearLayout.getChildAt(cevap)).setText(soruModelList.get(position).getCevap());

        }


        private void saveDataSoruKlasor () {
            SharedPreferences sharedPreferences = getSharedPreferences("soruklasor", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(soruModelList);
            editor.putString(ata, json);
            editor.apply();
        }
        private void loadDataSoruKlasor () {
            SharedPreferences sharedPreferences = getSharedPreferences("soruklasor", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(ata, null);
            Type type = new TypeToken<ArrayList<SoruModel>>() {
            }.getType();
            soruModelList = gson.fromJson(json, type);
            if (soruModelList == null) {
                soruModelList = new ArrayList<>();
            }
        }
    }
