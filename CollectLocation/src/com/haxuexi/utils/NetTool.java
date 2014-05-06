package com.haxuexi.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class NetTool {
	
	/**
	 * 
	 * @param urlPath 请求路径
	 * @param params Map中key为请求参数，value为请求参数的值
	 * @param encoding	编码方式
	 * @return
	 * @throws Exception
	 */
	
	//通过post向服务器端发送数据，并获得服务器端输出流
	public static String getInputStreamByPost(String urlPath,Map<String,String> params,String encoding) throws Exception{
	     StringBuffer sb = new StringBuffer();  
	        for(Map.Entry<String,String> entry:params.entrySet()){  
	            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encoding));  
	            sb.append("&");  
	        }  
	        String data = sb.deleteCharAt(sb.length()-1).toString(); 

		URL url = new URL(urlPath);
		//打开连接
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		//设置提交方式
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		//post方式不能使用缓存
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
		//设置连接超时时间
		conn.setConnectTimeout(6*1000);
		//配置本次连接的Content-Type，配置为application/x-www-form-urlencoded
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    //维持长连接  
        conn.setRequestProperty("Connection", "Keep-Alive");  
        //设置浏览器编码  
        conn.setRequestProperty("Charset", "UTF-8"); 
        //获得输出流，向服务器写入数据
        OutputStream outputStream = null;
		try {
			outputStream = conn.getOutputStream();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
//        outputStream.write(data);
		 DataOutputStream dos = new DataOutputStream(outputStream);
		  dos.writeBytes(data);  
	        dos.flush();  
	        dos.close();  
        int response = conn.getResponseCode();  //获得服务器的响应码
        return "ok";
    } catch (IOException e) {
        e.printStackTrace();
    }
    return "";
	}
	/*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     * Author    :   博客园-依旧淡然
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
    
    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     * Author    :   博客园-依旧淡然
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());    
        return resultData;
    }
	//通过输入流获得字节数组
	public static byte[] readStream(InputStream is) throws Exception {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		while((len=is.read(buffer)) != -1){
			bos.write(buffer, 0, len);
		} 
		is.close();
		return bos.toByteArray();
	}
	
}

