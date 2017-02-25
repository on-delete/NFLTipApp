package com.andre.nfltipapp.rest;

import android.content.Context;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Api instance = null;
    private ApiInterface apiInterface;

    public static Api getInstance(Context context) {
        if(instance==null){
            instance = new Api(context);
        }

        return instance;
    }

    private Api(Context context) {
        buildRetrofit(context);
    }

    public ApiInterface getApiInterface(){
        return this.apiInterface;
    }

    private void buildRetrofit(Context context) {
        OkHttpClient httpClient = getHttpClient(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    private OkHttpClient getHttpClient(Context context) {

        SSLContext sslContext;

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
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .build();
        }

        return new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .build();
    }
}
