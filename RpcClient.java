package ex5;
import java.io.*;
import java.net.*;
public class RpcClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            String request = "add 4 5"; 
            output.println(request);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = input.readLine();
            System.out.println("Response from server: " + response);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
