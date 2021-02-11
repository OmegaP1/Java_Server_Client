package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultListModel;

import shared.Trabalhos;
import shared.Mensagem;
import shared.Noticia;

public class DealWithCliente extends Thread {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ArrayList<Noticia> NoticiasFeitas;
	private ArrayList<Noticia> Noticias;
	private Servidor servidor;
	private Socket socket;
	private boolean fim;
	private Memoria memoria;

	public DealWithCliente(Socket socket, ArrayList<Noticia> Noticias, Memoria memoria, ObjectOutputStream out,
			ObjectInputStream in, Servidor servidor) {
		this.socket = socket;
		this.Noticias = Noticias;
		this.servidor = servidor;
//		while (servidor.getTodaNoticias().size() != 0) {
//			System.out.println(servidor.getTodaNoticias().size());
//			try {
////				this.Noticias.add(new Noticia(servidor.pollFromTodasNoticias().gettitulo(),
////						servidor.pollFromTodasNoticias().gettexto()));
//				this.Noticias.add(servidor.pollFromTodasNoticias());
//				System.out.println("ola");
//				System.out.println("olaaaaa" + Noticias.get(0));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		this.memoria = memoria;
		NoticiasFeitas = new ArrayList<Noticia>();
		this.fim = false;
		this.out = out;
		this.in = in;
	}

	@Override
	public void run() {
		while (true) {

			Mensagem mensagem = null;
			System.out.println("receber mensgaem \n");
			try {
				mensagem = (Mensagem) in.readObject();
				System.out.println("recebeu msg Cliente");
				dealWithMessageClient(mensagem);
				if (fim) {
					System.out.println("Deu fim");
					out.flush();
					out.close();
					in.close();
					socket.close();
					break;
				}
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("vai fechar");
				break;
			}
		}
	}

	private void dealWithMessageClient(Mensagem mensagem) {
		switch (mensagem.getTipo()) {
		case 0:
			try {
				System.out.println("0\n");
				out.writeObject(new Mensagem(getTexto(mensagem.getIndex())));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 1:
			System.out.println("1\n");
			CriarTrabalhos(mensagem.getTexto());
			break;
		case 3:
			System.out.println("3\n");
			fim = true;
			break;
		}
	}

	public String getTexto(int index) {
		return NoticiasFeitas.get(index).gettexto();
	}

	public void CriarTrabalhos(String textToFind) {
		ArrayList<Trabalhos> TrabalhoAFazer = new ArrayList<Trabalhos>();
		for (Noticia novaProcura : Noticias) {

			Trabalhos Trabalho = new Trabalhos(novaProcura, textToFind, socket.getPort());
			TrabalhoAFazer.add(Trabalho);
		}
		System.out.println("cria jobs");
		memoria.addJobsToDo(TrabalhoAFazer);
		receiveNews();
	}

	public void receiveNews() {
		NoticiasFeitas = memoria.clientNews(getClientPort(), servidor);
		// usa o compare too
		Collections.sort(NoticiasFeitas);
		DefaultListModel<String> listaFinal = new DefaultListModel<String>();
		for (int i = 0; i < NoticiasFeitas.size(); i++) {
			listaFinal.addElement(NoticiasFeitas.get(i).getcontador() + " - " + NoticiasFeitas.get(i).gettitulo());
			System.out.println(
					"Noticias" + NoticiasFeitas.get(i).getcontador() + " - " + NoticiasFeitas.get(i).gettitulo());
			try {
				servidor.addToFinal(NoticiasFeitas.get(i));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Mensagem mensagem = new Mensagem(listaFinal);
		try {
			System.out.println("Envia lista de Resultados ao Cliente");
			out.writeObject(mensagem);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getClientPort() {
		return socket.getPort();
	}
}