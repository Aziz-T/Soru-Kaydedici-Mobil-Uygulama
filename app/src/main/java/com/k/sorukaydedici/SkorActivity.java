package com.k.sorukaydedici;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SkorActivity extends AppCompatActivity {

    private Button bitir;
    private int doru,sayi;
    private TextView soruSayi, doruSoru, yanlisSoru;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skor);
        getSupportActionBar().hide();
        bitir=findViewById(R.id.bitir);
        soruSayi=findViewById(R.id.soruSayisi);
        doruSoru=findViewById(R.id.doruSayisi);
        yanlisSoru=findViewById(R.id.yanlisSayisi);

        doru=getIntent().getIntExtra("doru",0);
        sayi = getIntent().getIntExtra("sayi",0);

        soruSayi.setText(String.valueOf(sayi));
        doruSoru.setText(String.valueOf(doru));
        yanlisSoru.setText(String.valueOf(sayi-doru));



        bitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
