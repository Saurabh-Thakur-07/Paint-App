package com.example.paintapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paintapp.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    int defaultColor ;
    private static String fileName ;
    SignatureView signatureView ;
    ImageButton eraserBtn , colorBtn , saveBtn ;
    TextView txtPenSize ;
    SeekBar seekBar ;
    String externalStorage = System.getenv("EXTERNAL_STORAGE");
    File path = new File(externalStorage);
    File origPath = new File(path.getAbsolutePath()+"/myPaintings");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = findViewById(R.id.penSize);
        signatureView = findViewById(R.id.signature_view);
        eraserBtn = findViewById(R.id.btnEraser);
        colorBtn = findViewById(R.id.btnColor);
        saveBtn = findViewById(R.id.btnSave);
        txtPenSize = findViewById(R.id.txtPenSize);
        askPermissions();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = simpleDateFormat.format(new Date());
        fileName = path + "/"+date +".png";
        if(!path.exists())
        {
            path.mkdirs();
        }

        defaultColor = ContextCompat.getColor(MainActivity.this,R.color.black);
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });
        eraserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearCanvas();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtPenSize.setText(i+"dp");
                signatureView.setPenSize(i);
                seekBar.setMax(80);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!signatureView.isBitmapEmpty())
                {
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,"Couldn't Save File",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void saveImage() throws IOException {
        File file = new File(fileName);
        Bitmap bitmap = signatureView.getSignatureBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        Toast.makeText(MainActivity.this,"File Created",Toast.LENGTH_SHORT).show();
    }
    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color ;
                signatureView.setPenColor(defaultColor);
            }
        });
        ambilWarnaDialog.show();
    }
    private void askPermissions() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this,"Granted",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}