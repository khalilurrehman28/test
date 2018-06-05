package com.application.onlineTestSeries.Network


import com.application.onlineTestSeries.Chapters.Models.ChapterResponse
import com.application.onlineTestSeries.Course.Models.CourseResponse
import com.application.onlineTestSeries.Home.Models.subscriptionResponse
import com.application.onlineTestSeries.Legal_maxim.Models.LeximsResponse
import com.application.onlineTestSeries.PYQPTest.Models.PYQPStates
import com.application.onlineTestSeries.PYQPTest.Models.PYQPTestResponse
import com.application.onlineTestSeries.PYQPTest.Test.Models.PYQP_Response
import com.application.onlineTestSeries.PYQPTest.Test.TestResult.model.GetTestResultResponse
import com.application.onlineTestSeries.PracticeTest.CreateTest.Models.CategoryTest
import com.application.onlineTestSeries.PracticeTest.Models.PracticeTestListResponse
import com.application.onlineTestSeries.PracticeTest.Models.QuestionReportResponse
import com.application.onlineTestSeries.PracticeTest.Models.QuestionUpdateResponse
import com.application.onlineTestSeries.PracticeTest.Models.practiceTestResponse
import com.application.onlineTestSeries.Subscription_package.Models.SubscriptionResponse
import com.application.onlineTestSeries.forgotPassword.model.forgotPasswordResponse
import com.application.onlineTestSeries.landmarkJudgement.models.JudgementResponse
import com.application.onlineTestSeries.login.Models.LoginData
import com.application.onlineTestSeries.profile.model.profileUpdateResponse
import com.application.onlineTestSeries.register.Models.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by khalil on 6/3/18.
 */
interface ApiService {

    @FormUrlEncoded
    @POST("Login_request")
    fun getData(@Field("email") email:String,@Field("password") password:String): Call<LoginData>

    @FormUrlEncoded
    @POST("Register_request")
    fun registerUsers(@Field("name") name:String,@Field("email") email:String,@Field("mobile") mobile:String,@Field("gender") gender:Int,@Field("dob") dob:String,@Field("password") password:String): Call<RegisterResponse>


    @GET("get_bare_India")
    fun getcourseList(): Call<CourseResponse>

    @FormUrlEncoded
    @POST("get_chapter")
    fun get_chapter_list(@Field("COURSE_ID") courseID:String): Call<ChapterResponse>

    @GET("get_legal_maxim")
    fun get_legal_maxim(): Call<LeximsResponse>

    @FormUrlEncoded
    @POST("get_allquestion")
    fun get_pratice_question(@Field("USER_ID") userID: Int, @Field("TEST_ID") testID9: String, @Field("SUBSCRIPTION_FLAG") flag: Int, @Field("maxquestion") maxquestion: String, @Field("STATUS") usersStat: Boolean): Call<practiceTestResponse>

    @FormUrlEncoded
    @POST("get_add_question")
    fun getAddQuestion(@Field("USER_ID") userID:Int,@Field("answer") answer:String,@Field("questionid") questionid:String,@Field("testid") testID:Int): Call<QuestionUpdateResponse>

    @FormUrlEncoded
    @POST("get_judgement")
    fun get_judgement(@Field("page_no") page_no:Int): Call<JudgementResponse>

    @FormUrlEncoded
    @POST("get_search_judgement")
    fun searchJudgement(@Field("DATE") date:String,@Field("CASE_NO") caseno:String): Call<JudgementResponse>

    @FormUrlEncoded
    @POST("empty_records_usertest")
    fun empty_records_usertest(@Field("USER_ID") userID:Int): Call<QuestionUpdateResponse>

    @FormUrlEncoded
    @POST("updatepassword")
    fun updatepassword(@Field("USER_EMAIL") USER_EMAIL:String,@Field("PASSWORD") PASSWORD:String,@Field("KEY") questionid:Int): Call<forgotPasswordResponse>

    @FormUrlEncoded
    @POST("forgetpassword")
    fun forgetpassword(@Field("USER_EMAIL") USER_EMAIL:String): Call<forgotPasswordResponse>

    @FormUrlEncoded
    @POST("getpreviousyearquestionpaper")
    fun getpreviousyearquestionpaper(@Field("USER_ID") userID:String,@Field("STATE_ID") stateID:String): Call<PYQPTestResponse>

    @FormUrlEncoded
    @POST("profile_update")
    fun profile_update(@Field("USER_NAME") name:String,@Field("USER_EMAIL") email:String,@Field("USER_PHONE") USER_PHONE:String,@Field("USER_DOB") dob:String,@Field("USER_ID") userID: Int): Call<profileUpdateResponse>

