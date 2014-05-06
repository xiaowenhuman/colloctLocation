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
	 * @param urlPath ����·��
	 * @param params Map��keyΪ���������valueΪ���������ֵ
	 * @param encoding	���뷽ʽ
	 * @return
	 * @throws Exception
	 */
	
	//ͨ��post��������˷������ݣ�����÷������������
	public static String getInputStreamByPost(String urlPath,Map<String,String> params,String encoding) throws Exception{
	     StringBuffer sb = new StringBuffer();  
	        for(Map.Entry<String,String> entry:params.entrySet()){  
	            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encoding));  
	            sb.append("&");  
	        }  
	        String data = sb.deleteCharAt(sb.length()-1).toString(); 

		URL url = new URL(urlPath);
		//������
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		//�����ύ��ʽ
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		//post��ʽ����ʹ�û���
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
		//�������ӳ�ʱʱ��
		conn.setConnectTimeout(6*1000);
		//���ñ������ӵ�Content-Type������Ϊapplication/x-www-form-urlencoded
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    //ά�ֳ�����  
        conn.setRequestProperty("Connection", "Keep-Alive");  
        //�������������  
        conn.setRequestProperty("Charset", "UTF-8"); 
        //�����������������д������
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
        int response = conn.getResponseCode();  //��÷���������Ӧ��
        return "ok";
    } catch (IOException e) {
        e.printStackTrace();
    }
    return "";
	}
	/*
     * Function  :   ��װ��������Ϣ
     * Param     :   params���������ݣ�encode�����ʽ
     * Author    :   ����԰-���ɵ�Ȼ
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //�洢��װ�õ���������Ϣ
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //ɾ������һ��"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
    
    /*
     * Function  :   �������������Ӧ�������������ת�����ַ�����
     * Param     :   inputStream����������Ӧ������
     * Author    :   ����԰-���ɵ�Ȼ
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //�洢������
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
	//ͨ������������ֽ�����
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

