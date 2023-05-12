package com.metanetglobal.knowledge.worker.common;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Retrofit 을 이용하여 서버통신을 하기 위한 RestClient
 *
 * @author      namki.an
 * @version     1.0.0
 * @param       <T>
 * @see         Retrofit
 */
public class DefaultRestClient<T> {
    private T service;

    /**
     * Get Rest Client
     *
     * @param type Class
     * @return T
     */
    public T getClient(Class<? extends T> type) {
        if(service == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    /**
                     * Request Header 에 Session 정보 삽입
                     */
                    Request.Builder builder = original.newBuilder();
                    Set<String> cookies = AMSettings.COOKIESET;

                    for(String cookie : cookies) {
                        Timber.tag("DefaultRestClient").d("Request Cookie : " + cookie);
                        builder.addHeader(AMSettings.SESSION_NAME, cookie);
                    }

                    builder.removeHeader("User-Agent").addHeader("User-Agent", "Android");

                    return chain.proceed(builder.build());
                }
            })
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response original = chain.proceed(chain.request());

                    /**
                     * JSessionID 를 App 내부에 저장 후 나중에 Request Header 에 보내준다.
                     */
                    if(!original.headers("Set-Cookie").isEmpty()) {
                        HashSet<String> cookies = new HashSet<>();

                        for(String header : original.headers("Set-Cookie")) {
                            Timber.tag("DefaultRestClient").d("Set-Cookie : " + header);
                            cookies.add(header);
                        }

                        AMSettings.COOKIESET.clear();
                        AMSettings.COOKIESET.addAll(cookies);
                    }

                    return original;
                }
            }).build();

            /**
             * Base Url 및 OKHttpClient 를 설정해준다.
             * GsonConverterFactory 도 추가해줬다.
             */
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(AMSettings.baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = client.create(type);
        }

        return service;
    }
}