    @Multipart
    @POST("profile_image_update")
    fun profile_image_update(@Part("USER_ID") user_id: Int, @Part file: MultipartBody.Part): Call<profileUpdateResponse>

    @FormUrlEncoded
    @POST("get_test_pyqp")
    fun get_test_pyqp(@Field("PYQP_TEST_ID") testID:String): Call<PYQP_Response>

    @FormUrlEncoded
    @POST("add_result_pyqp")
    fun add_result_pyqp(
            @Field("USER_ID") userID: String,
            @Field("TEST_ID") testID: String,
            @Field("CORRECT_ANSWER") correctAns: String,
            @Field("TOTAL_QUESTION") totalAns: String,
            @Field("WRONG_ANSWER") wrongAns: String,
            @Field("ATTEMPTED_ANSWER") AttempedAns: String,
            @Field("TIME_TAKEN") second: Long
    ): Call<PYQP_Response>

    @FormUrlEncoded
    @POST("get_submit_pyqp_answer")
    fun get_submit_pyqp_answer(
            @Field("USER_ID") userID:String,
            @Field("TEST_ID") testID:String
    ): Call<practiceTestResponse>

    @FormUrlEncoded
    @POST("get_final_result")
    fun get_final_result(@Field("USER_ID") user_id:Int,@Field("TEST_ID") test_id:String): Call<GetTestResultResponse>

    /*   @FormUrlEncoded
       @POST("get_all_pt")
       fun get_all_practice_test(@Field("USER_ID") user_id:Int): Call<PracticeTestListResponse>
   */
    @FormUrlEncoded
    @POST("getmcqtest")
    fun get_all_practice_test(@Field("USER_ID") user_id: Int, @Field("page_no") counter: Int): Call<PracticeTestListResponse>


    @FormUrlEncoded
    @POST("add_remarks")
    fun add_remarks(@Field("USER_ID") user_id:Int,@Field("QUESTION_ID") questionID:String,@Field("REMARK") REMARK:String): Call<QuestionReportResponse>

    @FormUrlEncoded
    @POST("reset_test")
    fun resetTest(@Field("USER_ID") userID:String,@Field("TEST_ID") testID:String): Call<QuestionUpdateResponse>

    @GET("get_question_category")
    fun get_question_category(): Call<CategoryTest>

    @FormUrlEncoded
    @POST("test_random_create_request")
    fun topic_question_test(@Field("topic_question") cateList: String, @Field("USER_ID") userID:String,@Field("testname") testName:String): Call<QuestionUpdateResponse>

    @FormUrlEncoded
    @POST("getsubscriptionpackage")
    fun getsubscriptionpackage(@Field("USER_ID") userID:String): Call<SubscriptionResponse>

    @FormUrlEncoded
    @POST("generatehashkey")
    fun generateHash(@Field("key_data") keydata:String): Call<QuestionUpdateResponse>

    @FormUrlEncoded
    @POST("deleteTest")
    fun deleteTest(@Field("TEST_ID") tesetID:String,@Field("USER_ID") userID:String): Call<QuestionUpdateResponse>

    @FormUrlEncoded
    @POST("getUserSubscription")
    fun getUserSubscription(@Field("USER_ID") userID:String): Call<subscriptionResponse>

    /*HASH,MERCHANT_TRANSACTION_ID,RESPONSE,MERCHANT_RESPONSE,TRANSACTION_ID,USER_ID,STATUS*/
    @FormUrlEncoded
    @POST("updateusersubscription")
    fun updateusersubscription(@Field("USER_ID") userID:String,@Field("STATUS") TRANSACTION_STATUS:String,@Field("RESPONSE") TRANSACTION_RESPONSE:String,@Field("MERCHANT_RESPONSE") TRANSACTION_MERCHANT_RESPONSE:String,@Field("HASH") hash:String,@Field("TRANSACTION_ID") TRANSACTION_ID:String): Call<subscriptionResponse>

    @GET("get_subscription")
    fun getSubscriptionData():Call<SubscriptionResponse>

    @GET("getpyqpstate")
    fun getpyqpstate():Call<PYQPStates>

    @FormUrlEncoded
    @POST("testpaperpurchase")
    fun testpaperpurchase(@Field("USER_ID") userID: String, @Field("STATUS") TRANSACTION_STATUS: String, @Field("RESPONSE") TRANSACTION_RESPONSE: String, @Field("MERCHANT_RESPONSE") TRANSACTION_MERCHANT_RESPONSE: String, @Field("HASH") hash: String, @Field("TRANSACTION_ID") TRANSACTION_ID: String, @Field("TEST_ID") TEST_ID: String, @Field("TEST_PRICE") TEST_PRICE: String, @Field("TEST_TYPE") TEST_TYPE: String): Call<subscriptionResponse>
}

