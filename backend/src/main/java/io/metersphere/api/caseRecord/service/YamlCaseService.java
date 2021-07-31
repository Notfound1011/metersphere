package io.metersphere.api.caseRecord.service;

import io.metersphere.api.caseRecord.dto.YamlCaseDTO;
import io.metersphere.api.caseRecord.request.YamlCaseRequest;
import io.metersphere.base.mapper.ext.ExtYamlCaseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class YamlCaseService {
    @Resource
    ExtYamlCaseMapper extYamlCaseMapper;

    public List<YamlCaseDTO> queryTestCaseList(String query) {
        return extYamlCaseMapper.queryTestCaseList(query);
    }

    public List<YamlCaseDTO> queryTestCaseListByName(String name) {
        return extYamlCaseMapper.queryTestCaseListByName(name);
    }


    public void addTestCase(YamlCaseRequest request) {
        request.setName(request.getName());
        request.setTestCase(request.getTestCase());
        request.setRemarks(request.getRemarks());
        request.setCreateTime(System.currentTimeMillis());
        request.setUpdateTime(System.currentTimeMillis());
        extYamlCaseMapper.addTestCase(request);
    }

    public void updateTestCase(YamlCaseRequest request) {
        request.setId(request.getId());
        request.setName(request.getName());
        request.setTestCase(request.getTestCase());
        request.setRemarks(request.getRemarks());
        request.setUpdateTime(System.currentTimeMillis());
        extYamlCaseMapper.updateTestCase(request);
    }

    public void deleteTestCase(Long id){
        extYamlCaseMapper.deleteTestCase(id);
    }
}
