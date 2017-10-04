package com.cardist.ntrtest.data;

import com.cardist.ntrtest.model.MainModel;
import com.cardist.ntrtest.utils.NetworkUtils;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;


public interface ApiService {

    @GET(NetworkUtils.URL_DATA)
    Observable<Response<MainModel>> requestData();
}
