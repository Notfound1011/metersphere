<template>
  <ms-container>
    <ms-main-container v-loading="result.loading">
      <el-card>
        <template>
          <!--      <h2 style="margin-left: 40px;font-weight:bold">功能用例统计</h2>-->
          <el-select v-model="value" filterable placeholder="切换成员" clearable @change="getInfoAll()"
                     style='margin-left: 40px;margin-bottom: 10px;margin-top: 20px'>
            <el-option
              v-for="item in users"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
          <el-date-picker
            style='margin-left: 20px'
            @change="getInfoAll()"
            v-model="time"
            value-format="timestamp"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :default-time="['00:00:00', '23:59:59']">
          </el-date-picker>
          <el-button type="primary" @click="getInfoAll()" style="margin-left: 20px">查 询</el-button>
        </template>
        <el-row :gutter="20">
          <el-col :span="6" style="margin-left: 30px">
            <el-card :body-style="{ padding: '0px' }" style="background: rgba(55, 96, 186, 1);margin: 10px">
              <div style="padding:15px; color: #FFFFFF;float: left">
                <div style="float: top" v-if="value ===''">
                  用例总数<br>
                  <span style="font-size:30px">{{ allCaseCountNumber }}</span>
                </div>
                <div style="float: top" v-if="value !=='' && value !== '1'">个人用例总数<br>
                  <span style="font-size:30px">{{ value }}: {{ allCaseCountNumberByUser }}</span>
                </div>
                <div style="float: top" v-if="value === '1'">个人用例总数<br>
                  <span style="font-size:30px">QA: {{ allCaseCountNumberByUser }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <template>
          <div class="Echarts" id="main" style="width:90%;height:500%;">
          </div>
        </template>
      </el-card>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";
import {groupByMonth, groupByWeek, groupByYear} from "@/common/js/utils";

export default {
  name: "analysis",
  components: {MsChart, MsMainContainer, MsContainer},
  data() {
    return {
      result: {},
      users: [],
      value: '',
      time: '',
      allCaseCountNumber: 0,
      allCaseCountNumberByUser: 0,
      barSeriesDataDay: [],
      barSeriesDataWeek: [],
      barSeriesDataMonth: []
    }
  },
  activated() {
    this.getUser()
    this.getInfoAll()
  },

  methods: {
    getInfoAll() {
      this.myEcharts();
    },

    getUser() {
      let path = "user/list/";
      this.$get(path, res => {
        this.users = res.data
      });
    },

    myEcharts() {
      let param = {}
      if (this.value === '' && (this.time === '' || this.time == null)) {
        param = {};
      } else if (this.value !== '' && (this.time === '' || this.time == null)) {
        param = {createUser: this.value};
      } else if (this.value === '' && this.time !== '' && this.time !== null) {
        param = {
          startTime: this.time[0],
          endTime: this.time[1]
        };
      } else {
        param = {
          createUser: this.value,
          startTime: this.time[0],
          endTime: this.time[1]
        };
      }

      let url = "/track/count/all"
      this.$axios.post(url, param).then((res) => {
          this.allCaseCountNumber = res.data.data.allCaseCountNumber
          this.allCaseCountNumberByUser = res.data.data.allCaseCountNumberByUser
          let data = res.data.data.caseCount //获取数组的长度
          if (document.getElementById('main') == null) {
            return
          }
          this.$echarts.dispose(document.getElementById('main'))
          let myChart = this.$echarts.init(document.getElementById('main'))
          let xAxisData = []
          let dayX = []
          let weekX = []
          let monthX = []
          let yearX = []
          let barSeriesDataDay = []
          let barSeriesDataWeek = []
          let barSeriesDataMonth = []
          let barSeriesDataYear = []

          let option = {
            visualMap: {
              show: false,
              type: 'continuous',
              seriesIndex: 0,
              min: 0,
              max: 600
            },
            title: {
              text: '用例新增趋势图',
              subtext: 'Trend Chart of New Testcase',
              left: '4%',
              textStyle: {
                fontSize: 25,
                color: "rgba(55, 96, 186, 1)"
              }
            },
            legend: {
              data: ['按天', '按周', '按月', '按年'],
              selectedMode: 'single', // 单选
              selected: {
                按天: true,
                按周: false,
                按月: false,
                按年: false
              }
            },
            toolbox: {
              show: true,
              feature: {
                dataZoom: {
                  yAxisIndex: 'none'
                },
                dataView: {readOnly: false},
                magicType: {type: ['line', 'bar']},
                restore: {},
                saveAsImage: {}
              }
            },
            tooltip: {
              trigger: 'axis',
              transitionDuration: 0, //echarts防止tooltip的抖动
            },
            xAxis: {
              data: xAxisData,
              name: '日期'
            },
            yAxis: [
              {
                name: '用例数',
              },
              {
                type: 'value',
              }
            ],
            grid: {
              top: '20%',
              bottom: 100
            },
            series: [
              {
                name: '按天',
                type: 'line',
                showSymbol: false,
                data: barSeriesDataDay
              },
              {
                name: '按周',
                type: 'line',
                showSymbol: false,
                data: barSeriesDataWeek
              },
              {
                name: '按月',
                type: 'line',
                showSymbol: false,
                data: barSeriesDataMonth
              },
              {
                name: '按年',
                type: 'line',
                showSymbol: false,
                data: barSeriesDataYear
              }
            ]
          }
          for (var i = 0; i < data.length; i++) {
            xAxisData.push(data[i].groupField);
            dayX.push(data[i].groupField);
            barSeriesDataDay.push(data[i].countNumber);
          }

          const week = groupByWeek(xAxisData, barSeriesDataDay);
          const month = groupByMonth(xAxisData, barSeriesDataDay);
          const year = groupByYear(xAxisData, barSeriesDataDay);
          for (var h = 0; h < week.newDate.length; h++) {
            weekX.push(week.newDate[h]);
            barSeriesDataWeek.push(week.newValue[h]);
          }
          for (var j = 0; j < month.newDate.length; j++) {
            monthX.push(month.newDate[j]);
            barSeriesDataMonth.push(month.newValue[j]);
          }
          for (var k = 0; k < year.newDate.length; k++) {
            yearX.push(year.newDate[k]);
            barSeriesDataYear.push(year.newValue[k]);
          }
          myChart.setOption(option, true)
          myChart.on('legendselectchanged', obj => {
            var options = myChart.getOption()
            //这里是选择切换什么样的x轴，那么他会进行对Y值的切换
            if (obj.name == '按天') {
              options.xAxis = {
                data: dayX,
                name: '日期'
              }
            } else if (obj.name == '按周') {
              options.xAxis = {
                data: weekX,
                name: '日期'
              }
            } else if (obj.name == '按月') {
              options.xAxis = {
                data: monthX,
                name: '日期'
              }
              // 大多数数据更新了后，已经初始化后的图表X轴或者Y轴仍然不变。 因为xAxis或者yAxis，在初始化的时候赋值为一个对象，
              // 而刷新数据的时候只是刷新xAxis的data部分，这个时候xAxis是找不到数组中到底哪个数据在刷新，
              // 因此，你可以直接修改整个xAxis的值，而不是xAxis.data
            } else if (obj.name == '按年') {
              options.xAxis = {
                data: yearX,
                name: '日期'
              }
            }
            myChart.setOption(options, true)
          })

          // const barChartDom = document.getElementById('main');
          // const myBarChart = this.$echarts.init(barChartDom);
        }
      )
    },

  }
}
</script>

<style scoped>
.ms-div {
  margin-bottom: 20px;
}

.Echarts {
  margin-top: 20px;
}
</style>
