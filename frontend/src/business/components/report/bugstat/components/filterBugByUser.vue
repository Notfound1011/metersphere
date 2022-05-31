<template>
  <div>
    <div slot="header" style="margin: 10px">
      <el-link :underline="false" :href=this.url target="_blank">
        <span class="cardTitle" style="color: rgba(55, 96, 186, 1); margin: 10px">
          开发bug统计
        </span>
      </el-link>
    </div>
    <div class="header">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <!-- table主体内容 -->
            <el-table :data="tableDataNew" style="width: 100% ;margin: 10px" border height="440">
              <el-table-column type="index" label="序号" width="60"></el-table-column>
              <el-table-column prop="developer" label="developer" width="160" sortable></el-table-column>
              <el-table-column prop="total" label="总计" width="80" align="center" sortable>
                <template slot-scope="scope">
                  <el-link :href='scope.row.url' target="_blank">
                    <div v-if="scope.row.url !== '' && scope.row.url != null" style="font-size: 15px; color: blue">
                      {{ scope.row.total }}
                    </div>
                  </el-link>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <el-table :data="tableDataNew1" style="width: 100% ;margin: 10px" border height="440">
              <el-table-column type="index" label="序号" width="60"></el-table-column>
              <el-table-column prop="project" label="project" width="160" sortable></el-table-column>
              <el-table-column prop="total" label="总计" width="80" align="center" sortable>
                <template slot-scope="scope">
                  <el-link :href='scope.row.url' target="_blank">
                    <div v-if="scope.row.url !== '' && scope.row.url != null" style="font-size: 15px; color: blue">
                      {{ scope.row.total }}
                    </div>
                  </el-link>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import {jiraAuth, jiraAddress, groupArray} from "@/common/js/utils";

export default {
  name: "filterBugByUser",
  props: ['qaCreatedBugJQL'],
  data() {
    return {
      tableDataNew: [],
      total: 0,
      tableDataNew1: [],
      jira_auth: jiraAuth(),
      jira_address: jiraAddress(),
      url: jiraAddress() + "/issues/?jql=" + this.qaCreatedBugJQL
    }
  },
  methods: {
    filterBugByUser(data) {
      this.total = data.length
      groupArray(data, 'customfield_10300').forEach((value) => { //数组循环
          const valueObj = {};
          valueObj["developer"] = value["location"];
          valueObj["url"] = this.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL + " AND developer = \"" + value["location"] + "\"";
          valueObj["total"] = value["count"];
          this.tableDataNew.push(valueObj)
        }
      )
    },
    filterBugByProject(data) {
      groupArray(data, 'project').forEach((value) => { //数组循环
          const valueObj = {};
          valueObj["project"] = value["location"];
          valueObj["url"] = this.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL + " AND project = \"" + value["location"].replace("&", '%26') + "\"";
          valueObj["total"] = value["count"];
          this.tableDataNew1.push(valueObj)
        }
      )
    }
  }
}
</script>

<style scoped>
.cardTitle {
  color: rgba(55, 96, 186, 1);
  margin: 10px 0;
  font-size: 20px;
  font-weight: bold;
}
</style>
