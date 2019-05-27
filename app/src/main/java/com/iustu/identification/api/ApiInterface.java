package com.iustu.identification.api;


import com.iustu.identification.api.message.UploadImageCallBack;
import com.iustu.identification.api.message.UploadImagePost;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Liu Yuchuan on 2017/11/25.
 */

public interface ApiInterface {
//    @POST("/")
//    Observable<Message<PreLoginResponse>> preLogin(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<LoginResponse>> login(@Body RequestBody message);
//    @Multipart
//    @POST("/")
//    Observable<Message<LoginResponse>> faceLogin(@Part("message") RequestBody message, @Part MultipartBody.Part file);
//    @POST("/")
//    Observable<Message<SearchImageHistoryResponse>> queryCapturedFace(@Body RequestBody message);
////    @POST("/")
////    Observable<Message<List<AdvancedFaceSearchResponse>>> queryAdvancedFaceSearchRecord(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<List<FaceSet>>> getFaceSetList(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<String>> createFaceSet(@Body RequestBody message);
//    @POST("/")
//    Observable<Message> modifyFaceSet(@Body RequestBody message);
//    @POST("/")
//    Observable<Message> destroyFaceSet(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<PeopleListResponse>> getPeopleList(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<PersonInfo>> getPeopleInfo(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<String>> addPeople(@Body RequestBody message);
//    @POST("/")
//    Observable<Message> modfiyPeople(@Body RequestBody message);
//    @POST("/")
//    Observable<Message> delPeople(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<List<String>>> getFaceList(@Body RequestBody message);
//    @Multipart
//    @POST("/")
//    Observable<Message<String>> addFace(@Part("message") RequestBody message, @Part MultipartBody.Part images);
//    @POST("/")
//    Observable<Message> delFace(@Body RequestBody message);
//    /**
//     * @param type pass face
//     * @return url
//     */
//    @GET("image")
//    Observable<String> getFaceImageFace(@Query("type") String type, @Query("face_set_id") String faceSetId, @Query("people_id") String peopleId,@Query("id") String id, @Query("session") String session);
//    @POST("/")
//    Observable<Message<ImageCompareResponse>> imageCompare(@Body RequestBody message);
//    @POST("/")
//    Observable<Message<FaceSearchResponse>> faceSearch(@Body RequestBody message);
////    @Multipart
////    @POST("/")
////    Observable<Message<List<AdvancedFaceSearchResponse>>> advancedFaceSearch(@Part("message") RequestBody message,@Part MultipartBody.Part files);
////    @Multipart
////    @POST("/")
////    Observable<Response<Message<List<AdvancedFaceSearchResponse>>>> advancedFaceSearch(@Part("message") RequestBody message,@Part MultipartBody.Part files);
//    @Multipart
//    @POST("/")
//    Observable<Message<Compare2Response>> imageCompare(@Part("message") RequestBody message, @Part MultipartBody.Part img1, @Part MultipartBody.Part img2);
//    @GET("image")
//    Observable<Response<String>> getFaceSearchImage(@Query("type") String type, @Query("id") String id, @Query("session") String session);
//    @POST("/")
//    Observable<Message> keepAlive(@Body RequestBody message);
    /**
     * 人脸平台图片上传接口
     */
    @POST("haiou/uploadGlassImage")
    Observable<UploadImageCallBack> uploadImage(@Part UploadImagePost uploadImagePost);
}