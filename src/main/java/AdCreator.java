import com.facebook.ads.sdk.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdCreator {

    private AdAccount _account;
    AdCreator(AdAccount account){
        _account = account;
    }

    public String createAdSet(
            String name,
            Long bidAmount,
            String CampaignID,
            List<String> Countries,
            String AppID,
            String url,
            String DeviceType
    ) {
        try {
            String json = "{\"application_id\": " + AppID + "," + "\"object_store_url\":" + "\"" + url + "\"" + "}";
            AdSet adSet = _account.createAdSet()
                    .setName(name)
                    .setOptimizationGoal(AdSet.EnumOptimizationGoal.VALUE_APP_INSTALLS)
                    .setBidStrategy(AdSet.EnumBidStrategy.VALUE_LOWEST_COST_WITH_BID_CAP)
                    .setBidAmount(bidAmount)
                    .setDailyBudget((long) 50000)
                    .setCampaignId(CampaignID)
                    .setTargeting(
                            new Targeting()
                                .setFieldGeoLocations(
                                        new TargetingGeoLocation()
                                            .setFieldCountries(Countries)
                                )
                                .setFieldUserDevice(Arrays.asList(DeviceType))
                                .setFieldUserOs(Arrays.asList("Android"))
                                .setFieldPublisherPlatforms(Arrays.asList("facebook"))
                    )
                    .setPromotedObject(json)
                    .setStatus(AdSet.EnumStatus.VALUE_ACTIVE)
                    .setBillingEvent(AdSet.EnumBillingEvent.VALUE_IMPRESSIONS)
                    .execute();
            return adSet.getFieldId();
        } catch (APIException e) {
            System.out.println(e.getException());
            System.out.println(e.getHeader());
            e.printStackTrace();
            return "Error";
        }
    }

    public String createAdCreative(
            String name, String caption, String ImageHash, String message, String PageID, String storeURL
    ) {
        try {
            AdCreative Creative = _account.createAdCreative()
                    .setName(name)
                    .setTitle(caption)
                    .setObjectStorySpec(
                            new AdCreativeObjectStorySpec()
                                    .setFieldLinkData(
                                            new AdCreativeLinkData()
                                                    .setFieldCallToAction(
                                                            new AdCreativeLinkDataCallToAction()
                                                                    .setFieldType(AdCreativeLinkDataCallToAction.EnumType.VALUE_INSTALL_MOBILE_APP)
                                                    )
                                                    .setFieldImageHash(ImageHash)
                                                    .setFieldUseFlexibleImageAspectRatio(true)
                                                    .setFieldMessage(message)
                                                    .setFieldDescription(caption)
                                                    .setFieldName(caption)
                                                    .setFieldLink(storeURL)
                                    )
                                    .setFieldPageId(PageID)
                    )
                    .execute();
            return Creative.getFieldId();
        } catch (APIException e) {
            System.out.println(e.getException());
            System.out.println(e.getHeader());
            e.printStackTrace();
            return "Error";
        }
    }

    public String createAdCreativeVideo( String name, String caption, String ImageHash, String message, String PageID, String storeURL, String VideoID,
                                         int i
    ) {
        try {
            AdCreative Creative =  _account.createAdCreative()
                .setName(name)
                .setTitle(caption)
                .setObjectStorySpec(
                        new AdCreativeObjectStorySpec()
                                .setFieldPageId(PageID)
                                .setFieldVideoData(
                                        new AdCreativeVideoData()
                                                .setFieldCallToAction(
                                                        new AdCreativeLinkDataCallToAction()
                                                                .setFieldType(AdCreativeLinkDataCallToAction.EnumType.VALUE_INSTALL_MOBILE_APP)
                                                                .setFieldValue(
                                                                        new AdCreativeLinkDataCallToActionValue()
                                                                                .setFieldLink(storeURL)
                                                                                )
                                                )
                                                .setFieldImageHash(ImageHash)
                                                .setFieldVideoId(VideoID)
                                                .setFieldMessage(message)
                                                .setFieldTitle(caption)
                                                .setFieldLinkDescription(caption)
                                )
                ).execute();
            return Creative.getFieldId();
    } catch (APIException e) {
            if (i%50 == 0) {
                System.out.println("Video Probably hasn't been ready to be used in ad. Please wait a bit longer.");
            }
            return createAdCreativeVideo(name, caption, ImageHash, message, PageID, storeURL, VideoID, i+1);
        }
    }

    public String createAdCreativeDev(String name, String PostID, String PageID) {
        try {
            AdCreative Creative = _account.createAdCreative()
                    .setName(name)
                    .setObjectStoryId(PostID)
                    .execute();
            return Creative.getFieldId();
        } catch (APIException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public String createAd(
            String name,
            String AdsetID,
            String CreativeID
    ) {
        try {
            Ad ad = _account.createAd()
                    .setName(name)
                    .setAdsetId(AdsetID)
                    .setCreative(new AdCreative().setFieldId(CreativeID))
                    .setStatus(Ad.EnumStatus.VALUE_ACTIVE)
                    .execute();
            return ad.getFieldId();
        } catch (APIException e) {
            System.out.println(e.getException());
            System.out.println(e.getHeader());
            e.printStackTrace();
            e.getRawResponse();
            return "Error";
        }
    }

}
