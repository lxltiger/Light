package com.kimascend.light.api;

import android.arch.lifecycle.LiveData;

import com.kimascend.light.clock.ClockList;
import com.kimascend.light.clock.ClockResult;
import com.kimascend.light.device.entity.LampList;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.home.entity.GroupList;
import com.kimascend.light.home.entity.HubList;
import com.kimascend.light.mesh.AddMeshResult;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.mesh.MeshList;
import com.kimascend.light.model.RequestResult;
import com.kimascend.light.model.User;
import com.kimascend.light.scene.AddGroupSceneResult;
import com.kimascend.light.scene.DeviceSetting;
import com.kimascend.light.scene.GroupDevice;
import com.kimascend.light.scene.SceneList;
import com.kimascend.light.user.Profile;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * 注意：如果返回的数据没有观察者 不会发生HTTP请求
 */
public interface KimAscendService {
    @POST("user/regist")
    LiveData<ApiResponse<RequestResult>> regist(@Body RequestBody request);

    @POST("user/validate")
    LiveData<ApiResponse<RequestResult>> getAuthCode(@Body RequestBody request);

    @POST("user/checkAccount")
    LiveData<ApiResponse<RequestResult>>  checkAccount(@Body RequestBody request);

    @POST("user/SMSvalidate")
    LiveData<ApiResponse<RequestResult>> SMSvalidate(@Body RequestBody request);

    @POST("user/login")
    LiveData<ApiResponse<Profile>> login(@Body RequestBody request);

    //
    @POST("user/logout")
    LiveData<ApiResponse<RequestResult>> logout();

    @POST("user/resetPassword")
    LiveData<ApiResponse<RequestResult>> resetpassword(@Body RequestBody request);

    @POST("user/setPassword")
    LiveData<ApiResponse<RequestResult>> setPassword(@Body RequestBody request);

    @POST("mesh/meshList")
    LiveData<ApiResponse<MeshList>> meshList(@Body RequestBody request);

    @POST("mesh/getMeshById")
    LiveData<ApiResponse<DefaultMesh>> meshDetail(@Body RequestBody request);

    @POST("mesh/setDefault")
    LiveData<ApiResponse<RequestResult>> setDefaultMesh(@Body RequestBody request);

    @POST("mesh/shareMesh")
    LiveData<ApiResponse<RequestResult>> shareMesh(@Body RequestBody request);

    @POST("mesh/deleteMesh")
    LiveData<ApiResponse<RequestResult>> deleteMesh(@Body RequestBody request);

