import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Server {

	private static HashMap<String, Client> clients = new HashMap<String, Client>(13);

	private static ArrayList<User> users = new ArrayList<>();

	public static void generate_users() throws IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
		JsonParser jsonParser = new JsonParser();
		JsonArray users_object = (JsonArray) jsonParser.parse(decrypt("resources/enc.file"));

		int users_s = users_object.size();

		for (int i = 0; i < users_s; i++) {
			User user = new User(
					users_object.get(i).getAsJsonObject().get("id").getAsString(),
					users_object.get(i).getAsJsonObject().get("password").getAsString(),
					users_object.get(i).getAsJsonObject().get("secret-key").getAsString());
			users.add(user);
		}

	}

	private static String decrypt(String path) throws IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
		Scanner reader = new Scanner(new File(path));
		String output = "";
		while (reader.hasNextLine())
		{
			String inString = reader.nextLine();
			String[] sections = inString.split("\\|");
			byte[] input = new byte[sections.length];

			for (int i = 0; i < input.length; i++)
			{
				input[i] = (byte) Integer.parseInt(sections[i]);
			}

			output += Encrypto.getString(Encrypto.decode(input, "bananananana")) + "\n";

		}
		return output;

	}



	private static String generateNum(Client client) {
		String secretKey = "";
		for(User u : users)
		{
			if(Objects.equals(u.getId(), client.getID()))
				secretKey = u.getKey();
		}
		String lastCode = null;
		while (true) {
			Base32 base32 = new Base32();
			byte[] bytes = base32.decode(secretKey);
			String hexKey = Hex.encodeHexString(bytes);
			String code = TOTP.getOTP(hexKey);
			if (!code.equals(lastCode)) {
				System.out.println("CODE--- " + code);
				return code;
			}
			lastCode = code;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {};
		}
	}

	private static boolean check_id(Client client)
	{
		boolean found = false;
		for (User u : users)
		{
			if(Objects.equals(client.getID(), u.getId()))
				found = true;

		}
		return found;
	}

	private static boolean check_password(Client client)
	{
		User user = null;
		for (User u : users)
			if(Objects.equals(u.getId(), client.getID()))
				user = u;

		return Objects.equals(user.getPassword(), client.getPassword());
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		try {
			generate_users();
		} catch (FileNotFoundException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
			e.printStackTrace();
			System.out.println("Failed to read users.\nShutting down server.");
			System.exit(0);
		}

		ServerSocket ss = new ServerSocket(1234);

		System.out.println("Server awaiting connections...");

		while (true) {
			Socket socket = ss.accept();

			System.out.println("Connection from " + socket + "!");

			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

			Client client = (Client) objectInputStream.readObject();

			if (!check_id(client)) {
				objectOutputStream.writeObject(new Output(Status.OFFLINE, "User not found."));
				System.out.println("Closing socket for user: " + socket + "\nReason: User not found.");

			} else if (!check_password(client)) {
				objectOutputStream.writeObject(new Output(Status.OFFLINE, "Password do not match."));
				System.out.println("Closing socket for user: " + socket + "\nReason: Password do not match.");
			}else if (clients.containsKey(client.getID())) {
				objectOutputStream.writeObject(new Output(Status.OFFLINE, "Client already active on the server."));
				System.out.println("Closing socket for user: " + socket + "\nReason: Client already active on the server.");
			}
			else {
				objectOutputStream.writeObject(new Output(Status.IDLE, "Correct password."));

				clients.put(client.getID(), client);


				ClientThread ct = new ClientThread( client, objectOutputStream);
				ct.start();


				System.out.println("Number of clients: " + clients.size());

				for (Client c : clients.values()) {
					System.out.println(c);
				}
			}
		}
	}
}
