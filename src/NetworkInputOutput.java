import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*
 * Copyright 2020, for Ahmad
 *
 * This software is released under the terms of the
 * GNU GPL license. See https://www.gnu.org/licenses/gpl-3.0.html
 * for more information.
 *
 * @author Ahmad
 * @date 1/15/2020
 * @package abstractSocket
 */

public class NetworkInputOutput {
    private Socket socket;

    /**
     * @param IP of the receiver
     * @param port of the application lessening for requests
     * @throws IOException if creating socket raise an exception
     */
    public NetworkInputOutput(String IP, int port) throws IOException {
        socket = new Socket(IP, port);
    }

    public NetworkInputOutput(Socket socket){
        this.socket = socket;
    }

    /**
     * closing communication socket and release the port
     *
     * @throws IOException when closing socket encounter a problem
     */
    public void close() throws IOException {
        socket.close();
    }

    /**
     * when sending data raise a problem
     *
     * @return data sent to this communication channel
     * @throws IOException when communication channel encounter a problem in receiving data
     */
    public String readUTF() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        return in.readUTF();
    }

    /**
     * @param data data to be send on this channel
     * @throws IOException when sending data encounter a problem
     */
    public void writeUTF(String data) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(data);
    }

    /**
     * @return integer value of data send by this channel
     * @throws IOException when communication channel encounter a problem in receiving data
     */
    public int readInt() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        return in.readInt();
    }

    /**
     * @param number data to be send on this channel
     * @throws IOException when sending data encounter a problem
     */
    public void writeInt(int number) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(number);
    }
}
