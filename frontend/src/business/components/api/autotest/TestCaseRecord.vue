<template>
  <ms-container>
    <ms-main-container>

      <div>
        <h3>接口自动化用例录入平台</h3>
        <el-button type="primary" @click="openTestCaseAddDialog" class="add-btn" plain v-permission="['PROJECT_API_CASE_RECORD:READ+CREATE']">添加测试用例</el-button>

        <div>
          <el-divider></el-divider>
          <el-form :inline="true" class="demo-form-inline">
            <template>
              <el-select v-model="value" placeholder="请选择" style="margin-right: 10px" @change="clearKeywords">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </template>
            <el-form-item>
              <el-input v-model="keywords" placeholder="请输入查询内容" @keyup.enter.native="search(keywords)" clearable
                        v-if="value ==='id' || value ==='case_name' "></el-input>
              <el-input v-model="keywords" placeholder='多条标签用逗号隔开' @keyup.enter.native="search(keywords)" clearable
                        v-if="value ==='mark'"></el-input>
              <el-select v-model="keywords" clearable placeholder="请选择" v-if="value==='method'">
                <el-option label="get" value="get"></el-option>
                <el-option label="post" value="post"></el-option>
                <el-option label="put" value="put"></el-option>
                <el-option label="delete" value="delete"></el-option>
              </el-select>
              <el-select v-model="keywords" clearable placeholder="请选择" v-if="value==='template_type'">
                <el-option label="非模板" value="not_template"></el-option>
                <el-option label="合约" value="contract"></el-option>
                <el-option label="现货" value="spot"></el-option>
              </el-select>
              <el-select v-model="keywords" clearable placeholder="请选择" v-if="value==='case_type'">
                <el-option label="rest_api" value="rest_api"></el-option>
                <el-option label="pub_api" value="pub_api"></el-option>
              </el-select>
              <el-select v-model="keywords" clearable placeholder="请选择" v-if="value==='web_site'">
                <el-option label=国际站 value="phemex"></el-option>
                <el-option label="土耳其站" value="turkey"></el-option>
              </el-select>
              <el-select v-model="keywords" clearable placeholder="请选择" v-if="value==='status'">
                <el-option label="启用" value="true"></el-option>
                <el-option label="停用" value="false"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="search(keywords)">查询</el-button>
            </el-form-item>
            <!--高级搜索-->
            <ms-table-adv-search-bar :condition.sync="condition" class="adv-search-bar" @search="combineSearch"
                                     v-if="condition.components !== undefined && condition.components.length > 0"/>
          </el-form>
        </div>

        <!-- table主体内容 -->
        <el-table :data="tableData" style="width: 100%" border @filter-change="filter" @cell-dblclick="cell_dblclick">
          <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
          <el-table-column prop="case_name" label="用例名称" width="300"></el-table-column>
          <el-table-column prop="method" label="请求方法" width="100"
                           column-key="method">
          </el-table-column>
          <el-table-column prop="path" label="接口path" width="180"></el-table-column>
          <!--          <el-table-column prop="body_by_json" label="json" :formatter="formatObject"-->
          <!--                           align="left" width="140" show-overflow-tooltip>-->
          <!--          </el-table-column>-->
          <!--          <el-table-column prop="expect" label="预期结果" :formatter="formatObject"-->
          <!--                           align="left" width="140" show-overflow-tooltip>-->
          <!--          </el-table-column>-->
          <el-table-column prop="mark" label="mark" header-align="center" width="240">
            <template slot-scope="scope">
              <el-tag v-for="item in scope.row.mark" :key="item" type="" effect="plain" class="tag-group">
                {{ item }}
              </el-tag>
            </template>
          </el-table-column>
          <!--          <el-table-column prop="created_at" label="创建时间" sortable></el-table-column>-->
          <!--          <el-table-column prop="updated_at" label="更新时间" sortable></el-table-column>-->
          <el-table-column prop="web_site" label="站点" width="80"
                           column-key="web_site">
          </el-table-column>
          <el-table-column prop="template_type" width="120" label="模板类型"
                           column-key="template_type">
          </el-table-column>
          <el-table-column prop="case_type" label="用例类型" width="100" column-key="case_type">
          </el-table-column>
          <el-table-column prop="yapi_url" label="yapi链接" width="80" align="center">
            <template slot-scope="scope">
              <el-link :href="scope.row.yapi_url" target="_blank">
                <div v-if="scope.row.yapi_url !== '' && scope.row.yapi_url != null">
                  <i class="el-icon-link" style="font-size: 15px; color: blue"></i>
                </div>
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注"
                           align="left" width="200" show-overflow-tooltip>
          </el-table-column>
          <el-table-column prop="created_person" label="创建人" :formatter="formatCreatedData"
                           width="100"></el-table-column>
          <el-table-column prop="updated_person" label="更新人" :formatter="formatUpdatedData"
                           width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.status===true" type="success" effect="dark">启用</el-tag>
              <el-tag v-if="scope.row.status===false" type="danger" effect="dark">停用</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operation" label="操作" width="80">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-edit" @click="openTestCaseEditDialog(scope.row)"
                         circle v-permission="['PROJECT_API_CASE_RECORD:READ+EDIT']"></el-button>
              <!--              <el-button type="danger" icon="el-icon-delete" @click="delCase(scope.$index)" circle disabled></el-button>-->
            </template>
          </el-table-column>
        </el-table>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                       :current-page="queryInfo.page" :page-sizes="[10, 20, 50, 100]" :page-size="10"
                       layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>

        <test-case-add ref="testCaseAddDialog" @openTestCaseAddDialog="openTestCaseAddDialog" @refresh="getCaseList"/>
        <test-case-modify v-bind:tableData="tableData" ref="testCaseEditDialog"
                          @openTestCaseEditDialog="openTestCaseEditDialog" @refresh="getCaseList"/>

        <!--table单元格弹窗-->
        <el-dialog title="详细信息" :visible.sync="tableDialogVisible" width="800px" append-to-body>
          <el-input
            type="textarea"
            v-model="json_body_detail"
            :autosize="{ minRows: 2, maxRows: 20}"
            :disabled="true">
          </el-input>
        </el-dialog>

      </div>

    </ms-main-container>
  </ms-container>
