package e.hp.uploadimage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.util.ULocale;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static e.hp.uploadimage.Constants.UPLOAD_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



       // public static final String UPLOAD_URL = "http://192.168.43.11/TEST100/upload.php";
        //public static final String UPLOAD_KEY = "image";
    private static final int STORAGE_PERMISSION_CODE = 123;


    private int PICK_IMAGE_REQUEST = 1;


        private Button buttonChoose;
        private Button buttonUpload;
        private Button buttonView;
        private EditText  editTextname,editTextAdminID,HSCategory,
                HSlocation,HSRent,HSDescription;


    private ImageView imageView;

        private Bitmap bitmap;

        private Uri filePath;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            requestStoragePermission();

            buttonChoose = findViewById(R.id.buttonChoose);
            buttonUpload = findViewById(R.id.buttonUpload);
            buttonView = findViewById(R.id.buttonViewImage);

            imageView = findViewById(R.id.imageView);
            editTextname = findViewById(R.id.editTextName);
            editTextAdminID= findViewById(R.id.editTextAdminID);
            HSCategory=findViewById(R.id.editTextHS_Category);
            HSlocation=findViewById(R.id.editTextHS_location);
            HSRent=findViewById(R.id.editTextHS_Rent);
            HSDescription=findViewById(R.id.editTextHS_Description);




            buttonChoose.setOnClickListener(this);
            buttonUpload.setOnClickListener(this);
            buttonView.setOnClickListener(this);
        }

       /* public void AddHomeStay() {

            String Admin_ID = editTextAdminID.getText().toString().trim();
            String HS_Category = HSCategory.getText().toString().trim();
            String HS_location = HSlocation.getText().toString().trim();
            String HS_Rent = HSRent.getText().toString().trim();
            String HS_Description = HSDescription.getText().toString().trim();

            //noinspection deprecation
            @SuppressLint("StaticFieldLeak")
            class Product extends AsyncTask<Void, Void, String> {

                private ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    //this method will be running on UI thread
                    pdLoading.setMessage("\tLoading...");
                    pdLoading.setCancelable(false);
                    pdLoading.show();
                }

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("Admin_ID", Admin_ID);
                    params.put("HS_Category", HS_Category);
                    params.put("HS_location", HS_location);
                    params.put("HS_Rent", HS_Rent);
                    params.put("HS_Description", HS_Description);

                    //returing the response
                    return requestHandler.sendPostRequest(UPLOAD_URL, params);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    pdLoading.dismiss();


                    try {
                        //converting response to json object

                        JSONObject obj;
                        obj = new JSONObject(s);
                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddHomeStay.this, "Exception: " + e, Toast.LENGTH_LONG).show();
                    }
                }

            }


                Product prod_exec = new Product();
                prod_exec.execute();

            }*/



    public void uploadMultipart() {
        //getting name for the image
        String name = editTextname.getText().toString().trim();
        String Admin_ID = editTextAdminID.getText().toString().trim();
        String HS_Category = HSCategory.getText().toString().trim();
        String HS_location = HSlocation.getText().toString().trim();
        String HS_Rent = HSRent.getText().toString().trim();
        String HS_Description = HSDescription.getText().toString().trim();





        //getting the actual path of the image
        String path = getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
           MultipartUploadRequest request = new MultipartUploadRequest(this, uploadId, UPLOAD_URL);
            request.addFileToUpload(path, "image");//Adding file
            request.addParameter("name", name);
            request.addParameter("Admin_ID", Admin_ID);
            request.addParameter("HS_Category", HS_Category);
            request.addParameter("HS_location", HS_location);
            request.addParameter("HS_Rent", HS_Rent);
            request.addParameter("HS_Description", HS_Description);//Adding text parameter to the request
            request.setMaxRetries(2);

            request.startUpload(); //Starting the upload

            Toast.makeText(this, path +" "+name +" "+Admin_ID+" "+HS_Category +" "+HS_Rent +" "+HS_Description, Toast.LENGTH_SHORT).show();





        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if (v == buttonUpload) {
            uploadMultipart();
        }
        if(v==buttonView){
            viewImage();

        }
    }

        private void viewImage() {
            startActivity(new Intent(this, Main2Activity.class));
        }
}
