package cb.sigApi;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Message {
    public OffsetDateTime sent_date;
    public SigContact from;
    public List<SigContact> to;
    public String body;
    public UUID user_id = UUID.randomUUID(); //Allow override but just set random if not given
}
