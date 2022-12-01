import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MSPServer {
    static ArrayList<String> users;
    static boolean closed = false;

    public static void main(String[] args){

        int portNumber = 1313;


        try {
            // Ligar el servicio al puerto
            ServerSocket serverSocket
                    = new ServerSocket(portNumber);

            // Esperar conexiones
            Socket clientSocket;

            Thread handler = new Thread(new ServerHandler());
            handler.start();

            while (!closed) {
                clientSocket = serverSocket.accept();

                // Flujo de salida (enviar datos al cliente)
                PrintWriter out
                        = new PrintWriter(clientSocket.getOutputStream(), true);

                // Flujo de entrada (leer datos que envía el cliente)
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                //recibe una línea de texto que corresponde a una funcion
                String command = in.readLine();


                if (command.contains("CONNECT ")) {
                    //checar si el comando que contiene "CONNECT " tiene el formato correcto
                    char[] conncheck = new char[8];
                    for (int i = 0; i < 8; i++) {
                        conncheck[i] = command.charAt(i);
                    }
                    if (Objects.equals(Arrays.toString(conncheck), "CONNECT ") && (command.length() > 8)) {
                        ArrayList<Character> userlist = new ArrayList<>();
                        for (int i = 8; i < command.length(); i++) {
                            userlist.add(command.charAt(i));
                        }
                        StringBuilder sb = new StringBuilder();
                        for (Character u : userlist) {
                            sb.append(u);
                        }
                        String username = sb.toString();
                        users.add(username);
                    }
                }
                if (command.contains("DISCONNECT ")) {
                    //checar si el comando que contiene "DISCONNECT " tiene el formato correcto
                    char[] disccheck = new char[11];
                    for (int i = 0; i < 11; i++) {
                        disccheck[i] = command.charAt(i);
                    }
                    if (Objects.equals(Arrays.toString(disccheck), "DISCONNECT ") && (command.length() > 11)) {
                        ArrayList<Character> userlist = new ArrayList<>();
                        for (int i = 11; i < command.length(); i++) {
                            userlist.add(command.charAt(i));
                        }
                        StringBuilder sb = new StringBuilder();
                        for (Character u : userlist) {
                            sb.append(u);
                        }
                        String username = sb.toString();
                        users.remove(username);
                    }
                }
                if (command.contains("LIST ")) {
                    //checar si el comando que contiene "LIST " tiene el formato correcto

                    char[] listcheck = new char[5];
                    for (int i = 0; i < 5; i++) {
                        listcheck[i] = command.charAt(i);
                    }
                    if (Objects.equals(Arrays.toString(listcheck), "DISCONNECT ") && (command.length() > 5)) {

                        ArrayList<Character> atlist = new ArrayList<>();
                        for (int i = 5; i < command.length(); i++) {
                            atlist.add(command.charAt(i));
                        }
                        StringBuilder sb = new StringBuilder();
                        for (Character u : atlist) {
                            sb.append(u);
                        }
                        String atuser = sb.toString();
                        String list = users.toString();
                        out.println('@' + atuser + " LIST " + list);
                    }
                    if (command.contains("SEND #") && command.contains("@")) {
                        //checar si el comando que contiene "SEND #" tiene el formato correcto
                        char[] sendcheck = new char[6];
                        for (int i = 0; i < 6; i++) {
                            sendcheck[i] = command.charAt(i);
                        }
                        String send = Arrays.toString(sendcheck);

                        if (send.equals("SEND #") && (command.length() > 6)) {
                            ArrayList<Character> messagelist = new ArrayList<>();
                            int count = 6;
                            while (count < command.length()) {
                                messagelist.add(command.charAt(count));
                                count++;
                            }
                            String message = messagelist.toString();
                            out.println(message);
                        }
                    }
                    clientSocket.close();
                }
            }
        } catch(IOException e){
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    public static boolean nameTaken(String newuser){
        int c = 0;
        for (String user : users) {
            if (user.equals(newuser)) {
                c++;
            }
        }
        return c <= 0;
    }

    public static void serverClosed(){
        closed = true;
    }
}