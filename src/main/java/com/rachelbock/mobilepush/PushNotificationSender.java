package com.rachelbock.mobilepush;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.PublishRequest;

/**
 * Sends notification to registered apps.
 */
public class PushNotificationSender {

    private static final String APP_ARN = "arn:aws:sns:us-west-2:432593804115:endpoint/APNS_SANDBOX/FloorManager/04e7f818-bee6-3353-a2f8-9fa8e7a94217";
    private static final String TOPIC_ARN = "arn:aws:sns:us-west-2:432593804115:floor-manager";

    public void publishMessage(String message) {
        AWSSecurityTokenServiceClient stsClient =
                new AWSSecurityTokenServiceClient(new ProfileCredentialsProvider());

        GetSessionTokenRequest getSessionTokenRequest =
                new GetSessionTokenRequest();

        GetSessionTokenResult sessionTokenResult =
                stsClient.getSessionToken(getSessionTokenRequest);
        Credentials sessionCredentials = sessionTokenResult.getCredentials();

        BasicSessionCredentials basicSessionCredentials =
                new BasicSessionCredentials(sessionCredentials.getAccessKeyId(),
                        sessionCredentials.getSecretAccessKey(),
                        sessionCredentials.getSessionToken());

        AmazonSNSClient snsClient = new AmazonSNSClient(basicSessionCredentials);

        snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        PublishRequest publishRequest = new PublishRequest(TOPIC_ARN, message);
        snsClient.publish(publishRequest);
    }
}
