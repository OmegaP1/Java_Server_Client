package shared;
import java.io.Serializable;

public class Noticia implements Comparable<Noticia>, Serializable {

	
	private static final long serialVersionUID = 1L;
	private String titulo;
	private String texto;
	private int contador;

	public Noticia(String titulo, String texto) {
		super();
		this.titulo = titulo;
		this.texto = texto;
	}

	public String gettitulo() {
		return titulo;
	}

	public String gettexto() {
		return texto;
	}

	public void setcontador(int contador) {
		this.contador = contador;
	}

	public int getcontador() {
		return contador;
	}

	@Override
	public int compareTo(Noticia arg0) {
		return arg0.getcontador() - getcontador();
	}
}