<template>
  <ms-container>
    <ms-main-container>
      <h2 style="font-weight:bold">任务运行</h2>
      <div style="margin: 20px">
        <!-- table主体内容 -->
        <el-table :data="tableData" style="width: 100%" border>
          <el-table-column prop="name" label="job名称" show-overflow-tooltip width="170"></el-table-column>
          <el-table-column prop="description" label="描述" show-overflow-tooltip width="180"></el-table-column>
          <!--          <el-table-column prop="numExecutors" label="执行机器数量"  width="120"></el-table-column>-->
          <el-table-column prop="url" label="jenkins地址" width="100" align="center">
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
                       v-if="['ApiAutoTestToPhemex','ApiAutoTestBase','_DEBUG','ApiAutoTestToTurkey'].indexOf(scope.row.name) !== -1"
                       target="_blank">
                <div v-if="scope.row.allure_report !== '' && scope.row.allure_report != null">
                  <i class="el-icon-view" style="font-size: 15px; color: blue"></i>
                </div>
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="上次构建用时" width="110"></el-table-column>
          <el-table-column prop="timestamp" label="上次构建时间" width="160"></el-table-column>
          <el-table-column prop="result" label="上次构建结果" width="110">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.result==='SUCCESS'" type="success" effect="dark">SUCCESS</el-tag>
              <el-tag v-if="scope.row.result==='RUNNING'" type="success" effect="dark">RUNNING</el-tag>
              <el-tag v-if="scope.row.result==='FAILURE'" type="danger" effect="dark">FAILURE</el-tag>
              <el-tag v-if="scope.row.result==='UNSTABLE'" type="warning" effect="dark">UNSTABLE</el-tag>
              <el-tag v-if="scope.row.result==='ABORTED'" type="info" effect="dark">ABORTED</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operation" label="操作" width="80">
            <template slot-scope="scope">
              <!--              <el-button type="primary" icon="el-icon-video-play" @click="runJobs(scope.row.name)" circle></el-button>-->
              <el-button type="primary" icon="el-icon-video-play" @click="openDialog(scope.row)" circle></el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog title="调度参数（不修改则使用默认值）" :visible.sync="dialogVisible" width="70%" :before-close="dialogClose">
          <el-form :inline="true" :model="parameters" :rules="rules" ref="paramForm" label-width="140px">
            <el-form-item label="站点" prop="web_site">
              <el-select v-model="parameters.web_site" placeholder="选择站点" :style="{ width: width * 0.16+'px'}">
                <el-option label="主站" value="phemex"></el-option>
                <el-option label="土耳其站" value="turkey"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="运行环境" prop="env">
              <el-select v-model="parameters.env" placeholder="选择执行环境" :style="{ width: width * 0.16+'px'}"
                         @change="changePhemexHost">
                <el-option label="qa" value="qa"></el-option>
                <el-option label="prod" value="prod"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="rest_host" prop="rest_api_host">
              <el-input placeholder="默认qa环境" v-model="parameters.rest_api_host"
                        :style="{ width: width * 0.16+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="pub_host" prop="pub_api_host">
              <el-input placeholder="默认qa环境" v-model="parameters.pub_api_host"
                        :style="{ width: width * 0.16+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="ws_host" prop="ws_host">
              <el-input placeholder="默认qa环境" v-model="parameters.ws_host"
                        :style="{ width: width * 0.16+'px'}"></el-input>
            </el-form-item>

            <el-form-item label="现货币对列表" prop="spot_symbol_list">
              <el-tooltip class="tooltip" effect="dark"
                          content='设置现货默认symbol, 依赖plan_type, 如果plan_type中不包含spot, 将不执行; 多条用","隔开'
                          placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder='输入现货symbol, 多条用","隔开' v-model="parameters.spot_symbol_list"
                        :style="{ width: width * 0.5+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="合约币对列表" prop="contract_symbol_list">
              <el-tooltip class="tooltip" effect="dark"
                          content='设置现货默认symbol, 依赖plan_type, 如果plan_type中不包含contract, 将不执行; 多条用","隔开'
                          placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder='输入合约symbol, 多条用","隔开' v-model="parameters.contract_symbol_list"
                        :style="{ width: width * 0.5+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="case类型" prop="case_type" class="form-item">
              <el-select v-model="parameters.case_type" multiple placeholder="选择用例类型" style="width: 120%;">
                <el-option label="rest_api" value="rest_api"></el-option>
                <el-option label="pub_api" value="pub_api"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="计划执行类型" prop="plan_type" class="form-item">
              <el-select placeholder='请选择计划执行类型' multiple v-model="parameters.plan_type" style="width: 140%;">
                <el-option label="spot" value="spot"></el-option>
                <el-option label="contract" value="contract"></el-option>
                <el-option label="normal" value="normal"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="指定case id" prop="case_id_List" class="form-item">
              <el-input placeholder='填写case_id，多条用","隔开' v-model="parameters.case_id_List"
                        style="width: 130%;">
              </el-input>
            </el-form-item>
            <el-form-item label="spot价值(倍数)" prop="total_spot_price_multiple" class="form-item">
              <el-tooltip class="tooltip" effect="dark"
                          content="设置倍数；价值基数为对应币对的最小quote价格" placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder="USDT" v-model="parameters.total_spot_price_multiple"
                        class="el-input_inner"></el-input>
            </el-form-item>
            <el-form-item label="合约成交数量" prop="total_contract_number" class="form-item">
              <el-input placeholder="" v-model="parameters.total_contract_number" class="el-input_inner"></el-input>
            </el-form-item>
            <el-form-item label="是否取markPrice" prop="mark_price_by_contract" class="form-item">
              <el-tooltip class="tooltip" effect="dark"
                          content="合约是否按mark price计算; 如果是新合约, 没有ws推送的话, 这里会报错, 默认按last price计算." placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-switch v-model="parameters.mark_price_by_contract"></el-switch>
            </el-form-item>
            <el-form-item label="是否需要机器人挂单" prop="robot_pending_order" class="form-item">
              <el-tooltip class="tooltip" effect="dark"
                          content="是否需要机器人挂单; 如果是新合约, 没有机器人挂单的话, 需要取消勾选" placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-switch v-model="parameters.robot_pending_order"></el-switch>
            </el-form-item>
            <el-form-item label="筛选执行的用例" prop="execution_case_mark" class="form-item">
              <el-tooltip class="tooltip" effect="dark" content='设置要执行的用例筛选的条件; 多条用","隔开' placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder='' v-model="parameters.execution_case_mark" class="el-input_inner2"></el-input>
            </el-form-item>
            <el-form-item label="不执行的用例" prop="not_execution_case_mark">
              <el-tooltip class="tooltip" effect="dark"
                          content='设置不要执行的用例的筛选条件,多条用","隔开；线上账号kyc全部被剥夺, 需要过滤kyc的case.'
                          placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder='' v-model="parameters.not_execution_case_mark"
                        class="el-input_inner2"></el-input>
            </el-form-item>
          </el-form>
          <span slot="footer">
            <el-button @click="dialogClose">取 消</el-button>
            <el-button type="primary" @click="runConfirm">运 行</el-button>
          </span>
        </el-dialog>

        <el-dialog title="调度参数（不修改则使用默认值）" :visible.sync="turkeyDialogVisible" width="70%"
                   :before-close="turkeyDialogClose">
          <el-form :inline="true" :model="turkeyParameters" :rules="rules" ref="turkeyParamForm" label-width="140px">
            <el-form-item label="站点" prop="web_site">
              <el-select v-model="turkeyParameters.web_site" disabled placeholder="选择站点"
                         :style="{ width: width * 0.16+'px'}">
                <el-option label="土耳其站" value="turkey"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="运行环境" prop="env">
              <el-select v-model="turkeyParameters.env" @change="changeTurkeyHost" placeholder="选择执行环境"
                         :style="{ width: width * 0.16+'px'}">
                <el-option label="qa" value="qa"></el-option>
                <el-option label="prod" value="prod"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="rest_host" prop="rest_api_host">
              <el-input placeholder="默认qa环境" v-model="turkeyParameters.rest_api_host"
                        :style="{ width: width * 0.16+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="ws_host" prop="ws_host">
              <el-input placeholder="默认qa环境" v-model="turkeyParameters.ws_host"
                        :style="{ width: width * 0.16+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="pub_host" prop="pub_api_host">
              <el-input placeholder="默认qa环境" v-model="turkeyParameters.pub_api_host" disabled
                        :style="{ width: width * 0.16+'px'}"></el-input>
            </el-form-item>
            <el-form-item label="case类型" prop="case_type" class="form-item">
              <el-select v-model="turkeyParameters.case_type" multiple placeholder="选择用例类型" style="width: 120%;">
                <el-option label="rest_api" value="rest_api"></el-option>
                <el-option label="pub_api" value="pub_api"></el-option>
              </el-select>
            </el-form-item>
            <!--            <el-form-item label="计划执行类型" prop="plan_type" class="form-item">-->
            <!--              <el-select placeholder='请选择计划执行类型' disabled v-model="turkeyParameters.plan_type">-->
            <!--                <el-option label="normal" value="normal"></el-option>-->
            <!--              </el-select>-->
            <!--            </el-form-item>-->
            <el-form-item label="指定case id" prop="case_id_List" class="form-item">
              <el-input placeholder='填写case_id，多条用","隔开' v-model="turkeyParameters.case_id_List"
                        style="width: 120%;">
              </el-input>
            </el-form-item>
            <el-form-item label="筛选执行的用例" prop="execution_case_mark" class="form-item">
              <el-tooltip class="tooltip" effect="dark" content='设置要执行的用例筛选的条件; 多条用","隔开' placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder='' v-model="turkeyParameters.execution_case_mark"
                        class="el-input_inner2"></el-input>
            </el-form-item>
            <el-form-item label="不执行的用例" prop="not_execution_case_mark">
              <el-tooltip class="tooltip" effect="dark" content='设置不要执行的用例的筛选条件,多条用","隔开' placement="right">
                <i class="el-icon-question"/>
              </el-tooltip>
              <el-input placeholder='' v-model="turkeyParameters.not_execution_case_mark"
                        class="el-input_inner2"></el-input>
            </el-form-item>
          </el-form>
          <span slot="footer">
              <el-button @click="turkeyDialogClose">取 消</el-button>
              <el-button type="primary" @click="turkeyRunConfirm">运 行</el-button>
            </span>
        </el-dialog>
        <el-dialog title="任务调度" :visible.sync="normalDialogVisible" width="20%">
            <span slot="footer">
              <el-button @click="normalDialogClose">取 消</el-button>
              <el-button type="primary" @click="normalRunConfirm">运 行</el-button>
            </span>
        </el-dialog>
      </div>

    </ms-main-container>
  </ms-container>
