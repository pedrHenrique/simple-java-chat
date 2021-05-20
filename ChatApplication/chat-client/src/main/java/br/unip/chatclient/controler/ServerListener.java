package br.unip.chatclient.controler;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import br.unip.chatclient.model.server.ServerConnection;
import br.unip.chatclient.util.notifier.UserMessageNotifier;
import br.unip.chatclient.view.Chat;
import model.FileObjectData;

/**
 * ServerListener - A ideia é escutar e tratar, <b>todo tipo de informação
 * enviada automaticamente pelo servidor</b>.<br>
 * E claro, aplicar as devidas ações tratadas no Chat correspondente!.
 * <p>
 * 
 * Essa Classe é uma extensão de uma Thread. Então podemos continuar utilizando
 * o chat normalmente que esse carinha aqui ficará escutando tudo o que o
 * servidor estiver dizendo em background.
 * <p>
 * 
 * Descrição gerada automaticamente lol \/
 * <p>
 * 
 * The listener interface for receiving server events. The class that is
 * interested in processing a server event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addServerListener<code> method. When the server event
 * occurs, that object's appropriate method is invoked.
 *
 * @see ServerEvent
 */
public class ServerListener extends Thread {

	private ServerConnection connection;

	private Chat chat;

	public ServerListener(Chat chat, ServerConnection connection) {
		this.chat = chat;
		this.connection = connection;
	}

	@Override
	public void run() {
		System.out.println("Iniciando Listener");
		try {
			Object readObject;
			while ((readObject = connection.getObjectInputStream().readObject()) != null) {
				if (readObject instanceof String) {
					String line = (String) readObject;
					String[] tokens = StringUtils.split(line);
					if (tokens != null && tokens.length > 0) {
						System.out.println(line);
						String cmd = tokens[0];
						if (cmd.equalsIgnoreCase("online")) {
							chat.onlineUser(tokens[1]);
						} else if (cmd.equalsIgnoreCase("offline")) {
							chat.offlineUser(tokens[1]);
						} else if (cmd.equalsIgnoreCase("acceptFrom")) {
							String[] split = StringUtils.split(line, null, 3);
							chat.messageReceved(split[1], split[2]);
						} else if (cmd.equalsIgnoreCase("sendTo")) {
							String[] split = StringUtils.split(line, null, 4);
							chat.messageSent(split[1], split[2], split[3]);
						} else if (cmd.equalsIgnoreCase("logoff")) {
							chat.getServerCommunication().getConnection().finalizaComunicacaoComServidor();
							chat.setUsuario(null);
							System.exit(0);
						} else {
							System.out.println("Comando não reconhecido recebido!");
						}
					}
				} else if (readObject instanceof FileObjectData) {
					FileObjectData file = (FileObjectData) readObject;
					chat.fileReceived(file);
				} else {
					System.out.println("Tipo Desconhecido");
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			UserMessageNotifier.errorMessagePane(chat, "Uma das thread que ficam ouvindo o servidor acabaram falhando.\nO programa precisará ser encerrado!");
			this.interrupt();
			System.exit(1);
		}
	}
}
