package com.netflix.simianarmy.aws;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.netflix.simianarmy.MonkeyConfiguration;

/** Wrapper class for AWS-specific details used by AWS monkey contexts.
 * @author mgeis
 *
 */
public class AWSContext {

    private final String region;

    private final String account;

    private final String secret;

    /** The AWS credentials provider to be used. */
    private AWSCredentialsProvider awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();

    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return awsCredentialsProvider;
    }

    /** If configured, the ARN of Role to be assumed. */
    private final String assumeRoleArn;

    /** Constructor.
     * @param config
     */
    public AWSContext(MonkeyConfiguration config) {
        account = config.getStr("simianarmy.client.aws.accountKey");
        secret = config.getStr("simianarmy.client.aws.secretKey");
        region = config.getStrOrElse("simianarmy.client.aws.region", "us-east-1");

        assumeRoleArn = config.getStr("simianarmy.client.aws.assumeRoleArn");
        if (assumeRoleArn != null) {
            this.awsCredentialsProvider = new STSAssumeRoleSessionCredentialsProvider(assumeRoleArn);
        }

        // if credentials are set explicitly make them available to the AWS SDK
        if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(secret)) {
            exportCredentials(account, secret);
        }
    }

    /**
     * Exports credentials as Java system properties
     * to be picked up by AWS SDK clients.
     * @param accountKey
     * @param secretKey
     */
    public void exportCredentials(String accountKey, String secretKey) {
        System.setProperty("aws.accessKeyId", accountKey);
        System.setProperty("aws.secretKey", secretKey);
    }

    /**
     * Gets the region.
     * @return the region
     */
    public String region() {
        return region;
    }

}
