package edu.tcu.mi.info.system.student;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import edu.tcu.mi.info.system.auth.InfoSystemAuthentication;
import edu.tcu.mi.info.system.io.Action;
import edu.tcu.mi.info.system.student.StudentInfo.Event;

public class ListHistory implements Action {

	@Override
	public void doSomething(List<String> collection) {

		if(collection != null && collection.size() == 2){
			String account = collection.get(0);
			String password = collection.get(1);
			CookieStore cookieStore = new BasicCookieStore();
			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);
			
			CloseableHttpClient client = HttpClientBuilder.create().build();
			InfoSystemAuthentication auth = new InfoSystemAuthentication(client, context);
			auth.authenticationStudentInfo(account, password);
			StudentInfo student = new StudentInfo(client, context);
			int days = student.F5_S502Count();
			if(days == -1){
				System.out.println("登入失敗! 請重新確認帳號密碼。");
				return;
			}
			List<Event> events = student.getEvents();
			for(Event event : events){
				System.out.println(event.toString());
			}
		}		
	}

}
