<template>
  <div>
    <el-row :gutter="10">
      <el-col :span="24" class="grid-content">
        <el-card>
          <h3 style="margin-left: 20px">{{ "账号充值" }}</h3>
          <el-form :inline="true" :model="request" style="margin: 20px;">
            <el-form-item label="环境">
              <el-select v-model="request.host" placeholder="选择环境" style='width: 80px'>
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="userId" prop="userId">
              <el-input v-model="request.urlParam.userId" placeholder="用户userId" clearable></el-input>
            </el-form-item>
            <el-form-item label="币种" prop="currency">
              <el-input v-model="request.urlParam.currency" placeholder="填写币种" clearable></el-input>
            </el-form-item>
            <el-form-item label="充值金额" prop="amountEv">
              <el-input v-model="request.urlParam.amountEv" placeholder="amountEv" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSubmit">充值</el-button>
              <el-button type="primary" @click="reset">{{ $t('api_test.reset') }}</el-button>
            </el-form-item>
            <el-input
              type="textarea"
              :disabled="true"
              :autosize="{ minRows: 2, maxRows: 4}"
              placeholder="执行结果"
              v-model="depositResponse">
            </el-input>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {request} from "@/common/js/ajax";

export default {
  name: "account",
  activated() {
    this.reload();
  },
  data() {
    return {
      options: [{
        value: 'https://qa.phemex.com',
        label: 'qa'
      }, {
        value: 'https://pet.phemex.com',
        label: 'pet'
      }],
      depositResponse: "",
      request: {
        host: "https://qa.phemex.com",
        path: "/api/spot/public/wallet",
        urlParam: {
          userId: "",
          currency: "",
          amountEv: ""
        }
      }
    }
  },
  methods: {
    onSubmit() {
      let url = "dataFactory/wallet/deposit"
      this.result = this.$axios.post(url, this.request).then(res => {
          if (res.data.code === 0) {
            this.$message({
              message: '充值成功',
              type: 'success'
            })
            this.depositResponse = "code:" + res.data.code + "\n" + JSON.stringify(res.data.data);
          } else {
            this.$message({
              message: "充值失败",
              type: 'error'
            });
            this.depositResponse = "code:" + res.data.code + "\nmsg:" + res.data.msg;
          }
        }
      )
    },
    reset() {
      this.request = {
        host: "https://qa.phemex.com",
        path: "/api/spot/public/wallet",
        urlParam: {
          userId: "",
          currency: "",
          amountEv: ""
        }
      }
      this.depositResponse = ""
    }
  }
}
</script>

<style scoped>

</style>
