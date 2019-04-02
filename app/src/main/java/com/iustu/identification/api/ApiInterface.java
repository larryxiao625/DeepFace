package com.iustu.identification.api;

import com.iustu.identification.api.message.Message;
import com.iustu.identification.api.message.response.AdvancedFaceSearchResponse;
import com.iustu.identification.api.message.response.SearchImageHistoryResponse;
import com.iustu.identification.api.message.response.Compare2Response;
import com.iustu.identification.api.message.response.CompareHistoryResponse;
import com.iustu.identification.api.message.response.FaceSearchResponse;
import com.iustu.identification.api.message.response.LoginResponse;
import com.iustu.identification.bean.FaceSet;
import com.iustu.identification.api.message.response.ImageCompareResponse;
import com.iustu.identification.api.message.response.PeopleListResponse;
import com.iustu.identification.bean.PersonInfo;
import com.iustu.identification.api.message.response.PreLoginResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Liu Yuchuan on 2017/11/25.
 */

public interface ApiInterface {
    @POST("/")
    Observable<Message<PreLoginResponse>> preLogin(@Body RequestBody message);
    @POST("/")
    Observable<Message<LoginResponse>> login(@Body RequestBody message);
    @Multipart
    @POST("/")
    Observable<Message<LoginResponse>> faceLogin(@Part("message") RequestBody message, @Part MultipartBody.Part file);
    @POST("/")
    Observable<Message<SearchImageHistoryResponse>> queryCapturedFace(@Body RequestBody message);
    @POST("/")
    Observable<Message<List<AdvancedFaceSearchResponse>>> queryAdvancedFaceSearchRecord(@Body RequestBody message);
    @POST("/")
    Observable<Message<List<FaceSet>>> getFaceSetList(@Body RequestBody message);
    @POST("/")
    Observable<Message<String>> createFaceSet(@Body RequestBody message);
    @POST("/")
    Observable<Message> modifyFaceSet(@Body RequestBody message);
    @POST("/")
    Observable<Message> destroyFaceSet(@Body RequestBody message);
    @POST("/")
    Observable<Message<PeopleListResponse>> getPeopleList(@Body RequestBody message);
    @POST("/")
    Observable<Message<PersonInfo>> getPeopleInfo(@Body RequestBody message);
    @POST("/")
    Observable<Message<String>> addPeople(@Body RequestBody message);
    @POST("/")
    Observable<Message> modfiyPeople(@Body RequestBody message);
    @POST("/")
    Observable<Message> delPeople(@Body RequestBody message);
    @POST("/")
    Observable<Message<List<String>>> getFaceList(@Body RequestBody message);
    @Multipart
    @POST("/")
    Observable<Message<String>> addFace(@Part("message") RequestBody message, @Part MultipartBody.Part images);
    @POST("/")
    Observable<Message> delFace(@Body RequestBody message);
    /**
     * @param type pass face
     * @return url
     */
    @GET("image")
    Observable<String> getFaceImageFace(@Query("type") String type, @Query("face_set_id") String faceSetId, @Query("people_id") String peopleId,@Query("id") String id, @Query("session") String session);
    @POST("/")
    Observable<Message<ImageCompareResponse>> imageCompare(@Body RequestBody message);
    @POST("/")
    Observable<Message<FaceSearchResponse>> faceSearch(@Body RequestBody message);
//    @Multipart
//    @POST("/")
//    Observable<Message<List<AdvancedFaceSearchResponse>>> advancedFaceSearch(@Part("message") RequestBody message,@Part MultipartBody.Part files);
    @Multipart
    @POST("/")
    Observable<Response<Message<List<AdvancedFaceSearchResponse>>>> advancedFaceSearch(@Part("message") RequestBody message,@Part MultipartBody.Part files);
    @Multipart
    @POST("/")
    Observable<Message<Compare2Response>> imageCompare(@Part("message") RequestBody message, @Part MultipartBody.Part img1, @Part MultipartBody.Part img2);
    @GET("image")
    Observable<Response<String>> getFaceSearchImage(@Query("type") String type, @Query("id") String id, @Query("session") String session);
    @POST("/")
    Observable<Message> keepAlive(@Body RequestBody message);
}