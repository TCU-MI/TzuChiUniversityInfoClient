package edu.tcu.mi.info.system.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Maps;

import edu.tcu.mi.info.system.io.HttpIOCommon;
import edu.tcu.mi.info.system.io.StringIOCommon;

public class InfoSystemAuthentication {

	private CloseableHttpClient client;
	private HttpClientContext context;
	private Properties properties;
	
	public InfoSystemAuthentication(CloseableHttpClient _client, HttpClientContext _context){
		client = _client;
		context = _context;
		
		ClassPathResource resource = new ClassPathResource("tzuchi.properties"); 
		properties = new Properties();
		try {
			properties.load(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream authenticationMyInfo(String account, String password){
		Map<String, String> map = Maps.newTreeMap();
		map.put("page_id", "");
		map.put("page_service_desc", "");
		map.put("mail_acc", account);
		map.put("pwd", password);
		map.put("go", "登入系統");
		String url = properties.getProperty("myinfo.login.check");
		InputStream is = HttpIOCommon.post(url, map, client, context);
		return is;
	}
	
	public InputStream authenticationStudentInfo(String account, String password){
		authenticationMyInfo(account, password);
		String _url = properties.getProperty("myinfo.ap_go");
		InputStream _is = HttpIOCommon.get(_url, client, context);
		String _output;
		try {
			_output = IOUtils.toString(_is, "big5");
			Map<String, String> map = mapInfo(_output);
			String url = properties.getProperty("sap.stdinfo.login.check");
			InputStream is = HttpIOCommon.post(url, map, client, context);		
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return _is;
	}
	
	private Map<String, String> mapInfo(String output){
		Map<String, String> map = Maps.newTreeMap();

		{
			String strA01 = "<input type=\"hidden\" name=\"user\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("user", info);
		}
		{
			String strA01 = "<input type=\"hidden\" name=\"sign_page\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("sign_page", info);
		}
		{
			String strA01 = "<input type=\"hidden\" name=\"ap_id\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("ap_id", info);
		}
		{
			String strA01 = "<input type=\"password\" name=\"password\" style=\"display:none\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("password", info);
		}
		{
			String strA01 = "<input type=\"hidden\" name=\"url\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("url", info);
		}
		{
			String strA01 = "<input type=\"hidden\" name=\"ap_group\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("ap_group", info);
		}
		{
			String strA01 = "<input type=\"hidden\" name=\"func_no\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("func_no", info);
		}
		{
			String strA01 = "<input type=\"hidden\" name=\"sys_version\" value=\"";
			String strA02 = "\">";
			String info = StringIOCommon.extractInfo(output, strA01, strA02);
			map.put("sys_version", info);
		}
		return map;
	}
	
}
