<template>
  <ms-container>
    <ms-main-container v-loading="result.loading" style="background: #fbfbfb">
      <h2 style="margin-left: 20px;font-weight:bold">{{ "TASK统计" }}</h2>
      <el-form :inline="true" style="margin-left: 20px;">
        <el-form-item label="自定义JQL">
          <el-tooltip style="margin-right: 10px;" effect="dark"
                      content="QA TASK的自定义JQL；默认：issuetype in (Task,Story) AND assignee in membersOf(QA) AND status != Cancelled">
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-input
            style="width: 520px;margin-right: 20px;"
            placeholder=""
            v-model="customJQL">
          </el-input>
        </el-form-item>
        <el-form-item label="选择时间">
          <el-tooltip style="margin-right: 10px;" effect="dark"
                      content="默认本年度，可点选时间区间或手动输入">
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-date-picker
            style='margin-left: 20px'
            :picker-options="pickerOptions"
            v-model="timePicker"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search" style='margin-left: 10px'>查询</el-button>
          <el-button type="primary" @click="reset">{{ $t('api_test.reset') }}</el-button>
        </el-form-item>
      </el-form>
      <el-row :gutter="20">
        <!--        <el-col :span="11" class="grid-content">-->
        <!--          <el-card>-->
        <!--            <task-statistics></task-statistics>-->
        <!--          </el-card>-->
        <!--        </el-col>-->
        <el-col :span="22" class="grid-content">
          <el-card>
            <qa-task-statistics ref="qaTaskStat" id="qaTaskStatistics" :JQL="JQL"></qa-task-statistics>
          </el-card>
        </el-col>
      </el-row>
      <el-divider></el-divider>
      <el-row :gutter="10">
        <el-col :span="11" class="grid-content">
          <el-card>
            <task-by-creator-pie-stat ref="taskByCreatorPieStat" id="taskByCreatorPieStat"
                                      :backToDevJQL="backToDevJQL"></task-by-creator-pie-stat>
          </el-card>
        </el-col>
        <el-col :span="11" class="grid-content">
          <el-card>
            <task-by-project-pie-stat ref="taskByProjectPieStat" id="taskByProjectPieStat"
                                      :backToDevJQL="backToDevJQL"></task-by-project-pie-stat>
          </el-card>
        </el-col>
      </el-row>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsContainer from "@/business/components/common/components/MsContainer";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import QaTaskStatistics from "@/business/components/report/taskstat/components/qaTaskStatistics";
import {jiraAuth, fullScreenLoading, stopFullScreenLoading} from "@/common/js/utils";
import TaskByCreatorPieStat from "@/business/components/report/taskstat/components/taskByCreatorPieStat";
import TaskByProjectPieStat from "@/business/components/report/taskstat/components/taskByProjectPieStat";

