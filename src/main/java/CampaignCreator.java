import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;

public class CampaignCreator {
    private AdAccount _account;

    public CampaignCreator(AdAccount account) {
        _account = account;
    }

    public String CreateCampaign(
            String name
    ) {
        try {
            Campaign campaign = _account.createCampaign()
                    .setObjective(Campaign.EnumObjective.VALUE_APP_INSTALLS)
                    .setName(name)
                    .setStatus(Campaign.EnumStatus.VALUE_ACTIVE)
                    .execute();
            return (campaign.getFieldId());
        } catch (APIException e){
            System.out.println(e.getException());
            System.out.println(e.getHeader());
            e.printStackTrace();
            return ("Error");
        }

    }
}
