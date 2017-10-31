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
import com.amazonaws.services.sns.model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends notification to registered apps.
 */
public class PushNotificationHandler {

    private static final String APP_ARN = "arn:aws:sns:us-west-2:432593804115:endpoint/APNS_SANDBOX/FloorManager/04e7f818-bee6-3353-a2f8-9fa8e7a94217";
    private static final String PLATFORM_APP_ARN = "arn:aws:sns:us-west-2:432593804115:app/APNS_SANDBOX/FloorManager";
    private static final String TOPIC_ARN = "arn:aws:sns:us-west-2:432593804115:floor-manager";

    private String mobileEndpointARN = null;

    public AmazonSNSClient getClient() {
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
        return snsClient;
    }

    public void publishMessage(String message) {
        AmazonSNSClient snsClient = getClient();
        PublishRequest publishRequest = new PublishRequest(TOPIC_ARN, message);
        snsClient.publish(publishRequest);
    }

    public void subscribeNewMobileEndpoint() {
        AmazonSNSClient snsClient = getClient();
        snsClient.subscribe(TOPIC_ARN, "application", mobileEndpointARN);
    }

    public void registerWithSNS(String token) {
        String endpointArn = retrieveEndpointArn();
        boolean createNeeded = (null == endpointArn);

        if (createNeeded) {
            // No platform endpoint ARN is stored; need to call createEndpoint.
            endpointArn = createEndpoint(token);
            subscribeNewMobileEndpoint();
            System.out.println("Endpoint arn = " + endpointArn + " mobile endpointArn = " + mobileEndpointARN);
        }
    }

    /**
     * @return never null
     * */
    private String createEndpoint(String token) {
        AmazonSNSClient client = getClient();

        String endpointArn = null;
        try {
            System.out.println("Creating platform endpoint with token " + token);
            CreatePlatformEndpointRequest cpeReq =
                    new CreatePlatformEndpointRequest()
                            .withPlatformApplicationArn(PLATFORM_APP_ARN)
                            .withToken(token);
            CreatePlatformEndpointResult cpeRes = client
                    .createPlatformEndpoint(cpeReq);
            endpointArn = cpeRes.getEndpointArn();
        } catch (InvalidParameterException ipe) {
            String message = ipe.getErrorMessage();
            System.out.println("Exception message: " + message);
            Pattern p = Pattern
                    .compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                            "with the same token.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                endpointArn = m.group(1);
            } else {
                // Rethrow the exception, the input is actually bad.
                throw ipe;
            }
        }
        storeEndpointArn(endpointArn);
        return endpointArn;
    }

    /**
     * @return the ARN the app was registered under previously, or null if no
     *         platform endpoint ARN is stored.
     */
    private String retrieveEndpointArn() {
        // Retrieve the platform endpoint ARN from permanent storage,
        // or return null if null is stored.
        return mobileEndpointARN;
    }

    /**
     * Stores the platform endpoint ARN in permanent storage for lookup next time.
     * */
    private void storeEndpointArn(String endpointArn) {
        // Write the platform endpoint ARN to permanent storage.
        mobileEndpointARN = endpointArn;
    }
}