    @Multipart
    @POST("mesh/reportBleMesh")
    LiveData<ApiResponse<AddMeshResult>> reportBleMesh(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    //    LiveData<ApiResponse<RequestResult>> reportBleMesh(@Header("accessToken") String authorization, @Part MultipartBody.Part file, @Part("json") RequestBody requestBody);
    @Multipart
    @POST("mesh/updateMesh")
    LiveData<ApiResponse<RequestResult>> upateMesh(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("belMesh/deleteLampMeshByMeshAndUser")
    LiveData<Response<Profile>> deleteLampMeshByMeshAndUser(@Body RequestBody request);



    @POST("device/deviceList")
    LiveData<ApiResponse<LampList>> deviceList(@Body RequestBody request);

    /*添加灯具的时候首先获取灯具的序号，作为mesh address 这样可以保证mesh address全局唯一*/


    @POST("device/getAvailableDeviceId")
    LiveData<ApiResponse<RequestResult>> getDeviceId(@Body RequestBody request);

    //上报灯具
    @POST("device/reportDevice")
    LiveData<ApiResponse<RequestResult>> reportDevice(@Body RequestBody request);


    @POST("gateway/reportLampGateway")
    LiveData<ApiResponse<RequestResult>> reportHub(@Body RequestBody request);


    @POST("gateway/gatewayList")
    LiveData<ApiResponse<HubList>> hubList(@Body RequestBody request);

    //参数 id
    @POST("gateway/deleteGateway")
    LiveData<ApiResponse<RequestResult>> deleteHub(@Body RequestBody request);

    @POST("device/updateDevice")
    LiveData<Response<Profile>> updateDevice(@Body RequestBody request);

    @POST("device/deleteDevice")
    LiveData<ApiResponse<RequestResult>> deleteDevice(@Body RequestBody request);

    /*获取场景列表,场景就是一个灯具组*/
    @POST("group/getGroupsByUserIdOrMesh")
    LiveData<ApiResponse<GroupList>> groupList(@Body RequestBody request);

    @Multipart
    @POST("group/createGroup")
    LiveData<ApiResponse<AddGroupSceneResult>> createGroup(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @Multipart
    @POST("group/updateGroup")
    LiveData<ApiResponse<AddGroupSceneResult>> updateGroup(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("group/updateGroup")
    LiveData<ApiResponse<AddGroupSceneResult>> updateGroup(@QueryMap Map<String, String> map);

    /*删除场景*/
    @POST("group/deleteGroup")
    LiveData<ApiResponse<RequestResult>> deleteGroup(@Body RequestBody request);

    /*添加设备到场景*/
    @POST("group/addDeviceToGroup")
    LiveData<ApiResponse<RequestResult>> addDeviceToGroup(@Body RequestBody request);

    @POST("group/deleteDeviceFromGroup")
    LiveData<ApiResponse<RequestResult>> deleteDeviceFromGroup(@Body RequestBody request);



    /*获取场景详情 用来修改*/
    @POST("group/getGroupById")
    LiveData<ApiResponse<Group>> getGroupById(@Body RequestBody request);

    /*获取场景添加的设备*/
    @POST("group/getDevicesByGroupId")
    LiveData<ApiResponse<GroupDevice>> getDevicesByGroupId(@Body RequestBody request);

    @POST("scene/getSceneList")
    LiveData<ApiResponse<SceneList>> getSceneList(@Body RequestBody request);

    @Multipart
    @POST("scene/createScene")
    LiveData<ApiResponse<AddGroupSceneResult>> createScene(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @Multipart
    @POST("user/updateUser")
    LiveData<ApiResponse<RequestResult>> updateUser(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("user/updateUser")
    LiveData<ApiResponse<RequestResult>> updateUser( @QueryMap Map<String, String> map);

    @POST("user/getUserInfo")
    LiveData<ApiResponse<User>> getUserInfo();

    @Multipart
    @POST("scene/updateScene")
    LiveData<ApiResponse<AddGroupSceneResult>> updateScene(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("scene/updateScene")
    LiveData<ApiResponse<AddGroupSceneResult>> updateScene(@QueryMap Map<String, String> map);

    @POST("scene/deleteScene")
    LiveData<ApiResponse<RequestResult>> deleteScene(@Body RequestBody request);

    @POST("scene/addDeviceToScene")
    LiveData<ApiResponse<RequestResult>> addDeviceToScene(@Body RequestBody request);

    @POST("scene/deleteDeviceFromScene")
    LiveData<ApiResponse<RequestResult>> deleteDeviceFromScene(@Body RequestBody request);

    @POST("scene/addGroupToScene")
    LiveData<ApiResponse<RequestResult>> addGroupToScene(@Body RequestBody request);

    @POST("scene/getDevicesBySceneId")
    LiveData<ApiResponse<GroupDevice>> getDevicesBySceneId(@Body RequestBody request);

    @POST("scene/getGroupsBySceneId")
    LiveData<ApiResponse<GroupList>> getGroupsBySceneId(@Body RequestBody request);

    @POST("device/getDeviceSettingByObjectId")
    LiveData<ApiResponse<List<DeviceSetting>>> getDeviceSettingByObjectId(@Body RequestBody request);

    @POST("device/createDeviceSetting")
    LiveData<ApiResponse<RequestResult>> createDeviceSetting(@Body RequestBody request);

    @POST("clock/getClockList")
    LiveData<ApiResponse<ClockList>> getClockList(@Body RequestBody request);

    @POST("clock/createClock")
    LiveData<ApiResponse<ClockResult>> createClock(@QueryMap Map<String, String> map);

    @POST("clock/updateClock")
    LiveData<ApiResponse<RequestResult>> updateClock(@QueryMap Map<String, String> map);

    @POST("clock/setClockSwitch")
    LiveData<ApiResponse<RequestResult>> switchClock(@Body RequestBody request);


    @POST("clock/deleteClock")
    LiveData<ApiResponse<RequestResult>> deleteClock(@Body RequestBody request);

    @POST("clock/addDeviceToClock")
    LiveData<ApiResponse<RequestResult>> addDeviceToClock(@Body RequestBody request);
//    LiveData<ApiResponse<RequestResult>> addDeviceToClock(@QueryMap Map<String, String> map);


    @POST("clock/deleteDeviceFromClock")
    LiveData<ApiResponse<RequestResult>> deleteDeviceFromClock(@Body RequestBody request);


    @POST("clock/getDevicesByClockId")
    LiveData<ApiResponse<GroupDevice>> getDevicesByClockId(@Body RequestBody request);

    @POST("feedback/createFeedback")
    LiveData<ApiResponse<RequestResult>> feedBack(@Body RequestBody request);
}
