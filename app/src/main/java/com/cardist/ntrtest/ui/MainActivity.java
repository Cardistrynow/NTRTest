package com.cardist.ntrtest.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.cardist.ntrtest.BuildConfig;
import com.cardist.ntrtest.R;
import com.cardist.ntrtest.data.ApiService;
import com.cardist.ntrtest.model.MainModel;
import com.cardist.ntrtest.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private TextView nameText;
    private TextView typeText;
    private TextView phoneText;
    private TextView cityText;
    private TextView streetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = findViewById(R.id.nameText);
        typeText = findViewById(R.id.typeText);
        phoneText = findViewById(R.id.phoneText);
        cityText = findViewById(R.id.cityText);
        streetText = findViewById(R.id.streetText);

        if(checkInternetConnection()) {
            mCompositeDisposable.add(
                    getData()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(response -> {
                                if (response.getName() != null)
                                    nameText.setText(getString(R.string.name_text, response.getName()));

                                if (response.getType() != null)
                                    typeText.setText(getString(R.string.type_text, response.getType()));

                                if (response.getPhone() != null)
                                    phoneText.setText(getString(R.string.phone_text, response.getPhone()));

                                if (response.getAddress() != null) {
                                    if (response.getAddress().getCity() != null)
                                        cityText.setText(getString(R.string.city_text, response.getAddress().getCity()));

                                    if (response.getAddress().getStreet() != null)
                                        streetText.setText(getString(R.string.street_text, response.getAddress().getStreet()));
                                }

                            }, e -> Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show())
            );
        } else {
            Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        }
    }

    private Observable<MainModel> getData() {
        return retrofitInit().requestData()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .map(Response::body)
                .onErrorResumeNext(throwable -> {
                    return Observable.error(throwable);
                })
                .timeout(NetworkUtils.WAIT_TIMEOUT, TimeUnit.SECONDS);
    }

    private ApiService retrofitInit() {

        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(NetworkUtils.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(NetworkUtils.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NetworkUtils.WRITE_TIMEOUT, TimeUnit.SECONDS);

        // логирование
        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            okHttpClient.addInterceptor(logging);
        }

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.URL_BACKEND)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build();

        return retrofit.create(ApiService.class);
    }

    public boolean checkInternetConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
