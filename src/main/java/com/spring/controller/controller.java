package com.spring.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spring.dto.BookDTO;

@Controller
@RequestMapping("/test")
public class controller {
	
	String json;
	Gson gs= new Gson();
	@GetMapping("/case1")
	public String index() {
		return "index";
	}
	
	//DTO -> JSON
	@ResponseBody
	@GetMapping("/project01")
	public String project01() {
		BookDTO dto = new BookDTO("자바",21000,"에이콘",670);
		json=gs.toJson(dto);
		System.out.println(json);
		
		return json;
	}

	
	// JSON -> DTO
	@GetMapping("/project02")
	public String project02() {
		BookDTO dto = gs.fromJson(json, BookDTO.class);  
		System.out.println(dto.toString());
		
		return "index";
	}
	
	// 여러DTO --> Arraylist --> JSON 
	@ResponseBody
	@GetMapping("/project03")
	public String project03() {
		BookDTO dto1 = new BookDTO("자바1",21000,"에이콘1",670);
		BookDTO dto2 = new BookDTO("자바2",22000,"에이콘2",670);
		BookDTO dto3 = new BookDTO("자바3",23000,"에이콘3",670);
		
		ArrayList<BookDTO> list = new ArrayList<BookDTO>();
		list.add(dto1);
		list.add(dto2);
		list.add(dto3);
		
		String jsonlist = gs.toJson(list);
		System.out.println(dto1.toString());
		
		// JSON(ArrayList) --> ArrayList<BookDTO>
		
		ArrayList<BookDTO> jsontolist = gs.fromJson(jsonlist,new TypeToken<ArrayList<BookDTO>>(){}.getType());
		
		for(int i=0; i< jsontolist.size();i++) {
			BookDTO tmp=jsontolist.get(i);
			System.out.println("tmp:"+tmp);
		}
		
		for(BookDTO vo: jsontolist) {
			System.out.println("vo:"+vo.toString());
			
		} 
	
		
		
		return jsonlist;
	}
	
	// JSON -->  Arraylist --> 여러DTO   
	// q.fromJson(lstJson,.....)
	
	
	
	@GetMapping("/project04")
	public String project04() {
		
		JSONObject student1 = new JSONObject(); 
		JSONObject student2 = new JSONObject(); 
		
		student1.put("name", "홍길동"); 
		student1.put("phone", "010-111-1111"); 
		student1.put("address", "서울"); 
		System.out.println(student1);
		
		student2.put("name", "나길동"); 
		student2.put("phone", "010-222-2222"); 
		student2.put("address", "광주"); 
		System.out.println(student2);
		 
		JSONArray students = new JSONArray(); 
		students.put(student1); 
		students.put(student2); 
		 
		System.out.println(students);
		
		JSONObject object = new JSONObject();
		object.put("students", students);
		System.out.println(object.toString());
	
		return "index";
	}
		
