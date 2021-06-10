package br.edu.appcontatos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btnFoto;
    Button btnVideo;
    Button btnSalvar;
    ImageView fotografia;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() throws IOException {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(createImageFile().getAbsolutePath().toString());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFoto = findViewById(R.id.btnFoto);
        btnVideo = findViewById(R.id.btnVideo);
        fotografia = findViewById(R.id.fotografia);

        btnFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(videoIntent, 9);
                //dispatchTakePictureIntent();
                try {
                    galleryAddPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoIntent, 0);
            }
        });

    }

    //Recuperar o retordo do startActivityForResult()

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int codigo, int resultado, Intent it) {
        super.onActivityResult(codigo, resultado, it);
        if(codigo == 13 && resultado == Activity.RESULT_OK){
            Uri uri = it.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null);
            while(cursor.moveToNext()){
                String nome = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Toast.makeText(this, "Contato selecionado : \n" + nome, Toast.LENGTH_SHORT).show();
            }
        }
        if(codigo == 9 && resultado == Activity.RESULT_OK){
            Bundle extras = it.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            fotografia.setImageBitmap(image);
        }
    }

    //Esse método esta vinculado com a propriedade onClick
    public void agenda(View v){
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        Intent it = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(it, 13);
    }


}