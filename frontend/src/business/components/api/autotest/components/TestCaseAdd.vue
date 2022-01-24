<template>
  <div>
    <!-- 新增用例编辑框 -->
    <el-dialog title="新增测试用例" :visible.sync="addCaseDialogVisible" width="70%" :before-close="addCaseHandleClose">
      <el-form :model="addCaseObj" :rules="rules1" ref="addCaseForm1" label-width="120px">
        <el-form-item label="用例名称" prop="case_name">
          <el-input placeholder="必填项" type="textarea" style='width: 95%' :autosize="{ minRows: 1, maxRows: 10 }" v-model="addCaseObj.case_name"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input placeholder="非必填项" type="textarea" style='width: 95%' :autosize="{ minRows: 1, maxRows: 10 }" v-model="addCaseObj.remark"></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" :rules="rules2" ref="addCaseForm2" :model="addCaseObj" label-width="120px">
        <el-form-item label="接口path" prop="path">
          <el-input placeholder="必填项" v-model="addCaseObj.path"></el-input>
        </el-form-item>
        <el-form-item label="请求方式" prop="method">
          <el-select v-model="addCaseObj.method" placeholder="必选项">
            <el-option label="get" value="get"></el-option>
            <el-option label="post" value="post"></el-option>
            <el-option label="put" value="put"></el-option>
            <el-option label="delete" value="delete"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用例类型" prop="case_type">
          <el-select v-model="addCaseObj.case_type" style='width: 178px' placeholder="必选项">
            <el-option label="rest_api" value="rest_api"></el-option>
            <el-option label="pub_api" value="pub_api"></el-option>
            <!--            <el-option label="debug" value="debug"></el-option>-->
          </el-select>
        </el-form-item>
        <el-form-item label="模板类型" prop="template_type">
          <el-select v-model="addCaseObj.template_type" style='width: 178px' placeholder="必选项">
            <el-option label="非模板" value="not_template"></el-option>
            <el-option label="现货模板" value="spot"></el-option>
            <el-option label="合约模板" value="contract"></el-option>
            <!--            <el-option label="debug" value="debug"></el-option>-->
          </el-select>
        </el-form-item>
        <el-form-item label="站点" prop="web_site">
          <el-select v-model="addCaseObj.web_site" placeholder="必选项">
            <el-option label="国际站" value="phemex"></el-option>
            <el-option label="土耳其站" value="turkey"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="触发条件单" prop="trigger_conditional_order">
          <el-tooltip class="tooltip" effect="dark"
                      content="是否需要达成触发价格" placement="right">
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-switch v-model="addCaseObj.trigger_conditional_order" style="margin-left: 20px;"></el-switch>
        </el-form-item>
        <el-form-item label="挂对手单" prop="need_help">
          <el-tooltip class="tooltip" effect="dark"
                      content="是否需要协助挂对手单" placement="right">
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-switch v-model="addCaseObj.need_help" style="margin-left: 20px;"></el-switch>
        </el-form-item>
        <el-form-item label="用例状态" prop="status">
          <el-switch v-model="addCaseObj.status"></el-switch>
        </el-form-item>
      </el-form>
      <el-form :model="addCaseObj" :rules="rules3" ref="addCaseForm3" label-width="120px">
        <el-form-item label="mark" prop="mark">
          <el-select v-model="addCaseObj.mark" multiple filterable allow-create default-first-option
                     placeholder="支持多选，模糊筛选，自定义标签" style="width: 95%">
            <el-option-group
              v-for="group in options"
              :key="group.label"
              :label="group.label">
              <el-option
                v-for="item in group.options"
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-option-group>
          </el-select>
          <!--          <el-tag :key="tag" v-for="tag in addCaseObj.mark" closable-->
          <!--                  :disable-transitions="false" @close="handleClose(tag)">{{ tag }}-->
          <!--          </el-tag>-->
          <!--          <el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue"-->
          <!--                    ref="saveTagInput" size="small" @keyup.enter.native="handleInputConfirm"-->
          <!--                    @blur="handleInputConfirm">-->
          <!--          </el-input>-->
          <!--          <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 新标签</el-button>-->
        </el-form-item>
        <el-form-item label="params" prop="params">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style='width: 95%'
                    placeholder='接口参数: json格式,get请求拼接在url后面,样例：
{
    "params_key":"params_value"
}'
                    v-model="addCaseObj.params">
          </el-input>
        </el-form-item>
        <el-form-item label="json" prop="body_by_json">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style='width: 95%'
                    placeholder='json格式，样例：
{
    "json_key":"json_value"
}'
                    v-model="addCaseObj.body_by_json"></el-input>
        </el-form-item>
        <el-form-item label="form" prop="body_by_form">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style='width: 95%'
                    placeholder='json格式，样例：
{
    "form_key": "form_value"
}'
                    v-model="addCaseObj.body_by_form">
          </el-input>
        </el-form-item>
        <el-form-item label="依赖" prop="depend">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style='width: 95%'
                    placeholder='数组+json格式，样例：
