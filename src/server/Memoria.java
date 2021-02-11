package server;

import java.util.ArrayList;

import shared.Noticia;
import shared.Trabalhos;

public class Memoria {

	public ArrayList<DealWithCliente> ListaClientes = new ArrayList<DealWithCliente>();
	public ArrayList<Integer> contador = new ArrayList<Integer>();
	public ArrayList<Trabalhos> TrabalhoAFazer = new ArrayList<Trabalhos>();
	public ArrayList<Trabalhos> TrabalhoFeito = new ArrayList<Trabalhos>();

	public synchronized void addJobsToDo(ArrayList<Trabalhos> trabalho) {

		contador.set(Index(trabalho.get(0).getPorto()), trabalho.size());
		for (int i = 0; i < trabalho.size(); i++) {
			TrabalhoAFazer.add(trabalho.get(i));
		}

		System.out.println("FORAM ADICIONADOS trabalho");
		notifyAll();
	}

	public synchronized ArrayList<Noticia> clientNews(int clientPort, Servidor s) {
		ArrayList<Noticia> clientNews = new ArrayList<Noticia>();
		ArrayList<Trabalhos> jobDoneTemp = new ArrayList<Trabalhos>();
		if (!areMyJobsReady(clientPort)) {
			System.out.println("Cliente a espera de obter resultados");
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < TrabalhoFeito.size(); i++) {
			if (TrabalhoFeito.get(i).getPorto() == clientPort) {
				if (TrabalhoFeito.get(i).getNovaParaProcura().getcontador() != 0) {
					clientNews.add(TrabalhoFeito.get(i).getNovaParaProcura());
				}
			} else {
				try {
					s.pollFromFinal();
					System.out.println("Final esta com:" + s.getNoticiasfinais().size());
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				jobDoneTemp.add(TrabalhoFeito.get(i));
			}
		}

		TrabalhoFeito.clear();
		TrabalhoFeito.addAll(jobDoneTemp);
		return clientNews;
	}

	public synchronized void AdicionarTrabalhosFeitos(Trabalhos trabalho) {
		TrabalhoFeito.add(trabalho);
		contador.set(Index(trabalho.getPorto()), contador.get(Index(trabalho.getPorto())) - 1);
		if (areMyJobsReady(trabalho.getPorto())) {
			System.out.println("Notifica que foram adicionados trabalho");
			notifyAll();
		}
	}

	private boolean areMyJobsReady(int clientPort) {
		for (int i = 0; i < ListaClientes.size(); i++) {
			if (ListaClientes.get(i).getClientPort() == clientPort) {
				if (contador.get(i) == 0) {

					System.out.println("Os trabalho já estao prontos");
					return true;
				}
			}
		}
		System.out.println("Os trabalho ainda não estao prontos");
		return false;
	}

	public void addClient(DealWithCliente client) {
		ListaClientes.add(client);
		contador.add(0);
	}

	public synchronized Trabalhos startSendingJobs() {
		while (TrabalhoAFazer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Trabalhos trabalho = TrabalhoAFazer.remove(0);
		return trabalho;
	}

	private int Index(int clientPort) {
		int index = 0;
		for (int i = 0; i < ListaClientes.size(); i++) {
			if (ListaClientes.get(i).getClientPort() == clientPort) {
				index = i;
			}
		}
		return index;
	}

}