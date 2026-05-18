package com.emme.palmarebdibimbi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InventoryApi {
    @POST("api/inventory/register")
    Call<InventoryResponse> register(@Body InventoryRequest request);
}
