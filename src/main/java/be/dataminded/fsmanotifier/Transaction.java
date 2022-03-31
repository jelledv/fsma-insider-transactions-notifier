package be.dataminded.fsmanotifier;

import java.util.StringJoiner;

public class Transaction {

    private final String publicationDate;
    private final String issuer;
    private final String transactionLink;
    private final String notifyingPerson;

    public Transaction(String publicationDate, String issuer, String transactionLink, String notifyingPerson) {
        this.publicationDate = publicationDate;
        this.issuer = issuer;
        this.transactionLink = transactionLink;
        this.notifyingPerson = notifyingPerson;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getTransactionLink() {
        return transactionLink;
    }

    public String getNotifyingPerson() {
        return notifyingPerson;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Transaction.class.getSimpleName() + '[', "]")
            .add("publicationDate='" + publicationDate + '\'')
            .add("issuer='" + issuer + '\'')
            .add("transactionLink='" + transactionLink + '\'')
            .add("notifyingPerson='" + notifyingPerson + '\'')
            .toString();
    }
}
