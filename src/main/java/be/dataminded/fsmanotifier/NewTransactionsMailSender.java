package be.dataminded.fsmanotifier;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class NewTransactionsMailSender {

    private final SesClient sesClient;

    public NewTransactionsMailSender(String region) {
        sesClient = SesClient.builder()
            .region(Region.of(region))
            .build();
    }

    public void sendEmailsWithNewTransactions(String sourceEmail, String[] recipients, List<Transaction> transactions) {
        Destination destination = Destination.builder()
            .toAddresses(recipients)
            .build();

        Message message = Message.builder()
            .subject(makeSubject(transactions))
            .body(makeBody(transactions))
            .build();

        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
            .source(sourceEmail)
            .destination(destination)
            .message(message)
            .build();

        sesClient.sendEmail(sendEmailRequest);
    }

    private Content makeSubject(List<Transaction> transactions) {
        String issuerName = transactions.stream()
            .map(Transaction::getIssuer)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Calling send method with empty list of transactions!!!"));

        return Content.builder()
            .data("New manager transactions reported to the FSMA for issuer: " + issuerName)
            .charset(StandardCharsets.UTF_8.name())
            .build();
    }

    private Body makeBody(List<Transaction> transactions) {
        return Body.builder()
            .text(Content.builder()
                .data(transactions.stream()
                    .map(Transaction::toString)
                    .collect(Collectors.joining("\n\n")))
                .charset(StandardCharsets.UTF_8.name())
                .build())
            .build();
    }
}
