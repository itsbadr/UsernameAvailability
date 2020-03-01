package com.java.gmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, MessagingException {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String email = "";
        String password = "";
        String recipient = "";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        Scanner in = new Scanner(System.in);

        String username = in.next();

        String json = String.format("[\"%s\"]", username);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/profiles/minecraft"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assert response != null;


        while (true) {
            Thread.sleep(10000);
            if (response.body().length() == 2) {
                Transport.send(sendMessage(session, email, recipient));
                System.out.println("Available");
                break;
            } else {
                System.out.println(response.body());
            }
        }

    }


    private static Message sendMessage(Session session, String email, String recipient) throws MessagingException {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject("Your account name is available to grab! Do it right now!");
        message.setText("https://www.minecraft.net/en-us/");

        return message;

    }
}
