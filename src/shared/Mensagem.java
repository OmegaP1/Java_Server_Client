package shared;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

public class Mensagem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DefaultListModel<String> lsita;
	private Trabalhos trabalho;
	private int index;
	private ArrayList<Integer> nPalavra;
	private String texto;
	private int tipo;

	public Mensagem(int index) {
		super();
		this.tipo = 0;
		this.index = index;
	}

	public Mensagem(String texto) {
		super();
		this.tipo = 1;
		this.texto = texto;
	}

	public Mensagem(DefaultListModel<String> lsita) {
		this.tipo = 2;
		this.lsita = lsita;
	}

	public Mensagem() {
		this.tipo = 3;
	}

	public Mensagem(Trabalhos trabalho) {
		this.trabalho = trabalho;
		this.tipo = 4;
	}

	public Mensagem(ArrayList<Integer> nPalavra) {
		this.nPalavra = nPalavra;
		this.tipo = 5;
	}

	public int getIndex() {
		return index;
	}

	public String getTexto() {
		return texto;
	}

	public int getTipo() {
		return tipo;
	}

	public DefaultListModel<String> getLsita() {
		return lsita;
	}

	public Trabalhos getTrabalho() {
		return trabalho;
	}

	public ArrayList<Integer> getNPalavra() {
		return nPalavra;
	}
}