package io.metersphere.api.caseRecord.request;

import lombok.Data;

@Data
public class YamlCaseRequest {
    private Long id;
    private String name;
    private String testCase;
    private String remarks;
    private Long createTime;
    private Long updateTime;
}
