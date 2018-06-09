package cn.ittiger.im;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;


import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Chu_xi on 2016/12/19.
 */

public class RetrofitManager {

    private static final String TAG = "RetrofitManager";

    private Gson mGson;
    private OkHttpClient mHttpClient;
    private Retrofit mRetrofit;
    private static int versionCode;


    public static RetrofitManager getInstance(@NonNull String baseUrl) {
        return new RetrofitManager(baseUrl);
    }

    public static RetrofitManager getInstance(@NonNull String baseUrl, OkHttpClient client) {
        return new RetrofitManager(baseUrl, client);
    }

    private RetrofitManager(String baseUrl) {
        this(baseUrl, null);
    }

    private RetrofitManager(String baseUrl, OkHttpClient client) {
        mGson = new Gson();
        if (client == null) {
            mHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                            Request request = chain.request();


                            //打印Request数据
                            logRequest(request);
                            Response response = chain.proceed(request);
                            logResponse(response);
                            return response;
                        }

                        private void logRequest(Request request) throws IOException {


                            Log.i(TAG, request.method() + "   " + request.url());
                            Headers headers = request.headers();
                            for (int i = 0, count = headers.size(); i < count; i++) {
                                String name = headers.name(i);
                                // Skip headers from the request body as they are explicitly logged above.
                                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                                    Log.i(TAG, name + ": " + headers.value(i));
                                }
                            }


                            if (request.body() != null) {
                                Buffer buffer = new Buffer();
                                request.body().writeTo(buffer);

                                Charset charset = Charset.forName("UTF-8");
                                MediaType contentType = request.body().contentType();
                                if (contentType != null) {
                                    charset = contentType.charset(Charset.forName("UTF-8"));
                                }

                                Log.i(TAG, buffer.readString(charset));

                                Log.i(TAG, "--> END " + request.method()
                                        + " (" + request.body().contentLength() + "-byte body)");
                            }

                        }

                        private void logResponse(Response response) throws IOException {
                            if (response != null) {

                                Log.i(TAG, response.code() + "  " + response.message());

                                Headers headers = response.headers();
                                for (int i = 0, count = headers.size(); i < count; i++) {
                                    Log.i(TAG, headers.name(i) + ": " + headers.value(i));
                                }
                                long contentLength = response.body().contentLength();

                                BufferedSource source = response.body().source();
                                source.request(Long.MAX_VALUE); // Buffer the entire body.
                                Buffer buffer = source.buffer();

                                Charset charset = Charset.forName("UTF-8");
                                MediaType contentType = response.body().contentType();
                                if (contentType != null) {
                                    charset = contentType.charset(Charset.forName("UTF-8"));
                                }

                                if (contentLength != 0) {
                                    Log.i(TAG, "");
                                    Log.i(TAG, buffer.clone().readString(charset));
                                }

                            } else {
                                Log.i(TAG, "Response is null");
                            }
                        }
                    })
                    .build();
        } else {
            mHttpClient = client;
        }
        mRetrofit = buildRetrofit(baseUrl);
    }

    /**
     * create retrofit instance
     * according to httpclient、rx、gson ect
     *
     * @return retrofit instance
     */
    private Retrofit buildRetrofit(@NonNull String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl)
                .client(mHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(mGson));
        return builder.build();
    }

    public <T> T create(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }


}
