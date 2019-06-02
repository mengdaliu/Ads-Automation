import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;

public class TestFBJavaSDK
{
    public static final APIContext context = new APIContext(
            "EAAIj5nzPn8QBALXBAfbDHZAlEN6HaK588B3hpXVGoEZCO1VlEvOjcYQ9aZBXhxDbZAZAmikLT7gxCU80KKjBnDjHtyHhacPx4ZBtev2NISyLej6Oncxn4X16kO62A6C94BQZAq0xmoLKPn2lfGdt7CP3Uu7bG7zpxNBro5WbnaUQoMiXOlP0dW8vw8MkTU4AOMZD",
            "9bb16b47ba38d031af5a5c25e6e2e714"
    );
    public static void main(String[] args)
    {
        AdAccount account = new AdAccount("act_1315526208586961", context);
        try {
            APINodeList<Campaign> campaigns = account.getCampaigns().requestAllFields().execute();
            for(Campaign campaign : campaigns) {
                System.out.println(campaign.getFieldId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}

