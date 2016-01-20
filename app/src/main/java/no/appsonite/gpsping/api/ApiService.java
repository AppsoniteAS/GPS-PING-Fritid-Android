package no.appsonite.gpsping.api;

import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.NonceAnswer;
import no.appsonite.gpsping.api.content.RegisterAnswer;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public interface ApiService {
    @GET("get_nonce/?controller=user&method=register")
    Observable<NonceAnswer> getNonce();

    @GET("user/register/")
    Observable<RegisterAnswer> registerUser(@Query("username") String username, @Query("user_pass") String password, @Query("email") String email, @Query("display_name") String userFullName, @Query("nonce") String nonce);

    @GET("user/generate_auth_cookie/")
    Observable<LoginAnswer> login(@Query("username") String username, @Query("password") String password);
}
