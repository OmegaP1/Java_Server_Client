package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import shared.BlockingQueue;
import shared.Mensagem;
import shared.Noticia;

public class Servidor {

	private ArrayList<Noticia> noticias;
	public Memoria memoria;
	private File ficheiro = new File("news");
	public BlockingQueue<Noticia> noticiasfinais = new BlockingQueue<Noticia>();
	public BlockingQueue<Noticia> TodaNoticias = new BlockingQueue<Noticia>();
	public boolean bq = false;

	public Servidor() {
		memoria = new Memoria();
		noticias = adicionarNoticias();
		startServing();
	}

	public void startServing() {
		ServerSocket s = null;
		Socket socket = null;

		try {
			s = new ServerSocket(8080);
			EnviarSoutInical(s);
			while (true) {
				socket = s.accept();
				System.out.println(s);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

				Mensagem mensagem = (Mensagem) in.readObject();
				System.out.println(mensagem.getTipo() + "\n");

				switch (mensagem.getTexto()) {
				case "Joao_o_Cliente":
					DealWithCliente client = new DealWithCliente(socket, noticias, memoria, out, in, this);
					client.start();
					memoria.addClient(client);
					break;
				case "Afonso_O_Trabalhador":
					DealWithTrabalhador work = new DealWithTrabalhador(socket, memoria, out, in);
					work.start();
					break;
				}
			}

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

	public void EnviarSoutInical(ServerSocket s) {

		System.out.println("************************************************************************** \n");
		System.out.println("Lançou ServerSocket: " + s + " \n");
		if (bq)
			System.out.println("Todas as noticias foram Carregadas com exito para a BQ");
		System.out.println("Servidor lancado com sucesso!");
		System.out.println("\n**************************************************************************");
	}

	public ArrayList<Noticia> adicionarNoticias() {
		ArrayList<Noticia> temporaria = new ArrayList<Noticia>();
		File[] todosFicheiros = ficheiro.listFiles();
		for (File ficheiro : todosFicheiros) {
			try {
				Scanner scan = new Scanner(ficheiro, "UTF-8");
				while (scan.hasNextLine()) {
					String titulo = scan.nextLine();
					String texto = titulo + "\n \n" + scan.nextLine();
					Noticia n = new Noticia(titulo, texto);
					temporaria.add(n);
					addToTodasNoticias(n);
					if (getTodaNoticias().size() == 648) {
						System.out.println(getTodaNoticias().size());
						bq = true;
					}
				}
				scan.close();
			} catch (FileNotFoundException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return temporaria;
	}

	public void addToTodasNoticias(Noticia n) throws InterruptedException {
		TodaNoticias.offer(n);
	}

	public Noticia pollFromTodasNoticias() throws InterruptedException {
		Noticia p;
		p = TodaNoticias.poll();
		return p;
	}

	public void addToFinal(Noticia p) throws InterruptedException {
		noticiasfinais.offer(p);
	}

	public Noticia pollFromFinal() throws InterruptedException {
		Noticia p;
		p = noticiasfinais.poll();
		return p;
	}

	public BlockingQueue<Noticia> getNoticiasfinais() {
		return noticiasfinais;
	}

	public BlockingQueue<Noticia> getTodaNoticias() {
		return TodaNoticias;
	}

	public static void main(String[] args) {
		new Servidor();

	}

}