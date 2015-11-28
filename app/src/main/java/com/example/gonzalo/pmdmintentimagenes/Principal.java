package com.example.gonzalo.pmdmintentimagenes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Principal extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private String name="";
    private Button btCargarImagen;
    private android.widget.ImageView imageView;
    private android.widget.RelativeLayout relativeLayout;
    private Button btGirar;
    private Button btGuardarImagen;
    private android.widget.EditText etGuardarImagen;
    private Button btCambiarColor;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        this.btCambiarColor = (Button) findViewById(R.id.btCambiarColor);
        this.etGuardarImagen = (EditText) findViewById(R.id.etGuardarImagen);
        this.btGuardarImagen = (Button) findViewById(R.id.btGuardarImagen);
        this.btGirar = (Button) findViewById(R.id.btGirar);
        this.relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.btCargarImagen = (Button) findViewById(R.id.btCargarImagen);
        init();
    }

    public void init(){
        if(getIntent().getData()!=null){
            Uri selectedImage = getIntent().getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {

            }
        }
    }


    public void cargarImagen (View v){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                try {
                    InputStream is;
                    is = getContentResolver().openInputStream(selectedImage);
                    if (is != null) {
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bitmap = BitmapFactory.decodeStream(bis);
                        imageView.setImageBitmap(bitmap);
                    }

                } catch (FileNotFoundException e) {
                }
            }
        }
    }

    public void girar(View v){
        BitmapDrawable bmpDraw = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();

        imageView.setImageBitmap(Principal.rotarBitmap(bitmap, 90));
    }
    public static Bitmap rotarBitmap(Bitmap bmpOriginal, float angulo) {
        Matrix matriz = new Matrix();
        matriz.postRotate(angulo);
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matriz, true);
    }


    public void cambiarColor(View v){
        BitmapDrawable bmpDraw = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = bmpDraw.getBitmap();

        imageView.setImageBitmap(Principal.toEscalaDeGris(bitmap));
    }
    public static Bitmap toEscalaDeGris(Bitmap bmpOriginal) {
        Bitmap bmpGris = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas lienzo = new Canvas(bmpGris);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(cmcf);
        lienzo.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGris;
    }


    public static int factorDeEscalado (int anchoBmp, int altoBmp, int anchoIv, int altoIv) {
        int factor = 1;
        if (altoBmp > altoIv || anchoBmp > anchoIv) {
            int alto = altoBmp / 2;
            int ancho = anchoBmp / 2;
            while ((alto / factor) > altoIv && (ancho / factor) > anchoIv) {
                factor = factor * 2;
            }
        }
        return factor;
    }


    public void guardar(View v){

        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        String nombre= etGuardarImagen.getText().toString()+".jpeg";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), nombre);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this, "La foto se ha guardado correctamente",Toast.LENGTH_SHORT).show();
            etGuardarImagen=(EditText)findViewById(R.id.etGuardarImagen);
            etGuardarImagen.setHint("Nombre");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
