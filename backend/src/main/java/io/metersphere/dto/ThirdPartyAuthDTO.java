package io.metersphere.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdPartyAuthDTO {
    private String jira_address;
    private String jira_username;
    private String jira_password;
    private String jenkins_username;
    private String jenkins_password;
    private String jira_auth;
    private String jenkins_auth;
}
