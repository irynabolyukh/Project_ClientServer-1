package client_server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentials {

    private String login;
    private String password;

    public JSONObject toJSON(){

        JSONObject json = new JSONObject("{"+"\"login\":\""+login+"\", \"password\":\""+password+"\"}");
        return json;
    }
}