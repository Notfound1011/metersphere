package io.metersphere.base.mapper.ext;

import io.metersphere.api.caseRecord.dto.YamlCaseDTO;
import io.metersphere.api.caseRecord.request.YamlCaseRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtYamlCaseMapper {
    List<YamlCaseDTO> queryTestCaseList(String query);
    List<YamlCaseDTO> queryTestCaseListByName(@Param("name") String name);
    void addTestCase(@Param("request") YamlCaseRequest request);
    void updateTestCase(@Param("request") YamlCaseRequest request);
    void deleteTestCase(@Param("id") Long id);
}
