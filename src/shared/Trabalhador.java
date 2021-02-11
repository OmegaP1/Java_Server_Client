package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import shared.Mensagem;
import shared.Noticia;

public class Trabalhador extends Thread {

	private static int SERVER_PORT;
	private Socket socket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	public Trabalhador(int serverPort, String name) {
		super();
		SERVER_PORT = serverPort;
		try {
			Inic();
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Inic() throws IOException {
		boolean connected = false;
		while (!connected) {
			try {
				InetAddress address = InetAddress.getByName(null);
				socket = new Socket(address, SERVER_PORT);
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
				EnviarsoutInicial();
				out.writeObject(new Mensagem("Afonso_O_Trabalhador"));
				out.flush();
				connected = true;
			} catch (SocketException e) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void run() {
		while (true) {
			Receber();
		}
	}

	public void EnviarsoutInicial() {
		System.out.println("*****************************************************\n");
		System.out.println(socket.getInetAddress() + this.toString() + " Connected");
		System.out.println("\n*****************************************************");
	}

	public void Receber() {
		try {
			Mensagem mensagem = null;
			System.out.println("Recebe msg");
			mensagem = (Mensagem) in.readObject();
			System.out.println(mensagem.getTipo() + "\n");
			// recebe tipo 4 -- trabalho
			dealWithJobs(mensagem.getTrabalho().getpalavraAProcurar(), mensagem.getTrabalho().getNovaParaProcura());
		} catch (ClassNotFoundException | IOException e) {
			fecharLigacoes();
		}
	}

	public void fecharLigacoes() {
		try {
			socket.close();
			Inic();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void dealWithJobs(String textToFind, Noticia palavraNova) {
		try {
			Mensagem mensagem = new Mensagem(contador(textToFind, palavraNova));
			System.out.println("Envia msg para o Server");
			out.writeObject(mensagem);
			out.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public ArrayList<Integer> contador(String palavraProcurar, Noticia palavraNova) {
		String[] palavra = palavraNova.gettexto().split(" ");
		ArrayList<Integer> nPalavra = new ArrayList<Integer>();
		for (int i = 0; i < palavra.length; i++) {
			if (palavra[i].contains(palavraProcurar)) {
				nPalavra.add(i);
			}
		}
		palavraNova.setcontador(nPalavra.size());
		return nPalavra;
	}

	public static void main(String[] args) {
		new Trabalhador(8080, "Afonso");
	}
}