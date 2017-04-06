package cb;

import cb.sigApi.SignatureRequest;
import cb.sigApi.SignatureResponse;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SignatureClient {
    private final String apiKey;

    SignatureClient(String apiKey) {
        this.apiKey = apiKey;
    }
    protected SignatureResponse resolveSignatures(SignatureRequest signatureRequest) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.circleback.com/service/sig-capture/scan");
        httpPost.addHeader("X-CB-ApiKey", apiKey);
        httpPost.setHeader("Content-Type", "application/json");
        String body = Main.objectMapper.writeValueAsString(signatureRequest);
        httpPost.setEntity(new StringEntity(body));

        CloseableHttpResponse response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            return Main.objectMapper.readValue(EntityUtils.toString(response.getEntity()), SignatureResponse.class);
        }

        throw new RuntimeException("Signature API had troubles. Status: " + response.getStatusLine().getStatusCode() +
                " request: " + body);
    }

}
