package model;

public class Transfer {
    private String source_account;
    private Long amount;
    private String title;
    private String source_name;
    private String destination_name;


    public String getSource_account() {
        return source_account;
    }

    public void setSource_account(String source_account) {
        this.source_account = source_account;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public void setDestination_name(String destination_name) {
        this.destination_name = destination_name;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "source_account='" + source_account + '\'' +
                ", amount=" + amount +
                ", title='" + title + '\'' +
                ", source_name='" + source_name + '\'' +
                ", destination_name='" + destination_name + '\'' +
                '}';
    }
}
