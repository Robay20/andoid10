package com.example.mappp;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class Main extends AppCompatActivity {

    
    private ImageView imageView;
    private Bitmap originalBitmap;
    private boolean isBlackAndWhite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.main_activity);

                    imageView = findViewById(R.id.imageView);
                    registerForContextMenu(imageView);
                    originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alarm);

                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            v.showContextMenu();
                            return true;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_copy) {
            ImageView imageView = findViewById(R.id.imageView);
            if (imageView != null && imageView.getDrawable() != null) {
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                copyToClipboard(bitmap);
                return true;
            } else {
                Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (itemId == R.id.menu_rotate) {
            rotateImage(90);
            return true;
        } else if (itemId == R.id.menu_toggle_bw_color) {
            toggleBlackAndWhite();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void copyToClipboard(Bitmap bitmap) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Image", "Image copied to clipboard");
        ClipData.Item item = new ClipData.Item(bitmapToUri(bitmap)); // Convert Bitmap to Uri
        clip.addItem(item);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Image copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void rotateImage(float degrees) {
        ImageView imageView = findViewById(R.id.imageView);
        if (imageView != null && imageView.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(rotatedBitmap);

            Toast.makeText(this, "Image rotated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show();
        }
    }


    private Uri bitmapToUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }




    private void toggleBlackAndWhite() {
        if (isBlackAndWhite) {
            imageView.setImageBitmap(originalBitmap);
            isBlackAndWhite = false;
            Toast.makeText(this, "Image colored", Toast.LENGTH_SHORT).show();
        } else {
            Bitmap blackAndWhiteBitmap = toBlackAndWhite(originalBitmap);
            imageView.setImageBitmap(blackAndWhiteBitmap);
            isBlackAndWhite = true;
            Toast.makeText(this, "Image black and white", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap toBlackAndWhite(Bitmap bitmap) {
        Bitmap blackAndWhiteBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixel = bitmap.getPixel(x, y);
                int red = (int) (0.299 * Color.red(pixel));
                int green = (int) (0.587 * Color.green(pixel));
                int blue = (int) (0.114 * Color.blue(pixel));
                int gray = red + green + blue;
                blackAndWhiteBitmap.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }
        return blackAndWhiteBitmap;
    }


}
