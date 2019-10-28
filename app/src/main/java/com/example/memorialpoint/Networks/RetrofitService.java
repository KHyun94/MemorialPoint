package com.example.memorialpoint.Networks;

import com.example.memorialpoint.Models.Comments;
import com.example.memorialpoint.Models.Like;
import com.example.memorialpoint.Models.PlaceResponse;
import com.example.memorialpoint.Models.PostData;
import com.example.memorialpoint.Models.ProfileImage;
import com.example.memorialpoint.Models.ResponseData;
import com.example.memorialpoint.Models.RoomData;
import com.example.memorialpoint.Models.UserData;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {

    //SignIn.class 로그인 체크
    @FormUrlEncoded
    @POST("/memorial_point/PHPs/loginUser.php")
    Call<Integer> getLogin(@FieldMap Map<String, String> option);

    @FormUrlEncoded
    @POST("/memorial_point/PHPs/emailAuthentication.php")
    Call<List<ResponseData>> getAuthenticationEmail(@Field("email") String email);

    //SignUp-Fragment2 회원가입 아이디 중복
    @FormUrlEncoded
    @POST("/memorial_point/PHPs/duplicate_ID.php")
    Call<List<ResponseData>> getDuplicateId(@Field("id") String id);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/insertUser.php")
    Call<UserData> setUserData(@FieldMap Map<String, String> option);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/user.php")
    Call<UserData> getUser(@Field("id") String id);

    @Multipart
    @POST("memorial_point/PHPs/upload_profile.php")
    Call<ResponseData> uploadImage(@Part("id") String id, @Part MultipartBody.Part File);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/download_profile.php")
    Call<ProfileImage> getProfile(@Field("id") String id);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/search_place.php")
    Call<PlaceResponse> getSearch(@Field("where") String where, @Field("location") String location);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/upload_marker_str.php")
    Call<List<PostData>> uploadMarkerStr(@Field("writer") String writer, @Field("uri") String uri,
                                         @Field("lat") double lat, @Field("lng") double lng,
                                         @Field("cAddress") String cAddress, @Field("dAddress") String dAddress,
                                         @Field("contents") String contents, @Field("friend") String friend,
                                         @Field("hashTag") String hashTag, @Field("colorNum") int colorNum,
                                         @Field("isShared") boolean isShared);

    @Multipart
    @POST("memorial_point/PHPs/upload_marker_uri.php")
    Call<List<PostData>> uploadMarkerUri(@Part("writer") String writer, @Part MultipartBody.Part File,
                                         @Part("lat") double lat, @Part("lng") double lng,
                                         @Part("cAddress") String cAddress, @Part("dAddress") String dAddress,
                                         @Part("contents") String contents, @Part("friend") String friend,
                                         @Part("hashTag") String hashTag, @Part("colorNum") int colorNum,
                                         @Part("isShared") boolean isShared);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_marker.php")
    Call<List<PostData>> loadMarker(@Field("writer") String writer);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_global_post.php")
    Call<List<PostData>> loadGlobalPost(@Field("id") String id, @Field("last") String last, @Field("count") int count);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/refresh_post.php")
    Call<List<PostData>> onPullToRefresh(@Field("no") int no);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_group_comments.php")
    Call<List<Comments>> loadGroupComments(@Field("post") int post);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_child_comments.php")
    Call<List<Comments>> loadChildComments(@Field("post") int post, @Field("group") int group);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/upload_comments.php")
    Call<Comments> uploadComments(@Field("post") int post, @Field("group") int group, @Field("id") String id, @Field("comments") String comments);

    //채팅 대기방을 받아오는 인터페이스
    @POST("memorial_point/PHPs/room.php")
    Call<List<RoomData>> getRooms();

    //채팅방 만드는 인터페이스
    @FormUrlEncoded
    @POST("memorial_point/PHPs/create_room.php")
    Call<RoomData> createRooms(@Field("r_name") String r_name, @Field("r_host") String r_host);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/remove_room.php")
    Call<Void> removeRooms(@Field("r_no") int r_no);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_my_post.php")
    Call<List<PostData>> loadMyPosts(@Field("writer") String writer);


    //My Page에서 누른 포스트의 값만 가져온다.
    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_one_post.php")
    Call<PostData> loadOnePost(@Field("no") int no);

    //FavoritesFragment에서 내가 좋아요를 누른 포스트만 가져온다.
    @FormUrlEncoded
    @POST("memorial_point/PHPs/load_favorites_post.php")
    Call<List<PostData>> loadFavoritesPost(@Field("user_id") String id,@Field("last") String last, @Field("count") int count);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/like.php")
    Call<ResponseData> getLike(@Field("req_data") int req, @Field("user_id") String userId, @Field("post_no") int postNo);

    @FormUrlEncoded
    @POST("memorial_point/PHPs/remove_post.php")
    Call<ResponseData> removePost(@Field("post_no") int postNo);

}
