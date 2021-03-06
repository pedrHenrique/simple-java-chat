package br.unip.chatserver.model.clientactions;

import static br.unip.chatserver.model.clientactions.ClientNotificationActions.enviaMensagem;
import static br.unip.chatserver.model.clientactions.ClientNotificationActions.enviaChatMensagem;
import static br.unip.chatserver.model.clientactions.ClientNotificationActions.notificaClientViaOutput;
import static br.unip.chatserver.model.clientactions.util.ActionsUtil.validaToken;
import static br.unip.chatserver.model.clientactions.util.ActionsUtil.retornaClienteComUsuarioInformado;

import org.apache.commons.lang3.StringUtils;

import br.unip.chatserver.model.ChatMessage;
import br.unip.chatserver.model.ClientConnection;

public final class ClientMessageActions {
	
	private ClientMessageActions() {
		
	}
	
	protected static String[] validaTokenMensagem(String linha) {
		String[] tokens = formataTokenMensagem(linha);
		return (validaToken(tokens)) ? tokens : null; //Retorna null se os tokens não forem válidos	
	}
	
	// retorna a seguinte 'array': msg, destinatario, 'Uma mensagem completa, podendo conter espaços e outras paradas'
	protected static String[] formataTokenMensagem(String linha) {
		return StringUtils.split(linha, null, 3);
	}

	protected static void notificaUsuario(ClientConnection clientRemetente, String destinatario, String mensagem) {
		if (clientRemetente.getUser().getLogin().equalsIgnoreCase(destinatario)) {
			notificaClientViaOutput(clientRemetente, "Porquê você estaria tentando enviar uma mensagem para você mesmo?\n");
			return;
		}
		ClientConnection clientDestinatario = retornaClienteComUsuarioInformado(destinatario);
		if (clientDestinatario != null) {				
			//String mensagem = formataMensagemChat(clientRemetente, mensagem);
			//enviaMensagem(clientRemetente, mensagem , clientDestinatario);
			ChatMessage chatMensagem = new ChatMessage(clientRemetente.getUser(), mensagem, clientDestinatario.getUser());
			enviaChatMensagem(chatMensagem);
			return;
		}
		notificaClientViaOutput(clientRemetente, "Não foi possível encontrar o usuário " + destinatario + ".\n");
	}	
	
	protected static String formataMensagemChat(ClientConnection remetente, String mensagem) {
		return remetente.getUser().getLogin() + ": " + mensagem + "\n";
	}

}
