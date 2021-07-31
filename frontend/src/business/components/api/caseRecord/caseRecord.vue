<template>
  <ms-container>
    <ms-main-container>

      <div>
        <h3>测试用例录入平台</h3>
        <el-card>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-input v-model="caseInfo.name" placeholder="请输入测试用例名称"></el-input>
            </el-col>
            <el-col :span="6">
              <el-input type="textarea" autosize  v-model="caseInfo.testCase" placeholder="请输入测试用例内容"></el-input>
            </el-col>
            <el-col :span="6">
              <el-input v-model="caseInfo.remarks" placeholder="备注信息"></el-input>
            </el-col>
          </el-row>
          <el-button type="primary" @click="addCase" class="add-btn" plain>添加测试用例</el-button>
        </el-card>

        <div>
          <el-divider></el-divider>
          <el-divider></el-divider>
          <el-form :inline="true"  class="demo-form-inline">
            <el-form-item label="用例查询">
              <el-input v-model="queryInfo.query" placeholder="用例名称"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getCaseList">查询</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 主体内容 -->
        <el-table :data="tableData" style="width: 100%" border>
          <el-table-column prop="id" label="序号" sortable width="80"></el-table-column>
          <el-table-column prop="name" label="测试用例名称" width="180"></el-table-column>
          <el-table-column prop="testCase" label="测试用例内容" show-overflow-tooltip
                           width="400px" align="center"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" sortable></el-table-column>
          <el-table-column prop="updateTime" label="更新时间" sortable></el-table-column>
          <el-table-column prop="remarks" label="备注信息"></el-table-column>
          <el-table-column prop="operation" label="操作">
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-edit" @click="editCase(scope.row,scope.$index)" circle></el-button>
              <el-button type="danger" icon="el-icon-delete" @click="delCase(scope.$index)" circle></el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum" :page-sizes="[10, 20, 50, 100]" :page-size="queryInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>
        <!-- 编辑框 -->
        <el-dialog title="编辑用例信息" :visible.sync="dialogVisible" width="70%" :before-close="handleClose">
          <el-form ref="form" :model="editObj" label-width="100px">
            <el-form-item label="用例名称" required>
              <el-input placeholder="请输入测试用例名称" v-model="editObj.name"></el-input>
            </el-form-item>
            <el-form-item label="用例内容" required>
              <el-input type="textarea" autosize placeholder="请输入测试用例内容" v-model="editObj.testCase"></el-input>
            </el-form-item>
            <el-form-item label="备注"><el-input v-model="editObj.remarks"></el-input></el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="confirm">确 定</el-button>
          </span>
        </el-dialog>
      </div>

    </ms-main-container>
  </ms-container>
</template>

<script>

import axios from "axios";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";

export default {
  components: {MsMainContainer, MsContainer},
  data(){
    return{
      deleteInfo:{
      },
      queryInfo:{
        pageNum: 1,
        pageSize: 10,
        listObject:[],
        itemCount:0,
      },
      total:0,
      caseInfo:{
        id:'',
        name:'',
        testCase: '',
        remarks: '',
        createTime: '',
        updateTime: '',
      },
      tableData: [],
      dialogVisible: false,
      editObj:{
        id:'',
        name:'',
        testCase: '',
        remarks: '',
        createTime: '',
        updateTime: '',
      },
      userIndex:0
    }
    // return {
    //   queryInfo:{
    //     pageNum: 1,
    //     pageSize: 10
    //   },
    //   performanceCaseList:[],
    // }
  },
  created() {
    this.getCaseList();
  },
  methods:{
    getCaseList(){
      axios.get("/testcase/listAll",{params: this.queryInfo}).then(res =>{
        this.tableData = res.data.data.listObject
        this.total = res.data.data.itemCount
      })
    },
    // <!--监听pageSize变化-->
    handleSizeChange(newSize){
      this.queryInfo.pageSize = newSize
      this.getCaseList()
    },
    // <!--监听页码值变化-->
    handleCurrentChange(newPage){
      this.queryInfo.pageNum = newPage
      this.getCaseList()
    },
    //添加
    addCase(){
      if(!this.caseInfo.name){
        this.$message({
          message: '请输入测试用例名称！',
        });
        return;
      }
      if(!this.caseInfo.testCase){
        this.$message({
          message: '请输入测试用例内容！',
          type: 'warning'
        });
        return;
      }
      axios.post("/testcase/addTestCase",this.caseInfo).then(() => {
        this.$notify.success({
          title: this.caseInfo.name,
          message: this.$t('用例添加成功').toString()
        });
        this.tableData.push(this.caseInfo);
        this.caseInfo = {
            name:'',
            testCase: '',
            remarks: ''
        };
      }).catch(() => {
        this.$notify.error({
          title: this.caseInfo.name,
          message: this.$t('用例添加失败，用例名称重复').toString()
        });
      })
    },
    //删除
    delCase(idx){
      this.$confirm('确认删除此用例信息？')
        .then(_ => {
          this.$notify.warning({
            title: this.editObj.name,
            message: this.$t('用例已删除').toString()
          });
          this.deleteInfo.id =this.tableData.splice(idx, 1)[0].id;
          axios.get("/testcase/deleteTestCase",{params: this.deleteInfo})
        })
        .catch(_ => {});
    },
    //编辑
    editCase(item, idx){
      this.userIndex = idx;
      this.editObj = {...item};    // ...item 相当于 name: item.name,testCase: item.testCase,remarks: item.remarks,
      this.dialogVisible = true;
    },

    handleClose(){
      this.dialogVisible = false;
    },

    confirm(){
      if(!this.editObj.name){
        this.$message({
          message: '请输入测试用例名称！',
        });
        return;
      }
      if(!this.editObj.testCase){
        this.$message({
          message: '请输入测试用例内容！',
          type: 'warning'
        });
        return;
      }
      axios.post("/testcase/updateTestCase",this.editObj).then(res =>{
        this.$notify.success({
          title: this.editObj.name,
          message: this.$t('用例修改成功').toString()
        });
        this.dialogVisible = false;
        this.$set(this.tableData, this.userIndex, this.editObj);
      }).catch(() => {
        this.$notify.error({
          title: this.editObj.name,
          message: this.$t('用例修改失败，用例名称重复').toString()
        });
      })
    }
  }
}

</script>

<style scoped>

.add-btn{
  margin-top: 20px;
  width: 100%;
}

</style>
