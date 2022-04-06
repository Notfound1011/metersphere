<template>
  <div id="pieBugStatistics" style="width: 600px;height:450px;margin: 20px"></div>
</template>

<script>
import {JIRA_ADDRESS, JIRA_AUTH} from "@/common/js/constants";

export default {
  name: "pieBugStatistics",
  data() {
    return {
      pieData: {},
    }
  },
  created() {
    this.pieBugStatistics()
  },
  methods: {
    pieBugStatistics() {
      let url = "jira/rest/gadget/1.0/statistics?filterId=10869&statType=statuses&_=1646032901468"
      this.$axios.get(url, {headers: {'Authorization': JIRA_AUTH}}).then((res) => {
          if (res.status === 200) {
            this.pieData = res.data
            var keyObj = {}
            var valueObj = {}
            var urlObj = {}
            this.pieData.results.forEach((value, i) => { //数组循环
                keyObj["key" + i] = value["key"];
                valueObj["value" + i] = value["value"];
                urlObj["url" + i] = JIRA_ADDRESS + value["url"];
              }
            )
          }

          const pieChartDom = document.getElementById('pieBugStatistics');
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
              color: ['#8eb021', '#3b7fc4', '#d04437', '#f6c342', '#654982', '#f691b2', '#999999'],
              title: {
                text: 'BUG总数 ' + this.pieData.issueCount,
                subtext: 'bug数据统计饼图',
                left: 'center',
                link: JIRA_ADDRESS + this.pieData.filterUrl,
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
                  data: [
                    {value: valueObj.value0, name: keyObj.key0, url: urlObj.url0},
                    {value: valueObj.value1, name: keyObj.key1, url: urlObj.url1},
                    {value: valueObj.value2, name: keyObj.key2, url: urlObj.url2},
                    {value: valueObj.value3, name: keyObj.key3, url: urlObj.url3},
                    {value: valueObj.value4, name: keyObj.key4, url: urlObj.url4},
                    {value: valueObj.value5, name: keyObj.key5, url: urlObj.url5},
                    {value: valueObj.value6, name: keyObj.key6, url: urlObj.url6},
                  ]
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
