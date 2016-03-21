package edu.tcu.mi.info.system.student;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import edu.tcu.mi.info.system.io.HttpIOCommon;
import edu.tcu.mi.info.system.io.StringIOCommon;

public class StudentInfo {

	private CloseableHttpClient client;
	private HttpClientContext context;
	private Properties properties;
	private List<Event> events;
	
	public StudentInfo(CloseableHttpClient _client, HttpClientContext _context){
		client = _client;
		context = _context;
		
		ClassPathResource resource = new ClassPathResource("tzuchi.properties"); 
		properties = new Properties();
		try {
			properties.load(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		events = new ArrayList<Event>();
	}
	
	
	/**
	 * 外宿申請
	 * @param stay_out_date_s 外宿開始期間 ex:1000429  (民國年)
	 * @param stay_out_date_e 外宿結束期間 ex:1000501  (民國年)
	 * @param Submit_desc 外宿事由，ex:您不需要知道
	 * @param conn_tel 聯絡電話，ex:手機號碼
	 * @param conn_addr 聯絡地址，ex:同上
	 * @return 回應訊息
	 */
	public String F5_S502(String stay_out_date_s, String stay_out_date_e, String Submit_desc, String conn_tel, String conn_addr){
//		Event event = new Event(stay_out_date_s, stay_out_date_e, Submit_desc, conn_tel, conn_addr);
//		System.out.println(event.toString());
		Map<String, String> map = Maps.newTreeMap();
		map.put("stay_out_date_s", stay_out_date_s);
		map.put("stay_out_date_e", stay_out_date_e);
		map.put("Submit_desc", Submit_desc);
		map.put("conn_tel", conn_tel);
		map.put("conn_addr", conn_addr);
		map.put("apply", "申請");
		String url = properties.getProperty("sap.stdinfo.F5_S502_exam");
		InputStream is = HttpIOCommon.post(url, map, client, context);
		String output = null;
		try {
			output = IOUtils.toString(is, "big5");
		} catch (IOException e) {
			e.printStackTrace();
		}
		String strA01 = "<INPUT name=\"sysmsg\" value = \"";
		String strA02 = "\" style=\"DISPLAY: none\">";
		return StringIOCommon.extractInfo(output, strA01, strA02);
	}
	
	
	/**
	 * @param semesterDate 學期開始日期 ex:104/02/01
	 * @return 本學期已請外宿次數
	 */
	public int F5_S502Count(){
		int sum = 0;
		String url = properties.getProperty("sap.stdinfo.F5_S502");
		InputStream is = HttpIOCommon.get(url, client, context);
		try {
			String output = IOUtils.toString(is, "big5");
			String strA01 = "<TABLE width=\"700px\" BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\" bordercolor=\"white\" bordercolordark=\"white\" bordercolorlight=\"black\">";
			String strA02 = "</TABLE>";			
			String content = StringIOCommon.extractInfo(output, strA01, strA02);
			if(content == null){
				return -1;
			}
			output = "<TABLE>" + content + "</TABLE>";
			output = output.toLowerCase();
			Document doc = Jsoup.parse(output);
			Element elementsByTag = doc.getElementsByTag("table").get(0);
			Elements rows = elementsByTag.getElementsByTag("tr");
			for (Element row : rows) {
				Elements cols = row.getElementsByTag("td");
				if(cols.size() == 5){
					Element applyDate = cols.get(0);
					Element during = cols.get(1);
					Element event = cols.get(2);
					Element tel = cols.get(3);
					Element addr = cols.get(4);

					String _applyDate = applyDate.text();
					String _during = during.text();
					String _event = event.text();
					String _tel = tel.text();
					String _addr = addr.text();
					
					Event data = new Event(_applyDate, _during, _event, _tel, _addr);
					events.add(data);
					
					String[] tmp = _during.split("∼");
					if(tmp.length != 2) continue;
					String[] begin = tmp[0].trim().split("/");
					String[] end = tmp[1].trim().split("/");
					int bYear = Integer.valueOf(begin[0]);
					int bMonth = Integer.valueOf(begin[1]);
					int bDay = Integer.valueOf(begin[2]);
					int eYear = Integer.valueOf(end[0]);
					int eMonth = Integer.valueOf(end[1]);
					int eDay = Integer.valueOf(end[2]);
					DateTimeZone Taiwan = DateTimeZone.forID("Asia/Taipei");
					String semesterBeginDate = properties.getProperty("semester.begin.date");
					String[] semesterDate = semesterBeginDate.trim().split("/");
					int sbYear = Integer.valueOf(semesterDate[0]);
					int sbMonth = Integer.valueOf(semesterDate[1]);
					int sbDay = Integer.valueOf(semesterDate[2]);
					DateTime sbDate = new DateTime(sbYear+1911, sbMonth, sbDay, 5, 0, 0, Taiwan);
					DateTime _start = new DateTime(bYear+1911, bMonth, bDay, 5, 0, 0, Taiwan);
					DateTime _end = new DateTime(eYear+1911, eMonth, eDay, 5, 0, 0, Taiwan);

					if(sbDate.isBefore(_start)){
						int days = Days.daysBetween(_start.toLocalDate(), _end.toLocalDate()).getDays();
						sum += days;
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sum;
	}
	
	
	
	public List<Event> getEvents() {
		return events;
	}

	public class Event {

		private String applyDate = "";
		private String during = "";
		private String event = "";
		private String tel = "";
		private String addr = "";
		public Event(String applyDate, String during, String event, String tel, String addr) {
			super();
			this.applyDate = applyDate;
			this.during = during;
			this.event = event;
			this.tel = tel;
			this.addr = addr;
		}
		public String getApplyDate() {
			return applyDate;
		}
		public String getDuring() {
			return during;
		}
		public String getEvent() {
			return event;
		}
		public String getTel() {
			return tel;
		}
		public String getAddr() {
			return addr;
		}
		
		@Override
		public String toString(){
	        return MoreObjects
	        		.toStringHelper(this)
	        		.add("applyDate", applyDate)
	        		.add("during", during)
	        		.add("event", event)
	        		.add("tel", tel)
	        		.add("addr", addr)
	        		.toString();
		}
		
	}
}
