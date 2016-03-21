package edu.tcu.mi.info.system.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpIOCommon {


	public static InputStream post(String url, Map<String, String> map, CloseableHttpClient client, HttpClientContext context){
		try{
			List <NameValuePair> nvp = new ArrayList <NameValuePair>(); //初始化參數
			Set<String> keys = map.keySet();
			for(String key : keys){
				nvp.add(new BasicNameValuePair(key, map.get(key)));
			}
			HttpPost httpPost = new HttpPost(); //初始化Post請求
			URI uri = new URI(url); //創建URI Object, 賦予網站登錄的URL
			httpPost.setURI(uri); //設定Post請求所需的URI
			httpPost.setHeader("Content-Type","application/x-www-form-urlencoded; charset=big5"); //設定Post請求的Header 
			httpPost.setEntity(new UrlEncodedFormEntity(nvp,"big5")); //設定Post請求的Body的格式
			HttpResponse httpResponse = client.execute(httpPost, context); //執行Http的Post動作
			InputStream is = httpResponse.getEntity().getContent();
			return is;
		} catch(URISyntaxException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream get(String url, CloseableHttpClient client, HttpClientContext context) {
		try{
			HttpGet httpGet = new HttpGet(); //初始化Post請求
			URI uri = new URI(url); //創建URI Object, 賦予網站登錄的URL
			httpGet.setURI(uri); //設定Post請求所需的URI
			httpGet.setHeader("Content-Type","application/x-www-form-urlencoded; charset=big5"); //設定Post請求的Header 
			HttpResponse httpResponse = client.execute(httpGet, context); //執行Http的Post動作
			InputStream is = httpResponse.getEntity().getContent();
			return is;
		} catch(URISyntaxException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
