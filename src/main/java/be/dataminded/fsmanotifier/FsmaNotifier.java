package be.dataminded.fsmanotifier;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;


public class FsmaNotifier implements RequestHandler<ScheduledEvent, Void> {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final String ENVIRONMENT_KEY_ISSUER_IDS = "ISSUER_IDS";
    public static final String ENVIRONMENT_KEY_SOURCE_EMAIL= "SOURCE_EMAIL";
    public static final String ENVIRONMENT_KEY_EMAILS = "EMAILS";

    @Override
    public Void handleRequest(ScheduledEvent scheduledEvent, Context context) {
        try {
            NewTransactionsMailSender newTransactionsMailSender = new NewTransactionsMailSender(scheduledEvent.getRegion());
            LocalDate runDate = scheduledEvent.getTime().toLocalDate();
            String[] issuerIdsToScrape = System.getenv(ENVIRONMENT_KEY_ISSUER_IDS).split(",");
            String sourceEmail = System.getenv(ENVIRONMENT_KEY_SOURCE_EMAIL);
            String[] alertEmailAddresses = System.getenv(ENVIRONMENT_KEY_EMAILS).split(",");

            for (String issuerId : issuerIdsToScrape) {
                String url = buildScrapeUrl(runDate, issuerId);
                List<Transaction> transactions = scrapeTransactions(url);

                if (!transactions.isEmpty()) {
                    newTransactionsMailSender.sendEmailsWithNewTransactions(sourceEmail, alertEmailAddresses, transactions);
                }
            }

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private String buildScrapeUrl(LocalDate runDate, String issuerId) throws URISyntaxException {
        URI uri = new URI(
            "https",
            "www.fsma.be",
            "/en/transaction-search",
            String.format("issuer=%s&date[min]=%s&date[max]=%s",
                issuerId,
                runDate.toString(DATE_FORMAT),
                runDate.plusDays(1).toString(DATE_FORMAT)),
            null);

        return uri.toString();
    }

    private List<Transaction> scrapeTransactions(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements tableRows = document.getElementsByTag("tbody").select("tr");

        return tableRows.stream()
            .map(x -> x.select("td"))
            .map(this::mapRowToTransaction)
            .collect(Collectors.toList());
    }

    private Transaction mapRowToTransaction(Elements row) {
        return new Transaction(
            row.get(0).text(),
            row.get(1).text(),
            "https://www.fsma.be" + row.get(1).child(0).attr("href"),
            row.get(2).text());
    }

    // Manual E2E test with configured environment variables (AWS_PROFILE and all of the above environment keys)
    public static void main(String[] args) {
        FsmaNotifier fsmaNotifier = new FsmaNotifier();
        ScheduledEvent scheduledEvent = new ScheduledEvent();
        scheduledEvent.setRegion("eu-central-1");
        scheduledEvent.setTime(new DateTime(2022, 3, 18, 12, 0));
        fsmaNotifier.handleRequest(scheduledEvent, null);
    }
}
