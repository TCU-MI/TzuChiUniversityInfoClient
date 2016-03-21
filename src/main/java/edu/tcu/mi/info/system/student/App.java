package edu.tcu.mi.info.system.student;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;

import edu.tcu.mi.info.system.auth.InfoSystemAuthentication;
import edu.tcu.mi.info.system.student.StudentInfo.Event;

public class App {
	


	public static void main(String[] args) {
//		String account = "";
//		String password = "";
//		String stay_out_date_s = "104/06/12";
//		String stay_out_date_e = null;
//
//		int days = 1;
//		String Submit_desc = "Lab";
//		String conn_tel = "";
//		String conn_addr = "同上";
//		App app = new App();
		
		
		ArgumentParser parser = 
				ArgumentParsers
				.newArgumentParser("App").defaultHelp(true).description("選擇外宿申請功能!");

        parser
        	.addArgument("-l", "--listHistory").dest("list").action(Arguments.storeConst()).setConst(new ListHistory())
            .help("查看申請歷史記錄。");
        parser
	    	.addArgument("-c", "--count").dest("count").action(Arguments.storeConst()).setConst(new Count())
	        .help("計算本學期申請次數。");
        parser
	    	.addArgument("-p", "--apply").dest("apply").action(Arguments.storeConst()).setConst(new Apply())
	        .help("申請外宿。");
        parser.addArgument("account").metavar("account").type(String.class).help("");
        parser.addArgument("password").metavar("password").type(String.class).help(""); 
        parser.addArgument("stay_out_date_s").metavar("startEnd").nargs("?").setDefault("今天日期").type(String.class).help("申請外宿開始日期。"); 
        parser.addArgument("stay_out_date_e").metavar("endDate").nargs("?").type(String.class).help("申請外宿結束日期。"); 
        parser.addArgument("days").metavar("days").nargs("?").setDefault("1").type(String.class).help("外宿開始日期延伸幾天。與外宿結束日期擇一使用。"); 
        parser.addArgument("submit_desc").metavar("description").nargs("?").setDefault("不要問很可怕").type(String.class).help(""); 
        parser.addArgument("conn_tel").metavar("telphone").nargs("?").setDefault("0000000000").type(String.class).help("");   
        parser.addArgument("conn_addr").metavar("address").nargs("?").setDefault("同上。").type(String.class).help("");       
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            Object list = ns.get("list");
            if(list != null){
            	List<String> par =  Lists.newArrayList();
            	par.add((String) ns.get("account"));
            	par.add((String) ns.get("password"));
            	((Action)list).doSomething(par);
            }
            Object count = ns.get("count");
            if(count != null){
            	List<String> par =  Lists.newArrayList();
            	par.add((String) ns.get("account"));
            	par.add((String) ns.get("password"));
            	((Action)count).doSomething(par);
            }
            Object apply = ns.get("apply");
            if(apply != null){
            	List<String> par =  Lists.newArrayList();
            	par.add(0, (String) ns.get("account"));
            	par.add(1, (String) ns.get("password"));
            	par.add(2, (String) ns.get("stay_out_date_s"));
            	par.add(3, (String) ns.get("stay_out_date_e"));
            	par.add(4, (String) ns.get("days"));
            	par.add(5, (String) ns.get("submit_desc"));
            	par.add(6, (String) ns.get("conn_tel"));
            	par.add(7, (String) ns.get("conn_addr"));
            	((Action)apply).doSomething(par);
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
	}

	private static interface Action {
		void doSomething(List<String> collection);
	}
	
	private static class Count implements Action{

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

				String semesterEndDate = properties.getProperty("semester.end.date");
				String[] semesterDate = semesterEndDate.trim().split("/");
				int seYear = Integer.valueOf(semesterDate[0]);
				int seMonth = Integer.valueOf(semesterDate[1]);
				int seDay = Integer.valueOf(semesterDate[2]);
				
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
				DateTimeZone Taiwan = DateTimeZone.forID("Asia/Taipei");
				DateTime sbDate = DateTime.now();
				DateTime seDate = new DateTime(seYear+1911, seMonth, seDay, 5, 0, 0, Taiwan);

				int _days = Days.daysBetween(sbDate.toLocalDate(), seDate.toLocalDate()).getDays();
				
				
				System.out.println("本學期已申請外宿 : " + days + "天");
				System.out.println("距離期末尚有 : " + _days + "天");
			}
		}
		

		
	}
	private static class ListHistory implements Action{
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

	private static class Apply implements Action{

		@Override
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
}