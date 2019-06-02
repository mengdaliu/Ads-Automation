
import org.junit.Test;

import java.util.ArrayList;

public class AdTest {

    @Test
    public void CampaignTest() {
        CampaignCreator campaignCreator = new CampaignCreator(Main.getAccount());
        String campaignID = campaignCreator.CreateCampaign("SORRY NO MORE HELLO");
        System.out.println(campaignID);
    }

    @Test
    public void AdSetTest() {
        System.out.println(Main.getAccount());
        AdCreator adCreator = new AdCreator(Main.getAccount());
        ArrayList<String> Countries = new ArrayList<String>(){
        };
        Countries.add("MX");
        String AdSetID = adCreator.createAdSet("Derrick is OK", 2L,"23843590092490152", Countries, "2176165979361240", "http://play.google.com/store/apps/details?id=com.speedinessmobile.junkfilecleaner","samsung");
        System.out.println(AdSetID);
    }

    @Test
    public void AdCreativeTest() {
        AdCreator adCreator = new AdCreator(Main.getAccount());
        String AdCreativeID = adCreator.createAdCreative("Hello world", "Hello World", "dcb4a947be57ef595fc69d421f9a884a", "hello World", "426237294788346", "https://www.facebook.com/139305293348361");
        System.out.println(AdCreativeID);
    }

    @Test
    public void AdCreativeDevTest() {
        AdCreator adCreator = new AdCreator(Main.getAccount());
        String AdCreativeID = adCreator.createAdCreativeDev("Hello world", "426237294788346_426983254713750", "426237294788346");
        System.out.println(AdCreativeID);
    }

    @Test
    public void AdTest() {
        AdCreator adCreator = new AdCreator(Main.getAccount());
        String AdID = adCreator.createAd("H", "23843590092650152", "23843590071860152");
        System.out.println(AdID);
    }

}