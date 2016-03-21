package edu.tcu.mi.info.system.student;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.springframework.core.io.ClassPathResource;

import edu.tcu.mi.info.system.auth.InfoSystemAuthentication;
import edu.tcu.mi.info.system.io.Action;

public class Count implements Action {

	private Properties properties;
	
	public Count(){
		ClassPathResource resource = new ClassPathResource("tzuchi.properties"); 
		properties = new Properties();
		try {
			properties.load(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void doSomething(List<String> collection) {
		if(collection != null && collection.size() == 2){
			String account = collection.get(0);
			String password = collection.get(1);
			
			/* 登入帳號密碼 */
			CookieStore cookieStore = new BasicCookieStore();
			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);
			
			CloseableHttpClient client = HttpClientBuilder.create().build();
			InfoSystemAuthentication auth = new InfoSystemAuthentication(client, context);
			auth.authenticationStudentInfo(account, password);
			StudentInfo student = new StudentInfo(client, context);
			

			String semesterEndDate = properties.getProperty("semester.end.date");
			String[] semesterDate = semesterEndDate.trim().split("/");
			int seYear = Integer.valueOf(semesterDate[0]);
			int seMonth = Integer.valueOf(semesterDate[1]);
			int seDay = Integer.valueOf(semesterDate[2]);
			
			int days = student.F5_S502Count();
			if(days == -1){
				System.out.println("登入失敗! 請重新確認帳號密碼。");
				return;
			}
			DateTimeZone Taiwan = DateTimeZone.forID("Asia/Taipei");
			DateTime sbDate = DateTime.now();
			DateTime seDate = new DateTime(seYear+1911, seMonth, seDay, 5, 0, 0, Taiwan);

			int _days = Days.daysBetween(sbDate.toLocalDate(), seDate.toLocalDate()).getDays();
			
			
			System.out.println("本學期已申請外宿 : " + days + "天");
			System.out.println("距離期末尚有 : " + _days + "天");
		}
	}
	

}
