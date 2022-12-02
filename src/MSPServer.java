import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MSPServer {
    static ArrayList<String> users;
    static boolean closed;

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
            closed = false;

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

                //crear formas de revisar el formato
                ArrayList<Character> checklist = new ArrayList<>();
                String conncheck, disccheck, sendcheck, listcheck;
                int counter;

                //CONNECT
                counter = 0;
                while(counter < command.length() && (counter < 7)){
                    checklist.add(command.charAt(counter));
                    counter++;
                }
                conncheck = checklist.toString();
                checklist.clear();

                //DISCONNECT
                counter = 0;
                while(counter < command.length() && (counter < 11)){
                    checklist.add(command.charAt(counter));
                    counter++;
                }
                disccheck = checklist.toString();
                checklist.clear();

                //LIST
                counter = 0;
                while(counter < command.length() && (counter < 5)){
                    checklist.add(command.charAt(counter));
                    counter++;
                }
                listcheck = checklist.toString();
                checklist.clear();

                //SEND #
                counter = 0;
                while(counter < command.length() && (counter < 6)){
                    checklist.add(command.charAt(counter));
                    counter++;
                }
                sendcheck = checklist.toString();
                checklist.clear();

                if (conncheck.equals("CONNECT ")) {
                    //checar si el comando que contiene "CONNECT " tiene el formato correcto
                    ArrayList<Character> userlist = new ArrayList<>();
                    for (int i = 8; i < command.length(); i++) {
                        userlist.add(command.charAt(i));
                    }
                    String username = userlist.toString();
                    users.add(username);
                }
                if (disccheck.equals("DISCONNECT ")) {
                    //checar si el comando que contiene "DISCONNECT " tiene el formato correcto
                    ArrayList<Character> userlist = new ArrayList<>();
                    for (int i = 11; i < command.length(); i++) {
                        userlist.add(command.charAt(i));
                    }
                    String username = userlist.toString();
                    users.remove(username);
                }
                if (listcheck.equals("LIST ")) {
                    //checar si el comando que contiene "LIST " tiene el formato correcto
                    ArrayList<Character> atlist = new ArrayList<>();
                    for (int i = 5; i < command.length(); i++) {
                        atlist.add(command.charAt(i));
                    }
                    String atuser = atlist.toString();
                    String list = users.toString();
                    out.println('@' + atuser + " LIST " + list);
                }
                if (sendcheck.equals("SEND #")) {
                    //checar si el comando que contiene "SEND #" tiene el formato correcto
                    ArrayList<Character> messagelist = new ArrayList<>();
                    int count = 6;
                    while (count < command.length()) {
                        messagelist.add(command.charAt(count));
                        count++;
                    }
                    String message = messagelist.toString();
                    out.println(message);
                }
                clientSocket.close();
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
        System.out.println("Fin de operaciones");
        System.exit(0);
    }
}