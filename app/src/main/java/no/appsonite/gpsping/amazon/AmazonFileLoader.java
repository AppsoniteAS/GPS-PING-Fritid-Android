package no.appsonite.gpsping.amazon;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;

import no.appsonite.gpsping.utils.ImageUtils;

import static no.appsonite.gpsping.Application.getContext;

/**
 * Created by taras on 10/16/17.
 */

public class AmazonFileLoader {
    private static final String IDENTITY_POOL_ID = "eu-west-1:1d49973f-8111-48d4-9c26-dcdf6c50ee8d";
    private static final String MY_BUCKET = "fritidbucket";
    private static AmazonS3 s3;

    private AmazonFileLoader() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),
                IDENTITY_POOL_ID,
                Regions.EU_WEST_1
        );

        s3 = new AmazonS3Client(credentialsProvider);
    }

    private static void initLoader() {
        if (s3 == null) {
            new AmazonFileLoader();
        }
    }

    public static TransferObserver uploadPhoto(String path, String id) {
        initLoader();
        File file = ImageUtils.copyFileFromUri(path);
        TransferUtility.Builder builder = TransferUtility.builder();
        TransferUtility transferUtility = builder.s3Client(s3).context(getContext()).build();
        return transferUtility.upload(
                MY_BUCKET,
                id,
                file,
                CannedAccessControlList.PublicRead
        );
    }
}
