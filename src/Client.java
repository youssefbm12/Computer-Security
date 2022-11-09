import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.StringTokenizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Client implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private int delay;
	private String actions;
	private int counter;

	@Override
	public String toString() {
		return "Client [username=" + username + ", password=" + password + ", delay=" + delay + ", actions=" + actions
				+ "]";
	}
	public String getActions()
	{
		return actions;
	}

	public Client(String username, String password, int delay, String actions) {

		this.username = username;
		this.password = password;
		this.delay = delay;
		this.actions = actions;
		this.counter = 0;
	}

	public int getDelay()
	{
		return delay;
	}

	public int getCounter() {
		return counter;
	}

	public void increaseCounter(int amount) {
		counter += amount;
	}

	public void decreaseCounter(int amount) {
		counter -= amount;
	}

	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}

			if (bufferedWriter != null) {
				bufferedWriter.close();
			}

			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFile() {
		JFrame chooser_f = new JFrame();
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json", "JSON");
		chooser.setFileFilter(filter);
		chooser.showOpenDialog(chooser_f);
		return chooser.getSelectedFile().getPath();
	}

	public static void main(String[] args) throws Exception {

		String filename = "";
		if(args.length != 0)
		{
			filename = args[0];
		}
		else {
			throw new Exception("Json file not specified");
		}

		JsonParser jsonParser = new JsonParser();

		try (FileReader reader = new FileReader(filename)) {

			JsonObject clienteObjeto = (JsonObject) jsonParser.parse(reader);

			// data
			String id = clienteObjeto.get("id").getAsString();
			// data
			String pass = clienteObjeto.get("password").getAsString();

			JsonObject server = clienteObjeto.getAsJsonObject("server");

			// data
			String ipServer = server.get("ip").getAsString();
			// data
			int portServer = Integer.parseInt(server.get("port").getAsString());

			JsonObject jsoActions = clienteObjeto.getAsJsonObject("actions");

			// data
			String sDelay = jsoActions.get("delay").getAsString();

			int delay = Integer.parseInt(sDelay) * 1000;



			// data
			JsonArray jsaSteps = jsoActions.getAsJsonArray("steps");

			String actions = "";

			for (JsonElement e : jsaSteps) {

				String opAmount = e.getAsString();

				StringTokenizer st = new StringTokenizer(opAmount, " ");

				String op = st.nextToken();

				int amount = Integer.parseInt(st.nextToken());

				String a = op + " " + amount;
				actions += a + ",";
			}

			// donde dice localhost es la IP: localhost es 127.0.0.1 y 1234 es el port
			Socket socket = new Socket(ipServer, portServer);


			Client client = new Client(id, pass, delay, actions);



			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(client);


			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

			Output outA = (Output) objectInputStream.readObject();
			System.out.println(outA.getStatus_description());
			if (outA.getStatus() == Status.OFFLINE)
				System.exit(0);


			Output outB = (Output) objectInputStream.readObject();
			System.out.println(outB.getStatus_description());
			if (outB.getStatus() == Status.OFFLINE)
				System.exit(0);




		} catch (IOException | JsonParseException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public String getID() {

		return username;
	}
/*
	public static void counter_method() {
		Scanner value = new Scanner(System.in);
		while (true) {
			boolean name = test();
			if (name) {
				System.out.println("How much do you want to increase ");
				double value_1 = value.nextDouble();
				Server.counter += value_1;
				System.out.println("The counter:" + Server.counter);
			} else {
				System.out.println("How much do you want to decrease ");
				double value_1 = value.nextDouble();
				Server.counter = Server.counter - value_1;
				System.out.println("The counter is equal to : " + Server.counter);
			}
		}
	}
*/
	public String getPassword() {
		return password;
	}

	public static String twoFac() {
		Scanner scanner = new Scanner(System.in);
		String s = "";
		System.out.print("Please input your two factor authentication code:\t");
		while (scanner.hasNextLine()) {
			s = scanner.nextLine();
			if (!Objects.equals(s, ""))
				break;
		}

		return s;
	}
}