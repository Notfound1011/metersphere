<template>
  <div class="ms-div">
    <template>
      <h2 style="margin-left: 10px;font-weight:bold">报表统计</h2>
      <el-select v-model="value" filterable placeholder="切换job"
                 style='margin-left: 40px;margin-bottom: 20px'>
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button type="primary" @click="getInfoAll(value)" style="margin-left: 20px">查 询</el-button>
    </template>
    <el-row :gutter="20">
      <el-col :span="10">
        <div id="recentPie" style="width: 500px;height:400px;margin-left: 50px"></div>
      </el-col>
      <el-col :span="8" style="margin-left: 80px">
        <el-row :span="7">
          <el-card :body-style="{ padding: '0px' }" style="background: #008B8B;margin: 10px">
            <div style="padding:15px; color: #FFFFFF;float: left">
              <div style="float: top">
                最近一次构建
              </div>
              <el-divider></el-divider>
              <div style="margin-left:20px" class="bottom clearfix">
                <span>构建名称：{{ lastBuildInfo.fullDisplayName }}</span><br>
                <time class="time">上次执行时间：{{ lastBuildInfo.datetime }}</time>
                <br>
                <span>执行结果：{{ lastBuildInfo.result }}</span>
              </div>
            </div>
          </el-card>
        </el-row>
        <el-row :span="7">
          <el-card :body-style="{ padding: '0px' }" style="background: #ff9000;margin: 10px">
            <div style="padding:15px; color: #FFFFFF">
              <div style="float: top">Job调度次数<br>
                <span style="font-size:30px">{{ scheduling_times }}</span>
              </div>
              <el-divider></el-divider>
              <div style="margin-left:20px">
                <span>Job触发的调度次数</span>
              </div>
            </div>
          </el-card>
        </el-row>
      </el-col>
    </el-row>
    <template>
      <div class="Echarts">
        <div id="main" style="width: 80%;height:500%"></div>
      </div>
    </template>
  </div>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";
import {formatTimeStamp, jenkinsAuth} from "@/common/js/utils";