export default {
  name: "TaskStat",
  components: {
    QaTaskStatistics,
    TaskByCreatorPieStat,
    TaskByProjectPieStat,
    MsMainContainer, MsContainer
  },
  data() {
    return {
      jira_auth: jiraAuth(),
      result: {},
      currentYear: new Date().getFullYear().toString(),
      customJQL: '',
      JQL: '',
      backToDevJQL: "issuetype in (Task, Story) AND \"rejected Num\" > 0",
      timePicker: null,
      pickerMinDate: "",//第一次选中的时间
      pickerOptions: {
        onPick: (obj) => {
          this.pickerMinDate = new Date(obj.minDate).getTime();
        },
        disabledDate: (time) => {
          if (this.pickerMinDate) {
            const day1 = 366 * 24 * 3600 * 1000
            let maxTime = this.pickerMinDate + day1
            let minTime = this.pickerMinDate - day1
            return time.getTime() > maxTime || time.getTime() < minTime || time.getTime() > Date.now()
          } else {
            return time.getTime() > Date.now()
          }
        }
      }
    }
  },
  mounted() {
    let that = this;
    that.search();
  },
  methods: {
    search() {
      // created > startOfYear() AND
      let JQL = ""
      let backToDevJQL = ""
      if (this.customJQL == '') {
        JQL = "issuetype in (Task,Story) AND assignee in membersOf(QA) AND status != Cancelled"
      } else if (this.customJQL !== '') {
        JQL = this.customJQL
      }
      if (this.timePicker !== null) {
        JQL = JQL + " AND \"created\" >= " + this.timePicker[0] + " AND \"created\" <= " + this.timePicker[1]
        backToDevJQL = "issuetype in (Task, Story) AND \"rejected Num\" > 0" + " AND \"created\" >= " + this.timePicker[0] + " AND \"created\" <= " + this.timePicker[1]
      } else if (this.timePicker == null) {
        JQL = JQL + " AND created > startOfYear()"
        backToDevJQL = "issuetype in (Task, Story) AND \"rejected Num\" > 0" + " AND created > startOfYear()"
      }
      if (JQL.startsWith(" AND")) {
        JQL = JQL.replace(/AND/, '').trim()
      }
      this.JQL = JQL
      this.query(JQL)
      this.backToDevJQL = backToDevJQL
      this.backToDev(backToDevJQL);
    },

    query(JQL) {
      let that = this;
      const maxResults = 1000;
      let initUrl = 'jira/rest/api/2/search?jql=' + JQL + "&maxResults=" + maxResults + "&fields=creator"
      let url = 'jira/rest/api/2/search?jql=' + JQL + "&maxResults=" + maxResults + "&fields=creator,assignee,status,issuetype"
      const instance = that.$axios.create({
        headers: {
          'Authorization': that.jira_auth
        }
      });
      const loading = fullScreenLoading(this);
      getAll();

      async function getAll() {
        try {
          const response = await instance.get(initUrl);
          const totalData = response.data.total;

          const totalPages = Math.ceil(totalData / maxResults);
          const promiseArray = [];
          for (let i = 0; i < totalPages; i++) {
            promiseArray.push(instance.get(url + `&startAt=` + maxResults * `${i}`))
          }
          // promise.all allows you to make multiple axios requests at the same time.
          // It returns an array of the results of all your axios requests
          let resolvedPromises = await Promise.all(promiseArray)
          let data = []
          for (let i = 0; i < resolvedPromises.length; i++) {
            // This will give you access to the output of each API call
            data = data.concat(resolvedPromises[i].data.issues)
          }
          that.$refs.qaTaskStat.handleResponse(data)
        } catch (err) {
          that.$message.error(err)
        }
      }

      stopFullScreenLoading(loading, 2000);
    },

    backToDev(backToDevJQL) {
      let that = this;
      const maxResults = 1000;
      let initUrl = 'jira/rest/api/2/search?jql=' + backToDevJQL + "&maxResults=" + maxResults + "&fields=creator"
      let url = 'jira/rest/api/2/search?jql=' + backToDevJQL + "&maxResults=" + maxResults + "&fields=creator,assignee,status,issuetype,project"
      const instance = that.$axios.create({
        headers: {
          'Authorization': that.jira_auth
        }
      });
      getAll();

      async function getAll() {
        try {
          const response = await instance.get(initUrl);
          const totalData = response.data.total;

          const totalPages = Math.ceil(totalData / maxResults);
          const promiseArray = [];
          for (let i = 0; i < totalPages; i++) {
            promiseArray.push(instance.get(url + `&startAt=` + maxResults * `${i}`))
          }
          // promise.all allows you to make multiple axios requests at the same time.
          // It returns an array of the results of all your axios requests
          let resolvedPromises = await Promise.all(promiseArray)
          let data = []
          for (let i = 0; i < resolvedPromises.length; i++) {
            // This will give you access to the output of each API call
            data = data.concat(resolvedPromises[i].data.issues)
          }
          that.$refs.taskByCreatorPieStat.handleResponse(data)
          that.$refs.taskByProjectPieStat.handleResponse(data)
        } catch (err) {
          that.$message.error(err)
        }
      }
    },
    reset() {
      this.customJQL = []
      this.timePicker = null
    }
  }
}
</script>

<style scoped>

.grid-content {
  border-radius: 4px;
  min-height: 36px;
  margin: 15px;
}
</style>
