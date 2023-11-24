package Calculator;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

public class TCPServer1 {
    public static void main(String argv[]) throws Exception {
        // Create a server socket and bind it to port 6789.
        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Server started.\n");

        while (true) {
            // Wait for a connection request from a client.
            Socket connectionSocket = welcomeSocket.accept();

            // When a connection request comes in, a new thread is created to process the client's request.
            new Thread(new ClientHandler(connectionSocket)).start();
        }
    }
}

// A class to handle client connections in a separate thread.
class ClientHandler implements Runnable {
    private Socket connectionSocket;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try {
            // Create input and output streams for communication with the client.
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // Read the client's request (an operation) from the input stream.
            String operation = inFromClient.readLine();
            double num1, num2;

            // Split the operation into tokens.
            String[] tokens = operation.split(" ");

            // Check the number of arguments in the operation.
            if (tokens.length < 3) {
                outToClient.writeBytes("400\r\nError message: Too few arguments\r\n\r\n");
                connectionSocket.close();
                return;
            } else if (tokens.length > 3) {
                outToClient.writeBytes("400\r\nError message: Too many arguments\r\n\r\n");
                connectionSocket.close();
                return;
            }

            try {
                // Parse the numbers from tokens.
                num1 = Double.parseDouble(tokens[1]);
                num2 = Double.parseDouble(tokens[2]);
            } catch (NumberFormatException e) {
                outToClient.writeBytes("400\r\nError message: Invalid number format\r\n\r\n");
                connectionSocket.close();
                return;
            }

            System.out.println("FROM CLIENT: " + tokens[0] + " " + num1 + " " + num2);

            double result = 0.0;

            try {
                // Perform the calculator operation based on the first token.
                switch (tokens[0]) {
                    case "ADD":
                        result = num1 + num2;
                        break;
                    case "MINUS":
                        result = num1 - num2;
                        break;
                    case "MULTI":
                        result = num1 * num2;
                        break;
                    case "DIV":
                        if (num2 == 0) {
                            throw new ArithmeticException("divided by zero");
                        }
                        result = num1 / num2;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operation");
                }
            } catch (ArithmeticException | IllegalArgumentException e) {
                // Handle exceptions and send an error response to the client.
                outToClient.writeBytes("400\r\nError message: " + e.getMessage() + "\r\n\r\n");
                connectionSocket.close();
                return;
            }

            // Format the result and send it to the client.
            // The Double type can cause incorrect calculation errors due to floating point.
            // Therefore, only calculate to three decimal places.
            DecimalFormat df = new DecimalFormat("#.###");
            outToClient.writeBytes("200\r\nResult: " + df.format(result) + "\r\n\r\n");

            // Close the connection with the client.
            connectionSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
