package io.metersphere.api.caseRecord.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.api.caseRecord.dto.YamlCaseDTO;
import io.metersphere.api.caseRecord.request.YamlCaseRequest;
import io.metersphere.api.caseRecord.service.YamlCaseService;
import io.metersphere.commons.utils.PageUtils;
import io.metersphere.commons.utils.Pager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/testcase")
@RestController
public class YamlCaseController {
    @Resource
    YamlCaseService yamlCaseService;

    @GetMapping("/listAll")
    public Pager<List<YamlCaseDTO>> list(Integer pageNum, Integer pageSize, String query) {
        if (null == pageNum){
            pageNum = 1;
        }
        if (null == pageSize){
            pageSize = 10;
        }
        Page<Object> page = PageHelper.startPage(pageNum, pageSize, true);
        return PageUtils.setPageInfo(page,yamlCaseService.queryTestCaseList(query));
    }

    @GetMapping("/list")
    public List<YamlCaseDTO> list(String name) {
        return yamlCaseService.queryTestCaseListByName(name);
    }


    @PostMapping("/addTestCase")
    public void addTestCase(@RequestBody YamlCaseRequest request) {
        yamlCaseService.addTestCase(request);
    }


    @PostMapping("/updateTestCase")
    public void updateTestCase(@RequestBody YamlCaseRequest request) {
        yamlCaseService.updateTestCase(request);
    }

    @GetMapping("/deleteTestCase")
    public void deleteTestCase(Long id) {
        yamlCaseService.deleteTestCase(id);
    }
}
