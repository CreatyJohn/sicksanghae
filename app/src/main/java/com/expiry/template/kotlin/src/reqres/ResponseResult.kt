package com.expiry.template.kotlin.src.reqres

import com.expiry.template.kotlin.util.JWT
import com.google.gson.annotations.SerializedName
import retrofit2.http.Url
import java.sql.Timestamp

/** 모든 냉장고 조회 */
data class ResAllRefrigerator (
    @SerializedName("isSuccess")
    val isSuccess : Boolean,
    @SerializedName("code")
    val code : Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: List<GetAllRefrigerator>
)

data class GetAllRefrigerator (
    @SerializedName("ownerFg")
    val ownerFg: Int,
    @SerializedName("refrigeratorId")
    val refrigeratorId : Int,
    @SerializedName("refrigeratorName")
    val refrigeratorName: String
)

/** 모든 식품 조회 */
data class ResGetAllFoods (
    val isSuccess : Boolean,
    val code : Int,
    val message : String,
    val result : List<Foodsresult>
)

data class Foodsresult (
    val productId : Int,
    val productName : String,
    val productImg : String,
    val date : String,
    val description : String
)

/** 냉장고 정보 수정 */
data class ResPatchRefrig(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: String
)

/** 식품 등록 */
// 멀티파트를 위해 데이터 직렬화로 이름 지정하기
data class PostFoods(
    @SerializedName("productName") val productName: String,
    @SerializedName("date") val date: String,
    @SerializedName("description") val description: String,
    @SerializedName("refrigeratorId") val fridgeId: Int
)

data class ResPostFoods(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)

data class ResultObj(
    val productId: Int
)

/** 식품 수정 */
data class PatchEats(
    val productId: Int,
    val productName: String,
    val productImg: String,
    val date: String,
    val description: String
)

data class ResPatchEats(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ResultObj2
)

data class ResultObj2(
    val productId: Int,
    val productName: String,
    val productImg: String,
    val date: String,
    val description: String
)

/** 특정 회원조회 API */
// res
data class GetUserData (
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: GetUserDataResult
)

data class GetUserDataResult(
    val auth: Int,
    val safeCnt: Int,
    val createdAt: String,
    val followerCnt: Int,
    val followingCnt: Int,
    val name: String,
    val profileImg: Any,
    val rate: Double,
    val sellCnt: Int,
    val userIdx: Int,
    val introduce: String
)

/** 모든 카테고리 조회 API */
// res
data class GetCategoriesAPI (
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<GetCategoriesDataResult>
)

data class GetCategoriesDataResult(
    val mainCategory: Int,
    val mainCategoryName: String,
    val middleCategory: Int,
    val middleCategoryName: String,
    val subCategory: Int,
    val subCategoryName: String
)

/** 특정 회원에 대한 후기 조회 API */
// res
data class GetReviewData (
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<GetReviewDataResult>
)

data class GetReviewDataResult(
    val reviewIdx: Int,
    val rate: Double,
    val contents: String,
    val title: String,
    val name: String,
    val reviewerIdx: Int,
    val createdAt: Timestamp,
    val revieweeIdx: Int,
    val productIdx: Int
)

/** 특정 카테고리로 조회 API */
// res
data class GetCategoriesNumAPI (
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<GetCategoriesResult>
)

data class GetCategoriesResult(
    val productIdx: Int,
    val createdAt: Timestamp,
    val imageUrl: String,
    val title: String,
    val location: String,
    val price: String,
    val tradeStatus: String,
    val isFreeShip: Boolean,
    val isFav: Boolean,
    val isSafepay: Boolean
)

/** 특정 상품의 이미지 조회 API */
// res
data class GetGoodsImageAPI (
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: GetGoodsImageResult
)

data class GetGoodsImageResult (
    val productIdx: Int,
    val imageList: List<String>
)

/** 상품등록 API */
// req
data class PostGoodsData(
    val title: String,
    val price: String,
    val contents: String,
    val isSafepay: Boolean,
    val imageList: List<String>,
    val userIdx: Int,
    val mainCategoryIdx: Int,
    val middleCategoryIdx: Int,
    val subCategoryIdx: Int
)

// res
data class RequestPostGoods(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ResultReqPostGoods
)

