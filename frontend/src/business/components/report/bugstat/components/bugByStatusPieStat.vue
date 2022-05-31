<template>
  <div>
    <div class="header">
      <el-row :gutter="20">
        <el-col :span="10">
          <el-card shadow="hover" class="card">
            <span class="data">{{ this.bugTotal }} <br>
            </span>
            <span class="description">测试环境bug数
            </span>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <div :id="id" style="width: 600px;height:450px;margin: 20px"></div>
  </div>
</template>

<script>
import {jiraAuth, jiraAddress, groupArray} from "@/common/js/utils";

export default {
  name: "bugByStatusPieStat",
  props: ['id', 'qaCreatedBugJQL', 'bugTotal'],
  data() {
    return {
      pieData: {},
      jira_auth: jiraAuth(),
      jira_address: jiraAddress(),
      myRecentPieChart: null
    }
  },
  methods: {
    bugByStatus(data) {
      let that = this
      if (that.myRecentPieChart != null && that.myRecentPieChart != "" && that.myRecentPieChart != undefined) {
        that.myRecentPieChart.dispose();
      }
      that.myRecentPieChart = that.$echarts.init(document.getElementById(this.id));

      const result = [];
      groupArray(data, 'status').forEach((value) => { //数组循环
          result.push({
            value: value["count"],
            name: value["location"],
            url: that.jira_address + "/issues/?jql=" + that.qaCreatedBugJQL + " AND status = \"" + value["location"] + "\""
          })
        }
      )

      that.myRecentPieChart.setOption(
        {
          tooltip: {
            trigger: 'item',
            // formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          toolbox: {
            show: true,
            right: '70',
            feature: {
              mark: {show: true},
              dataView: {show: true, readOnly: false},
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          // color: ['#8eb021', '#3b7fc4', '#d04437', '#f6c342', '#654982', '#f691b2', '#999999'],
          color: ['#91cc75', '#5470c6', '#ee6666', '#fac858', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
          title: {
            text: 'Bug by status',
            // subtext: 'BUG总数 ' + this.pieData.issueCount,
            left: 'center',
            link: that.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL,
            target: 'blank',
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
              name: 'bug 数据统计',
              type: 'pie',
              radius: ['30%', '70%'],
              label: {
                position: 'center',
                fontSize: 25,
                fontWeight: 'bold',
                show: false,
                formatter: function (params) { // 默认显示第一个数据
                  if (params.dataIndex === 0) {
                    return params.percent + '%' + '\n' + params.name
                  } else {
                    return ''
                  }
                },
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '25',
                  fontWeight: 'bold',
                  formatter: function (params) {
                    if (params.dataIndex !== 0) {
                      return params.percent + '%' + '\n' + params.name
                    }
                  }
                }
              },
              data: result
            }
          ]
        }
      )
      that.myRecentPieChart.on("click", function (e) {
        window.open(e.data.url)
      });
    }
  }
}
</script>

<style scoped>
.header {
  margin: 20px;
}

.data {
  color: rgb(255, 153, 0);
  font-size: 20px;
  font-weight: bold;
}

.description {
  color: rgb(128, 127, 127);
  font-size: 16px;
}

.card {
  display: flex;
  justify-content: center;
}
</style>
