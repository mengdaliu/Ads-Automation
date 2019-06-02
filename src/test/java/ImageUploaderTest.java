import com.facebook.ads.sdk.APIContext;
import org.junit.Test;
import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;


import java.io.File;

import static org.junit.Assert.*;

public class ImageUploaderTest {


    ImageUploader testUploader = new ImageUploader(Main.getAccount());
    ImageUploader testBatchUploader = new ImageUploader("/Users/liumengda/Downloads/", "JPEG", Main.getAccount());

    @Test
    public void batchUploadTest() {
        System.out.println(testBatchUploader.batchUpload());
    }

}