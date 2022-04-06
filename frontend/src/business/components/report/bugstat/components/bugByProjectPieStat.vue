<template>
  <div id="main2" style="width: 600px;height:450px;margin: 20px"></div>
</template>

<script>
import {jiraAuth, jiraAddress} from "@/common/js/utils";

export default {
  name: "bugByProjectPieStat",
  data() {
    return {
      pieData: {},
      jira_auth: jiraAuth(),
      jira_address: jiraAddress()
    }
  },
  created() {
    this.bugByProject()
  },
  methods: {
    bugByProject() {
      let url = "jira/rest/gadget/1.0/statistics?filterId=10869&statType=project"
      this.$axios.get(url, {headers: {'Authorization': this.jira_auth}}).then((res) => {
          if (res.status === 200) {
            this.pieData = res.data
            var result = []
            this.pieData.results.forEach((value) => { //数组循环
                result.push({value: value["value"], name: value["key"], url: this.jira_address + value["url"]})
              }
            )
          }

          const pieChartDom = document.getElementById('main2');
          const myRecentPieChart = this.$echarts.init(pieChartDom);

          myRecentPieChart.setOption(
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
              color: ['#8eb021', '#3b7fc4', '#d04437', '#f6c342', '#654982', '#f691b2', '#999999', '#815b3a', '#f79232', '#59afe1', '#f15c75'],
              title: {
                text: 'bug项目统计',
                subtext: 'BUG总数 ' + this.pieData.issueCount,
                left: 'center',
                link: this.jira_address + this.pieData.filterUrl,
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
          myRecentPieChart.on("click", function (e) {
            window.open(e.data.url)
          });
        }
      )
    },
  }
}
</script>

<style scoped>

</style>
