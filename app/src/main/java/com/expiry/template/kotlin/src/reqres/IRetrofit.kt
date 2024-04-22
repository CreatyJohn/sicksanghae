package com.expiry.template.kotlin.src.reqres//package com.expiry.template.kotlin.src.reqres

import com.expiry.template.kotlin.util.API
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


/** <레트로핏2 모듈 사용(GSON형식으로 파싱), HTTP로 통신> */
interface IRetrofit {

    /** GET */
    // 회원 조회
    @GET(API.USERS)
    fun getUsers(
        @Header ("X-ACCESS-TOKEN") jwt: String
    ) : Flowable<ResUser>

    // 특정 회원 조회
    @GET(API.GET_SOME_USERS)
    fun getSomeUsers(
        @Query("name") name: String
    ) : Flowable<ResAllUsers>

    // 모든 회원 조회
    @GET(API.GET_ALL_USERS)
    fun getAllUsers() : Flowable<ResAllUsers>

    // 그룹 참여된 회원 조회
    @GET(API.GET_GROUP_IN_USER)
    fun getGroupInUsers(
        @Query("fridgeId") fridgeId: Int
    ) : Flowable<ResAllUsers>

    // 그룹 참여 안된 회원 조회
    @GET(API.GET_GROUP_NOT_IN_USER)
    fun getGroupNotInUsers(
        @Query("fridgeId") fridgeId: Int
    ) : Flowable<ResAllUsers>
    
    // 전체 냉장고 조회
    @GET(API.GET_ALL_REFRIS)
    fun getRefrisList(
        @Header ("X-ACCESS-TOKEN") jwt: String
    ) : Flowable<ResAllRefrigerator>

    // 전체 식품 조회
    @GET(API.GET_ALL_EATS)
    fun getFoodsList(
        @Path ("refriId") refriId: Int
    ) : Flowable<ResGetAllFoods>

    // 식품 1개 조회
    @GET(API.GET_FOODS)
    fun getFood(
        @Query("productId") productId: Int
    ) : Flowable<ResProductId>

    // 식품에 달린 댓글 전체 조회
    @GET(API.GET_FOODS_COMMENTS)
    fun getFoodsComment(
        @Path ("productId") productId: Int
    ) : Flowable<ResFoodsComment>


    /** POST */
    // 카카오 로그인 (JWT res가 목적)
    @POST(API.POST_SOCIAL_LOGIN)
    fun postSocialLogin(
        @Body token: Token
    ) : Flowable<ResultToken>

    // 냉장고 생성
    @POST(API.POST_REFRIS)
    fun postRefrigerator(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Path("name") name: String
    ) : Flowable<PostRefrigerator>

    // 식품 등록
    @Multipart
    @POST(API.POST_FOODS)
    fun postFoods(
        @Part file: MultipartBody.Part, // MultipartFile을 MultipartBody.Part로 변환해야 합니다.
        @Part("product") product: RequestBody // Product 객체를 RequestBody로 변환해야 할 수도 있습니다.
    ) : Flowable<ResPostFoods>

    // 식품에 댓글 작성
    @POST(API.POST_FOODS_COMMENTS)
    fun postfoodsComments(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Body fCommentBody: FCommentBody
    ) : Flowable<ResponseParameters>
    
    //냉장고 그룹 초대
    @POST(API.POST_INVITE_GROUP)
    fun postInviteGroup(
        @Path("refriId") refriId: Int,
        @Path("userId") userId: Int
    ) : Flowable<ResPatchRefrig>


    /** PATCH */
    // 사용자 이미지(추가) 및 이름 업로드
    @Multipart
    @PATCH(API.PATCH_USERS_UPLOAD)
    fun uploadUsersInfo(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Part file: MultipartBody.Part, // MultipartFile을 MultipartBody.Part로 변환해야 합니다.
        @Part ("name") name: RequestBody // Product 객체를 RequestBody로 변환해야 할 수도 있습니다.
    ) : Flowable<ByeUsers>

    //로그아웃
    @PATCH(API.PATCH_USERS_LOGOUT)
    fun logoutUsers(
        @Header ("X-ACCESS-TOKEN") jwt: String
    ) : Flowable<ByeUsers>

    //냉장고 정보 수정
    @PATCH(API.PATCH_REFRIG)
    fun patchRefrige(
        @Path("name") name: String,
        @Path("refriId") refriId: Int
    ) : Flowable<ResPatchRefrig>

    // 닉네임 설정
    @PATCH(API.PATCH_NICKNAME)
    fun patchNickname(
        @Header("X-ACCESS-TOKEN") jwt: String,
        @Path("name") name: String
    ) : Flowable<ResponseParameters>

    /** DELETE */
    //식품 삭제
    @DELETE(API.DELETE_FOODS)
    fun deleteFoods(
        @Path("productId") productId: Int
    ) : Flowable<ResponseParameters>

    // 회원탈퇴
    @DELETE(API.USERS)
    fun deleteUsers(
        @Header ("X-ACCESS-TOKEN") jwt: String
    ) : Flowable<ByeUsers>

    //냉장고 삭제
    @DELETE(API.DELETE_REFRIG)
    fun deleteRefrige(
        @Path("refriId") refriId: Int
    ) : Flowable<ResponseParameters>

    //냉장고 그룹 탈퇴(추방)
    @DELETE(API.DELETE_GROUP)
    fun exileGroup(
        @Path("refriId") refriId: Int,
        @Path("userId") userId: Int
    ) : Flowable<ResponseParameters>
}

interface IRetrofitFCM {
    @POST(API.SEND_FCM)
    fun postFcmTest(
        @Header("Authorization") authorization: String,
        @Body datas: Datas
    ) : Flowable<FcmTest>
}