[
    {
        "test_item_1":{
            "code":666
        }
    }
]'
                    v-model="addCaseObj.depend">
          </el-input>
        </el-form-item>
        <el-form-item label="预期结果" prop="expect">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style='width: 95%'
                    placeholder='数组+json格式，样例：
[
    {
        "code":0
    }
]'
                    v-model="addCaseObj.expect">
          </el-input>
        </el-form-item>
        <el-form-item label="清理" prop="clear_up">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style='width: 95%'
                    placeholder='数组+json格式，样例：
[
    {
        "test_key":"test_value"
    }
]'
                    v-model="addCaseObj.clear_up">
          </el-input>
        </el-form-item>
        <el-form-item label="yapi链接" prop="yapi_url">
          <el-input placeholder='yapi_url' style='width: 95%'
                    v-model="addCaseObj.yapi_url">
          </el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="addCaseHandleClose">取 消</el-button>
        <el-button type="primary" @click="addCaseConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {removeEmptyField, isObjectValueEqual} from "@/common/js/utils";

export default {
  name: "TestCaseAdd",
  data() {
    let validateInput = (rule, value, callback) => {
      if (!value) {
        callback();
      }
      let pattern = /^[0-9]*$/;
      if (pattern.test(value)) {
        callback(new Error(this.$t("输入的内容格式不正确").toString()));
      }
      try {
        JSON.parse(value);//将字符串转换成json对象
      } catch (error) {
        callback(new Error(this.$t("输入的内容格式不正确").toString()));
      }
      callback();
    };
    let validateEmptyString = (rule, value, callback) => {
      if (!value.split(" ").join("")) {
        callback(new Error(this.$t("输入的内容格式不正确").toString()));
      }
      callback();
    };
    return {
      addCaseDialogVisible: false,
      inputVisible: false,
      inputValue: '',
      addCaseObjNew: [],
      addCaseObj: {
        case_name: '',
        mark: [],
        path: '',
        method: '',
        params: '',
        body_by_form: '',
        body_by_json: '',
        depend: '',
        expect: '',
        case_type: '',
        remark: '',
        template_type: '',
        created_person: {},
        updated_person: {},
        web_site: '',
        clear_up: '',
        trigger_conditional_order: false,
        need_help: false,
        status: true,
        yapi_url: ''
        // yapi_id: ''
      },
      rules1: {
        case_name: [
          {validator: validateEmptyString, required: true, message: '请填写用例名称', trigger: ['change', 'blur']},
          {min: 1, max: 200, message: this.$t('commons.input_limit', [1, 200]), trigger: ['change', 'blur']}
        ]
      },
      rules2: {
        path: [
          {validator: validateEmptyString, required: true, message: "请填写接口path", trigger: ['change', 'blur']},
        ],
        method: [
          {required: true, message: "缺少必填参数", trigger: 'blur'}
        ],
        case_type: [
          {required: true, message: "缺少必填参数", trigger: 'blur'}
        ],
        template_type: [
          {required: true, message: "缺少必填参数", trigger: 'blur'}
        ],
        web_site: [
          {required: true, message: "缺少必填参数", trigger: 'blur'}
        ]
      },
      rules3: {
        mark: [
          {required: true, message: "缺少必填参数", trigger: 'blur'}
        ],
        params: [
          {validator: validateInput, message: "内容格式不正确", trigger: ['change', 'blur']}
        ],
        body_by_json: [
          {validator: validateInput, message: "内容格式不正确", trigger: ['change', 'blur']}
        ],
        body_by_form: [
          {validator: validateInput, message: "内容格式不正确", trigger: ['change', 'blur']}
        ],
        expect: [
          {validator: validateInput, message: "内容格式不正确", trigger: ['change', 'blur']}
        ],
        depend: [
          {validator: validateInput, message: "内容格式不正确", trigger: ['change', 'blur']}
        ],
        clear_up: [
          {validator: validateInput, message: "内容格式不正确", trigger: ['change', 'blur']}
        ]
      },
      options: []
    }
  },
  created() {
    const adminToken = JSON.parse(localStorage.getItem("Admin-Token"));
    this.addCaseObj.created_person.id = adminToken.id;
    this.addCaseObj.created_person.name = adminToken.name;
    this.addCaseObj.created_person.email = adminToken.email;
  },
  methods: {
    //
    openTestCaseAddDialog() {
      this.getConfig();
      this.resetForm();
      this.addCaseDialogVisible = true;
      this.addCaseObjOrigin = JSON.parse(JSON.stringify(this.addCaseObj));
    },
    //打开后重置dialog中的内容
    resetForm() {
      //重置表单
      if (this.$refs['addCaseForm1']) {
        this.$refs['addCaseForm1'].validate(() => {
          this.$refs['addCaseForm1'].resetFields();
          return true;
        });
      }
      //重置表单
      if (this.$refs['addCaseForm2']) {
        this.$refs['addCaseForm2'].validate(() => {
          this.$refs['addCaseForm2'].resetFields();
          return true;
        });
      }
      //重置表单
      if (this.$refs['addCaseForm3']) {
        this.$refs['addCaseForm3'].validate(() => {
          this.$refs['addCaseForm3'].resetFields();
          return true;
        });
      }
    },
    //
    addCaseHandleClose() {
      if (isObjectValueEqual(JSON.parse(JSON.stringify(this.addCaseObj)), this.addCaseObjOrigin)) {
        this.addCaseDialogVisible = false;   //如果前后数据没有变化，则直接关闭弹窗
      } else {
        this.$confirm('编辑的用例内容未提交，确认关闭？')
          .then(_ => {
            this.addCaseDialogVisible = false;
          })
          .catch(_ => {
          });
      }
    },
    addCaseConfirm() {
      this.$refs['addCaseForm1'].validate((valid) => {
        let param = {};
        param = JSON.parse(JSON.stringify(this.addCaseObj)); //深拷贝，防止影响到原数据
        if (!valid) {
          param.case_name = param.case_name.trim();
          if (param.case_name === '') {
            this.$warning("请输入用例名称");
          }
        } else {
          this.$refs['addCaseForm2'].validate((valid) => {
            if (!valid) {
              if (param.path === '') {
                this.$warning("请输入接口path");
                return;
              }
              if (param.method === '') {
                this.$warning("请选择请求方式");
                return;
              }
              if (param.case_type === '') {
                this.$warning("请选择用例类型");
                return;
              }
              if (param.template_type === '') {
                this.$warning("请选择模板类型");
                return;
              }
              if (param.web_site === '') {
                this.$warning("请选择站点");
              }
            } else {
              this.addCaseObjNew = JSON.parse(JSON.stringify(this.addCaseObj)); //深拷贝，防止影响到原数据
              if (param.mark.length === 0) {
                this.$warning("请填写mark标签");
                return;
              }
              try {
                if (this.addCaseObjNew.params !== '') {
                  this.addCaseObjNew.params = JSON.parse(this.addCaseObjNew.params);//将字符串转换成json对象
                }
              } catch (error) {
                this.$warning("\"params\"：内容格式不正确");
                return;
              }
              try {
                if (this.addCaseObjNew.body_by_json !== '') {
                  this.addCaseObjNew.body_by_json = JSON.parse(this.addCaseObjNew.body_by_json);//将字符串转换成json对象
                }
              } catch (error) {
                this.$warning("\"json\"：内容格式不正确");
                return;
              }
              try {
                if (this.addCaseObjNew.body_by_form !== '') {
                  this.addCaseObjNew.body_by_form = JSON.parse(this.addCaseObjNew.body_by_form);//将字符串转换成json对象
                }
              } catch (error) {
                this.$warning("\"form\"：内容格式不正确");
                return;
              }
              try {
                if (this.addCaseObjNew.depend !== '') {
                  this.addCaseObjNew.depend = JSON.parse(this.addCaseObjNew.depend);//将字符串转换成json对象
                }
              } catch (error) {
                this.$warning("\"依赖\"：内容格式不正确");
                return;
              }
              try {
                if (this.addCaseObjNew.expect !== '') {
                  this.addCaseObjNew.expect = JSON.parse(this.addCaseObjNew.expect);//将字符串转换成json对象
                }
              } catch (error) {
                this.$warning("\"预期结果\"：内容格式不正确");
                return;
              }
              try {
                if (this.addCaseObjNew.clear_up !== '') {
                  this.addCaseObjNew.clear_up = JSON.parse(this.addCaseObjNew.clear_up);//将字符串转换成json对象
                }
              } catch (error) {
                this.$warning("\"清理\"：内容格式不正确");
                return;
              }
              removeEmptyField(this.addCaseObjNew);
              this.$axios.post("/pyServer/TestCase/Create", this.addCaseObjNew).then(res => {
                if (res.data.code === 0) {
                  this.$notify.success({
                    title: "用例名称: " + this.addCaseObjNew.case_name,
                    message: this.$t('用例新增成功').toString()
                  });
                  this.addCaseDialogVisible = false;
                  this.$emit("refresh");
                } else {
                  this.$notify.warning({
                    title: "用例名称: " + this.addCaseObjNew.case_name,
                    message: res.data
                  });
                  this.addCaseDialogVisible = true;
                }
                // this.$set(this.tableData, this.userIndex, this.addCaseObj);
                // this.$emit(this.tableData, this.userIndex, this.addCaseObj);
              }).catch((error) => {
                this.$notify.warning({
                  title: "用例名称: " + this.addCaseObjNew.case_name,
                  // message: this.$t('用例新增失败，请检查填写的内容').toString()
                  message: error
                });
              })
              return false;
            }
          });
        }
      })

    },

    // 处理mark-tag，标签关闭、输入框露出、输入框输入内容确认
    handleClose(tag) {
      this.addCaseObj.mark.splice(this.addCaseObj.mark.indexOf(tag), 1);
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.addCaseObj.mark.push(inputValue.trim());
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    async getConfig() {
      this.$axios.get("/pyServer/TestConfig/Search", {params: {'config_id': '6ea520fc-691d-11ec-940a-3a27e1d6caa4'}}).then(res => {
        this.options = res.data.data[0].config_data
      }).catch(() => {
      })
    }

  }
}
</script>

<style scoped>

.dialog-footer {
  text-align: center;
}

</style>
