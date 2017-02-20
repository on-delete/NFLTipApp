package com.andre.nfltipapp;


import android.content.Context;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

/**
 * Created by Andre on 06.01.2017.
 */

public class Utils {

    private static DateFormat sdf = new SimpleDateFormat("y-M-d h:m:s a", Locale.US);

    public static Calendar getActualGameTime(String gameTime){
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            Date date = sdf.parse(gameTime);
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isPredictionTimeOver(String gameTime, int offset){
        Calendar gameTimeCal = getActualGameTime(gameTime);
        Calendar rightNow = Calendar.getInstance();
        if(gameTimeCal != null){
            gameTimeCal.add(Calendar.MINUTE, offset);
            return rightNow.getTimeInMillis() > gameTimeCal.getTimeInMillis();
        }
        else{
            return false;
        }
    }

    public static OkHttpClient getHttpClient(Context context) {

        SSLContext sslContext;
        TrustManager[] trustManagers;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            InputStream certInputStream = context.getResources().openRawResource(R.raw.server_cert);
            BufferedInputStream bis = new BufferedInputStream(certInputStream);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry("www.rocciberge.de", cert);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
        } catch (Exception e) {
            e.printStackTrace(); //TODO replace with real exception handling tailored to your needs
            return null;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .build();

        return client;
    }
}
