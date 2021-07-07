package com.k.sorukaydedici;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EkleActivity extends AppCompatActivity {

    private EditText editbaslik,editSoru,editCevap;
    private Button kaydet;
    private ImageView imageView;
    private List<SoruModel> soruModelList;
    private FloatingActionButton ekleButton2;

    private static  final int CAMERA_REQUEST_CODE=200;
    private static  final int STORAGE_REQUEST_CODE=400;
    private static  final int IMAGE_PICK_GALLERY_CODE=1000;
    private static  final int IMAGE_PICK_CAMERA_CODE=1001;

    String cameraPermission[];
    String storagePermission[];

    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekle);
        editbaslik=findViewById(R.id.editBaslik);
        editSoru=findViewById(R.id.editSoru);
        editCevap=findViewById(R.id.editCevap);
        kaydet=findViewById(R.id.kaydetButon);
        imageView=findViewById(R.id.imageView);
        ekleButton2=findViewById(R.id.ekleButon2);

        soruModelList=new ArrayList<>();

        loadData();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE };
        showImageImportDialog();


        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soruModelList.add(new SoruModel(editbaslik.getText().toString(),editSoru.getText().toString(),editCevap.getText().toString()));
                Intent intent = new Intent(EkleActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ekleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageImportDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        areYouSure();
    }

    private void areYouSure() {
        String[] items = {"Evet","Hayır"};
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);


        dialog.setTitle("İptal etmek istediğinize emin misiniz?");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                   Intent intent = new Intent(EkleActivity.this,MainActivity.class);
                   startActivity(intent);
                   finish();

                }
                if(i==1){

                }
            }
        });
        dialog.create().show();
    }

    private void showImageImportDialog() {
        String[] items = {"Kamera","Galeri","El İle Yaz"};
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Resim Seç");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();

                    }else {
                        pickCamera();
                    }
                }
                if(i==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickGallery();
                    }
                } if(i==0){

                }
            }
        });
        dialog.create().show();
    }
    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private boolean checkStoragePermission() {
        boolean result=  ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }
    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");
        imageUri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_PICK_GALLERY_CODE);
    }
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1=  ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }else {
                        Toast.makeText(this, "İstek Reddedildi!", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if( writeStorageAccepted){
                        pickGallery();
                    }else {
                        Toast.makeText(this, "İstek Reddedildi!", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                if(data!=null)
                    CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
                else
                    CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE ) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
                Toast.makeText(this, "data Null IMAGE_PICK_CAMERA_CODE", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Hata", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    editSoru.setText(sb.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
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
}
