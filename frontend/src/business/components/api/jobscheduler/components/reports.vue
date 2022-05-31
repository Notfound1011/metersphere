<template>
  <ms-container>
    <ms-main-container>
      <div>
        <template>
          <h2 style="font-weight:bold">测试报告</h2>
          <el-select v-model="value" filterable placeholder="切换job"
                     style='margin-bottom: 20px'>
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
          <el-button type="primary" @click="getJobInfoList(value)" style="margin-left: 20px">查 询</el-button>
          <!--          <span style=" margin-left:200px ;color:#F00; font-weight:bold">{{value}}</span>-->
        </template>
        <!-- table主体内容 -->
        <el-table :data="(tableDataNew || []).slice((currentPage-1)*pageSize,currentPage*pageSize)"
                  style="width: 100%" border>
          <!--          <el-table-column prop="name" label="job名称" width="170"></el-table-column>-->
          <el-table-column prop="number" label="id" width="80"></el-table-column>
          <el-table-column prop="url" label="jenkins地址" width="120" align="center">
            <template slot-scope="scope">
              <el-link :href="scope.row.url" target="_blank">
                <div v-if="scope.row.url !== '' && scope.row.url != null">
                  <i class="el-icon-link" style="font-size: 15px; color: blue"></i>
                </div>
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="allure_report" label="查看测试报告" width="120" align="center">
            <template slot-scope="scope">
              <el-link :href="scope.row.allure_report"
                       target="_blank">
                <div v-if="scope.row.allure_report !== '' && scope.row.allure_report != null">
                  <i class="el-icon-view" style="font-size: 15px; color: blue"></i>
                </div>
              </el-link>
            </template>
          </el-table-column>
          <el-table-column label="case运行结果" align="center">
            <el-table-column label="通过率" prop="successRate" width="80"></el-table-column>
            <el-table-column label="成功数" prop="passed" width="80"></el-table-column>
            <el-table-column label="失败数" prop="failed" width="80"></el-table-column>
            <el-table-column label="总数" prop="total" width="80"></el-table-column>
          </el-table-column>

          <el-table-column prop="timestamp" label="构建时间" align="center" width="180"></el-table-column>
          <el-table-column prop="duration" label="用时" align="center" width="120"></el-table-column>
          <el-table-column prop="result" label="构建结果" align="center" width="120">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.result==='SUCCESS'" type="success" effect="dark">SUCCESS</el-tag>
              <el-tag v-if="scope.row.result==='FAILURE'" type="danger" effect="dark">FAILURE</el-tag>
              <el-tag v-if="scope.row.result==='UNSTABLE'" type="warning" effect="dark">UNSTABLE</el-tag>
              <el-tag v-if="scope.row.result==='RUNNING'" type="success" effect="dark">RUNNING</el-tag>
              <el-tag v-if="scope.row.result==='ABORTED'" type="info" effect="dark">ABORTED</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                       :current-page="currentPage" :page-sizes="[15, 30, 60, 100]" :page-size="pageSize"
                       layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>
      </div>

    </ms-main-container>
  </ms-container>
</template>

<script>
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";
import {formatTimeStamp, formatTime, jenkinsAuth} from "@/common/js/utils";

export default {
  name: "reports",
  components: {MsMainContainer, MsContainer},
  data() {
    return {
      loading: true,
      JobInfoList: {},
      Jenkins_Crumb: '',
      options: [
        {
          "value": "ApiAutoTestToPhemex",
          "label": "ApiAutoTestToPhemex"
        },
        {
          "value": "ApiAutoTestToTurkey",
          "label": "ApiAutoTestToTurkey"
        }],
      value: '',
      defaultValue: 'ApiAutoTestToPhemex',
      tableDataNew: [],
      tableData: [
        {
          name: '',
          url: '',
          number: '',
          allure_report: '',
          timestamp: '',
          broken: '',
          failed: '',
          passed: '',
          skipped: '',
          total: '',
          unknown: ''
        }
      ],
      total: 0,
      pageSize: 15,
      currentPage: 1,
      historyTrendList: [],
      jenkins_auth: jenkinsAuth()
    }
  },
  activated() {
    const JenkinsInfo = JSON.parse(localStorage.getItem("JenkinsInfo"));
    this.Jenkins_Crumb = JenkinsInfo.Jenkins_Crumb;
    this.getJobInfoList(this.defaultValue)
  },

  methods: {
    // <!--监听pageSize变化-->
    handleSizeChange(newSize) {
      this.pageSize = newSize
    },
    // <!--监听页码值变化-->
    handleCurrentChange(newPage) {
      this.currentPage = newPage;
    },
    // setOption() {     // 下拉框选项数组
    //   this.JenkinsJobList.forEach(e => {
    //     let option = {}
    //     option.value = e
    //     option.label = e
    //     this.options.push(option)
    //   });
    //   this.options = Array.from(new Set(this.options))
    // },
    getJobInfoList(jobName) {
      this.value = jobName
      if (jobName === '') {
        jobName = 'ApiAutoTestToPhemex'
      }
      let url = "/jenkins/job/" + jobName + "/api/json?tree=builds[number,result,duration,timestamp,url]{0,50}"
      this.$axios.post(url, null,
        {headers: {'Authorization': this.jenkins_auth, 'Jenkins-Crumb': this.Jenkins_Crumb}}).then((res) => {
        if (res.status === 200) {
          this.tableData = res.data.builds
          this.total = this.tableData.length
          this.tableData.forEach(e => {
            if (e.result === null) {
              this.$set(e, 'result', 'RUNNING')
            }
            this.$set(e, 'name', jobName)
            this.$set(e, 'allure_report', e.url + "allure/")
            this.$set(e, 'duration', formatTime(e.duration))
            this.$set(e, 'timestamp', formatTimeStamp(e.timestamp))
          })
          this.getHistoryTrend(jobName)
        }
      }).catch((e) => {
        console.log(e);
      })
    },
    getHistoryTrend(jobName) {
      let url = "/jenkins/job/" + jobName + "/allure/widgets/history-trend.json"
      this.$axios.post(url, null,
        {headers: {'Authorization': this.jenkins_auth, 'Jenkins-Crumb': this.Jenkins_Crumb}}).then((res) => {
        if (res.status === 200) {
          let historyTrendList = res.data
          let tableData = JSON.parse(JSON.stringify(this.tableData))
          historyTrendList.forEach(item => {
            tableData.forEach(item2 => {
                if (item2.number === item.buildOrder) {
                  item2.passed = item.data.passed
                  item2.failed = item.data.failed
                  item2.broken = item.data.broken
                  item2.skipped = item.data.skipped
                  item2.total = item.data.total
                  item2.unknown = item.data.unknown
                  item2.successRate = (Math.round(item.data.passed / item.data.total * 10000) / 100.00) + "%"
                  // this.$set(item2, 'passed', item.data.passed)
                }
              }
            )
          })
          // this.$set(this.tableDataNew, 'tableDataNew', this.tableData) //this.$set() 响应式新增与修改数据
          this.tableDataNew = JSON.parse(JSON.stringify(tableData));
        }
      }).catch((e) => {
        console.log(e);
      })
    },
  }
}

</script>

<style scoped>

</style>
