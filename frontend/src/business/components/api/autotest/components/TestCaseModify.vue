<template>
  <div>
    <!-- 修改用例编辑框 -->
    <el-dialog title="修改测试用例" :visible.sync="editCaseDialogVisible" width="70%" :before-close="editCaseHandleClose">
      <el-form :rules="rules1" ref="editCaseForm1" :model="editCaseObj.update_data" label-width="120px">
        <el-form-item label="用例名称" prop="case_name">
          <el-input placeholder="必填项" type="textarea" :autosize="{ minRows: 1, maxRows: 10 }"
                    style="width: 95%" v-model="editCaseObj.update_data.case_name"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input placeholder="非必填项" type="textarea" :autosize="{ minRows: 1, maxRows: 10 }"
                    style="width: 95%" v-model="editCaseObj.update_data.remark"></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" :rules="rules2" ref="editCaseForm2" :model="editCaseObj.update_data" label-width="120px">
        <el-form-item label="接口path" prop="path">
          <el-input placeholder="必填项" v-model="editCaseObj.update_data.path"></el-input>
        </el-form-item>
        <el-form-item label="请求方式" prop="method">
          <el-select v-model="editCaseObj.update_data.method" placeholder="必选项">
            <el-option label="get" value="get"></el-option>
            <el-option label="post" value="post"></el-option>
            <el-option label="put" value="put"></el-option>
            <el-option label="delete" value="delete"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用例类型" prop="case_type" required>
          <el-select v-model="editCaseObj.update_data.case_type" style='width: 178px' placeholder="必选项">
            <el-option label="rest_api" value="rest_api"></el-option>
            <el-option label="pub_api" value="pub_api"></el-option>
            <!--            <el-option label="debug" value="debug"></el-option>-->
          </el-select>
        </el-form-item>
        <el-form-item label="模板类型" prop="template_type" required>
          <el-select v-model="editCaseObj.update_data.template_type" style='width: 178px' placeholder="必选项">
            <el-option label="非模板" value="not_template"></el-option>
            <el-option label="现货模板" value="spot"></el-option>
            <el-option label="合约模板" value="contract"></el-option>
            <!--            <el-option label="debug" value="debug"></el-option>-->
          </el-select>
        </el-form-item>
        <el-form-item label="站点" prop="web_site" required>
          <el-select v-model="editCaseObj.update_data.web_site" placeholder="必选项">
            <el-option label="国际站" value="phemex"></el-option>
            <el-option label="土耳其站" value="turkey"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="触发条件单">
          <el-tooltip class="tooltip" effect="dark"
                      content="是否需要达成触发价格" placement="right">
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-switch v-model="editCaseObj.update_data.trigger_conditional_order" style="margin-left: 20px;"></el-switch>
        </el-form-item>
        <el-form-item label="挂对手单">
          <el-tooltip class="tooltip" effect="dark"
                      content="是否需要协助挂对手单" placement="right">
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-switch v-model="editCaseObj.update_data.need_help" style="margin-left: 20px;"></el-switch>
        </el-form-item>
        <el-form-item label="用例状态">
          <el-switch v-model="editCaseObj.update_data.status"></el-switch>
        </el-form-item>
      </el-form>
      <el-form ref="form" :model="editCaseObj.update_data" :rules="rules3" label-width="120px">
        <el-form-item label="mark" prop="mark">
          <el-select v-model="editCaseObj.update_data.mark" multiple filterable allow-create default-first-option
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
          <!-- <el-tag :key="tag" v-for="tag in editCaseObj.update_data.mark" closable
                        :disable-transitions="false" @close="handleClose(tag)">{{ tag }}
                </el-tag>
                <el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue"
                          ref="saveTagInput" size="small" @keyup.enter.native="handleInputConfirm"
                          @blur="handleInputConfirm">
                </el-input>
                <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 新标签</el-button> -->
        </el-form-item>
        <el-form-item label="params" prop="params">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style="width: 95%"
                    placeholder='接口参数: json格式,get请求拼接在url后面,样例：
{
    "params_key":"params_value"
}'
                    v-model="editCaseObj.update_data.params">
          </el-input>
        </el-form-item>
        <el-form-item label="json" prop="body_by_json">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style="width: 95%"
                    placeholder='json格式，样例：
{
    "json_key":"json_value"
}'
                    v-model="editCaseObj.update_data.body_by_json"></el-input>
        </el-form-item>
        <el-form-item label="form" prop="body_by_form">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style="width: 95%"
                    placeholder='json格式，样例：
{
    "form_key": "form_value"
}'
                    v-model="editCaseObj.update_data.body_by_form">
          </el-input>
        </el-form-item>
        <el-form-item label="依赖" prop="depend">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style="width: 95%"
                    placeholder='数组+json格式，样例：