</template>

<script>
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";
import {formatTimeStamp, formatTime, isObjectValueEqual, jenkinsAuth} from "@/common/js/utils";

export default {
  name: "runJobs",
  components: {MsMainContainer, MsContainer},
  data() {
    let validateStrInput = (rule, value, callback) => {
      if (!value) {
        callback();
      }
      let pattern = /^([a-zA-Z]+[,])*([a-zA-Z]+)$/;
      if (!pattern.test(value)) {
        callback(new Error(this.$t("输入的内容格式不正确").toString()));
      }
      callback();
    };
    return {
      result: {},
      width: document.body.clientWidth,
      JenkinsJobList: [],
      json: {},
      JobInfoList: {},
      Jenkins_Crumb: '',
      value: 'ApiAutoTestToPhemex',
      dialogVisible: false,
      turkeyDialogVisible: false,
      normalDialogVisible: false,
      parameters: {
        rest_api_host: 'https://qa.phemex.com',
        pub_api_host: 'https://testnet-api.phemex.com',
        ws_host: 'wss://qa.phemex.com',
        env: 'qa',
        web_site: 'phemex',
        spot_symbol_list: 'sBTCUSDT,sETHUSDT',
        contract_symbol_list: 'BTCUSD,uBTCUSD,cETHUSD',
        case_type: ['rest_api', 'pub_api'],
        total_spot_price_multiple: '1.5',
        total_contract_number: '5',
        case_id_List: '',
        robot_pending_order: true,
        plan_type: ['spot', 'contract', 'normal'],
        mark_price_by_contract: false,
        execution_case_mark: 'p0',
        not_execution_case_mark: 'error',
        assert_socket_switch: false
      },
      turkeyParameters: {
        rest_api_host: 'https://api.qa1.phemex.com.tr',
        pub_api_host: 'https://testnet-api.phemex.com',
        ws_host: 'wss://ws.qa1.phemex.com.tr',
        env: 'qa',
        web_site: 'turkey',
        case_type: ['rest_api', 'pub_api'],
        case_id_List: '',
        // plan_type: 'normal',
        execution_case_mark: 'p0',
        not_execution_case_mark: 'error',
        assert_socket_switch: false
      },
      runName: '',
      tableData: [
        {
          name: '',
          url: '',
          allure_report: '',
          result: ''
        }
      ],
      rules: {
        rest_api_host: [
          {required: true, message: '必填项', trigger: 'blur'},
        ],
        pub_api_host: [
          {required: true, message: '必填项', trigger: 'blur'},
        ],
        ws_host: [
          {required: true, message: '必填项', trigger: 'blur'},
        ],
        spot_symbol_list: [
          {validator: validateStrInput, message: '请检查数据格式', trigger: ['change', 'blur']},
        ],
        contract_symbol_list: [
          {validator: validateStrInput, message: '请检查数据格式', trigger: ['change', 'blur']},
        ],
        plan_type: [
          {validator: validateStrInput, message: '请检查数据格式', trigger: ['change', 'blur']},
        ],
      },
      jenkins_auth: jenkinsAuth()
    }
  },
  activated() {
    const JenkinsInfo = JSON.parse(localStorage.getItem("JenkinsInfo"));
    this.Jenkins_Crumb = JenkinsInfo.Jenkins_Crumb;
    this.getJobList();
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  beforeRouteLeave(to, from, next) {
    next();
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  methods: {
    changePhemexHost() {
      if (this.parameters.env === "prod") {
        this.parameters.rest_api_host = 'https://phemex.com'
        this.parameters.pub_api_host = 'https://api.phemex.com'
        this.parameters.ws_host = 'wss://phemex.com'
      } else {
        this.parameters.rest_api_host = 'https://qa.phemex.com'
        this.parameters.pub_api_host = 'https://testnet-api.phemex.com'
        this.parameters.ws_host = 'wss://qa.phemex.com'
      }
    },
    changeTurkeyHost() {
      if (this.turkeyParameters.env === "prod") {
        this.turkeyParameters.rest_api_host = 'https://api.phemex.com.tr'
        // this.parameters.pub_api_host = 'https://api.phemex.com'
        this.turkeyParameters.ws_host = 'wss://ws.phemex.com.tr'
      } else {
        this.turkeyParameters.rest_api_host = 'https://api.qa1.phemex.com.tr'
        // this.parameters.pub_api_host = 'https://testnet-api.phemex.com'
        this.turkeyParameters.ws_host = 'wss://ws.qa1.phemex.com.tr'
      }
    },
    getJobList() {
      this.$axios.post("/jenkins/api/json?tree=jobs[name,url,description,builds[number,result,duration,timestamp,url]{0,1}]", null,
        {
          headers: {
            'Authorization': this.jenkins_auth,
            'Jenkins-Crumb': this.Jenkins_Crumb
          }
        }).then((res) => {
        if (res.status === 200) {
          this.tableData = res.data.jobs
          this.tableData.forEach(e => {
            this.JenkinsJobList.push(e.name)
            if (e.builds.length !== 0) {
              if (e.builds[0].result === null) {
                e.result = "RUNNING"
              } else {
                e.result = e.builds[0].result
                e.allure_report = e.builds[0].url + "allure/"
              }
              e.duration = formatTime(e.builds[0].duration)
              e.timestamp = formatTimeStamp(e.builds[0].timestamp)
            }
          })
          if (JSON.stringify(this.tableData).indexOf("RUNNING") !== -1 || JSON.stringify(this.tableData).indexOf("duration: \"0秒\"") !== -1) {
            clearInterval(this.timer);
            this.timer = null;
            this.timer = setInterval(() => {
              setTimeout(this.getJobList, 0)
            }, 1000 * 10)
          }
        }
      }).catch((e) => {
        console.log(e);
      })
    },
    refreshJenkinsCrumb() {
      // Promise 异步处理接口请求，A接口处理完，再请求B接口
      const promise = new Promise(resolve => {
          this.$axios.get("/jenkins/crumbIssuer/api/xml",
            {
              params: {'xpath': 'concat(//crumbRequestField,":",//crumb)'},
              headers: {'Authorization': this.jenkins_auth}
            }).then(res => {
            if (res.status === 200) {
              this.json.Jenkins_Crumb = res.data.split(":")[1];
              localStorage.setItem("JenkinsInfo", JSON.stringify(this.json));
              const JenkinsInfo = JSON.parse(localStorage.getItem("JenkinsInfo"));
              this.Jenkins_Crumb = JenkinsInfo.Jenkins_Crumb;
            }
            resolve(res);
          }).catch((err) => {
            console.log(err)
          })
        }
      );
      promise.then(() => {
        this.getJobList()
      })
    },

    runJobs(name, body) {
      // 带参数的build
      if (body !== undefined) {
        let url = "/jenkins/job/" + name + "/buildWithParameters"
        this.$axios({
          url: url,
          method: 'post',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': this.jenkins_auth,
            'Jenkins-Crumb': this.Jenkins_Crumb
          },
          transformRequest: [function (data) {
            let ret = ''
            for (let it in data) {
              ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
            }
            return ret
          }],
          data: body,
          withCredentials: true
        }).then((res) => {
          this.$notify.success({
            title: "job执行成功",
            message: res.statusText
          });
          if (name === 'ApiAutoTestToPhemex' || name === '_DEBUG') {
            this.dialogVisible = false;
          } else if (name === 'ApiAutoTestToTurkey') {
            this.turkeyDialogVisible = false;
          }
          this.getJobList()
          this.timer = setInterval(() => {
            setTimeout(this.getJobList, 0)
          }, 1000 * 10)
        }).catch((e) => {
          console.log(e);
        })
      } else {
        // 不带参数的build
        let url = "/jenkins/job/" + name + "/build"
        this.$axios.post(url, null,
          {headers: {'Authorization': this.jenkins_auth, 'Jenkins-Crumb': this.Jenkins_Crumb}}).then((res) => {
          if (res.status === 201) {
            this.tableData = res.data.jobs
            this.$notify.success({
              title: "job执行成功",
              message: res.statusText
            });
            this.normalDialogVisible = false;
            this.getJobList()
            this.timer = setInterval(() => {
              setTimeout(this.getJobList, 0)
            }, 1000 * 10)
          } else {
            this.$notify.warning({
              title: "job执行失败",
              message: res.statusText
            });
          }
        }).catch((error) => {
          this.$notify.error({
            title: "job执行失败",
            message: error,
          });
        })
      }
    },

    openDialog(item) {
      if (['ApiAutoTestToPhemex', 'ApiAutoTestBase', '_DEBUG'].indexOf(item.name) !== -1) {
        this.resetForm();
        this.dialogVisible = true;
        this.runName = item.name;
        this.parametersOrigin = JSON.parse(JSON.stringify(this.parameters));
      } else if (item.name === 'ApiAutoTestToTurkey') {
        this.resetForm();
        this.turkeyDialogVisible = true;
        this.runName = item.name;
        this.turkeyParametersOrigin = JSON.parse(JSON.stringify(this.turkeyParameters));
      } else {
        this.normalDialogVisible = true;
        this.runName = item.name;
      }
    },
    resetForm() {
      //重置表单
      if (this.$refs['paramForm']) {
        this.$refs['paramForm'].validate(() => {
          this.$refs['paramForm'].resetFields();
          return true;
        });
      }
      //重置表单
      if (this.$refs['turkeyParamForm']) {
        this.$refs['turkeyParamForm'].validate(() => {
          this.$refs['turkeyParamForm'].resetFields();
          return true;
        });
      }
    },
    dialogClose() {
      if (isObjectValueEqual(JSON.parse(JSON.stringify(this.parameters)), this.parametersOrigin)) {
        this.dialogVisible = false;   //如果前后数据没有变化，则直接关闭弹窗
      } else {
        this.$confirm('job未执行，确认关闭？')
          .then(_ => {
            this.dialogVisible = false;
          })
          .catch(_ => {
          });
      }
    },
    turkeyDialogClose() {
      if (isObjectValueEqual(JSON.parse(JSON.stringify(this.turkeyParameters)), this.turkeyParametersOrigin)) {
        this.turkeyDialogVisible = false;   //如果前后数据没有变化，则直接关闭弹窗
      } else {
        this.$confirm('job未执行，确认关闭？')
          .then(_ => {
            this.turkeyDialogVisible = false;
          })
          .catch(_ => {
          });
      }
    },
    normalDialogClose() {
      this.normalDialogVisible = false;
    },

    runConfirm() {
      this.runJobs(this.runName, this.parameters)
    },
    turkeyRunConfirm() {
      this.runJobs(this.runName, this.turkeyParameters)
    },
    normalRunConfirm() {
      this.runJobs(this.runName)
    }

  }
}
</script>

<style scoped>
.el-input_inner {
  margin-left: 10px;
  width: 120px;
}

.el-input_inner2 {
  margin-left: 10px;
  width: 200px;
}

.tooltip {
  margin-right: 10px;
}

.form-item {
  margin-left: 20px;
}
</style>
