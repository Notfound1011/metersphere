<template>
  <div v-loading="result.loading">
    <el-form :model="form" size="small" :rules="rules" :disabled="show" ref="form">
      <el-form-item :label="$t('third_party_auth.jira_address')" prop="jira_address">
        <el-input v-model="form.jira_address" :placeholder="$t('third_party_auth.input_jira_address_placeholder')"/>
      </el-form-item>
      <el-form-item :label="$t('third_party_auth.jira_username')" prop="jira_username">
        <el-input v-model="form.jira_username" :placeholder="$t('third_party_auth.input_jira_username_placeholder')"/>
      </el-form-item>
      <el-form-item :label="$t('third_party_auth.jira_password')" prop="jira_password">
        <el-input v-model="form.jira_password" :placeholder="$t('third_party_auth.input_jira_password_placeholder')" show-password
                  auto-complete="new-password"/>
      </el-form-item>
      <el-form-item :label="$t('third_party_auth.jenkins_username')" prop="jenkins_username">
        <el-input v-model="form.jenkins_username" :placeholder="$t('third_party_auth.input_jenkins_username_placeholder')"/>
      </el-form-item>
      <el-form-item :label="$t('third_party_auth.jenkins_password')" prop="jenkins_password">
        <el-input v-model="form.jenkins_password" :placeholder="$t('third_party_auth.input_jenkins_password_placeholder')" show-password
                  auto-complete="new-password"/>
      </el-form-item>
    </el-form>

    <div>
      <el-button v-if="showEdit" size="small" @click="edit" v-permission="['SYSTEM_SETTING:READ+EDIT']">{{ $t('commons.edit') }}</el-button>
      <el-button type="success" v-if="showSave" size="small" @click="save('form')">{{ $t('commons.save') }}
      </el-button>
      <el-button type="info" v-if="showCancel" size="small" @click="cancel">{{ $t('commons.cancel') }}</el-button>
    </div>

  </div>
</template>

<script>
import MsDialogFooter from "../../common/components/MsDialogFooter";

export default {
  name: "ThirdPartyAuthSetting",
  components: {
    MsDialogFooter
  },
  data() {
    return {
      form: {open: false},
      loginForm: {},
      result: {},
      show: true,
      showEdit: true,
      showSave: false,
      showCancel: false,
      rules: {
        jira_address: {required: true, message: this.$t('third_party_auth.input_jira_address'), trigger: ['change', 'blur']},
        jira_username: {required: true, message: this.$t('third_party_auth.input_jira_username'), trigger: ['change', 'blur']},
        jira_password: {required: true, message: this.$t('third_party_auth.input_jira_password'), trigger: ['change', 'blur']},
        jenkins_username: {required: true, message: this.$t('third_party_auth.input_jenkins_username'), trigger: ['change', 'blur']},
        jenkins_password: {required: true, message: this.$t('third_party_auth.input_jenkins_password'), trigger: ['change', 'blur']},
      }
    }
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      this.result = this.$get("/system/thirdPartyAuth/info", response => {
        this.form = response.data;
        this.form.open = this.form.open === 'true';
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        })
      })
    },
    edit() {
      this.show = false;
      this.showEdit = false;
      this.showSave = true;
      this.showCancel = true;
    },
    cancel() {
      this.showEdit = true;
      this.showCancel = false;
      this.showSave = false;
      this.show = true;
      this.init();
    },
    save(form) {

      let param = [
        {paramKey: "third_party_auth.jira_address", paramValue: this.form.jira_address, type: "text", sort: 1},
        {paramKey: "third_party_auth.jira_username", paramValue: this.form.jira_username, type: "text", sort: 2},
        {paramKey: "third_party_auth.jira_password", paramValue: this.form.jira_password, type: "password", sort: 3},
        {paramKey: "third_party_auth.jenkins_username", paramValue: this.form.jenkins_username, type: "text", sort: 4},
        {paramKey: "third_party_auth.jenkins_password", paramValue: this.form.jenkins_password, type: "password", sort: 5},
      ];

      this.$refs[form].validate(valid => {
        if (valid) {
          this.result = this.$post("/system/save/thirdPartyAuth", param, () => {
            this.show = true;
            this.showEdit = true;
            this.showSave = false;
            this.showCancel = false;
            this.showLogin = false;
            this.$success(this.$t('commons.save_success'));
            this.init();
          });
        } else {
          return false;
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
