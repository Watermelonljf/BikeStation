package com.bicycle.watermelon_r.bicycle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.bicycle.watermelon.utils.PermissionUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
     //地图组件
    MapView mMapView = null;
    BaiduMap map = null;
    //定位
    public MyLocationListenner myListener = null;
    public LocationClient mLocationClient = null;
    boolean isFirstLoc = true;       // 是否首次定位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        getPersimmions();
        initMap();

    }


    /**
     * 初始化
     */
    protected void initMap(){
        mMapView = (MapView) findViewById(R.id.bmapView);
        initLocation();
    }

    /**
     * 设置定位服务，在Oncreate调用
     */
    protected void initLocation(){
        map = mMapView.getMap();
        map.setTrafficEnabled(true);						//打开交通图
        map.setMyLocationEnabled(true);                     //开启定位图层

        mLocationClient = new LocationClient(this);     //声明LocationClient类
        myListener = new MyLocationListenner();  //监听
        mLocationClient.registerLocationListener(myListener);    //注册监听函
        LocationClientOption option = new LocationClientOption();
        //funciton:设置高精度定位，及网络定位和GPS定位，优先返回高精度定位的结果
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000); //重新定位时间长度ms,若位置没变则返回上一次结果，
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//开启GPS
        option.disableCache(false);//开启缓存定位
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }



    /**
     * 定位监听函数，异步获取定位结果
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            map.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                //经纬度
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                Log.e("lat++++++++++++++",location.getLatitude()+"");
                Log.e("long++++++++++++++++",location.getLongitude()+"");
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

    }

    //准备申请的用户权限
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode==SDK_PERMISSION_REQUEST){
            if (PermissionUtil.verifyPermissions(grantResults)) {

                initMap();

            } else {


            }


        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
