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
 * Class that uses runnable to handle http request from the acap application and
 * javascript application
 * 
 * @author Yurdaer & Hadi
 *
 */
public class WebThread implements Runnable {
	private Socket socket;
	private File ROOT = new File(".");
	private String date_time, interval;
	private String fromTo[];

	/*
	 * The constructor that saves the client that was connected to be able to send
	 * response back
	 */
	public WebThread(Socket socket) {
		this.socket = socket;
	}

	/*
	 * Runnable used for the client http request
	 * 
	 * @see java.lang.Runnable#run()
	 */

	@Override
	public void run() {
		BufferedReader input = null;
		PrintWriter output = null;
		BufferedOutputStream dataOutput = null; // Used for writing back to the client the message
		String fileReq = null; // Used for specifying the request type to answer with back to the client
		String str, method;
		StringTokenizer parse; // Used for parsing the http request that was got from the client

		try {

			input = new BufferedReader(new InputStreamReader(socket.getInputStream())); // we read characters from the
																						// client via input stream on
																						// the socket

			output = new PrintWriter(socket.getOutputStream()); // we get character output stream to client (for
																// headers)

			dataOutput = new BufferedOutputStream(socket.getOutputStream()); // get binary output stream to client (for
																				// requested data)

			str = input.readLine(); // Get the type of request and content

			parse = new StringTokenizer(str); // Parsing the request to extract the request received

			method = parse.nextToken().toUpperCase(); // Parse the end to know what request

			if (method.equals("GET")) {

				interval = parse.nextToken("/ "); // Gets the sent interval in http by parsing it
				fromTo = interval.split("&"); // Splits the intervals in two (from and to)
				write_html(getInterval(fromTo[0], fromTo[1], fromTo[2], fromTo[3])); // Operation for filtering and
																						// getting the data from the
																						// database
																						// Writes then after to html to
																						// send to the client
				fileReq = "get_data.html"; // Specify the html file to send

				File file = new File(ROOT, fileReq); // Create a file of it
				int fileLength = (int) file.length(); // Get the length of the file

				byte[] fileData = file2Byte(file, fileLength); // Create a byte array for sending it

				// HTTP Headers to send to the client
				output.println("HTTP/1.1 200 OK");
				output.println("Date: " + new Date());
				output.println("Content-type: " + "text/html");
				output.println("Content-length: " + fileLength);
				output.println();
				output.flush();

				// Write the html file afterwards
				dataOutput.write(fileData, 0, fileLength);
				dataOutput.flush();

			} else if (method.equals("POST")) {

				// Parse and get the time it was triggered
				date_time = parse.nextToken("/ ");
				fileReq = "set_data.html";
				write_file(date_time);
				File file = new File(ROOT, fileReq);
				int fileLength = (int) file.length();
				byte[] fileData = file2Byte(file, fileLength);

				// HTTP Headers to send to the client
				output.println("HTTP/1.1 200 OK");
				output.println("Date: " + new Date());
				output.println("Content-type: " + "text/html");
				output.println("Content-length: " + fileLength);
				output.println();
				output.flush();
				dataOutput.write(fileData, 0, fileLength);
				dataOutput.flush();
			}

		} catch (FileNotFoundException fnfe) {
			System.out.println("FAILED! FILE NOT FOUND!");

		} catch (IOException ioe) {
			System.err.println("Server Err : " + ioe);
		} finally {
			try {
				input.close(); // close inputstream
				output.close(); // close outputstream
				dataOutput.close(); // close dataOutput
				socket.close(); // close the connection
			} catch (Exception e) {
				System.err.println("Error, Closing Stream : " + e.getMessage());
			}
			System.out.println("Closed connection.\n");

		}

	}