	@GetMapping("/project05")
	public String project05() {
		
		String client_id = "4hv51h0bie";
		String client_secret ="LCQ3zKx3buM8r0GKeaFPXVA0FJxq7XXtU4ViZqZf";
		
		BufferedReader io = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
				System.out.println("주소를 입력하세요:");
				String address = io.readLine();
				String addr= URLEncoder.encode(address,"UTF-8");
				
				String reqUrl ="https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="+addr;
				URL url = new URL(reqUrl);
				HttpURLConnection con= (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("x-ncp-apigw-api-key-id", client_id);
				con.setRequestProperty("x-ncp-apigw-api-key", client_secret);

				//200-ok
				int responseCode =con.getResponseCode();
				BufferedReader br=
				new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
				
				if(responseCode ==200) {
					br=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
				}else {
					br=new BufferedReader(new InputStreamReader(con.getErrorStream()));
				}
								
				String line;
				StringBuffer data = new StringBuffer();
				
				while((line=br.readLine())!=null) {
					data.append(line);
				}
				
				br.close();
				System.out.println(data);
				
				// step 6. JSON 객체로 변환하기
				//데이터 단위를 인식시키기 위해서 필요
				JSONTokener tok=new JSONTokener(data.toString());
				JSONObject object = new JSONObject(tok);
				// { }
				System.out.println("object: "+object.get("status"));
				System.out.println("object: "+object.toString());
				
				JSONObject meta= object.getJSONObject("meta");
				int totalCount = meta.getInt("totalCount");
				System.out.println("totalCOunt: "+totalCount);
				
				
				JSONArray arr= object.getJSONArray("addresses");
				JSONObject first= (JSONObject)arr.get(0);
				String x = first.getString("x");
				String y = first.getString("y");
				
				System.out.println("경도x: "+x);
				System.out.println("위도y: "+y);
				
				JSONArray arrb= first.getJSONArray("addressElements");
				JSONObject second= (JSONObject)arrb.get(7);
				String longName = second.getString("longName");
				System.out.println("longName: "+longName);
				
				for(int i=0;i<arr.length();i++) {
					JSONObject temp=(JSONObject) arr.getJSONObject(i);
					System.out.println("address: "+temp.get("roadAddress"));
					System.out.println("jibunAddress: "+temp.get("jibunAddress"));
					System.out.println("경도: "+temp.get("x"));
					System.out.println("위도: "+temp.get("y"));
				}
				
				getImage(x,y,addr);
				
		}catch (Exception e){e.printStackTrace();}	
		
		return "index";
	}
		
	public void getImage(String x, String y, String addr) {
		
		String client_id = "4hv51h0bie";
		String client_secret ="LCQ3zKx3buM8r0GKeaFPXVA0FJxq7XXtU4ViZqZf";
		//step 1 : URL 작성
		//https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?
		//w=300&
		//h=300&
		//center=127.1054221,37.3591614&
		//level=16' \
		//--header 'x-ncp-apigw-api-key-id: {API Key ID}' \
		//--header 'x-ncp-apigw-api-key: {API Key}'
		try {
			// step 1:URL 작성
			System.out.println("getImage try: 진입");
			String url = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";
			url +="w=300&h=300&";
			url +="level=16&";
			url +="center="+x+","+y+"&";
			String pos = URLEncoder.encode(x+" "+y,"UTF-8");
	      //  url += "&markers=type:t|size:mid|pos:"+pos+"|label:"+URLEncoder.encode(addr, "UTF-8");
	        url += "&markers=type:t|size:mid|pos:"+pos+"|label:"+addr;
	        
	        System.out.println("getImage try: 진입1"+url);
	        //step2 :요청발생
	        URL ur = new URL(url);
			HttpURLConnection con= (HttpURLConnection) ur.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("x-ncp-apigw-api-key-id", client_id);
			con.setRequestProperty("x-ncp-apigw-api-key", client_secret);
			
			System.out.println("getImage try: 진입2"+ur);
			//step3: 데이터 수신
			InputStream is = con.getInputStream();
			//이미지는 바이트 단위이기 대문에 바이트 배열을 사용한다.
			byte[] bytes = new byte[1024];
			//파일이름 짓기
			//Date dt = new Date();
			//Long lt = dt.getTime();
			//String img = lt.toString();
			String imgname = Long.valueOf(new Date().getTime()).toString();
			System.out.println("getImage try: 진입3"+imgname);
			//파일생성
			File file = new File(imgname+".jpg");
			System.out.println("getImage try: 진입4 file="+file);
			
			file.createNewFile();
			System.out.println("getImage try: 진입5 file="+file);
			int read=0;
			System.out.println("getImage try: 진입6 read= "+ read);
			FileOutputStream outputStream = new FileOutputStream(file);
			System.out.println("getImage try: 진입7 os= "+ outputStream);
			while((read=is.read(bytes))!= -1) {
				outputStream.write(bytes,0,read);
			}
			is.close();
	        outputStream.close();
		} catch(Exception e) {e.printStackTrace();}
	}
	
		
}