export default {
  name: "analysis",
  components: {MsChart, MsMainContainer, MsContainer},
  data() {
    return {
      lastBuildInfo: {
        fullDisplayName: ''
      },
      JobInfoList: {},
      allureOverallReport: '',
      Jenkins_Crumb: '',
      scheduling_times: '',
      options: [
        {
          "value": "ApiAutoTestToPhemex",
          "label": "ApiAutoTestToPhemex"
        },
        {
          "value": "ApiAutoTestToTurkey",
          "label": "ApiAutoTestToTurkey"
        }],
      value: 'ApiAutoTestToPhemex',
      defaultValue: 'ApiAutoTestToPhemex',
      historyTrendList: [],
      jenkins_auth: jenkinsAuth()
    }
  },
  activated() {
    const JenkinsInfo = JSON.parse(localStorage.getItem("JenkinsInfo"));
    this.Jenkins_Crumb = JenkinsInfo.Jenkins_Crumb;
    this.getInfoAll(this.value);
  },

  methods: {
    getInfoAll(jobName) {
      if (this.value === '') {
        jobName = this.defaultValue
      }
      this.getLastBuildInfo(jobName);
      this.getJobInfoList(jobName);
      this.myEcharts(jobName);
    },
    getLastBuildInfo(jobName) {
      let url = "/jenkins/job/" + jobName + "/lastBuild/api/json"
      this.$axios.post(url, null,
        {headers: {'Authorization': this.jenkins_auth, 'Jenkins-Crumb': this.Jenkins_Crumb}}).then(res => {
        if (res.status === 200) {
          this.lastBuildInfo = res.data
          this.lastBuildInfo.datetime = formatTimeStamp(this.lastBuildInfo.timestamp)
          if (this.lastBuildInfo.result === null) {
            this.lastBuildInfo.result = "RUNNING"
          }
        } else {
          this.$notify.warning({
            title: "获取Jenkins最近一次构建信息失败",
            message: res.statusText
          });
        }
      }).catch((error) => {
        console.log(error);
      })
    },
    getJobInfoList(jobName) {
      let url = "/jenkins/job/" + jobName + "/api/json"
      this.$axios.post(url, null,
        {headers: {'Authorization': this.jenkins_auth, 'Jenkins-Crumb': this.Jenkins_Crumb}}).then(res => {
        if (res.status === 200) {
          this.JobInfoList = res.data
          this.scheduling_times = this.JobInfoList.builds.length
          this.allureOverallReport = this.JobInfoList.url + 'allure'
        } else {
          this.$notify.warning({
            title: "获取Jenkins的任务信息列表",
            message: res.statusText
          });
        }
      }).catch((error) => {
        console.log(error);
      })
    },

    myEcharts(jobName) {
      let url = "/jenkins/job/" + jobName + "/allure/widgets/history-trend.json"
      this.$axios.post(url, null,
        {headers: {'Authorization': this.jenkins_auth, 'Jenkins-Crumb': this.Jenkins_Crumb}}).then((res) => {
        if (res.status === 200) {
          this.historyTrendList = res.data
        }

        const barChartDom = document.getElementById('main');
        const myBarChart = this.$echarts.init(barChartDom);
        const recentPieChartDom = document.getElementById('recentPie');
        const myRecentPieChart = this.$echarts.init(recentPieChartDom);
        var xAxisData = []
        var barSeriesData0 = []
        var barSeriesData1 = []
        var barSeriesData2 = []
        var barSeriesData3 = []
        var barSeriesData4 = []
        var passRateData = []
        for (let i = this.historyTrendList.length - 1; i >= 0; i--) {
          xAxisData.push(this.historyTrendList[i].buildOrder);
          barSeriesData0.push(this.historyTrendList[i].data.passed);
          barSeriesData1.push(this.historyTrendList[i].data.failed);
          barSeriesData2.push(this.historyTrendList[i].data.skipped);
          barSeriesData3.push(this.historyTrendList[i].data.broken);
          barSeriesData4.push(this.historyTrendList[i].data.unknown);
          passRateData.push(Math.round(this.historyTrendList[i].data.passed / this.historyTrendList[i].data.total * 10000) / 100.00)
        }
        myBarChart.setOption({
          title: {
            text: 'TREND',
            subtext: '用例执行结果趋势图',
            link: this.allureOverallReport,
            left: '5%',
            textStyle: {
              fontSize: 25,
              color: "rgba(55, 96, 186, 1)"
            }
          },
          legend: {
            data: ['passed', 'failed', 'skipped', 'broken', 'unknown', 'passRate'],
            left: '20%',
            top: "5%"
          },
          brush: {
            toolbox: ['rect', 'polygon', 'lineX', 'lineY', 'keep', 'clear'],
            xAxisIndex: 0
          },
          toolbox: {
            feature: {
              magicType: {
                type: ['stack']
              },
              dataView: {}
            }
          },
          tooltip: {},
          xAxis: {
            data: xAxisData,
            name: '(构建ID)',
            nameTextStyle: {
              padding: 20,
            },
            axisLine: {onZero: true},
            splitLine: {show: false},
            splitArea: {show: false}
          },
          yAxis: [
            {
              name: '(用例数)',
            },
            {
              type: 'value',
              name: 'pass rate(%)',
              min: 0,
              max: 100,
              interval: 20,
              axisLabel: {
                formatter: '{value}%'
              }
            }
          ],
          grid: {
            top: '20%',
            bottom: 100
          },
          series: [
            {
              name: 'passed',
              type: 'bar',
              stack: 'Ad',
              itemStyle: {
                color: 'rgba(33, 199, 0, 0.9)',
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(255, 99, 71, 1)'
                }
              },
              data: barSeriesData0
            },
            {
              name: 'failed',
              type: 'bar',
              stack: 'Ad',
              itemStyle: {
                color: 'rgba(255, 99, 71, 0.9)',
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(255, 99, 71, 1)'
                }
              },
              data: barSeriesData1
            },
            {
              name: 'skipped',
              type: 'bar',
              stack: 'Ad',
              itemStyle: {
                color: 'rgba(157,163,223, 0.8)',
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(255, 99, 71, 1)'
                }
              },
              data: barSeriesData2
            },
            {
              name: 'broken',
              type: 'bar',
              stack: 'Ad',
              itemStyle: {
                color: 'rgba(106, 90, 240,0.8)',
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(255, 99, 71, 1)'
                }
              },
              data: barSeriesData3
            },
            {
              name: 'unknown',
              type: 'bar',
              stack: 'Ad',
              itemStyle: {
                color: 'rgba(190, 180, 190, 1)',
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(255, 99, 71, 1)'
                }
              },
              data: barSeriesData4
            },
            {
              name: 'passRate',
              type: 'line',
              yAxisIndex: 1,
              color: "rgba(0, 0, 255, 0.6)",
              lineStyle: {
                color: "rgba(0, 0, 255, 0.6)"
              },
              data: passRateData
            }
          ]
        })

        let data0 = this.historyTrendList[0].data.passed;
        let data1 = this.historyTrendList[0].data.failed;
        let data2 = this.historyTrendList[0].data.skipped;
        let data3 = this.historyTrendList[0].data.broken;
        let data4 = this.historyTrendList[0].data.unknown;
        let url = this.historyTrendList[0].reportUrl;
        myRecentPieChart.setOption(
          {
            tooltip: {
              trigger: 'item'
            },
            color: ['rgba(33, 199, 0, 0.9)', 'rgba(255, 99, 71, 1)', 'rgba(157,163,223, 0.8)', 'rgba(106, 90, 240,0.8)', 'rgba(190, 180, 190, 1)'],
            title: {
              text: 'PASS RATE：' + (Math.round(this.historyTrendList[0].data.passed / this.historyTrendList[0].data.total * 10000) / 100.00) + "%",
              subtext: '接口自动化用例通过率（最近一次）',
              link: url,
              left: 'center',
              textStyle: {
                fontSize: 25,
                color: "rgba(55, 96, 186, 1)"
              }
            },
            legend: {
              orient: 'vertical',
              left: 'left'
            },
            series: [
              {
                name: 'passed Rate',
                type: 'pie',
                radius: ['40%', '70%'],
                label: {
                  position: 'center',
                  fontSize: 30,
                  fontWeight: 'bold',
                  show: false,
                  formatter: function (params) { // 默认显示第一个数据
                    if (params.dataIndex === 0) {
                      return params.percent + '%' + '\n' + params.name
                    } else {
                      return ''
                    }
                  },
                  emphasis: {
                    show: true,
                    formatter: function (params) {
                      if (params.dataIndex !== 0) {
                        return params.percent + '%' + '\n' + params.name
                      }
                    }
                  },
                },
                data: [
                  {value: data0, name: 'Passed'},
                  {value: data1, name: 'Failed'},
                  {value: data2, name: 'skipped'},
                  {value: data3, name: 'Broken'},
                  {value: data4, name: 'Unknown'}
                ]
              }
            ]
          }
        )
      })

    },

  }
}
</script>

<style scoped>
.ms-div {
  margin-bottom: 20px;
}
</style>
