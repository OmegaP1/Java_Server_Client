package cliente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import shared.Mensagem;

public class Cliente {

	private static int SERVER_PORT;
	private Socket socket;
	private String nome;
	private Janela Janela;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private String novoTexto;
	private boolean fim;
	private DefaultListModel<String> listaNoticias;

	public Cliente(int serverPort, String nome) {
		super();
		SERVER_PORT = serverPort;
		this.nome = nome;
		this.fim = false;
		this.Janela = new Janela(this);
		Janela.run();
		try {
			Ligacao();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Ligacao() throws IOException {
		boolean ligacao = false;
		while (!ligacao) {
			try {
				System.out.println("****************************** \n");
				InetAddress address = InetAddress.getByName(null);
				socket = new Socket(address, SERVER_PORT);
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
				System.out.println(socket.getInetAddress() + " " + nome + " ligacao");
				out.writeObject(new Mensagem("Joao_o_Cliente"));
				System.out.println("\n****************************** \n");
				out.flush();
				ligacao = true;
				dealWithMessage();
			} catch (SocketException e) {
				try {
					System.out.println("entra no catch");
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void enviar(Mensagem mensagem) {
		switch (mensagem.getTipo()) {
		case 3:
			closeConnection();
			break;
		}
		try {
			out.writeObject(mensagem);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setnovoTexto(String novoTexto) {
		this.novoTexto = novoTexto;
	}

	public void setlistaNoticias(DefaultListModel<String> listaNoticias) {
		this.listaNoticias = listaNoticias;
	}

	public String getnovoTexto() {
		return novoTexto;
	}

	public DefaultListModel<String> getlistaNoticias() {

		return listaNoticias;
	}

	protected void closeConnection() {
		try {
			fim = true;
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void dealWithMessage() {
		while (true) {
			Mensagem mensagem = null;

			try {
				mensagem = (Mensagem) in.readObject();
				System.out.println(mensagem.getTipo() + "\n");
				Janela.Status(true);
				switch (mensagem.getTipo()) {
				case 1:
					dealWithMessagecase1(mensagem);
					break;
				case 2:
					dealWithMessagecase2(mensagem);
					break;
				}

			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				try {
					if (!fim) {
						socket.close();
						Janela.Status(false);
						Ligacao();
					} else {
						break;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void dealWithMessagecase1(Mensagem mensagem) {
		System.out.println("1");
		setnovoTexto(mensagem.getTexto());
		Janela.getNovoTexto();
	}

	public void dealWithMessagecase2(Mensagem mensagem) {
		System.out.println("2");
		setlistaNoticias(mensagem.getLsita());
		Janela.obterNoticias();
	}

	public static void main(String[] args) {
		new Cliente(8080, "Joao");
	}

	class Janela {

		private Cliente cliente;
		private JFrame app;
		private JList<String> n;
		private JTextArea novotexto;
		protected boolean Status;
		private JTextField butaoProcura;

		public Janela(final Cliente cliente) {
			initialize();
			text();
			search();
			this.cliente = cliente;
			app.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.out.println("Closed");
					cliente.closeConnection();
					System.exit(0);
				}
			});
		}

		private void initialize() {
			app = new JFrame("ISCTE  Searcher...");
			app.setName("ISCTE  Searcher... ");
			app.getContentPane().setSize(new Dimension(10, 10));
			app.setBounds(100, 100, 777, 645);
			app.getContentPane().setLayout(new BorderLayout(0, 0));
		}

		private void text() {
			JPanel bottom = new JPanel();
			app.getContentPane().add(bottom, BorderLayout.CENTER);
			bottom.setLayout(new GridLayout(1, 0, 0, 0));
			n = new JList<String>();
			bottom.add(n);
			JScrollPane text = new JScrollPane(n);
			bottom.add(text);
			novotexto = new JTextArea();
			bottom.add(novotexto);
		}

		private void search() {
			JPanel panel1 = new JPanel(new GridLayout(1, 2));
			panel1.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel1.setAlignmentY(Component.TOP_ALIGNMENT);
			app.getContentPane().add(panel1, BorderLayout.NORTH);
			panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

			butaoProcura = new JTextField();
			butaoProcura.setPreferredSize(new Dimension(20, 20));
			butaoProcura.setSize(new Dimension(10, 10));
			butaoProcura.setMinimumSize(new Dimension(10, 100));
			panel1.add(butaoProcura);
			butaoProcura.setColumns(25);

			JButton btnNewButton = new JButton("  Search  ");
			btnNewButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String texto = butaoProcura.getText();
					novotexto.setText("");
					System.out.println("Enviou " + butaoProcura.getText());
					Mensagem m = new Mensagem(texto);
					cliente.enviar(m);

				}
			});

			n.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (n.getSelectedIndex() >= 0 && Status) {
						Mensagem m = new Mensagem(n.getSelectedIndex());
						cliente.enviar(m);
					}

				}
			});
			panel1.add(btnNewButton);
		}

		protected void Status(boolean s) {
			n.setEnabled(s);
			Status = s;
		}

		public void obterNoticias() {
			n.setModel(cliente.getlistaNoticias());
			System.out.println("Janela: Lista Recebida");
		}

		public void getNovoTexto() {
			System.out.println(cliente.getnovoTexto());
			novotexto.setText(cliente.getnovoTexto());
		}

		public void run() {
			app.setVisible(true);
		}
	}
}
