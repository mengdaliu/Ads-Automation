import com.facebook.ads.sdk.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class ReadMain {
    private static AdAccount _account = Main.getAccount();
    public static void main(String[] args) {
        try {
            File file = new File("SpecFiles/testRead.txt");
            Scanner sc = new Scanner(file);
            sc.next(); sc.next();
            String campaignID = sc.next();
            sc.next(); sc.next();
            Double unitPrice = Double.parseDouble(sc.next());
            readCampaign(campaignID, unitPrice);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readCampaign(String campaignID, double unitPrice) {
        try {
            final APIContext context = new APIContext(
                    "EAAIj5nzPn8QBAA5aPdGZAGqzYdPDFPhtWR9caZAOZAGduEVpM1WAoYehqePnuujZBBRQe5VBv6VgPHK2681JxHW1p8PHDZCzLjp0IcQg44G05yRZCGQBhYyzxme5jAoa6UY5nLaZB08GFWMdZBmiN1wD66W3yeaQyeYrQaM4veZBU1QZDZD",
                    "9bb16b47ba38d031af5a5c25e6e2e714"
            );
            Campaign campaign = Campaign.fetchById(campaignID, context);
            PrintStream csvWriter = new PrintStream(new File(campaign.getFieldName() + ".csv"));
            csvWriter.println("Adset, App Install, Total Spent, Unit Price, Profit");
            for (AdSet adset : campaign.getAdSets().execute()){
                String name = adset.getFieldId();
                APINodeList<AdsInsights> list = adset.getInsights().requestField("actions").execute();
                int install = 0;
                for (AdsInsights insight: list){
                    List pairs = insight.getFieldActions();
                    for (AdsActionStats pair : (List<AdsActionStats>)pairs){
                        if (pair.getFieldActionType().equals("mobile_app_install")) {
                            install = Integer.parseInt(pair.getFieldValue());
                        }
                    }
                }
                AdsInsights spent = null;
                try {
                spent = adset.getInsights().requestField("spend").execute().get(0);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
                double moneySpent = Double.parseDouble(spent.getFieldSpend());
                double shouldSpend = unitPrice * install;
                double profit = shouldSpend - moneySpent;
                csvWriter.println(name + "," + install + "," + moneySpent + "," + unitPrice + "," + profit);
                System.out.println(name + "," + install + "," + moneySpent + "," + unitPrice + "," + profit);
            }
        } catch (APIException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
