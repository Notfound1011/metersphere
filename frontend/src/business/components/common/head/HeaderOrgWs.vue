<template>
  <el-menu :unique-opened="true" mode="horizontal" router
           class="header-user-menu align-right"
           :background-color="color"
           active-text-color="#fff"
           default-active="1"
           text-color="#fff">
    <el-submenu index="1" popper-class="org-ws-menu">
      <!--组织菜单-->
      <template v-slot:title>{{ $t('commons.organization') }}:
        <span class="org-ws-name" :title="currentOrganizationName">
          {{ currentOrganizationName }}
        </span>
      </template>
      <el-input :placeholder="$t('project.search_by_name')+'组织'"
                prefix-icon="el-icon-search"
                v-model="searchOrg"
                clearable
                class="search-input"
                size="small"/>
      <div class="org-ws-menu">
        <el-menu-item :index="1+'-'+index" v-for="(item, index) in organizationList"
                      :popper-append-to-body="true"
                      :key="index">
          <template v-slot:title>
            <div @click="changeOrg(item)">
              {{ item.name }}
              <i class="el-icon-check" v-if="item.id === getCurrentOrganizationId()"></i>
            </div>
          </template>
        </el-menu-item>
      </div>
    </el-submenu>

    <!-- 工作空间   -->
<!--    <el-submenu index="2" popper-class="org-ws-menu"-->
<!--                :popper-append-to-body="true"-->
<!--                v-permission="['PROJECT_TRACK_CASE:READ','PROJECT_TRACK_PLAN:READ','PROJECT_TRACK_REVIEW:READ',-->
<!--                'PROJECT_API_DEFINITION:READ','PROJECT_API_SCENARIO:READ','PROJECT_API_REPORT:READ',-->
<!--                'PROJECT_PERFORMANCE_TEST:READ','PROJECT_PERFORMANCE_REPORT:READ', 'ORGANIZATION_USER:READ',-->
<!--                'WORKSPACE_USER:READ']">-->
<!--      <template v-slot:title>{{ $t('commons.workspace') }}:-->
<!--        <span class="org-ws-name" :title="currentWorkspaceName">-->
<!--          {{ currentWorkspaceName }}-->
<!--        </span>-->
<!--      </template>-->
<!--      <el-input :placeholder="$t('project.search_by_name')"-->
<!--                prefix-icon="el-icon-search"-->
<!--                v-model="searchWs"-->
<!--                clearable-->
<!--                class="search-input"-->
<!--                size="small"/>-->
<!--      <div class="org-ws-menu">-->
<!--        <el-menu-item :index="1+'-'+index" v-for="(ws,index) in workspaceList"-->
<!--                    :popper-append-to-body="true"-->
<!--                    :key="index">-->
<!--          <template v-slot:title>-->
<!--            <div @click="changeWs(ws)">-->
<!--              {{ ws.name }}-->
<!--              <i class="el-icon-check" v-if="ws.id === getCurrentWorkspaceId()"></i>-->
<!--            </div>-->
<!--          </template>-->
<!--        </el-menu-item>-->
<!--      </div>-->
<!--    </el-submenu>-->

    <!-- 一级菜单：工作空间   -->
    <el-submenu index="3" popper-class="org-ws-menu"
                :popper-append-to-body="true">
      <template v-slot:title>{{ $t('commons.project') }}:
        <span class="project-name" :title="currentProjectName" v-if="currentProjectName.length === 0">无项目/无权限</span>
        <span class="project-name" :title="currentProjectName" v-else-if= "currentProjectName.length !== 0">
          {{ currentProjectName }}
        </span>
      </template>
      <el-input :placeholder="$t('project.search_by_name')+'工作空间'"
                prefix-icon="el-icon-search"
                v-model="searchWs"
                clearable
                class="search-input"
                size="small"/>
      <div class="org-ws-menu">
        <el-submenu :index="1+'-'+index" v-for="(ws,index) in workspaceList"
                    :popper-append-to-body="true"
                    :key="index">
          <template v-slot:title>
            <div @click="changeWs(ws)">
              {{ ws.name }}
              <i class="el-icon-check" v-if="ws.id === getCurrentWorkspaceId()"></i>
            </div>
          </template>

          <!--二级菜单：项目-->
