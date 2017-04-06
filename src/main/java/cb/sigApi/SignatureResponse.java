package cb.sigApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureResponse {
    public long contact_count;
    public List<SignatureResults> results;
}
