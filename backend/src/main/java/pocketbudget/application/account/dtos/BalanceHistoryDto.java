package pocketbudget.application.account.dtos;

import java.util.List;

public class BalanceHistoryDto {
    public String accountId;
    public String accountName;
    public List<DataPoint> points;

    public BalanceHistoryDto() {}

    public BalanceHistoryDto(String accountId, String accountName, List<DataPoint> points) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.points = points;
    }

    public static class DataPoint {
        public String date;
        public double balance;

        public DataPoint() {}

        public DataPoint(String date, double balance) {
            this.date = date;
            this.balance = balance;
        }
    }
}
