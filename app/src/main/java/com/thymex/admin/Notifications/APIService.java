package com.thymex.admin.Notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA64lTmU4:APA91bFEzNdza8O1kajqYHLya64Dbf9lg6dhPuRW2lqj-Kwa2wJrfWAYQuOZbwRwuEj8ukS3lqTmDsdXx0nNcJYAxrCeUEBydi8abxLJhPxXlv7Z-aAtIwkFzBvp0IuR6rHl3cnBJbno"

    })
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);






}