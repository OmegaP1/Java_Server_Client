package shared;

import java.io.Serializable;

public class Trabalhos implements Serializable {

	private static final long serialVersionUID = 1L;
	private int Porto;
	private String palavraAProcurar;
	private Noticia NovaParaProcura;

	public Trabalhos(Noticia NovaParaProcura, String palavraAProcurar, int Porto) {
		super();
		this.NovaParaProcura = NovaParaProcura;
		this.palavraAProcurar = palavraAProcurar;
		this.Porto = Porto;
	}

	public int getPorto() {
		return Porto;
	}

	public String getpalavraAProcurar() {
		return palavraAProcurar;
	}

	public Noticia getNovaParaProcura() {
		return NovaParaProcura;
	}

}