[
    {
        "test_item_1":{
            "code":666
        }
    }
]'
                    v-model="editCaseObj.update_data.depend">
          </el-input>
        </el-form-item>
        <el-form-item label="预期结果" prop="expect">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style="width: 95%"
                    placeholder='数组+json格式，样例：
[
    {
        "code":0
    }
]'
                    v-model="editCaseObj.update_data.expect">
          </el-input>
        </el-form-item>
        <el-form-item label="清理" prop="clear_up">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 10 }" style="width: 95%"
                    placeholder='数组+json格式，样例：
[
    {
        "test_key":"test_value"
    }
]'
                    v-model="editCaseObj.update_data.clear_up">
          </el-input>
        </el-form-item>
        <el-form-item label="yapi链接">
          <el-input placeholder='yapi_url' style="width: 95%"
                    v-model="editCaseObj.update_data.yapi_url">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editCaseHandleClose">取 消</el-button>
        <el-button type="primary" @click="editCaseConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {removeEmptyField, isObjectValueEqual} from "@/common/js/utils";

export default {
  name: "TestCaseModify",
  props: ['tableData'],
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
      editCaseDialogVisible: false,
      inputVisible: false,
      inputValue: '',
      update_data_origin: '',
      editCaseObjNew: [],
      editCaseObj: {
        update_data: {
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
        },
        case_id: ''
      },
      rules1: {
        case_name: [
          {validator: validateEmptyString, required: true, message: '请填写用例名称', trigger: ['change', 'blur']},
          {min: 1, max: 200, message: this.$t('commons.input_limit', [1, 200]), trigger: ['change', 'blur']}
        ]
      },
      rules2: {
        case_name: [
          {required: true, message: '请填写用例名称', trigger: 'blur'}
        ],
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
      userIndex: 0,
      options: []
    }
  },
  methods: {
    //编辑弹窗，默认将table中的数据赋值给编辑页面，json数据做json格式化展示
    openTestCaseEditDialog(item, idx) {
      this.getConfig()
      // this.userIndex = idx;
      // this.editCaseObj.update_data = {...item};    // ...item 相当于 name: item.name,testCase: item.testCase,remarks: item.remarks,
      this.editCaseObj.update_data = {
        case_name: item.case_name,
        mark: item.mark,
        path: item.path,
        method: item.method,
        params: item.params,
        body_by_form: item.body_by_form,
        body_by_json: item.body_by_json,
        depend: item.depend,
        expect: item.expect,
        case_type: item.case_type,
        remark: item.remark,
        template_type: item.template_type,
        // created_person: item.created_person,
        updated_person: {},
        trigger_conditional_order: item.trigger_conditional_order,
        need_help: item.need_help,
        web_site: item.web_site,
        clear_up: item.clear_up,
        status: item.status,
        yapi_url: item.yapi_url
      };
      this.editCaseObj.case_id = item.id;
      const adminToken = JSON.parse(localStorage.getItem("Admin-Token"));
      this.editCaseObj.update_data.updated_person.id = adminToken.id;
      this.editCaseObj.update_data.updated_person.name = adminToken.name;
      this.editCaseObj.update_data.updated_person.email = adminToken.email;
      // 将json转成字符串
      if (item.params != null) {
        this.editCaseObj.update_data.params = JSON.stringify(item.params, null, 4);
      }
      if (item.body_by_json != null) {
        this.editCaseObj.update_data.body_by_json = JSON.stringify(item.body_by_json, null, 4);
      }
      if (item.body_by_form != null) {
        this.editCaseObj.update_data.body_by_form = JSON.stringify(item.body_by_form, null, 4);
      }
      if (item.depend != null) {
        this.editCaseObj.update_data.depend = JSON.stringify(item.depend, null, 4);
      }
      if (item.expect != null) {
        this.editCaseObj.update_data.expect = JSON.stringify(item.expect, null, 4);
      }
      if (item.clear_up != null) {
        this.editCaseObj.update_data.clear_up = JSON.stringify(item.clear_up, null, 4);
      }
      this.editCaseDialogVisible = true;
      this.update_data_origin = JSON.parse(JSON.stringify(this.editCaseObj.update_data))
    },
    //编辑弹窗取消编辑时，二次确认是否关闭页面
    editCaseHandleClose() {
      if (isObjectValueEqual(this.editCaseObj.update_data, this.update_data_origin)) {
        this.editCaseDialogVisible = false;   //如果前后数据没有变化，则直接关闭弹窗
      } else {
        this.$confirm('编辑的用例内容未提交，确认关闭？')
          .then(_ => {
            this.editCaseDialogVisible = false;
          })
          .catch(_ => {
          })
      }
    },

    editCaseConfirm() {
      if (isObjectValueEqual(this.editCaseObj.update_data, this.update_data_origin)) {
        this.editCaseDialogVisible = false;   //如果前后数据没有变化，则直接关闭弹窗
      } else {
        this.$refs['editCaseForm1'].validate((valid) => {
          let param = {};
          param = JSON.parse(JSON.stringify(this.editCaseObj.update_data)); //深拷贝，防止影响到原数据
          if (!valid) {
            if (param.case_name.trim() === '') {
              this.$warning("请输入用例名称");
            }
          } else {
            this.$refs['editCaseForm2'].validate((valid) => {     //数据检验：判空和json格式校验
              if (!valid) {
                if (param.path.trim() === '') {
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
                this.editCaseObjNew = JSON.parse(JSON.stringify(this.editCaseObj)); //深拷贝，防止影响到原数据
                if (param.mark.length === 0) {
                  this.$warning("请填写mark标签");
                  return;
                }
                try {
                  if (this.editCaseObjNew.update_data.params !== '') {
                    this.editCaseObjNew.update_data.params = JSON.parse(this.editCaseObjNew.update_data.params);//将字符串转换成json对象
                  }
                } catch (error) {
                  this.$warning("\"params\"：内容格式不正确");
                  return;
                }
                try {
                  if (this.editCaseObjNew.update_data.body_by_json !== '') {
                    this.editCaseObjNew.update_data.body_by_json = JSON.parse(this.editCaseObjNew.update_data.body_by_json);//将字符串转换成json对象
                  }
                } catch (error) {
                  this.$warning("\"json\"：内容格式不正确");
                  return;
                }
                try {
                  if (this.editCaseObjNew.update_data.body_by_form !== '') {
                    this.editCaseObjNew.update_data.body_by_form = JSON.parse(this.editCaseObjNew.update_data.body_by_form);//将字符串转换成json对象
                  }
                } catch (error) {
                  this.$warning("\"form\"：内容格式不正确");
                  return;
                }
                try {
                  if (this.editCaseObjNew.update_data.depend !== '') {
                    this.editCaseObjNew.update_data.depend = JSON.parse(this.editCaseObjNew.update_data.depend);//将字符串转换成json对象
                  }
                } catch (error) {
                  this.$warning("\"依赖\"：内容格式不正确");
                  return;
                }
                try {
                  if (this.editCaseObjNew.update_data.expect !== '') {
                    this.editCaseObjNew.update_data.expect = JSON.parse(this.editCaseObjNew.update_data.expect);//将字符串转换成json对象
                  }
                } catch (error) {
                  this.$warning("\"预期结果\"：内容格式不正确");
                  return;
                }
                try {
                  if (this.editCaseObjNew.update_data.clear_up !== '') {
                    this.editCaseObjNew.update_data.clear_up = JSON.parse(this.editCaseObjNew.update_data.clear_up);//将字符串转换成json对象
                  }
                } catch (error) {
                  this.$warning("\"清理\"：内容格式不正确");
                  return;
                }
                removeEmptyField(this.editCaseObjNew);
                this.$axios.post("/pyServer/TestCase/Update", this.editCaseObjNew).then(res => {
                  if (res.data.code === 0) {
                    this.$notify.success({
                      title: "用例ID: " + this.editCaseObjNew.case_id,
                      message: this.$t('用例更新成功').toString()
                    });
                    this.editCaseDialogVisible = false;
                    // this.$set(this.tableData, this.userIndex, this.editCaseObjNew.update_data);
                    this.$emit("refresh");
                  } else {
                    this.$notify.warning({
                      title: "用例名称: " + this.editCaseObjNew.case_id,
                      message: res.data
                    });
                  }
                }).catch((error) => {
                  this.$notify.error({
                    title: "用例ID: " + this.editCaseObjNew.case_id,
                    // message: this.$t('用例新增失败，请检查填写的内容').toString()
                    message: error
                  });
                })
                return false;
              }
            });
          }
        })
      }
    },
    // 处理mark-tag，标签关闭、输入框露出、输入框输入内容确认
    handleClose(tag) {
      this.editCaseObj.update_data.mark.splice(this.editCaseObj.update_data.mark.indexOf(tag), 1);
    },
    //点击后才会展示输入框
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    //输入框确认后关闭输入框，并把输入框内容push到mark数组中
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.editCaseObj.update_data.mark.push(inputValue);
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

</style>
