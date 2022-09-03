package com.example.qrtestapp.qrcodereader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


/**
 * Created by sagar on 10/24/2017.
 */



public class KeyManager {
    final String keyInstaitiate = "keyInstaitiate";
    SharedPreferences sharedPreferences,privateKeyStore,publicKeyStore;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    PrivateKey privateKey;
    PublicKey publicKey;
    String provider = "SC";
    String curveUsed = "secp256r1";
    String jsonFileName = "keyData.json";
    String priKey = "PrivateKey", pubKey = "PublicKey";

    public KeyManager() {
        Security.addProvider( new BouncyCastleProvider() );
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

    }

    public boolean checkFirstAttempt(Context c) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        boolean valueWritten = sharedPreferences.getBoolean(keyInstaitiate, false);
        if (!valueWritten) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(keyInstaitiate, true);
            editor.apply(); // Very important to save the preference
            instantiateKey();
            Toast.makeText(c, "key created", Toast.LENGTH_LONG).show();

            return true;
            //instantiate key
        } else {
            Toast.makeText(c, "key read", Toast.LENGTH_LONG).show();

            return false;
        }


    }

    void instantiateKey() {
        KeyPairGenerator keyPairGenerator;
        KeyPair keyPair;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("EC", provider);
            keyPairGenerator.initialize(new ECGenParameterSpec(curveUsed));
            keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    void writeKeyToPreferences(Context c) {
        privateKeyStore =c.getSharedPreferences(priKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = privateKeyStore.edit();
        editor.putString(priKey, Base64.encodeToString(privateKey.getEncoded(),Base64.DEFAULT));
        editor.apply();
        publicKeyStore =c.getSharedPreferences(pubKey, Context.MODE_PRIVATE);
         editor = publicKeyStore.edit();
        editor.putString(pubKey, Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT));
        editor.apply();
    }
    public void readFromPreferences(Context c){
        try {
            privateKeyStore = c.getSharedPreferences(priKey, Context.MODE_PRIVATE);
            String privateKeyRaw = privateKeyStore.getString(priKey, null);


            publicKeyStore = c.getSharedPreferences(pubKey, Context.MODE_PRIVATE);
            String publicKeyRaw = publicKeyStore.getString(pubKey, null);
            makeKeyFromPreferences(publicKeyRaw, privateKeyRaw);
        }
        catch(Exception e){
            Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    void makeKeyFromPreferences(String publicKeyRaw,String privateKeyRaw){
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("EC",provider);
            X509EncodedKeySpec encodedKey = new X509EncodedKeySpec(Base64.decode(publicKeyRaw,Base64.DEFAULT));
            publicKey = keyFactory.generatePublic(encodedKey);
            encodedKey = new X509EncodedKeySpec(Base64.decode(privateKeyRaw,Base64.DEFAULT));
            privateKey = keyFactory.generatePrivate(encodedKey);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
           // Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }


    }


}