package br.unip.chatserver.model.clientactions;

import static br.unip.chatserver.model.clientactions.util.ActionsUtil.retornaClienteComUsuarioInformado;

import java.io.IOException;

import javax.validation.Valid;

import br.unip.chatserver.model.ChatMessage;
import br.unip.chatserver.model.ClientConnection;
import br.unip.chatserver.model.Server;
import br.unip.chatserver.model.clientactions.util.ActionsUtil;

public class ClientNotificationActions {
	
	private ClientNotificationActions() {
		
	}
	
	/**
	 * Notifica todos os clientes que estejam com algum usuário logado, alguma mensagem.<p>
	 * 
	 * Não notifica o <b>remetente</b> da mensagem.
	 *
	 * @param remetente - O remetente da mensagem.
	 * @param mensagem  - A mensagem.
	 */
	public static void notificaTodosOsUsuarios(ClientConnection remetente, String mensagem) {
		for (ClientConnection client : Server.getClientList()) {
			if (clienteNaoForORemetente(remetente, client) && client.isUserLogado()) {
				enviaMensagem(remetente, mensagem, client);
			}
		}
	}

	private static boolean clienteNaoForORemetente(ClientConnection remetente, ClientConnection client) {
		return (remetente.getUser() != null) ? !remetente.getUser().equals(client.getUser()) : false;
	}

	/**
	 * Notifica para o client informado, todos os outros clientes que estejam com algum usuário online.
	 *
	 * @param client - O cliente que irá receber a lista de usuários online.
	 */
	public static void exibeUsuariosOnlineParaCliente(ClientConnection client) {
		notificaClientViaOutput(client, "Lista de usuários online.\n");
		for (ClientConnection cl : Server.getClientList()) {
			if (clienteNaoForORemetente(client, cl) && cl.isUserLogado()) {
				enviaMensagem(client, cl.getUser().getLogin() + "\n", client);
			}
		}
	}	

	/**
	 * Envia uma mensagem para o console do cliente.<p>
	 * 
	 * Neste caso, o mesmo <b>não precisa estar com um usuário logado</b> para poder visualizar a mensagem.
	 * 
	 * @param client - O Cliente que receberá a mensagem.
	 * @param mensagem - A mensagem.
	 */
	public static void notificaClientViaOutput(ClientConnection client, String mensagem) {
		try {
			client.getOutputStream().write(mensagem.getBytes());
		} catch (IOException e) {
			System.err.print("Não foi possível notificar pelo output o client " + client + ".\nMotivo: " + e.getStackTrace());
		}
	}
	
	/**
	 * Envia uma mensagem para o console do cliente.<p>
	 * 
	 * Neste caso, a mensagem só será enviada, se o remetente da mensagem estiver logado no sistema. 
	 * Caso contrário ele quem receberá uma mensagem o informando que o mesmo precisa estar logado.
	 * 
	 * @param clientRemetente - O Cliente que enviou a mensagem.
	 * @param mensagem - A mensagem.
	 * @param clientDestinatario - O Cliente que receberá a mensagem.
	 */
	public static void enviaMensagem(ClientConnection clientRemetente, String mensagem, ClientConnection clientDestinatario) {
		if (clientRemetente.isUserLogado()) {
			notificaClientViaOutput(clientDestinatario, mensagem);
			return;
		}
		notificaClientViaOutput(clientRemetente, "Você precisa estar logado para enviar alguma mensagem!\n");
	}
	
	/**
	 * Envia uma mensagem para o console do cliente.<p>
	 * 
	 * Neste caso, a mensagem só será enviada, se o remetente da mensagem estiver logado no sistema. 
	 * Caso contrário ele quem receberá uma mensagem o informando que o mesmo precisa estar logado.
	 * 
	 * @param clientRemetente - O Cliente que enviou a mensagem.
	 * @param mensagem - A mensagem.
	 * @param clientDestinatario - O Cliente que receberá a mensagem.
	 */
	public static void enviaChatMensagem(@Valid ChatMessage mensagem) {
		// TODO Validar Mensagem
		ClientConnection clientDestinatario = retornaClienteComUsuarioInformado(mensagem.getDestinatario().getLogin());
		notificaClientViaOutput(clientDestinatario, mensagem.toString());
	}

}
