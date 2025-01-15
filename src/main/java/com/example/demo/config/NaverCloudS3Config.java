package com.example.demo.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;

@Configuration
public class NaverCloudS3Config {

    @Bean
    public S3Client s3Client() {
        String accessKey = "ncp_iam_BPAMKR3rTCbcFKrMJxUM"; // 실제 액세스 키로 교체
        String secretKey = "ncp_iam_BPKMKRTYorBEuBSgW6fcdgZQusSe39BX8u"; // 실제 비밀 키로 교체
        String endpoint = "https://kr.object.ncloudstorage.com"; // 특정 엔드포인트로 교체

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(endpoint))
                .region(Region.AP_NORTHEAST_2) // 버킷의 리전에 맞게 변경
                .forcePathStyle(true) // 네이버 클라우드에서 필요할 수 있음
                .build();
    }
}