<!--          <el-input :placeholder="$t('project.search_by_name')"-->
<!--                    prefix-icon="el-icon-search"-->
<!--                    v-model="searchProj"-->
<!--                    clearable-->
<!--                    class="search-input"-->
<!--                    size="small"/>-->
          <div class="org-proj-menu">
            <el-menu-item v-if="ws.wsProjectList.length === 0">无项目/无权限</el-menu-item>
            <el-menu-item :index="1+'-'+index+'-'+index2" v-for="(proj,index2) in ws.wsProjectList"
                          :popper-append-to-body="false"
                          :key="index2">
              <template v-slot:title>
                <div @click="changeProj(proj)">
                  {{ proj.name }}
                  <i class="el-icon-check" v-if="proj.id === getCurrentProjectID()"></i>
                </div>
              </template>
            </el-menu-item>
          </div>

        </el-submenu>
      </div>
    </el-submenu>
  </el-menu>
</template>

<script>
import {getCurrentOrganizationId,getCurrentProjectID, getCurrentUser, getCurrentWorkspaceId, saveLocalStorage} from "@/common/js/utils";
import {ORGANIZATION_ID, PROJECT_ID, WORKSPACE_ID} from "@/common/js/constants";

export default {
  name: "MsHeaderOrgWs",
  created() {
    this.initMenuData();
    this.getCurrentUserInfo();
  },
  inject: [
    'reloadTopMenus',
    'reload',
  ],
  data() {
    return {
      organizationList: [
        {name: this.$t('organization.none')},
      ],
      workspaceList: [
        {name: this.$t('workspace.none')},
      ],
      projectList: [
        {name: this.$t('project.no_data')},
      ],
      wsProjectList: [
        {name: this.$t('project.no_data')},
      ],
      currentUserInfo: {},
      currentUserId: getCurrentUser().id,
      workspaceIds: [],
      currentOrganizationName: '',
      currentWorkspaceName: '',
      currentProjectName:'',
      searchOrg: '',
      searchWs: '',
      searchProj: '',
      searchArray: [],
      orgListCopy: [{name: this.$t('organization.none')}],
      wsListCopy: [{name: this.$t('workspace.none')}],
      wsProjectListCopy: [{name: this.$t('project.no_data')}]
    };
  },
  computed: {
    currentUser: () => {
      return getCurrentUser();
    },
  },
  props: {
    color: String
  },
  watch: {
    searchOrg(val) {
      this.query('org', val);
    },
    searchWs(val) {
      this.query('ws', val);
    },
    searchProj(val) {
      this.query('proj', val);
    }
  },
  methods: {
    getCurrentOrganizationId,
    getCurrentWorkspaceId,
    getCurrentProjectID,
    initMenuData() {
      this.$get("/organization/list/userorg/" + encodeURIComponent(this.currentUserId), response => {
        let data = response.data;
        this.organizationList = data;
        this.orgListCopy = data;
        let org = data.filter(r => r.id === getCurrentOrganizationId());
        if (org.length > 0) {
          this.currentOrganizationName = org[0].name;
        }

        this.organizationList.forEach(org => {
          this.$get("/workspace/list/orgworkspace/" + encodeURIComponent(this.currentUserId) + "/" + org.id, response => {
            let d = response.data;
            if (d.length === 0) {
              org.workspaceList = [{name: this.$t('workspace.none')}];
              // this.$set(org, 'workspaceList', [{name: this.$t('workspace.none')}]);
            } else {
              this.$set(org, 'workspaceList', d);
              org.wsListCopy = d;
              let workspace = d.filter(r => r.id === getCurrentWorkspaceId());

              if (workspace.length > 0) {
                this.currentWorkspaceName = workspace[0].name;
              }
              this.result = this.$post("/project/list/related", {userId: this.currentUserId}, response => {
                this.projectList = response.data;
                this.searchArray = response.data;
                for(const id in org.workspaceList){
                  this.$set(org.workspaceList[id], 'wsProjectList', []);
                  this.projectList.map(item =>{
                    if(org.workspaceList[id].id === item.workspaceId){
                      org.workspaceList[id].wsProjectList.push({name:item.name, id:item.id})
                    }
                  })
                }

                this.workspaceList = org.workspaceList
                let d = this.projectList;
                if (d.length === 0) {
                  // org.workspaceList = [{name: this.$t('workspace.none')}];
                  // this.$set(org, 'workspaceList', [{name: this.$t('workspace.none')}]);
                }
                else {
                  org.projListCopy = d;
                  let project = d.filter(r => r.id === getCurrentProjectID());
                  if (project.length > 0) {
                    this.currentProjectName = project[0].name;
                  }
                }}
              )

            }
          });
        });
      });
      if (!this.currentUser.lastOrganizationId) {
        return false;
      }
    },
    getCurrentUserInfo() {
      this.$get("/user/info/" + encodeURIComponent(this.currentUserId), response => {
        this.currentUserInfo = response.data;
      });
    },
    changeOrg(data) {
      let orgId = data.id;
      if (!orgId) {
        return false;
      }
      this.$post("/user/switch/source/org/" + orgId, {}, response => {
        saveLocalStorage(response);
        console.log("response.data:~~",response.data)
        sessionStorage.setItem(ORGANIZATION_ID, orgId);
        sessionStorage.setItem(WORKSPACE_ID, response.data.lastWorkspaceId);
        sessionStorage.setItem(PROJECT_ID, response.data.lastProjectId);

        this.$router.push('/').then(() => {
          this.reloadTopMenus();
        }).catch(err => err);
      });
    },
    changeWs(data) {
      let workspaceId = data.id;
      if (!workspaceId) {
        return false;
      }
      this.$post("/user/switch/source/ws/" + workspaceId, {}, response => {
        saveLocalStorage(response);
        sessionStorage.setItem(WORKSPACE_ID, workspaceId);
        sessionStorage.setItem(PROJECT_ID, response.data.lastProjectId);

        this.$router.push('/').then(() => {
          this.reloadTopMenus();
        }).catch(err => err);
      });
    },
    changeProj(data) {
      let projectId = data.id;
      if (!projectId) {
        return false;
      }
      // 保存session里的projectId
      sessionStorage.setItem(PROJECT_ID, projectId);
      this.$post("/user/update/current", {id: this.currentUserId, lastProjectId: projectId}, (response) => {
        saveLocalStorage(response);
        this.currentProjectId = projectId;
        this.$EventBus.$emit('projectChange');
        // 保存session里的projectId
        sessionStorage.setItem(PROJECT_ID, projectId);
        // 刷新路由
        this.reload();
        this.changeProjectName(projectId);
      });
    },
    changeProjectName(projectId) {
      if (projectId) {
        let project = this.searchArray.filter(p => p.id === projectId);
        let workspaceId = project[0].workspaceId;
        if (project.length > 0) {
          this.$emit("update:currentProject", workspaceId);
          sessionStorage.setItem(WORKSPACE_ID, workspaceId);
          this.$post("/user/switch/source/ws/" + workspaceId, {}, response => {
            saveLocalStorage(response);
            this.$router.push('/track/case/all').then(() => {
              this.reloadTopMenus();
            }).catch(err => err);
          });
        }
        this.currentProjectName = project[0].name;
      } else {
        this.$emit("update:currentProject", this.$t('project.select'));
      }
    },
    query(sign, queryString) {
      if (sign === 'org') {
        this.organizationList = queryString ? this.orgListCopy.filter(this.createFilter(queryString)) : this.orgListCopy;
      }
      if (sign === 'ws') {
        this.workspaceList = queryString ? this.wsListCopy.filter(this.createFilter(queryString)) : this.wsListCopy;
      }
      if (sign === 'proj') {
        this.workspaceList.forEach(ws => {
          let wsProjectListCopy = ws.wsProjectList;
          console.log("wsProjectListCopy",wsProjectListCopy)
          ws.wsProjectList = queryString ? wsProjectListCopy?.filter(this.createFilter(queryString)) : wsProjectListCopy;
        });
      }
    },
    createFilter(queryString) {
      return item => {
        return (item.name.toLowerCase().indexOf(queryString.toLowerCase()) !== -1);
      };
    },
  }
};
</script>

<style scoped>
.el-icon-check {
  color: #44b349;
  margin-left: 10px;
}

::-webkit-scrollbar {
  width: 10px;
  height: 10px;
  position: fixed;
}

::-webkit-scrollbar-thumb {
  border-radius: 1em;
  background-color: var(--color_shallow);
  position: fixed;
}

::-webkit-scrollbar-track {
  border-radius: 1em;
  background-color: transparent;
  position: fixed;
}

.org-ws-menu {
  height: 250px;
  overflow: auto;
}

.org-proj-menu {
  height: 200px;
  overflow: auto;
}

.search-input {
  padding: 0;
  margin-top: -4px;
  background-color: var(--color_shallow);
}

.search-input >>> .el-input__inner {
  border-radius: 0;
  background-color: var(--color);
  color: #d2ced8;
  border-color: #b4aebe;
}

.title {
  display: inline-block;
  padding-left: 15px;
  max-width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.org-ws-name {
  display: inline-block;
  padding-left: 5px;
  max-width: 50px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-name {
  display: inline-block;
  padding-left: 5px;
  width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

</style>
