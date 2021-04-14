package br.unip.chatserver.model;

import java.io.IOException;

public class ClientActions extends ClientNotificationActions {

	protected ClientActions() {

	}

//	public void handleLeave(String[] tokens) {
//		if (tokens.length > 1) {
//			String topic = tokens[1];
//			topicSet.remove(topic);
//		}
//	}
//
//	private boolean isMemberOfTopic(String topic) {
//		return topicSet.contains(topic);
//	}
//
//	private void handleJoin(String[] tokens) {
//		if (tokens.length > 1) {
//			String topic = tokens[1];
//			topicSet.add(topic);
//		}
//	}

	// format: "msg" "login" body...
	// format: "msg" "#topic" body...
	// FIXME - Se o cliente não estiver logado, um nullpointer é lançado ao tentar enviar uma mensagem. (Talvez validar primeiro se o usuário está logado)
	// FIXME - Aparentemente, o primeiro usuario, ao logar, e tentar conversar com o outro usuário que acabou de logar depois dele. O primeiro usuário não consegue enviar uma mensagem
	// FIXME - Por mais que encontre o usuário que deseja mandar a msg, ele notifica que não o encotrou
	// FIXME - Correções gerais na classe.
	public static void handleMessage(ClientConnection client, String[] tokens) {
		String sendTo = tokens[1];
		String body = tokens[2];

		boolean isTopic = sendTo.charAt(0) == '#';

		for (ClientConnection clientDestinatario : Server.getClientList()) {
//			if (isTopic) {
//				if (worker.isMemberOfTopic(sendTo)) {
//					String outMsg = "msg " + sendTo + ":" + user.getLogin() + " " + body + "\n";
//					this.send(this, outMsg);
//				}
//			} else {
			if (sendTo.equalsIgnoreCase(clientDestinatario.getUser().getLogin())) {
				String outMsg = client.getUser().getLogin() + ": " + body + "\n";				
				enviaMensagem(client, outMsg, clientDestinatario);
				break;
			}
			notificaClientViaOutput(client, "Não foi possível encontrar o usuário " + sendTo + ".\n");
		}
	}

	protected static void handleLogoff(ClientConnection client) {
		if (isUserLogado(client)) {
			notificaTodosOsUsuarios(client, client.getUser().getLogin() + " ficou offline.\n");
		}
		Server.removeClientConnection(client);
		finalizaClientSocket(client);
	}

	public static void finalizaClientSocket(ClientConnection client) {
		try {
			client.getClientSocket().close();
		} catch (IOException e) {
			System.err.println("Não foi possível fechar o soquete " + client.getClientSocket() + ".\nMotivo: "
					+ e.getStackTrace());
		}
	}

	protected void handleLogin(ClientConnection client, String[] tokens) {
		boolean isTokenValidado = this.validaLoginToken(client, tokens);
		// TODO -> Validar Usuário com Banco.
		if (isTokenValidado) {
			String login = tokens[1];
			String senha = tokens[2];
			Usuario usuario = new Usuario(null, login, senha);
			if (usuarioValido(usuario)) {
				client.setUser(usuario);
				notificaClientViaOutput(client, "Login realizado com sucesso!\n");
				exibeUsuariosOnlineParaCliente(client);
				notificaTodosOsUsuarios(client, client.getUser().getLogin() + " ficou online.\n");
				Server.notificaNoConsoleDoServidor("Login do usuário: " + usuario.toString());
			} else {
				Server.notificaNoConsoleDoServidor(
						"Cliente: " + client.getClientSocket() + " teve uma tentativa de login falha!");
				notificaClientViaOutput(client, "Usuário ou senha informados não correspondem!\n");
			}
		}
	}

	private boolean validaLoginToken(ClientConnection client, String[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].isEmpty()) {
				notificaClientViaOutput(client,
						"Ops, a forma como a senha ou o login foram informados não pode ser aceita.\n");
				return false;
			}
		}
		return true;
	}

	// TODO validar se o usuário passado está sendo validado corretamente e se
	// existe no banco
	// Esta validação é apenas para teste
	private boolean usuarioValido(Usuario usuario) {
		return (usuario.getLogin().equals("Felipe") && usuario.getPassword().equals("Felipe"))
				|| (usuario.getLogin().equalsIgnoreCase("Erick") && usuario.getPassword().equalsIgnoreCase("Erick")
				|| (usuario.getLogin().equalsIgnoreCase("Fernando") && usuario.getPassword().equalsIgnoreCase("Fernando"))
				|| (usuario.getLogin().equalsIgnoreCase("Karina") && usuario.getPassword().equalsIgnoreCase("Karina"))
				|| (usuario.getLogin().equalsIgnoreCase("Pedro") && usuario.getPassword().equalsIgnoreCase("Pedro"))
				|| (usuario.getLogin().equalsIgnoreCase("Gustavo") && usuario.getPassword().equalsIgnoreCase("Gustavo")));
	}

	protected static boolean isUserLogado(ClientConnection client) {
		return client.getUser() != null;
	}

}
