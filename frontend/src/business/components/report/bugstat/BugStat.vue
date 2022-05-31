<template>
  <ms-container>
    <ms-main-container v-loading="result.loading" style="background: #fbfbfb">
      <h2 style="margin-left: 20px;font-weight:bold">{{ "BUG统计" }}</h2>
      <el-form :inline="true" style="margin-left: 58%;margin-right: 2%;">
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
        </el-form-item>
      </el-form>
      <el-row :gutter="10">
        <el-col :span="11" class="grid-content">
          <el-card class="card">
            <online-bug-rate ref="onlineBugRate" id="onlineBugRate" :percentage="percentage"
                             :onlineBugTotal="onlineBugTotal" :onlineBugJQL="onlineBugJQL"></online-bug-rate>
          </el-card>
        </el-col>
        <el-col :span="12" class="grid-content">
          <el-card class="card">
            <qa-online-bug-rate :onlineBug="onlineBug" :qaBug="qaBug" :onlineBugJQL="onlineBugJQL"
                                v-if="onlineBugFlag&&qaBugFlag"></qa-online-bug-rate>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="11" class="grid-content">
          <el-card class="card">
            <online-bug-stat ref="onlineBugStat" id="onlineBugStat"
                             :onlineBugJQL="onlineBugJQL"></online-bug-stat>
          </el-card>
        </el-col>
        <el-col :span="12" class="grid-content">
          <el-card class="card">
            <qa-bug-created-stat ref="qaBugCreatedStat" id="qaBugCreatedStat"
                                 :qaCreatedBugJQL="qaCreatedBugJQL"></qa-bug-created-stat>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="11" class="grid-content">
          <el-card class="card">
            <bug-by-status-pie-stat ref="bugByStatusPieStat" id="bugByStatusPieStat" :bugTotal="bugTotal"
                                    :qaCreatedBugJQL="qaCreatedBugJQL"></bug-by-status-pie-stat>
          </el-card>
        </el-col>
        <el-col :span="12" class="grid-content">
          <el-card class="card">
            <bug-by-project-pie-stat ref="bugByProjectPieStat" id="bugByProjectPieStat" :bugTotal="bugTotal"
                                     :qaCreatedBugJQL="qaCreatedBugJQL"></bug-by-project-pie-stat>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="11" class="grid-content">
          <el-card class="card">
            <recent-created-bug-statistics ref="recentCreatedBugStatistics" id="recentCreatedBugStatistics"
                                           :qaCreatedBugJQL="qaCreatedBugJQL"></recent-created-bug-statistics>
          </el-card>
        </el-col>
        <el-col :span="12" class="grid-content">
          <el-card class="card">
            <filter-bug-by-user ref="filterBugByUser" :qaCreatedBugJQL="qaCreatedBugJQL"></filter-bug-by-user>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="23" class="grid-content">
          <el-card class="card">
            <dev-bug-stat ref="devBugStat" id="devBugStat" :qaCreatedBugJQL="qaCreatedBugJQL"
                          :ldapUser="ldapUser"></dev-bug-stat>
          </el-card>
        </el-col>
      </el-row>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsContainer from "@/business/components/common/components/MsContainer";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import RecentCreatedBugStatistics from "@/business/components/report/bugstat/components/recentCreatedBugStatistics";
import FilterBugByUser from "@/business/components/report/bugstat/components/filterBugByUser";
import BugByProjectPieStat from "@/business/components/report/bugstat/components/bugByProjectPieStat";
import BugByStatusPieStat from "@/business/components/report/bugstat/components/bugByStatusPieStat";
import OnlineBugRate from "@/business/components/report/bugstat/components/onlineBugRate";
import QaBugCreatedStat from "@/business/components/report/bugstat/components/qaBugCreatedStat";
import OnlineBugStat from "@/business/components/report/bugstat/components/onlineBugStat";
import {ChangeDecimalToPercentage, fullScreenLoading, jiraAuth, stopFullScreenLoading} from "@/common/js/utils";
import QaOnlineBugRate from "@/business/components/report/bugstat/components/qaOnlineBugRate";
import DevBugStat from "@/business/components/report/bugstat/components/devBugStat";