	/**
	 * Method for converting the html file to byte to be able to send to client
	 * 
	 * @param f      the HTML file to answer with
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

	/**
	 * This method is used for writing the received data from the acap application
	 * When a motion detection was detected
	 * 
	 * @param data the data to write to the body of the html
	 */
	private void write_file(String data) {
		File database = new File("test.txt");
		try {
			// Create the file
			if (database.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

			// Write Content
			FileWriter writer = new FileWriter(database, true);
			writer.write(data + "\n");
			System.out.println(data + "\n");
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method is used for filtering the database data, which was received by
	 * the acap application Filters the data and by checking between the intervals
	 * received by javaScript application
	 * 
	 * @param lowerDate the lower interval date
	 * @param lowerTime the lower interval time
	 * @param upperDate the upper interval date
	 * @param upperTime the upper interval time
	 * @return the filtered data between the two intervals as a string
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getInterval(String lowerDate, String lowerTime, String upperDate, String upperTime)
			throws FileNotFoundException {
		String line; // Variable used for reading the txt file each row
		FileReader fr = new FileReader("test.txt"); // Filreader used for reading the text.txt file containing the data
													// stored
		BufferedReader reader = new BufferedReader(fr); // BufferedReader used for reading the file

		String[] dateUpperReq = upperDate.split("-"); // Parsing the dateUpper by year, month and day
		String[] dateLowerReq = lowerDate.split("-"); // Parsing the dateLower by year, month and day

		String[] timeUpperReq = upperTime.split("-"); // Parsing the timeUpper by hour, minutes and seconds
		String[] timeLowerReq = lowerTime.split("-"); // Parsing the timeLower by hour, minutes and seconds

		String[] dateTimeFile, dateFile, timeFile; // Variables used for holding the current read line in the txt file

		/*
		 * Converting the dateUpper and dateLower into minutes. Because it is easier to
		 * compare
		 */
		int UpperMinutes = Integer.parseInt(dateUpperReq[1]) * 60 * 24 * 30
				+ Integer.parseInt(dateUpperReq[2]) * 60 * 24 + Integer.parseInt(timeUpperReq[0]) * 60
				+ Integer.parseInt(timeUpperReq[1]);
		int LowerMinutes = Integer.parseInt(dateLowerReq[1]) * 60 * 24 * 30
				+ Integer.parseInt(dateLowerReq[2]) * 60 * 24 + Integer.parseInt(timeLowerReq[0]) * 60
				+ Integer.parseInt(timeLowerReq[1]);
		int FileTime;

		JSONObject jObj = new JSONObject(); // Create a json object instance to store the filtered date&time
		JSONArray jArr = new JSONArray(); // Create a json array for storing each line

		Map map = new LinkedHashMap(60); // LinkedHashMap used for storing the values and add to the json array
		boolean put = false;

		try {
			/* Continues to read the file until */
			while ((line = reader.readLine()) != null) {
				/*
				 * Converting the dateTimeFile to minutes
				 */
				dateTimeFile = line.split("&"); // Split/seperate the date and time from each other
				dateFile = dateTimeFile[0].split("-"); // Split the date to year, month and day
				timeFile = dateTimeFile[1].split("-"); // Split the time to hour, minutes and seconds
				FileTime = Integer.parseInt(dateFile[1]) * 60 * 24 * 30 + Integer.parseInt(dateFile[2]) * 60 * 24
						+ Integer.parseInt(timeFile[0]) * 60 + Integer.parseInt(timeFile[1]);
				/*
				 * We check the line fetched from the txt file if the date is between these
				 * intervals requested
				 */
				if (Integer.parseInt(dateLowerReq[0]) <= Integer.parseInt(dateFile[0])
						&& Integer.parseInt(dateUpperReq[0]) >= Integer.parseInt(dateFile[0])) {
					if (FileTime >= LowerMinutes && FileTime <= UpperMinutes) {
						put = true;
					}

				}

				if (put) {
					map = new LinkedHashMap(2); // Create a new instance of the LinkedHashMap to add to the jsonarray
					map.put("Date", dateFile[0] + "-" + dateFile[1] + "-" + dateFile[2]); // Add the date
					map.put("Time", timeFile[0] + ":" + timeFile[1] + ":" + timeFile[2]); // Add the time
					jArr.put(map); // Add the map to the json array
					put = false; // reset the boolean to add
				}

			}
			jObj.put("DateTime", jArr); // Add the whole json array to the json object
			System.out.println(jObj.toString());
			reader.close(); // Close the reader, the work is done

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return jObj.toString();

	}

	/**
	 * This method is used for writing the text to a html file To be able to send to
	 * the client
	 * 
	 * @param b the body that will be added to the html file
	 */
	@SuppressWarnings("deprecation")
	private void write_html(String b) {
		File newHtml = null;
		try {
			File htmlFile = new File("get_data_templet.html"); // Get the template html file
			String htmlStr = FileUtils.readFileToString(htmlFile); // Read the file to a string
			String title = "Fetched Data";
			htmlStr = htmlStr.replace("$title", title); // Replace the title string with the wanted title
			htmlStr = htmlStr.replace("$body", b); // Replace the body with the wanted body
			newHtml = new File("get_data.html"); // declear the newhtml the other file to write to
			FileUtils.writeStringToFile(newHtml, htmlStr); // Write the data to the html file to send
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
