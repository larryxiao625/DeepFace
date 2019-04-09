//package com.iustu.identification.api;
//
//import com.google.gson.Gson;
//import com.iustu.identification.api.message.Message;
//import com.iustu.identification.api.message.request.Compare2Request;
//import com.iustu.identification.api.message.request.FaceSearchRequest;
//import com.iustu.identification.api.message.request.HistoryRequest;
//import com.iustu.identification.api.message.request.LoginRequest;
//import com.iustu.identification.api.message.request.PeopleInfoRequest;
//import com.iustu.identification.api.message.request.PeopleListRequest;
//import com.iustu.identification.api.message.response.AdvancedFaceSearchResponse;
//import com.iustu.identification.api.message.response.Compare2Response;
//import com.iustu.identification.api.message.response.LoginResponse;
//import com.iustu.identification.api.message.response.PeopleListResponse;
//import com.iustu.identification.api.message.response.PreLoginResponse;
//import com.iustu.identification.api.message.response.SearchImageHistoryResponse;
//import com.iustu.identification.bean.FaceSet;
//import com.iustu.identification.bean.PeopleFace;
//import com.iustu.identification.bean.PersonInfo;
//import com.iustu.identification.bean.User;
//import com.iustu.identification.config.LibraryConfig;
//import com.iustu.identification.config.ParametersConfig;
//import com.iustu.identification.config.SystemConfig;
//import com.iustu.identification.util.MD5Util;
//import com.iustu.identification.util.TextUtil;
//import com.iustu.identification.util.UserCache;
//
//import java.io.File;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import io.reactivex.Observable;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Response;
//
///**
// * Created by Liu Yuchuan on 2017/11/30.
// */
//
//public class Api {
//    private Api(){}
//
//    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
//    private static final MediaType MULTIPART_MESSAGE_MEDIA_TYPE = MediaType.parse("multipart/form-data");
//    private static final String REQUEST = "req";
//
//    private static RequestBody makeJson(Object message){
//        return RequestBody.create(JSON_MEDIA_TYPE, new Gson().toJson(message));
//    }
//
//    private static RequestBody makeMultiPartJson(Object message){
//        return RequestBody.create(MULTIPART_MESSAGE_MEDIA_TYPE, new Gson().toJson(message));
//    }
//
//    private static RequestBody multiPartFile(File file){
//        return RequestBody.create(MULTIPART_MESSAGE_MEDIA_TYPE, file);
//    }
//
//    private static RequestBody multiPartFile(byte[] bytes){
//        return RequestBody.create(MULTIPART_MESSAGE_MEDIA_TYPE, bytes);
//    }
//
//    private static void addSession(Message message){
//        User user = UserCache.getUser();
//        if(user.isVerify()){
//            message.setId(user.getId());
//            message.setSession(user.getSession());
//        }
//    }
//
////    public static Observable<Message<PreLoginResponse>> preLogin(){
////        Message preLoginMessage = new Message();
////        preLoginMessage.setType(REQUEST);
////        preLoginMessage.setName("PreLogin");
////        return ApiManager.getInstance()
////                .getApi()
////                .preLogin(makeJson(preLoginMessage))
////                .subscribeOn(Schedulers.io());
////    }
////
////    public static Observable<Message<LoginResponse>> login(String username, String password){
////        Message preLoginMessage = new Message();
////        preLoginMessage.setType(REQUEST);
////        preLoginMessage.setName("PreLogin");
////        return ApiManager.getInstance()
////                .getApi()
////                .preLogin(makeJson(preLoginMessage))
////                .flatMap(preLoginResponseMessage -> {
////                    Message<LoginRequest> loginMessage = new Message<>();
////                    loginMessage.setType(REQUEST);
////                    loginMessage.setName("Login");
////                    loginMessage.setId(preLoginResponseMessage.getId());
////                    LoginRequest loginRequest = new LoginRequest();
////                    String nonce = preLoginResponseMessage.getBody().getNonce();
////                    loginRequest.setUserName(username);
////                    loginRequest.setNonce(nonce);
////                    loginRequest.setAuthCode(MD5Util.getStringMD5(username + ":" + password + ":" + nonce));
////                    loginMessage.setBody(loginRequest);
////                    return  ApiManager.getInstance()
////                            .getApi()
////                            .login(makeJson(loginMessage));
////                })
////                .subscribeOn(Schedulers.io());
////    }
////
////    public static Observable<Message<LoginResponse>> faceLogin(byte[] img){
////        Message message = new Message<>();
////        message.setType(REQUEST);
////        message.setName("FaceLogin");
////        MultipartBody.Part imgPart = MultipartBody.Part.createFormData("files", String.valueOf(System.currentTimeMillis()), multiPartFile(img));
////        return ApiManager.getInstance()
////                .getApi()
////                .faceLogin(makeMultiPartJson(message), imgPart)
////                .subscribeOn(Schedulers.io());
////    }
////
////
////    public static Observable<Message<SearchImageHistoryResponse>> queryCapturedFace(Date beginTime, Date endTime, int page) {
////        Message<HistoryRequest> message = new Message<>();
////        message.setName("QueryFaceSearchRecord");
////        HistoryRequest request = new HistoryRequest();
////        request.setBeginTime(TextUtil.dateMessage(beginTime));
////        request.setEndTime(TextUtil.dateMessage(endTime));
////        request.setPage(page);
////        request.setPageSize(40);
////        message.setBody(request);
////        addSession(message);
////        return ApiManager.getInstance()
////                .getApi()
////                .queryCapturedFace(makeJson(message))
////                .subscribeOn(Schedulers.io());
////    }
//
////    public static Observable<Message<List<AdvancedFaceSearchResponse>>> queryComparedResult(String id) {
////        Message<String> message = new Message<>();
////        message.setType(REQUEST);
////        message.setName("QueryAdvancedFaceSearchRecord");
////        message.setBody(id);
////        addSession(message);
////        return ApiManager.getInstance()
////                .getApi()
////                .queryAdvancedFaceSearchRecord(makeJson(message))
////                .subscribeOn(Schedulers.io());
////    }
//
//    public static Observable<Message<List<FaceSet>>> getFaceSetList() {
//        Message message = new Message();
//        message.setName("GetFaceSetList");
//        message.setType(REQUEST);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .getFaceSetList(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message<String>> createFaceSet(String name, String remark) {
//        Message<FaceSet> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("CreateFaceSet");
//        FaceSet faceSet = new FaceSet();
//        faceSet.setName(name);
//        faceSet.setRemark(remark);
//        message.setBody(faceSet);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .createFaceSet(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message> modifyFaceSet(String remark, String name, String id) {
//        Message<FaceSet> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("ModifyFaceSet");
//        FaceSet faceSet = new FaceSet();
//        faceSet.setRemark(remark);
//        faceSet.setName(name);
//        faceSet.setId(id);
//        message.setBody(faceSet);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .modifyFaceSet(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message> destroyFaceSet(String id) {
//        Message<String> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("DestroyFaceSet");
//        message.setBody(id);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .destroyFaceSet(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
////    public static Observable<Message<PeopleListResponse>> getPeopleList(String faceSetId,int page) {
////        Message<PeopleListRequest> listMessage = new Message<>();
////        listMessage.setType(REQUEST);
////        listMessage.setName("GetPeopleList");
////        PeopleListRequest peopleListRequest = new PeopleListRequest();
////        peopleListRequest.setFaceSetId(faceSetId);
////        peopleListRequest.setPageSize(40);
////        peopleListRequest.setPage(page);
////        listMessage.setBody(peopleListRequest);
////        addSession(listMessage);
////        return ApiManager.getInstance()
////                .getApi()
////                .getPeopleList(makeJson(listMessage))
////                .subscribeOn(Schedulers.io());
////    }
//
//    public static Observable<Message<PersonInfo>> getPeopleInfo(String faceSetId, String peopleId) {
//        Message<PeopleFace> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("GetPeopleInfo");
//        addSession(message);
//        PeopleFace peopleFace = new PeopleFace();
//        peopleFace.setPeopleId(peopleId);
//        peopleFace.setFaceSetId(faceSetId);
//        message.setBody(peopleFace);
//        return ApiManager.getInstance()
//                .getApi()
//                .getPeopleInfo(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message<String>> addPeople(PersonInfo personInfo) {
//        Message<PersonInfo> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("AddPeople");
//        message.setBody(personInfo);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .addPeople(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message> modifyPeople(PersonInfo newInfo) {
//        Message<PersonInfo> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("ModfiyPeople");
//        message.setBody(newInfo);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .modfiyPeople(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message> delPeople(String id, String faceSetId) {
//        Message<PeopleInfoRequest> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("DelPeople");
//        PeopleInfoRequest peopleInfoRequest = new PeopleInfoRequest();
//        peopleInfoRequest.setId(id);
//        peopleInfoRequest.setFaceSetId(faceSetId);
//        message.setBody(peopleInfoRequest);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .delPeople(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message<List<String>>> getFaceList(String faceSetId, String peopleId) {
//        Message<PeopleFace> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("GetFaceList");
//        PeopleFace peopleFace = new PeopleFace();
//        peopleFace.setFaceSetId(faceSetId);
//        peopleFace.setPeopleId(peopleId);
//        message.setBody(peopleFace);
//        addSession(message);
//        return ApiManager.getInstance()
//                .getApi()
//                .getFaceList(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message<String>> addFace(String faceSetId, String peopleID, File file) {
//
//        Message<PeopleFace> message = new Message<>();
//        message.setType(REQUEST);
//        message.setName("AddFace");
//        PeopleFace peopleFace = new PeopleFace();
//        peopleFace.setFaceSetId(faceSetId);
//        peopleFace.setPeopleId(peopleID);
//        peopleFace.setImage("msg://1");
//        message.setBody(peopleFace);
//        addSession(message);
//        RequestBody body = multiPartFile(file);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", String.valueOf(System.currentTimeMillis()), body);
//        return ApiManager.getInstance()
//                .getApi()
//                .addFace(makeMultiPartJson(message), filePart)
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static Observable<Message> delFace(String faceSetId, String peopleId, String faceId) {
//        Message<PeopleFace> message = new Message<>();
//        message.setName("DelFace");
//        message.setType(REQUEST);
//        addSession(message);
//        PeopleFace peopleFace = new PeopleFace();
//        peopleFace.setFaceSetId(faceSetId);
//        peopleFace.setPeopleId(peopleId);
//        peopleFace.setId(faceId);
//        message.setBody(peopleFace);
//        return ApiManager.getInstance()
//                .getApi()
//                .delFace(makeJson(message))
//                .subscribeOn(Schedulers.io());
//    }
//
//    public static String getFaceImageFaceUrl(String faceSetId, String peopleId, String id) {
//        if(UserCache.getUser().isVerify()) {
//            return String.format(Locale.ENGLISH, "%simage?type=face&people_id=%s&face_set_id=%s&id=%s&session=%s", SystemConfig.getInstance().getIpAddress(), peopleId, faceSetId, id, UserCache.getUser().getSession());
//        }
//
//        return String.format(Locale.ENGLISH, "%simage?type=face&people_id=%s&face_set_id=%s&id=%s", SystemConfig.getInstance().getIpAddress(), peopleId, faceSetId, id);
//    }
//
////    @SuppressWarnings("ConstantConditions")
////    public static Observable<Response<Message<List<AdvancedFaceSearchResponse>>>> advancedFaceSearch(File file) {
////        Message<FaceSearchRequest> message = new Message<>();
////        message.setName("AdvancedFaceSearch");
////        message.setType(REQUEST);
////        addSession(message);
////        FaceSearchRequest faceSearchRequest = new FaceSearchRequest();
////        faceSearchRequest.setMaxResult(ParametersConfig.getInstance().getDisplayCount());
////        faceSearchRequest.setFaceSet(LibraryConfig.getInstance().getChosenLibs());
////        faceSearchRequest.setMinScore(ParametersConfig.getInstance().getThresholdValueFace());
////        faceSearchRequest.setImage("msg://0");
////        message.setBody(faceSearchRequest);
////        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", String.valueOf(System.currentTimeMillis()), multiPartFile(file));
////        return ApiManager.getInstance()
////                .getApi()
////                .advancedFaceSearch(makeMultiPartJson(message), filePart)
////                .doOnNext(messageResponse -> {
////                    if(messageResponse.isSuccessful() && messageResponse.body().getCode() == Message.CODE_SUCCESS){
////                        messageResponse.body().setId(file.getAbsolutePath());
////                    }
////                })
////                .subscribeOn(Schedulers.io());
////    }
//
////    public static Observable<Message<Compare2Response>> imageCompare(Object file1, Object file2){
////        Message<Compare2Request> message = new Message<>();
////        message.setType(REQUEST);
////        message.setName("ImageCompare");
////        Compare2Request compare2Request =new Compare2Request();
////        Compare2Request.Image image1 = new Compare2Request.Image();
////        image1.setImage("msg://0");
////        Compare2Request.Image image2 = new Compare2Request.Image();
////        image1.setImage("msg://1");
////        compare2Request.setImage1(image2);
////        addSession(message);
////        RequestBody body1 = file1 instanceof File? multiPartFile((File)file1) : multiPartFile((byte[])file1);
////        MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("files", String.valueOf(System.currentTimeMillis()), body1);
////        RequestBody body2 = file2 instanceof File? multiPartFile((File)file2) : multiPartFile((byte[])file2);
////        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("files", String.valueOf(System.currentTimeMillis()), body2);
////        return ApiManager.getInstance()
////                .getApi()
////                .imageCompare(makeMultiPartJson(message), filePart1, filePart2)
////                .subscribeOn(Schedulers.io());
////    }
//
//    public static String getFaceSearchImageUrl(String id){
//        if(UserCache.getUser().isVerify()) {
//            return String.format(Locale.ENGLISH, "%simage?type=face_search&id=%s&session=%s", SystemConfig.getInstance().getIpAddress(), id, UserCache.getUser().getSession());
//        }
//
//        return String.format(Locale.ENGLISH, "%simage?type=face_search&id=%s", SystemConfig.getInstance().getIpAddress(), id);
//}
//
////    public static Observable<Message> keepAlive(){
////        Message message = new Message();
////        message.setName("KeepAlive");
////        message.setType(REQUEST);
////        addSession(message);
////        return ApiManager.getInstance()
////                .getApi()
////                .keepAlive(makeJson(message))
////                .subscribeOn(Schedulers.io());
////    }
//}