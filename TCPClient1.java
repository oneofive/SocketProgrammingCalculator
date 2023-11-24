package Calculator;
import java.io.*;
import java.net.*;
import java.util.Properties;

class TCPClient1 {
    public static void main(String argv[]) throws Exception {
        // create a Properties object to store server information
        Properties prop = new Properties();
        String serverIP = "127.0.0.1";
        int serverPort = 6789;

        try {
            // Read server information from serverinfo.dat file
            prop.load(new FileInputStream("serverinfo.dat"));
            serverIP = prop.getProperty("serverIP", serverIP);
            serverPort = Integer.parseInt(prop.getProperty("serverPort", String.valueOf(serverPort)));
        } catch (IOException e) {
            // If reading the file fails, use default server information
            System.out.println("Unable to read from serverinfo.dat, using default server info.");
        }

        String sentence;

        // Create a BufferedReader to receive user input
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        // Create a Socket to connect to the server
        Socket clientSocket = new Socket(serverIP, serverPort);

        // Create a DataOutputStream to send data to the server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // Create BufferedReader to receive data from server
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Enter operation(ADD, MINUS, MULTI, DIV) num1 num2");

        // Receive calculation input from the user
        sentence = inFromUser.readLine();

        // Send the input operation and numbers to the server
        outToServer.writeBytes(sentence + "\r\n");

        // Receive results from the server and output them
        String responseLine;
        while ((responseLine = inFromServer.readLine()) != null) {
            System.out.println(responseLine);
        }

        // Terminate socket connection
        clientSocket.close();
    }
}
