package com.spring.project.chatapp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ChatHandler extends TextWebSocketHandler {
	
	private Map<String,WebSocketSession> usersessions =new ConcurrentHashMap<>();
	private Map<WebSocketSession,String> sessionModes= new ConcurrentHashMap<>();
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public void sendUserList() throws IOException, IOException {
		Set<String> usernames=usersessions.keySet();
		for(WebSocketSession session:usersessions.values()) {
			if(session.isOpen()) {
				session.sendMessage(new TextMessage(objectMapper.writeValueAsString
						(Map.of("type","userList","users",usernames))));
			}
		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String username=getUserNameFromSession(session);
		String mode=getModeFromSession(session);
		if(username==null||username.isEmpty()||mode==null) {
			session.close();
			return;
		}
		usersessions.put(username,session);
		sessionModes.put(session, mode);
		System.out.println("User joined: "+username+" in "+mode+" mode");
		
		if("public".equals(mode)) {
		broadcastMessage("System", username + " joined the Public chat!","public");
		}
		
		sendUserList();
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session,TextMessage message) throws Exception {
		try {
			JsonNode jsonNode=objectMapper.readTree(message.getPayload());
			String recipient=jsonNode.get("recipient").asText();
			String chatMeassage=jsonNode.get("message").asText();
			String senderUsername=getUserNameFromSession(session);
			String mode =sessionModes.get(session);
			
			if(recipient.equals(mode)) {
				broadcastMessage(senderUsername, chatMeassage,mode);
			}else {
				sendMessage(session, recipient, chatMeassage);
			}
			
			
		}catch(Exception e) {
			session.sendMessage(new TextMessage("{\"error\":\"Invalid message format. Use Json format.\"}"));
		}
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session,CloseStatus closeStatus) throws Exception {
		String username=getUserNameFromSession(session);
		String mode=sessionModes.get(session);
		if(username!=null) {
			usersessions.remove(username);
			sessionModes.remove(session);
			System.out.println(username+" left the chat");
			
			if("public".equals(mode)) {
			broadcastMessage("System",username+ " left the Public chat!","public");
			}
			
			sendUserList();
		}
		
		
	}
	
	private void sendMessage(WebSocketSession sender,String recipient,String message) throws Exception {
		WebSocketSession recipientSession=usersessions.get(recipient);
		
		if(recipientSession!=null && recipientSession.isOpen()) {
			recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("from",getUserNameFromSession(sender),"message",message))));
		}else {
			sender.sendMessage(new TextMessage("{\"error\": \"User " + recipient + " is not online.\"}"));
		}
	}
	
	private void broadcastMessage(String sender,String message,String mode) throws Exception {

		for(Map.Entry<String, WebSocketSession> entry: usersessions.entrySet()) {
			WebSocketSession session=entry.getValue();
			if(session.isOpen()&& sessionModes.get(session).equals(mode)) {
				session.sendMessage(new TextMessage(sender+": "+message));
			}
		}
	}
	
	private String getUserNameFromSession(WebSocketSession session) {
		String query=session.getUri().getQuery();
		if (query != null) {
	        Map<String, String> queryParams = parseQueryParams(query);
	        return queryParams.get("username");
	    }
		return null;
	}
	
	private String getModeFromSession(WebSocketSession session) {
		String query = session.getUri().getQuery();
	    if (query != null) {
	        Map<String, String> queryParams = parseQueryParams(query);
	        return queryParams.get("mode");
	    }
	    return null;
	}
	
	private Map<String, String> parseQueryParams(String query) {
	    Map<String, String> params = new HashMap<>();
	    for (String param : query.split("&")) {
	        String[] keyValue = param.split("=");
	        if (keyValue.length == 2) {
	            params.put(keyValue[0], keyValue[1]);
	        }
	    }
	    return params;
	}
	
	
	

}
