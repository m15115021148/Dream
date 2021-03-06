package com.romantic.dream.http.download;

import com.romantic.dream.http.download.listener.DownloadListener;
import com.romantic.dream.http.download.util.FileUtils;
import com.romantic.dream.http.okhttp.certificate.TrustAllCerts;
import com.romantic.dream.http.okhttp.certificate.TrustAllHostnameVerifier;
import com.romantic.dream.http.okhttp.interceptor.HeadInterceptor;
import com.romantic.dream.http.okhttp.interceptor.LogInterceptor;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description:
 * Company:
 * Created by chenmeng on 2017/9/3.
 */
public class DownloadHelper {

    private Call call;

    private String url;//接口url
    private String dirPath;//文件路径
    private String fileName;//文件名称


    public DownloadHelper(String url, String dirPath, String fileName) {
        this.url = url;
        this.dirPath = dirPath;
        this.fileName = fileName;
    }

    public void downloadFile(final DownloadListener downloadListener) {

        Request request = new Request.Builder().url(url).build();
        call = initClient(downloadListener).newCall(request);


        Observable.just(call)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Call>() {
                    @Override
                    public void accept(@NonNull Call call) throws Exception {
                        Response response = call.execute();

                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            FileUtils.saveFile(dirPath, fileName, response.body().byteStream());
                            response.close();

                            if (downloadListener != null) {
                                Observable.just(new File(dirPath, fileName)).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<File>() {
                                            @Override
                                            public void accept(@NonNull File file) throws Exception {
                                                downloadListener.onSuccess(file);
                                            }
                                        });
                            }
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) {
                        Observable.just(throwable).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        if (downloadListener != null) {
                                            downloadListener.onFailure(throwable);
                                        }
                                    }
                                });
                    }
                });

    }


    public void cancelDownload() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    private OkHttpClient initClient(final DownloadListener downloadListener) {

        return new OkHttpClient.Builder()
                .addInterceptor(new HeadInterceptor())
                .addInterceptor(new LogInterceptor())
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Accept-Encoding", "identity").build();
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), downloadListener)).build();
                    }
                })
                .build();
    }

    private OkHttpClient initClientCertificate(final DownloadListener downloadListener) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HeadInterceptor());
        builder.addInterceptor(new LogInterceptor());
        builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Accept-Encoding", "identity").build();
                        return chain.proceed(request);
                    }
                });
        builder.addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), downloadListener)).build();
                    }
                });
        builder.sslSocketFactory(TrustAllCerts.getSSL(),new TrustAllCerts());
        builder.hostnameVerifier(new TrustAllHostnameVerifier());
        return builder.build();
    }
}
