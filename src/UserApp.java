import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserApp {
  public static void main(String[] args) throws InterruptedException, IOException {
    String MapperReducerIP = "172.28.0.3";
    //        /home/ahmad/Downloads/input.txt

    String IP = "";
    while (!IP.matches(
        "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
            + "\\."
            + "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
            + "\\."
            + "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])"
            + "\\."
            + "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])")) {

      try {
        NetworkInputOutput net = new NetworkInputOutput(MapperReducerIP, 7302);

        net.writeUTF("Get");
        IP = net.readUTF();
        net.close();
      } catch (Exception e) {
        Thread.sleep(1000);
      }
    }

    NetworkInputOutput net = new NetworkInputOutput(IP, 7200);

    File file = new File("temp.txt");
    while (!file.isFile()) {
      System.out.print("Enter your file path: ");
      String filePath = new Scanner(System.in).nextLine();
      file = new File(filePath);
    }

    String name = file.getName();

    net.writeUTF(name);

    BufferedReader reader = new BufferedReader(new FileReader(file));

    String line;

    while ((line = reader.readLine()) != null) {
      net.writeUTF(line);
    }

    net.writeUTF("\n");

    net.close();

    int numberM = getNumber("Enter number of Mappers: ");

    sendMessage(IP, 9003, numberM);

    int numberR = getNumber("Enter number of Reducers: ");

    sendMessage(IP, 9503, numberR);

    File file1 = new File("/home/ahmad/allMapperAndReducers.yml");

    if (file1.isFile()) {
      boolean del = file1.delete();
      if (del) {
        file1.createNewFile();
      }
    }

    System.out.println(file1.getAbsolutePath());
    BufferedWriter writer = new BufferedWriter(new FileWriter(file1, true));

    writeToFile(writer, "version: '3'\n" + "\n" + "services:\n");

    for (int i = 1; i <= numberM; i++) {
      writeToFile(
          writer,
          "  Mapper"
              + i
              + ":\n"
              + "    tty: true\n"
              + "    image: ahmadsalih/mymapper\n"
              /*+ "    command: ./var/Mapper.sh\n"
              + "    volumes:\n"
              + "      - /home/ahmad/IdeaProjects/Project/All/:/Project\n"*/
              + "\n");
    }

    for (int i = 1; i <= numberR; i++) {
      writeToFile(
          writer,
          "  Reducer"
              + i
              + ":\n"
              + "    tty: true\n"
              + "    image: ahmadsalih/myreducer\n"
              /*+ "    command: ./var/Reducer.sh\n"
              + "    volumes:\n"
              + "      - /home/ahmad/IdeaProjects/Project/All/:/Project\n"*/
              + "\n");
    }

    writer.close();

    waitForOutput(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("/")));
  }

  private static int getNumber(String message) {
    int number = 0;
    while (number <= 0) {
      System.out.print(message);
      number = new Scanner(System.in).nextInt();
    }

    return number;
  }

  private static void sendMessage(String IP, int port, int number) throws IOException {
    NetworkInputOutput net = new NetworkInputOutput(IP, port);

    net.writeInt(number);

    net.close();
  }

  public static void waitForOutput(String path) throws IOException {
    ServerSocket serverSocket = new ServerSocket(7210);
    Socket socket = serverSocket.accept();

    DataInputStream in = new DataInputStream(socket.getInputStream());
    String name = in.readUTF();
    File file = new File(path + "/"+ name);
    if (file.isFile()) {
      boolean del = file.delete();
      if (del) {
        file.createNewFile();
      }
    }

    BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
    String line;
    while (!(line = in.readUTF()).equalsIgnoreCase("\n")) {
      writer.write(line);
      writer.flush();
    }

    serverSocket.close();
  }

  public static void writeToFile(BufferedWriter writer, String data) throws IOException {
    writer.write(data);
    writer.flush();
  }

  public static String broadcast(String broadcastMessage, InetAddress address, int port)
      throws IOException {
    DatagramSocket socket = new DatagramSocket();
    socket.setBroadcast(true);

    byte[] buffer = broadcastMessage.getBytes();

    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
    socket.send(packet);
    socket.setSoTimeout(1000);
    socket.receive(packet);

    String returned = new String(packet.getData()).trim();
    socket.close();

    String IPADDRESS_PATTERN =
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
    Matcher matcher = pattern.matcher(returned);
    if (matcher.find()) {
      return matcher.group();
    } else {
      return "";
    }
  }
}
