import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.AdAccount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class testMain {
    private static HashMap<String, ArrayList> inputList = new HashMap() {{
        put("Bids", new ArrayList<Integer>());
        put("Creatives", new ArrayList<String>());
    }};
    public static AdAccount getAccount(){
        return _account;
    }
    static final APIContext context = new APIContext(
            "EAAIj5nzPn8QBAF1ppsFmdasvmiS31yTkl7HnAZAfjxU7JFIMGlVrmiiXnIqKgw87SXdYDI6PknZAOOmkZAXV8fgyiClqjhYoYslNV58bEZBeaH1tkvuJeck6ZC9vLLyBdfAd2SISkZCZAX7KCLDoy7yvetZBoQ9scv8ZD",
            "9bb16b47ba38d031af5a5c25e6e2e714"
    );
    public static AdAccount _account = new AdAccount("284754935510311", context);
    public static void main (String[] args) {
        readInput(new File("SpecFiles/test.txt"));
        int i = 0;
        try {
            Scanner note = new Scanner(new File("SpecFiles/numTrack.out"));
            i = note.nextInt();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (Object bid : inputList.get("Bids")) {
            for (Object creative : inputList.get("Creatives")) {
                i += 1;
                CampaignCreator campaignCreator = new CampaignCreator(_account);
                String campaign = campaignCreator.CreateCampaign("ApexAd Campaign" + i);
                AdCreator adCreator = new AdCreator(_account);
                String adSet = adCreator.createAdSet("ApexAd Adset" + i, Long.valueOf((String) bid).longValue(), campaign, Arrays.asList("MX"), "123", "123", "samsung");
                String adCreative = adCreator.createAdCreativeDev("ApexAd AdCreative" + i, (String) creative, "426237294788346");
                String ad = adCreator.createAd("ApexAd Ad" + i, adSet, adCreative);
                System.out.println("Just created Ad " + i + ", ID: " + ad + ". Used Bid " + bid + " and Post " + creative + ".");
                System.out.println("Sleeping for 30 seconds. ");
                try {TimeUnit.SECONDS.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
        try {
            PrintWriter takeNotes  = new PrintWriter("numTrack.out");
            takeNotes.print(i);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readInput(File file) {
        try {
            Scanner specReader = new Scanner(file);
            while (specReader.hasNext()) {
                String spec = specReader.next();
                String input;
                assert(spec.charAt(spec.length() - 1) == ':') : "Input titles should end with colons. " +
                        "Title with erroneous format: " + spec;
                spec = spec.substring(0, spec.length() - 1);
                assert (inputList.containsKey(spec)) : spec + " is not a legal user input.";
                int i = 0;
                if (spec.equals("Creatives") || spec.equals("Bids")) {
                    specReader.nextLine();
                    while(specReader.hasNextLine()) {
                        input = specReader.nextLine();
                        if (input.length() == 0) {
                            break;
                        } else {
                            i += 1;
                            assert(input.substring(0, 2).equals(i + ".")) :  "Input lines should start with numbers." +
                                    "Erroneous line: " + input;
                            inputList.get(spec).add(input.substring(3));

                        }
                    }
                    System.out.println("You gave " + i + " types of " + spec + ".");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file path you entered is invalid");
            System.exit(1);
        }
    }
}

