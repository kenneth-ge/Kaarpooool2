import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Test {

	public static JSONParser parser;
	
	public static void main(String[] args) throws Exception{
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		
		parser = new JSONParser();
		
		final String password = "1234";
		
		JSONObject obj = create(password);
		
		System.out.println(obj.get("id"));
		System.out.println(obj.get("id").getClass());
		
		long id = (long) obj.get("id");
		
		login(id, password);
		//query_session();
	}
	
	public static JSONObject create(String password) throws Exception {
		URL obj = new URL("http://localhost:8080/create_session");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", "Mozilla/5.0");

		// For POST only - START
		con.setDoOutput(true);
		try(OutputStream os = con.getOutputStream()){
			os.write(("psw=" + password).getBytes());
			os.flush();
			os.close();
		}
		
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			return (JSONObject) parser.parse(response.toString());
		} else {
			System.out.println("POST request didn't work");
		}
		return null;
	}
	
	public static void login(long id, String password) throws Exception {
		URL obj = new URL("http://localhost:8080/login");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", "Mozilla/5.0");

		// For POST only - START
		con.setDoOutput(true);
		try(OutputStream os = con.getOutputStream()){
			os.write(("uname=" + id + "&psw=" + password).getBytes());
			os.flush();
			os.close();
		}
		
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request didn't work");
		}
	}
	
	public static void query_session(long id) throws Exception {
		URL url = new URL("http://localhost:8080/session/" + id);
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod("GET");
		
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		
		//con.setDoOutput(true);
		
		int responseCode = con.getResponseCode();
		
		System.out.println(responseCode);
		
		if(responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			System.out.println(response.toString());
		}
	}
	
}
