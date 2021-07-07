package com.k.sorukaydedici;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NasilActivity extends AppCompatActivity {

    private TextView txt1,txt2,txt3,txt4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nasil);
        getSupportActionBar().setTitle("Nasıl Kullanılır?");

        txt1 = findViewById(R.id.aciklama1);
        txt2 = findViewById(R.id.aciklama2);
        txt3 = findViewById(R.id.aciklama3);
        txt4 = findViewById(R.id.aciklama4);


        txt1.setText("Soru Ekleme");
        txt2.setText("   Soru eklemek için üzerinde göz ikonu olan butona basınız. Soru ekleme ekranı açıldığında 3 seneçek karşınıza çıkacaktır."+ "\n" + "\n" +
                "Kamera:" + "\n" +"   Kameraya bastığınızda eklemek istediğiniz sorunun fotoğrafını çekmeniz istenir. Soruyu mümkün olduğu kadar net şekilde çekmeniz gerekmektedir. Fotoğraf çekildikten sonra fotoğrafı sadece soruyu içeri alacak şekilde kırpınız."+
                "Kırpma işleminden sonra fotoğrafını çektiğiniz soru fotoğraftan yapay zeka ile okunup soru kısmına yazılacaktır." + "\n" + "\n" +"Galeri: "+ "\n" +
                "   Galeri işleminde eklemek istediğiniz soruyu galeriden seçmeniz istenmektedir."+"Eklemek istediğiniz soruyu galerinizden seçtikten sonra soruyu içeri alacak şekilde mümkün olduğu kadar okunaklı bir şekilde kırpınız."+
                "Soru seçilen fotoğraftan yapay zeka ile okunup soru kısmına yazılacaktır.");

        txt3.setText("Test Yapma");
        txt4.setText("  Test yapma işlemi için öncelikle klasör oluştumanız gerekmektedir. Klasör oluşturduktan sonra tek bir klasör içine en az 5 soru eklemeniz gerekmektedir." +
                "Klasör oluşturup oluşturulan klasöre 5 soru eklediğinizde o sorulardan test olabilirsiniz.");





    }
}