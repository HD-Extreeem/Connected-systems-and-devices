import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that uses runnable to handle http request from the 
 * acap application and javascript application
 * 
 * @author Yurdaer & Hadi
 *
 */
public class WebThread implements Runnable {
	private Socket socket;
	private File ROOT = new File(".");
	private String date_time,interval;
	private String fromTo[];
	
	public WebThread(Socket socket){
		this.socket = socket;
		System.out.println("X2");
	}
	
	/*
	 * Runnable used for the client http request
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("X3");
		BufferedReader input = null;
		PrintWriter output = null;
		BufferedOutputStream dataOutput = null;
		String fileReq = null;
		String str,method;
		StringTokenizer	parse;
		try {
			System.out.println("X4");
            // we read characters from the client via input stream on the socket
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("X5");
            // we get character output stream to client (for headers)
			output = new PrintWriter(socket.getOutputStream());
			System.out.println("X6");
            // get binary output stream to client (for requested data)
			dataOutput = new BufferedOutputStream(socket.getOutputStream());
			System.out.println("X7");
            // Get the type of request and content
			str = input.readLine();
			System.out.println("X8");
            //Parsing the request to extract the request received
			parse = new StringTokenizer(str);
			System.out.println("X9");
            // Parse the end to know what request
			method = parse.nextToken().toUpperCase();
			System.out.println("X10");
            /*// Parse and get the time it was triggered
            String date_time = parse.nextToken("/ ");
            
            // Parses the rest of the requested http received
            fileReq = parse.nextToken().toLowerCase();*/

            // Check if GET method requested and what request was called

            /*if (fileReq.endsWith("get_data")) {
                fileReq = "get_data.html";
            }
            else if(fileReq.endsWith("set_data")) {
                fileReq = "set_data.html";
            }*/
            
