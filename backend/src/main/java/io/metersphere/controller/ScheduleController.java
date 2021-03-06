package io.metersphere.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.api.service.ApiAutomationService;
import io.metersphere.base.domain.Schedule;
import io.metersphere.commons.constants.OperLogConstants;
import io.metersphere.commons.constants.OperLogModule;
import io.metersphere.controller.request.QueryScheduleRequest;
import io.metersphere.controller.request.ScheduleRequest;
import io.metersphere.dto.ScheduleDao;
import io.metersphere.log.annotation.MsAuditLog;
import io.metersphere.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("schedule")
@RestController
public class ScheduleController {
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private ApiAutomationService apiAutomationService;

    @PostMapping("/list/{goPage}/{pageSize}")
    public List<ScheduleDao> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryScheduleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return scheduleService.list(request);
    }

    @GetMapping("/findOne/{testId}/{group}")
    public Schedule schedule(@PathVariable String testId,@PathVariable String group) {
        Schedule schedule = scheduleService.getScheduleByResource(testId,group);
        return schedule;
    }

    @PostMapping(value = "/update")
    @MsAuditLog(module = OperLogModule.TRACK_TEST_PLAN_SCHEDULE, type = OperLogConstants.UPDATE, title = "#request.name", beforeEvent = "#msClass.getLogDetails(#request.id)", content = "#msClass.getLogDetails(#request.id)", msClass = ScheduleService.class)
    public void updateSchedule(@RequestBody Schedule request) {
        scheduleService.updateSchedule(request);
    }

    @PostMapping(value = "/create")
    @MsAuditLog(module = OperLogModule.TRACK_TEST_PLAN_SCHEDULE, type = OperLogConstants.CREATE, title = "#request.name", content = "#msClass.getLogDetails(#request)", msClass = ScheduleService.class)
    public void createSchedule(@RequestBody ScheduleRequest request) {
        scheduleService.createSchedule(request);
    }

    @GetMapping(value = "/getTaskInfo")
    public Object getTaskInfo() {
        return scheduleService.getCurrentlyExecutingJobs();
    }
}
