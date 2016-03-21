package edu.tcu.mi.info.system.student;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import edu.tcu.mi.info.system.auth.InfoSystemAuthentication;
import edu.tcu.mi.info.system.io.Action;

public class Apply implements Action {


	public void doSomething(List<String> collection) {
		if(collection != null & collection.size() == 8){
			String account = collection.get(0);
			String password = collection.get(1);
			String stay_out_date_s = collection.get(2);
			if(stay_out_date_s.equals("今天日期")){
				stay_out_date_s = null;
			}
			String stay_out_date_e = collection.get(3);
			int days = Integer.valueOf(collection.get(4));
			String Submit_desc = collection.get(5);
			String conn_tel = collection.get(6);
			String conn_addr = collection.get(7);
			try {
				apply(account, password, stay_out_date_s, stay_out_date_e, days, Submit_desc, conn_tel, conn_addr);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}

	public void apply(String account, String password, String stay_out_date_s, String stay_out_date_e, int days, String Submit_desc, String conn_tel, String conn_addr) throws URISyntaxException, ClientProtocolException, IOException {
		DateTime sosDate = null;
		DateTime soeDate = null;
		if(stay_out_date_s != null){
			String[] date = stay_out_date_s.trim().split("/");
			int year = Integer.valueOf(date[0]);
			int month = Integer.valueOf(date[1]);
			int day = Integer.valueOf(date[2]);

			DateTimeZone Taiwan = DateTimeZone.forID("Asia/Taipei");
			sosDate = new DateTime(year+1911, month, day, 5, 0, 0, Taiwan);
		} else {
			sosDate = DateTime.now();
		}
		if(stay_out_date_e != null){
			String[] date = stay_out_date_e.trim().split("/");
			int year = Integer.valueOf(date[0]);
			int month = Integer.valueOf(date[1]);
			int day = Integer.valueOf(date[2]);

			DateTimeZone Taiwan = DateTimeZone.forID("Asia/Taipei");
			soeDate = new DateTime(year+1911, month, day, 5, 0, 0, Taiwan);
		} else {
			soeDate = sosDate.plusDays(days);
		}
		
		stay_out_date_s  = (sosDate.getYear()-1911) + "" + String.format("%02d", sosDate.getMonthOfYear()) + "" + sosDate.getDayOfMonth();
		stay_out_date_e = (soeDate.getYear()-1911) + "" + String.format("%02d", soeDate.getMonthOfYear()) + "" + soeDate.getDayOfMonth(); 
		
		CookieStore cookieStore = new BasicCookieStore();
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		InfoSystemAuthentication auth = new InfoSystemAuthentication(client, context);
		auth.authenticationStudentInfo(account, password);
		StudentInfo student = new StudentInfo(client, context);
		String msg = student.F5_S502(stay_out_date_s, stay_out_date_e, Submit_desc, conn_tel, conn_addr);
		if(msg == null){
			System.out.println("登入失敗! 請重新確認帳號密碼。");
			return;
		}
		System.out.println(msg);
		
		/**
		 * 已受理您的外宿申請!請於外宿申請記錄列表中，查看您所填寫的申請資料內容。
		 * 您已辦理過該外宿期間的外宿申請，不可重覆申請!
		 * 外宿期間輸入錯誤，請重新填寫申請!
		 * */
	}

}
