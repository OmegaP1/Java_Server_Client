package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import shared.Mensagem;
import shared.Trabalhos;

public class DealWithTrabalhador extends Thread {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Memoria memoria;
	private Trabalhos trabalhoAfazer;

	public DealWithTrabalhador(Socket socket, Memoria memoria, ObjectOutputStream out, ObjectInputStream in)
			throws IOException {
		super();
		this.out = out;
		this.in = in;
		this.memoria = memoria;

	}

	public void run() {
		while (true) {
			try {
				System.out.println("A receber jobs");
				startSendingJobs(memoria.startSendingJobs());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public synchronized void startSendingJobs(Trabalhos trabalho) throws InterruptedException {
		try {

			trabalhoAfazer = trabalho;
			Mensagem mensagem = new Mensagem(trabalho);
			System.out.println("Envia msg ao worker");
			out.writeObject(mensagem);
			out.flush();
			receber();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private synchronized void receber() throws InterruptedException {
		try {
			System.out.println("Recebe msg do worker");
			Mensagem mensagem = (Mensagem) in.readObject();
			System.out.println(mensagem.getTipo() + "\n");
			trabalhoAfazer.getNovaParaProcura().setcontador(mensagem.getNPalavra().size());
			memoria.AdicionarTrabalhosFeitos(trabalhoAfazer);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
