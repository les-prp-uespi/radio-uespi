package br.uespi.piripiri.radio.News;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {
   /*@GET("index.php{page}")
   Call<String> baseNews(@Query("rest_route=%2Fwp%2Fv2%2Fposts%2F&page") int page);

   */

   @Headers({"Accept: application/json"})
   @GET("/prp/noticias.php")
   Call<List<News>> baseNews(@Query("pagina") int pagina);
}

