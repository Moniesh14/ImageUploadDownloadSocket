package ex5;
import java.io.*;
import java.net.*;
public class RpcServer {
	public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server is listening...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String request = input.readLine();
                System.out.println("Received request: " + request);
                String[] parts = request.split(" ");
                String operation = parts[0];
                int num1 = Integer.parseInt(parts[1]);
                int num2 = Integer.parseInt(parts[2]);
                int result = 0;
                switch (operation) {
                    case "add":
                        result = num1 + num2;
                        break;
                    case "multiply":
                        result = num1 * num2;
                        break;
                    default:
                        System.out.println("Unknown operation: " + operation);
                        continue;
                }
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                output.println("Result: " + result);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  }

