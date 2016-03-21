package edu.tcu.mi.info.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import edu.tcu.mi.info.system.io.HttpIOCommon;

public class AppTest {
	private Properties properties;
	
	@Before
	public void init(){
	}
	
	@Test
	public void test02() throws IOException{
		String url = "http://aap.tcu.edu.tw/PO_Vacation/Remote_Sign_In_Out/rr.asp";
		CookieStore cookieStore = new BasicCookieStore();
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		Map<String, String> map = Maps.newTreeMap();
		map.put("mail_acc", "");
		map.put("dwp", "");
		map.put("SignIn", "1");
		InputStream is = HttpIOCommon.post(url, map, client, context);
		String output = null;
		try {
			output = IOUtils.toString(is, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(output);
	}

	
	
}