export default {
  name: "BugStat",
  components: {
    DevBugStat,
    QaOnlineBugRate,
    OnlineBugStat,
    QaBugCreatedStat,
    OnlineBugRate,
    BugByStatusPieStat,
    BugByProjectPieStat,
    FilterBugByUser,
    RecentCreatedBugStatistics,
    MsMainContainer, MsContainer
  },
  mounted() {
    this.search()
  },
  data() {
    return {
      jira_auth: jiraAuth(),
      result: {},
      currentYear: new Date().getFullYear().toString(),
      qaCreatedBugJQL: "issuetype = Bug AND status != Cancelled AND Developer is not EMPTY AND creator in membersOf(QA)",
      onlineBugJQL: "project = \"CS Technical Issues\" AND IssueTypes = Bug AND (labels in (qa-issue) OR key in (CTI-830, CTI-832)) AND Tester in membersOf(QA)",
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
      },
      bugTotal: 0,
      onlineBugTotal: 0,
      percent: 0,
      onlineBugData: null,
      qaBugData: null,
      onlineBugFlag: false,
      qaBugFlag: false,
      ldapUser: null
    }
  },
  computed: {
    percentage() {
      return ChangeDecimalToPercentage(this.onlineBugTotal / this.bugTotal);
    },
    qaBug() {
      return this.qaBugData
    },
    onlineBug() {
      return this.onlineBugData
    }
  },
  methods: {
    search() {
      let qaCreatedBugJQL = ""
      let onlineBugJQL = ""
      if (this.timePicker !== null) {
        qaCreatedBugJQL = "issuetype = Bug AND status != Cancelled AND Developer is not EMPTY AND creator in membersOf(QA)" + " AND \"created\" >= " + this.timePicker[0] + " AND \"created\" <= " + this.timePicker[1]
        onlineBugJQL = "project = \"CS Technical Issues\" AND IssueTypes = Bug AND (labels in (qa-issue) OR key in (CTI-830, CTI-832))" + " AND \"created\" >= " + this.timePicker[0] + " AND \"created\" <= " + this.timePicker[1]   // AND Tester in membersOf(QA)
      } else if (this.timePicker == null) {
        qaCreatedBugJQL = "issuetype = Bug AND status != Cancelled AND Developer is not EMPTY AND creator in membersOf(QA)" + " AND created > startOfYear()"
        onlineBugJQL = "project = \"CS Technical Issues\" AND IssueTypes = Bug AND (labels in (qa-issue) OR key in (CTI-830, CTI-832))" + " AND created > startOfYear()" // AND Tester in membersOf(QA)
      }
      if (qaCreatedBugJQL.startsWith(" AND")) {
        qaCreatedBugJQL = qaCreatedBugJQL.replace(/AND/, '').trim()
      }
      const loading = fullScreenLoading(this);
      this.qaCreatedBugJQL = qaCreatedBugJQL
      this.qaCreatedBugQuery(qaCreatedBugJQL)
      this.onlineBugJQL = onlineBugJQL
      this.onlineBugQuery(onlineBugJQL)
      this.ldapUserQuery()
      stopFullScreenLoading(loading, 2000);
    },

    async qaCreatedBugQuery(JQL) {
      let that = this;
      const maxResults = 1000;
      let initUrl = 'jira/rest/api/2/search?jql=' + JQL + "&maxResults=" + maxResults + "&fields=creator"
      let url = 'jira/rest/api/2/search?jql=' + JQL + "&maxResults=" + maxResults + "&fields=creator,assignee,status,issuetype,project,customfield_10300,created"
      const instance = that.$axios.create({
        headers: {
          'Authorization': that.jira_auth
        }
      });
      try {
        const response = await instance.get(initUrl);
        const totalData = response.data.total;
        that.bugTotal = totalData

        const totalPages = Math.ceil(totalData / maxResults);
        const promiseArray = [];
        for (let i = 0; i < totalPages; i++) {
          promiseArray.push(instance.get(url + `&startAt=` + maxResults * `${i}`))
        }
        let resolvedPromises = await Promise.all(promiseArray)
        let data = []
        for (let i = 0; i < resolvedPromises.length; i++) {
          data = data.concat(resolvedPromises[i].data.issues)
        }
        that.qaBugData = data
        that.qaBugFlag = true
        that.$refs.qaBugCreatedStat.qaBugCreatedStat(data)
        that.$refs.bugByStatusPieStat.bugByStatus(data)
        that.$refs.bugByProjectPieStat.bugByProject(data)
        that.$refs.filterBugByUser.filterBugByUser(data)
        that.$refs.filterBugByUser.filterBugByProject(data)
        that.$refs.devBugStat.devBugStat(data)
        that.$refs.recentCreatedBugStatistics.recentlyCreated(data)
      } catch (err) {
        that.$message.error(err)
      }
    },
    async onlineBugQuery(JQL) {
      let that = this;
      const maxResults = 1000;
      let initUrl = 'jira/rest/api/2/search?jql=' + JQL + "&maxResults=" + maxResults + "&fields=creator"
      let url = 'jira/rest/api/2/search?jql=' + JQL + "&maxResults=" + maxResults + "&fields=creator,assignee,status,issuetype,customfield_10406"
      const instance = that.$axios.create({
        headers: {
          'Authorization': that.jira_auth
        }
      });
      try {
        const response = await instance.get(initUrl);
        const totalData = response.data.total;
        that.onlineBugTotal = totalData

        const totalPages = Math.ceil(totalData / maxResults);
        const promiseArray = [];
        for (let i = 0; i < totalPages; i++) {
          promiseArray.push(instance.get(url + `&startAt=` + maxResults * `${i}`))
        }
        let resolvedPromises = await Promise.all(promiseArray)
        let data = []
        for (let i = 0; i < resolvedPromises.length; i++) {
          data = data.concat(resolvedPromises[i].data.issues)
        }
        that.onlineBugData = data
        that.onlineBugFlag = true
        that.$refs.onlineBugStat.onlineBugStat(data)
        that.$refs.onlineBugRate.onlineBugRate(data)
      } catch (err) {
        that.$message.error(err)
      }
    },
    ldapUserQuery() {
      let url = "dataFactory/ldap/getUsers"
      this.$axios.get(url).then(res => {
          this.ldapUser = res.data
        }
      )
    },
  }
}
</script>

<style scoped>

.grid-content {
  border-radius: 4px;
  min-height: 36px;
  margin: 10px;
}

.card {
  overflow: auto;
}
</style>