data class ResultReqPostGoods(
    val productIdx: Int
)

/** 상품 찜하기 & 찜해제 API */
data class PostFavorite(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<ResultFavorite>
)

data class ResultFavorite(
    val favoriteIdx: Int
)

/** 상품조회 API */
data class GetGoodsData(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: List<ResultGoods>
)

data class ResultGoods(
    val productIdx: Int,
    val imageUrl: String,
    val isSafepay: Boolean,
    val title: String,
    val price: String,
    val location: String,
    val contents: String,
    val createdAt: Timestamp,
    val favCnt: Int,
    val isFav: Boolean
)

/** 상품조회 API */
data class GetSpecialGoodsData(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: ResultGetSpecialGoods
)

data class ResultGetSpecialGoods(
    val productIdx: Int,
    val price: String,
    val title: String,
    val location: String,
    val createdAt: Timestamp,
    val productStatus: String,
    val quantity: Int,
    val favCnt: Int,
    val chatCnt: Int,
    val contents: String,
    val isFreeShip: Boolean,
    val isChangable: Boolean
)

/** 액세스 토큰 API */
data class Token(
    @SerializedName("authFg")
    val authFg: String,
    @SerializedName("deviceToken")
    val deviceToken: String,
    @SerializedName("token")
    val token: String
)

/** 카카오 로그인 API */
data class ResultToken(
    @SerializedName("code")
    val code : Int,
    @SerializedName("isSuccess")
    val isSuccess : Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result : Result
)

data class Result(
    @SerializedName("jwt")
    val jwt: String,
    @SerializedName("loginInfo")
    val loginInfo: String,
    @SerializedName("userId")
    val userId: Int
)

/** 회원 정보 API */
data class ResUser(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: UsersInfo
)

data class UsersInfo(
    @SerializedName("deviceToken") val deviceToken:String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("profileImg") val profileImg: String,
    @SerializedName("userId") val userId: Int
)

/** 전체 회원 API */
data class ResAllUsers(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<AllUsersInfo>
)

data class AllUsersInfo(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("profileImg") val profileImg: String,
    @SerializedName("userId") val userId: Int
)

/** 특정 삭제 및 로그아웃 res */
data class ByeUsers(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: String
)

/** 소개글 API */
data class Introduce(
    val introduce: String
)

/** 상품 내용 수정 API */
data class EditProducts(
    val quantity: Int,
    val mainCategory: Int,
    val middleCategory: Int,
    val subCategory: Int,
    val title: String,
    val price: String,
    val location: String,
    val contents: String,
    val productStatus: String,
    val isChangable: Boolean,
    val isFreeShip: Boolean,
    val isSafePay: Boolean
)

/** 토큰삽입 API */
data class PostRefrigerator(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Int
)

/** Bodies */
data class Tokens(
    val token: String
)

data class RefrigeratorName(
    val refrigeratorName: String,
    val userId: Int
)

/** 식품 한개 조회 */
data class ResProductId(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ResResulT
)

data class ResResulT(
    val productId: Int,
    val productName: String,
    val productImg: String,
    val date: String,
    val description: String
)

/** 식품에 달린 댓글 전체 조회 */
data class ResFoodsComment(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<ResFoodsCommentResult>
)

data class ResFoodsCommentResult(
    val commentId: Int,
    val productId: Int,
    val comment: String,
    val userId: Int,
    val name: String
)

/** 식품에 댓글 달기 */
data class FCommentBody(
    val productId: Int,
    val comment: String
)

/** 가장 기본적 ResponseParameters */
data class ResponseParameters(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: String
)

/** FCM test Model */
data class FcmTest(
    @SerializedName("multicast_id")
    val multicast_id: Long,
    @SerializedName("success")
    val success: Int,
    @SerializedName("failure")
    val failure: Int,
    @SerializedName("canonical_ids")
    val canonical_ids: Int,
    @SerializedName("results")
    val results: List<FcmTestResult>
)

data class FcmTestResult(
    @SerializedName("message_id")
    val message_id: String?,
    @SerializedName("error")
    val error: String?
)

/** Fcm 파라미터 Body Data */
data class Datas(
    val to: String,
    val priority: String,
    val data: Data
)

data class Data(
    val title: String,
    val body: String
)