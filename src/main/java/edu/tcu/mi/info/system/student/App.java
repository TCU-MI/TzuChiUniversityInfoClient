package edu.tcu.mi.info.system.student;

import java.util.List;

import com.google.common.collect.Lists;

import edu.tcu.mi.info.system.io.Action;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

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
}