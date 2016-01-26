package no.appsonite.gpsping.api;

import no.appsonite.gpsping.api.content.ApiAnswer;
import no.appsonite.gpsping.api.content.FriendsAnswer;
import no.appsonite.gpsping.api.content.LoginAnswer;
import no.appsonite.gpsping.api.content.NonceAnswer;
import no.appsonite.gpsping.api.content.RegisterAnswer;
import no.appsonite.gpsping.api.content.TrackersAnswer;
import no.appsonite.gpsping.api.content.UsersAnswer;
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

    @GET("user/register/?seconds=999999999")
    Observable<RegisterAnswer> registerUser(@Query("username") String username, @Query("user_pass") String password, @Query("email") String email, @Query("first_name") String firstName, @Query("last_name") String lastName, @Query("display_name") String displayName, @Query("nonce") String nonce);

    @GET("user/generate_auth_cookie/?seconds=999999999")
    Observable<LoginAnswer> login(@Query("username") String username, @Query("password") String password);

    @GET("user/update_user_meta/")
    Observable<ApiAnswer> updateUser(@Query("meta_key") String fieldName, @Query("meta_value") String fieldValue);

    @GET("tracker/add_tracker/")
    Observable<ApiAnswer> addTracker(@Query("name") String trackerName, @Query("imei_number") String imei, @Query("tracker_number") String number, @Query("reciver_signal_repeat_time") long repeatTime, @Query("check_for_stand") boolean checkForStand, @Query("type") String type);

    //    http://host/api/tracker/update_tracker/?cookie=cookie&tracker_id=long&name=string&reciver_signal_repeat_time=long&check_for_stand=bool
    @GET("tracker/update_tracker/")
    Observable<ApiAnswer> updateTracker(@Query("imei_number") String imei, @Query("name") String trackerName, @Query("reciver_signal_repeat_time") long repeatTime, @Query("check_for_stand") boolean checkForStand);

    @GET("tracker/get_trackers/")
    Observable<TrackersAnswer> getTrackers();

    @GET("tracker/remove_tracker/")
    Observable<ApiAnswer> removeTracker(@Query("imei_number") String imei);

    @GET("friends/add/")
    Observable<ApiAnswer> addFriend(@Query("id") long id);

    @GET("friends/get/")
    Observable<FriendsAnswer> getFriends();

    @GET("friends/set_seeing_trackers/")
    Observable<ApiAnswer> setSeeingTrackers(@Query("id") long id, @Query("is_seeing_trackers") boolean isSeeingTrackers);

    @GET("friends/confirm/")
    Observable<ApiAnswer> confirmFriendship(@Query("id") long id);

    @GET("friends/remove/")
    Observable<ApiAnswer> removeFriend(@Query("id") long id);

    @GET("friends/search/")
    Observable<UsersAnswer> searchFriends(@Query("q") String searchString);
}