            if(method.equals("GET")) {
            	interval = parse.nextToken("/ ");
            	//Operations for building html with the data
            	fromTo = interval.split("&");
            	write_html(getInterval(fromTo[0],fromTo[1],fromTo[2],fromTo[3]));
            	fileReq = "get_data.html";
            	//fileReq = "get.html";
            	File file = new File(ROOT, fileReq);
            	int fileLength = (int) file.length();

            	byte[] fileData = file2Byte(file, fileLength);
            	System.out.println("debug 1");
                // HTTP Headers to send
            	output.println("HTTP/1.1 200 OK");
            	output.println("Date: " + new Date());
            	output.println("Content-type: " + "text/html");
            	output.println("Content-length: " + fileLength);
            	output.println(); 
            	output.flush();
            	System.out.println("debug 2");
            	dataOutput.write(fileData, 0, fileLength);
            	dataOutput.flush();
            	System.out.println("debug 3");
            } else if(method.equals("POST")){
            	// Parse and get the time it was triggered
            	date_time = parse.nextToken("/ ");
            	fileReq = "set_data.html";
            	write_file(date_time);
            	File file = new File(ROOT, fileReq);
            	int fileLength = (int) file.length();
            	byte[] fileData = file2Byte(file, fileLength);
                // HTTP Headers to send
            	output.println("HTTP/1.1 200 OK");
            	output.println("Date: " + new Date());
            	output.println("Content-type: " + "text/html");
            	output.println("Content-length: " + fileLength);
            	output.println();
            	output.flush();
            	dataOutput.write(fileData, 0, fileLength);
         //   	dataOutput.flush();
            }



        } catch (FileNotFoundException fnfe) {
        	System.out.println("FAILED! FILE NOT FOUND!");

        } catch (IOException ioe) {
        	System.err.println("Server Err : " + ioe);
        } finally {
        	try {
                input.close(); 		// close inputstream
                output.close(); 	// close outputstream
               // dataOutput.close(); // close dataOutput
                socket.close(); 	// close the connection
            } catch (Exception e) {
            	System.err.println("Error, Closing Stream : " + e.getMessage());
            }

            
            System.out.println("Closed connection.\n");
            
        }

    }
	/**
	 * Method for converting the html file to byte and send it
	 * @param f the HTML file to answer with
	 * @param length size of the HTML file
	 * @return html file as a byte array
	 * @throws IOException
	 */
	private byte[] file2Byte(File f, int length) throws IOException {
		FileInputStream fileInputStream = null;
		byte[] fileData = new byte[length];

		try {
			fileInputStream = new FileInputStream(f);
			fileInputStream.read(fileData);
		} finally {
			if (fileInputStream != null)
				fileInputStream.close();
		}

		return fileData;
	}

	private void write_file(String data){
		File database = new File("test.txt");
		try {
        //Create the file
			if (database.createNewFile())
			{
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

        //Write Content
			FileWriter writer = new FileWriter(database,true);

			writer.write(data+"\n");
			System.out.println(data+"\n");
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getInterval(String lowerDate, String lowerTime, String upperDate, String upperTime) throws FileNotFoundException{
		String line;
		FileReader fr = new FileReader("test.txt");
		BufferedReader reader = new BufferedReader(fr);
		
		String[] dateUpperReq = upperDate.split("-");
		String[] dateLowerReq = lowerDate.split("-");

		String[] timeUpperReq = upperTime.split("-");
		String[] timeLowerReq = lowerTime.split("-");

		String[] dateTimeFile, dateFile, timeFile;

		JSONObject jObj = new JSONObject();
		JSONArray jArr = new JSONArray(); 

    	//int i=0;
		Map map= new LinkedHashMap(60); 
		try {
			while((line=reader.readLine())!=null){

				dateTimeFile = line.split("&");

				dateFile = dateTimeFile[0].split("-");
				timeFile = dateTimeFile[1].split("-");
				//Problems here need to be solved- What if other day but time is different
				if(Integer.parseInt(dateLowerReq[0])<=Integer.parseInt(dateFile[0]) && Integer.parseInt(dateUpperReq[0])>=Integer.parseInt(dateFile[0]) && Integer.parseInt(dateLowerReq[1])<=Integer.parseInt(dateFile[1]) && Integer.parseInt(dateUpperReq[1])>=Integer.parseInt(dateFile[1])&& Integer.parseInt(dateLowerReq[2])<=Integer.parseInt(dateFile[2]) && Integer.parseInt(dateUpperReq[2])>=Integer.parseInt(dateFile[2])){
					if (Integer.parseInt(dateLowerReq[2])<Integer.parseInt(dateFile[2]) && Integer.parseInt(dateUpperReq[2])>Integer.parseInt(dateFile[2])){
						/*If we want to sort the hours it can be done here, this let us use less code on the java script side*/
						map = new LinkedHashMap(2); 
						map.put("Date", dateFile[0]+"-"+dateFile[1]+"-"+dateFile[2]); 
						map.put("Time", timeFile[0]+":"+timeFile[1]+":"+timeFile[2]); 
						jArr.put(map); 

					} else if(Integer.parseInt(dateLowerReq[2])==Integer.parseInt(dateFile[2]) || Integer.parseInt(dateUpperReq[2])==Integer.parseInt(dateFile[2])){
						if (Integer.parseInt(timeLowerReq[0])<=Integer.parseInt(timeFile[0]) && Integer.parseInt(timeUpperReq[0])>=Integer.parseInt(timeFile[0])&& Integer.parseInt(timeLowerReq[1])<=Integer.parseInt(timeFile[1]) && Integer.parseInt(timeUpperReq[1])>=Integer.parseInt(timeFile[1])) {
							map = new LinkedHashMap(2); 
							map.put("Date", dateFile[0]+"-"+dateFile[1]+"-"+dateFile[2]); 
							map.put("Time", timeFile[0]+":"+timeFile[1]+":"+timeFile[2]); 
							jArr.put(map); 

						}
					} 
				}
				
			}
			jObj.put("DateTime", jArr); 
			System.out.println(jObj.toString());
			reader.close();

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return jObj.toString();

	}

	@SuppressWarnings("deprecation")
	private void write_html(String b){
		File newHtml = null;
		try {
			File htmlFile = new File("get_data_templet.html");
			String htmlStr = FileUtils.readFileToString(htmlFile);
			String title = "Fetched Data";
			String body = b;
			//body = getInterval(from,to);
			htmlStr = htmlStr.replace("$title", title);
			htmlStr = htmlStr.replace("$body", body);
			newHtml = new File("get_data.html");
			FileUtils.writeStringToFile(newHtml,htmlStr);
		} catch (IOException e) {
			e.printStackTrace();
		} 	
	}
}
