package com.example.adminblinkit.api

import com.example.adminblinkit.models.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {



    @Headers(
        "Content-Type: application/json",
    "Authorization: key=AAAA5i75LoI:APA91bHElvHPUcNtN2usuLICBfZpWJfgbf_0xKjfQZVXl0NS2vf5GiegeKeUvjJswm8ELaAwRNSOf1MaozQB12VAbpS0hQNT84t9tnIFcrTISELE_DOXi8o3kuKeXesImVghM0kM7MJH"
    )

    @POST("fcm/send")
    fun sendNotification(@Body notification: Notification) : Call<Notification>

}