</template>

<script>

import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";
import TestCaseAdd from "./components/TestCaseAdd";
import TestCaseModify from "./components/TestCaseModify";
import TestCaseEdit from "../../track/case/components/TestCaseEdit";
import {humpToLine} from "@/common/js/utils";
import MsTableAdvSearchBar from "@/business/components/common/components/search/MsTableAdvSearchBar";
import {AUTO_TEST_SEARCH_CASE} from "../../common/components/search/search-components";

export default {
  components: {TestCaseEdit, TestCaseModify, TestCaseAdd, MsMainContainer, MsContainer, MsTableAdvSearchBar},
  data() {
    return {
      condition: {
        components: AUTO_TEST_SEARCH_CASE
      },
      options: [
        {value: 'id', label: 'ID'},
        {value: 'case_name', label: '用例名称'},
        {value: 'mark', label: 'mark'},
        {value: 'method', label: '请求类型'},
        {value: 'case_type', label: '用例类型'},
        {value: 'template_type', label: '模板类型'},
        {value: 'web_site', label: '站点'},
        {value: 'status', label: '用例状态'},
      ],
      value: 'case_name',
      keywords: '',
      deleteInfo: {
        case_id: 0
      },
      queryInfo: {
        page: 1,
        limit: 10
      },
      queryInfoNew: '',
      total: 0,
      tableData: [],
      tableDataList: [],
      json_body_detail: [],
      expect: [],
      body_by_json: '',
      tableDialogVisible: false
    }
  },
  activated() {
    this.getCaseList();
  },
  methods: {
    clearKeywords() {
      this.keywords = ''
    },
    combineSearch() {
      let combineQuery = JSON.parse(JSON.stringify(this.queryInfo))
      for (const i in this.condition.combine) {
        if (i === 'mark') {
          combineQuery[i] = this.condition.combine[i].value.trim().split(/,|，|\s+/)
        } else {
          combineQuery[i] = this.condition.combine[i].value
        }
      }
      this.getCaseList(combineQuery)
    },
    getCaseList(body = this.queryInfo) {
      this.$axios.post("/pyServer/TestCase/Search", body).then(res => {
        if (res.data.code === 0) {
          this.tableData = this.tableDataList = res.data.data
          this.total = res.data.total
        } else {
          this.$notify.warning({
            title: "用例查询失败",
            message: res.data
          });
        }
        // this.webSiteFilter = res.data.data.map(u => {
        //   return {text: u.web_site, value: u.web_site};
        // })
      }).catch((error) => {
        this.$notify.error({
          title: "用例查询失败",
          message: error
        });
      })
    },
    search(keywords) {
      if (keywords.split(' ').join('').length === 0) {
        this.getCaseList()
      } else {
        switch (this.value) {
          case "id":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.id = keywords.trim()
            break;
          case "case_name":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.case_name = keywords.trim()
            break;
          case "method":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.method = keywords.trim()
            break;
          case "template_type":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.template_type = keywords.trim()
            break;
          case "case_type":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.case_type = keywords.trim()
            break;
          case "mark":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.mark = keywords.trim().split(/,|，|\s+/)
            break;
          case "web_site":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.web_site = keywords.trim()
            break;
          case "status":
            this.queryInfoNew = JSON.parse(JSON.stringify(this.queryInfo))
            this.queryInfoNew.status = keywords.trim()
            break;
          default:
            break;
        }
        this.getCaseList(this.queryInfoNew)
      }
    },
    filter(filters) {
      // if (!this.queryInfo.filters) {
      // }
      for (let filter in filters) {
        if (filters.hasOwnProperty(filter)) {
          if (filters[filter] && filters[filter].length > 0) {
            this.queryInfo[humpToLine(filter)] = filters[filter].toString();
          } else {
            this.queryInfo[humpToLine(filter)] = null;
          }
        }
      }
      // this.getCaseList(this.queryInfoNew);
    },
    humpToLine(name) {
      return name.replace(/([A-Z])/g, "_$1").toLowerCase();
    },
    cell_dblclick(row, column, cell, event) {
      //双击单元格打开展示详细信息
      if (column.property === "body_by_json") {
        //根据body_by_json获取详细信息
        this.json_body_detail = JSON.stringify(row.body_by_json, null, 4);
        if (this.json_body_detail != null) {
          this.tableDialogVisible = true;
        }
      }
      if (column.property === "expect") {
        //根据expect循环获取预期结果详细信息
        this.json_body_detail = JSON.stringify(row.expect, null, 4);
        if (this.json_body_detail != null) {
          this.tableDialogVisible = true;
        }
      }
    },

    formatObject(row, column) {
      let expect = row[column.property];
      if (expect == null) {
        return expect
      }
      return JSON.stringify(expect, null, 4);
    },
    formatCreatedData(row, column) {
      let created_person = row[column.property];
      if (created_person !== null) {
        return created_person.name
      } else {
        return ''
      }
    },
    formatUpdatedData(row, column) {
      let updated_person = row[column.property];
      if (updated_person !== null) {
        return updated_person.name
      } else {
        return ''
      }
    },

    formatBoolean(row, column, cellValue) {
      let ret = '';
      if (cellValue) {
        ret = "启用"
      } else {
        ret = "停用"
      }
      return ret;
    },
    // <!--监听pageSize变化-->
    handleSizeChange(newSize) {
      this.queryInfo.limit = newSize
      this.getCaseList()
    },
    // <!--监听页码值变化-->
    handleCurrentChange(newPage) {
      this.queryInfo.page = newPage
      this.getCaseList()
    },

    //删除测试用例
    delCase(idx) {
      this.$confirm('确认删除此用例信息？')
        .then(_ => {
          this.deleteInfo.case_id = this.tableData.splice(idx, 1)[0].id;
          this.$axios.delete("/pyServer/TestCase/Delete", {params: this.deleteInfo, headers: {'x_power': true}})
          this.$notify.warning({
            title: this.deleteInfo.case_id,
            message: this.$t('用例已删除').toString()
          });
        })
        .catch(_ => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
    },

    openTestCaseAddDialog(data) {
      this.$refs.testCaseAddDialog.openTestCaseAddDialog(data);
    },
    openTestCaseEditDialog(data) {
      this.$refs.testCaseEditDialog.openTestCaseEditDialog(data);
    }

  }
}

</script>

<style scoped>

.add-btn {
  text-align: center;
  margin-top: 10px;
  margin-bottom: 10px;
  width: 10%;
}

.tag-group {
  margin: 2px;
}

</style>
