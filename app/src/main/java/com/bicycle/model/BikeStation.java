package com.bicycle.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Watermelon_R on 2016/12/10.
 */

public class BikeStation {
    //根据调试发现该网站有后台接口返回json数据
    private String url = "http://218.93.33.59:85/map/wzmap/ibikestation.asp?id=";

    private int staId;					//站点Id
    private String stationName;		//站点名字
    private double  latitude;       //站点维度
    private double  longitude;		//站点纬度
    private int capacity;			//站点可借总数
    private int availBike;			//站点可借数量
    private String address;			//站点地址名称

    public BikeStation(int staId) {
        this.staId = staId;
        //获取数据并解析保存
        //http://blog.csdn.net/iijse/article/details/6201101
        BufferedReader in = null;
        String result="";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 建立实际的连接
            conn.connect();
            // 获取所有响应头字段
            //Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            /*for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }*/
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            //保存一行
            String line;
            while ((line = in.readLine()) != null) {
                //换行添加
                result += "/n" + line;
            }
            //解析数据http://blog.csdn.net/wenzhi20102321/article/details/53185805
            //JSONTokener这个是系统为JSONObject和JSONArray构造器解析JSON source string的类，它可以从source string中提取数值信息。
            Log.e("result++++++++++++",result);

        } catch (Exception e) {
         System.out.println("get异常："+e);
            e.printStackTrace();

        } // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getStaId() {
        return staId;
    }

    public void setStaId(int staId) {
        this.staId = staId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailBike() {
        return availBike;
    }

    public void setAvailBike(int availBike) {
        this.availBike = availBike;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
