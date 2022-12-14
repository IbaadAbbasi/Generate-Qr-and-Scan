package com.example.qrtestapp.qrcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrtestapp.R;


public class QR_CODE extends AppCompatActivity implements View.OnClickListener,KeyEvent.Callback {
    EditText messageToEncrypt;
    ImageView QRCodeImage;
    generator _generator;
    Encrypt ECEncryption;
    Button generateQRCode,shareQRCode;
    String code="EncryptedQR_CODE";
    KeyManager keyManager;
    public static String key ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qr__code);
            messageToEncrypt = (EditText) findViewById(R.id.editText);
            keyManager=new KeyManager();
            keyManager.readFromPreferences(getApplicationContext());
            generateQRCode = (Button) findViewById(R.id.button3);
            generateQRCode.setOnClickListener(this);
            shareQRCode = (Button) findViewById(R.id.button4);
            shareQRCode.setOnClickListener(this);
            ECEncryption=new EllipticCurve();
            QRCodeImage = (ImageView) findViewById(R.id.imageView);
            _generator = new codeGenerator(getApplicationContext());
            _generator.setImageView(QRCodeImage);
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

    }

   // @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button3: {
                ECEncryption.startEncryption(keyManager.getPublicKey(),messageToEncrypt.getText().toString());
                _generator.setMessage(code+ECEncryption.getEncryptedData());
                _generator.generateCode();
                QRCodeImage.setVisibility(View.VISIBLE);
                QRCodeImage = _generator.getGeneratedCodeImage();
                break;
            }
            case R.id.button4: {
                try{
                Intent sharing=new Intent(Intent.ACTION_SEND);
                sharing.setType("image/jepg");
                sharing.putExtra(Intent.EXTRA_STREAM,_generator.getUri());
                    startActivity(Intent.createChooser(sharing,"Share Using"));
            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();

            }
            break;
            }

        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                finish();
                break;
            }
        }
        return true;
    }
}
