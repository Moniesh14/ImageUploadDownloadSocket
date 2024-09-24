package ex5;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
public class SimpleHttpServer1 {
	 private static final int PORT = 8084;
	    private static final String UPLOAD_DIR = "uploads/";

	    public static void main(String[] args) throws IOException {
	        ServerSocket serverSocket = new ServerSocket(PORT);
	        System.out.println("Server started at http://localhost:" + PORT);
	        File uploadDir = new File(UPLOAD_DIR);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdir();
	        }
	        while (true) {
	            Socket clientSocket = serverSocket.accept();
	            handleClient(clientSocket);
	        }
	    }
	    private static void handleClient(Socket clientSocket) {
	        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	             OutputStream out = clientSocket.getOutputStream()) {
	            String line = in.readLine();
	            if (line == null) return;
	            String[] request = line.split(" ");
	            String method = request[0];
	            String path = request[1];

	            if (method.equals("GET")) {
	                handleGetRequest(out, path);
	            } else if (method.equals("POST")) {
	                handlePostRequest(in, out, path, clientSocket);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void handleGetRequest(OutputStream out, String path) throws IOException {
	        if (path.equals("/")) {

	            sendResponse(out, "200 OK", "text/html", getHtmlForm());
	        } else if (path.startsWith("/uploads/")) {
	            File file = new File(UPLOAD_DIR + path.substring(9));
	            if (file.exists()) {
	                sendFileResponse(out, file);
	            } else {
	                sendResponse(out, "404 Not Found", "text/html", "<h1>File not found</h1>");
	            }
	        } else {
	            sendResponse(out, "404 Not Found", "text/html", "<h1>Page not found</h1>");
	        }
	    }

	    private static void handlePostRequest(BufferedReader in, OutputStream out, String path, Socket clientSocket) throws IOException {
	        Map<String, String> headers = new HashMap<>();
	        String line;
	        while (!(line = in.readLine()).isEmpty()) {
	            String[] header = line.split(": ");
	            if (header.length == 2) {
	                headers.put(header[0], header[1]);
	            }
	        }
	        String boundary = headers.get("Content-Type").split("boundary=")[1];
	        DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
	        skipHeaders(dataIn, boundary);
	        String fileName = "uploaded_image.jpg";
	        FileOutputStream fileOut = new FileOutputStream(UPLOAD_DIR + fileName);
	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = dataIn.read(buffer)) != -1) {
	            fileOut.write(buffer, 0, bytesRead);
	            if (new String(buffer, 0, bytesRead).contains(boundary)) {
	                break;
	            }
	        }
	        fileOut.close();
	        sendResponse(out, "200 OK", "text/html", "<h1>File uploaded successfully</h1><a href=\"/uploads/" + fileName + "\">Download Image</a>");
	    }

	    private static void skipHeaders(DataInputStream in, String boundary) throws IOException {
	        String line;
	        while (!(line = in.readLine()).contains(boundary)) {
	        }
	        while (!(line = in.readLine()).isEmpty()) {
	        }
	    }

	    private static void sendResponse(OutputStream out, String status, String contentType, String content) throws IOException {
	        PrintWriter writer = new PrintWriter(out);
	        writer.println("HTTP/1.1 " + status);
	        writer.println("Content-Type: " + contentType);
	        writer.println("Content-Length: " + content.length());
	        writer.println();
	        writer.println(content);
	        writer.flush();
	    }

	    private static void sendFileResponse(OutputStream out, File file) throws IOException {
	        byte[] fileData = Files.readAllBytes(file.toPath());

	        PrintWriter writer = new PrintWriter(out);
	        writer.println("HTTP/1.1 200 OK");
	        writer.println("Content-Type: image/jpeg");
	        writer.println("Content-Length: " + fileData.length);
	        writer.println();
	        writer.flush();

	        out.write(fileData);
	        out.flush();
	    }

	    private static String getHtmlForm() {
	        return """
	                <html>
	                <head><title>Image Upload</title></head>
	                <body>
	                    <h1>Upload Image</h1>
	                    <form method="POST" enctype="multipart/form-data" action="/">
	                        <input type="file" name="file" accept="image/*"><br><br>
	                        <input type="submit" value="Upload Image">
	                    </form>
	                </body>
	                </html>
	                """;
	    }
}
