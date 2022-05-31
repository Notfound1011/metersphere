<template>
  <div :id=id style="width: 600px;height:450px;margin: 20px"></div>
</template>

<script>
import {jiraAuth, jiraAddress, groupArray} from "@/common/js/utils";

export default {
  name: "taskByProjectPieStat",
  props: ['id', 'backToDevJQL'],
  data() {
    return {
      jira_auth: jiraAuth(),
      jira_address: jiraAddress(),
      myRecentPieChart: null
    }
  },
  methods: {
    handleResponse(data) {
      let totalCount = data.length
      const pieData = [];
      if (this.myRecentPieChart != null && this.myRecentPieChart != "" && this.myRecentPieChart != undefined) {
        this.myRecentPieChart.dispose();
      }
      this.myRecentPieChart = this.$echarts.init(document.getElementById(this.id));
      groupArray(data, 'project').forEach((value) => { //数组循环
          pieData.push({
            value: value["count"],
            name: value["location"],
            url: this.jira_address + "/issues/?jql=" + this.backToDevJQL + " AND project = " + value["location"]
          })
        }
      )
      this.myRecentPieChart.setOption(
        {
          tooltip: {
            trigger: 'item',
            // formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          toolbox: {
            show: true,
            right: '60',
            feature: {
              mark: {show: true},
              dataView: {show: true, readOnly: false},
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          color: ['#91cc75', '#5470c6', '#ee6666', '#fac858', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
          title: {
            text: 'BackToDev by project',
            subtext: '提测打回任务(项目维度) 总数:' + totalCount,
            left: 'center',
            link: this.jira_address + "/issues/?jql=" + this.backToDevJQL,
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
              name: 'backToDev by creator',
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
              data: pieData
            }
          ]
        }
      )
      this.myRecentPieChart.on("click", function (e) {
        window.open(e.data.url)
      });
    },
  }
}
</script>

<style scoped>

</style>
