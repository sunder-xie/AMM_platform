package com.amm.gps;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Badger on 16/9/18.
 */
public class WebRequest {
    String apiUrl = "http://api.map.baidu.com/geoconv/v1/";
    String coordStr = "";
    String accessKey = "Lf4vzvGaMijyBRRRofgZLNRIPzQqM4ac";
    String param;

    private JsonParser jsonParser;
    JsonArray resultArray;

    public JsonArray getResultList(String cordStr) {
        resultArray = null;

        if (cordStr.length() > 0 && cordStr.charAt(cordStr.length() - 1) == ';') {
            cordStr = cordStr.substring(0, cordStr.length() - 2);
        }

        param = "coords=" + cordStr + "&from=1&to=5&ak=" + accessKey;
        String resultStr = sendGet(apiUrl, param);
        jsonParser = new JsonParser();
        try {
            JsonObject jsonObject = (JsonObject) jsonParser.parse(resultStr);
            int status = jsonObject.get("status").getAsInt();
            resultArray = jsonObject.get("result").getAsJsonArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultArray;
    }

    public static List<GpsResultDetail> getGpsFixed(String newCoordStr) {

//        String newCoordStr = lng + "," + lat;
//        newCoordStr = "114.21892734521,29.575429778924;114.21892734521,29.575429778924";

        String param = "coords=" + newCoordStr + "&from=1&to=5&ak=" + "Lf4vzvGaMijyBRRRofgZLNRIPzQqM4ac";
        String result = WebRequest.sendGet("http://api.map.baidu.com/geoconv/v1/", param);
        System.out.println(result);
        ConvertResult convertResult = new ConvertResult();

        ArrayList<GpsResultDetail> list = convertResult.getObj(result);



//        gpsPoint[0] = new Double(result.substring(result.indexOf("\"x\":") + 4, result.indexOf(",\"y\"")));
//        gpsPoint[1] = new Double(result.substring(result.indexOf(",\"y\":") + 5, result.indexOf("}]}")));


        return list;
    }

    public WebRequest() {
        coordStr = "114.21892734521,29.575429778924";
    }

    public WebRequest(String coordStr) {
        this.coordStr = coordStr;
    }

//    public static void main(String[] args) {
////        new WebRequest().convert();
//        //11735.82821,3402.19032
//        Double[] bigDecimals = getGpsFixed(117.592168, 34.036599);
//        System.out.println(bigDecimals[0]);
//        System.out.println(bigDecimals[1]);
//    }


    public void convert() {

        if (coordStr.length() > 0 && coordStr.charAt(coordStr.length() - 1) == ';') {
            coordStr = coordStr.substring(0, coordStr.length() - 2);
        }

        param = "coords=" + coordStr + "&from=1&to=5&ak=" + accessKey;
        String msg = sendGet(apiUrl, param);
        System.out.println(msg);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;

            System.out.println(urlNameString);

            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }
}