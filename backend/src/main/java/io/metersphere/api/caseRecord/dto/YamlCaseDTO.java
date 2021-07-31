package io.metersphere.api.caseRecord.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YamlCaseDTO {
    private Long id;
    private String name;
    private String testCase;
    private String remarks;
    private Long createTime;
    private Long updateTime;
}
