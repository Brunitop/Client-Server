import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class MSPClient {
    static String sendcheck;
    public static void main(String[] args) {
        Scanner ns = new Scanner(System.in);
        String hostName = "127.0.0.1";

        try {
            // abrir la conexion con el servidor
            Socket theSocket = new Socket(hostName, 1313);
            // Obtener flujo de salida
            PrintWriter out
                    = new PrintWriter(theSocket.getOutputStream(), true);
            // Obtener flujo de entrada
            BufferedReader in = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
            String command;

            //conectarse al servidor
            System.out.println("Para conectarse al servidor, ingrese la cadena 'CONNECT'" +
                    " mas el nombre del usuario que va a utilizar.");
            System.out.println("Si esta conectado y desea no continuar, ingrese 'DISCONNECT'.");
            System.out.print(">>");
            command = ns.next();
            ArrayList<Character> checklist = new ArrayList<>();
            String username, conncheck;

            int counter = 0;
            while(counter < command.length() && (counter < 7)){
                checklist.add(command.charAt(counter));
                counter++;
            }
            StringBuilder sb = new StringBuilder();
            for(char a: checklist){
                sb.append(a);
            }
            conncheck = sb.toString();

            if (!conncheck.equals("CONNECT") && (command.length() < 9)){
                System.out.println("Comando no valido.");
            }
            else {
                ArrayList<Character> namelist = new ArrayList<>();
                for (int i = 8; i < command.length(); i++) {
                    namelist.add(command.charAt(i));
                }
                StringBuilder sbuser = new StringBuilder();
                for(char a: checklist){
                    sbuser.append(a);
                }
                username = sbuser.toString();

                //checa si el nombre de usuario ya está en uso
                if (MSPServer.nameTaken(username)) {
                    System.out.println("Ese nombre ya esta en uso.");
                } else {
                    //agregar nombre de usuario a la lista

                    // hilo que lee los mensajes que llegan desde el servidor
                    Thread client = new Thread(new ClientHandler(theSocket, username));
                    client.start();

                    String message, destination;
                    while (!command.equals("DISCONNECT")) {
                        //recibir comando
                        System.out.println(">>");
                        command = ns.next();

                        //checar si el comando es para enviar un mensaje
                        if (command.length() > 6) {
                            char[] send = new char[6];
                            for (int i = 0; i < 4; i++) {
                                send[i] = command.charAt(i);
                            }
                            StringBuilder sbsend = new StringBuilder();
                            for(char a: send){
                                sbsend.append(a);
                            }
                            sendcheck = sbsend.toString();
                        }

                        if (sendcheck.equals("SEND #") && command.contains("@")) {
                            ArrayList<Character> messagelist = new ArrayList<>();
                            ArrayList<Character> destlist = new ArrayList<>();
                            int count = 6;

                            //lista de todos los caracteres entre # y @
                            while (count < command.length() && command.charAt(count) != '@' && (count <= 146)) {
                                messagelist.add(command.charAt(count));
                                count++;
                            }
                            message = messagelist.toString();

                            //
                            if (command.length() > count) {
                                while (count < command.length()) {
                                    destlist.add(command.charAt(count));
                                    count++;
                                }
                            }
                            destination = destlist.toString();

                            out.println("SEND #" + message + '@' + destination);
                        }

                        if (command.equals("LIST")) {
                            out.println(command + " " + username);
                            message = null;
                            //variable para verificar que el mensaje de respuesta es para este usuario
                            String listcheck = null;
                            ArrayList<Character> arrlistcheck = new ArrayList<>();

                            //espera hasta que reciba un mensaje de respuesta para la funcion LIST con el formato correcto
                            while (!Objects.equals(listcheck, "@" + username + " LIST ")) {
                                message = in.readLine();
                                for (int i = 0; i < (username.length() + 7); i++) {
                                    arrlistcheck.add(message.charAt(i));
                                }
                                listcheck = arrlistcheck.toString();
                            }
                            ArrayList<Character> arrlist = new ArrayList<>();
                            //convierte solo la parte del mensaje con la informacion de la lista a una cadena
                            for (int i = (username.length() + 7); i < message.length(); i++) {
                                arrlist.add(message.charAt(i));
                            }
                            String list = arrlist.toString();
                            //imprime la cadena
                            System.out.println(list);
                        }
                        command = ns.next();
                    }
                    System.out.println("Usuario desconectado");
                    out.println("DISCONNECT " + username);
                    client.interrupt();
                    theSocket.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Error de formato");
            System.exit(1);
        }
    }
}