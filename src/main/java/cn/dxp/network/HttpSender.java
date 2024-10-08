package cn.dxp.network;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.dxp.*;

public class HttpSender extends Thread{
    static final String serverip = "http://10.100.233.123:8080";

    public PhysicalRealTimeRouteFinder p;
    public ArrayList<HashMap<String, Object>> dataQueue = new ArrayList<HashMap<String, Object>>();

    public HttpSender(PhysicalRealTimeRouteFinder prtrf){
        this.p = prtrf;
    }

    @Override
    public void run(){
        while(true){
            try{
            //sleep(500);
                if(this.dataQueue.size() != 0){
                    System.out.println(doPost(this.dataQueue.get(0)));
                    this.dataQueue.remove(0);
                }else{
                    sleep(500);
                }
            }catch (InterruptedException e){
                e.getStackTrace();
                this.p.resetHttpSender();
            }
        }
    }

    public void addData(final String s){
        if(s.isEmpty()){
            return;
        }
        this.dataQueue.add(new HashMap<String, Object>(){
            {
                put("json", s);
            }
        });
    }

    public void addData(HashMap<String, Object> d){
        this.dataQueue.add(d);
    }

    public static String doGet(){
        String url = serverip;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try{
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        finally{
            // 关闭资源
            if(null != response){
                try{
                    response.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(null != httpClient){
                try{
                    httpClient.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String doPost(Map<String, Object> paramMap){
        String url = serverip;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        // 封装post请求参数
        if(null != paramMap && paramMap.size() > 0){
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
            while(iterator.hasNext()){
                Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
            }

            // 为httpPost设置封装好的请求参数
            try{
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        try{
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        finally{
            // 关闭资源
            if(null != httpResponse){
                try{
                    httpResponse.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(null != httpClient){
                try{
                    httpClient.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
