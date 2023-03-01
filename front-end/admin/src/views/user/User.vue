<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <!-- 登录方式 -->
        <el-select
            v-model="loginType"
            clearable
            placeholder="请选择登录方式"
            size="small"
            style="margin-right:1rem"
        >
          <el-option
              v-for="item in typeList"
              :key="item.type"
              :label="item.desc"
              :value="item.type"
          />
        </el-select>
        <el-input
            v-model="keywords"
            placeholder="请输入昵称"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="searchUsers"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="searchUsers"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table v-loading="loading" :data="userList" border>
      <!-- 表格列 -->
      <el-table-column
          align="center"
          label="头像"
          prop="linkAvatar"
          width="100"
      >
        <template slot-scope="scope">
          <img :src="scope.row.avatar" height="40" width="40"/>
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          label="昵称"
          prop="nickname"
          width="140"
      />
      <el-table-column
          align="center"
          label="登录方式"
          prop="loginType"
          width="80"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.loginType == 1" type="success">邮箱</el-tag>
          <el-tag v-if="scope.row.loginType == 2">QQ</el-tag>
          <el-tag v-if="scope.row.loginType == 3" type="danger">微博</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="用户角色" prop="roleList">
        <template slot-scope="scope">
          <el-tag
              v-for="(item, index) of scope.row.roleList"
              :key="index"
              style="margin-right:4px;margin-top:4px"
          >
            {{ item.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="禁用" prop="isDisable" width="100">
        <template slot-scope="scope">
          <el-switch
              v-model="scope.row.isDisable"
              :active-value="1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              @change="changeDisable(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          label="登录ip"
          prop="ipAddress"
          width="140"
      />
      <el-table-column
          align="center"
          label="登录地址"
          prop="ipSource"
          width="140"
      />
      <el-table-column
          align="center"
          label="创建时间"
          prop="createTime"
          width="130"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <el-table-column
          align="center"
          label="上次登录时间"
          prop="lastLoginTime"
          width="130"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.lastLoginTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column align="center" label="操作" width="100">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="openEditModel(scope.row)"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
        :current-page="current"
        :page-size="size"
        :page-sizes="[10, 20]"
        :total="count"
        background
        class="pagination-container"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="sizeChange"
        @current-change="currentChange"
    />
    <!-- 修改对话框 -->
    <el-dialog :visible.sync="isEdit" width="30%">
      <div slot="title" class="dialog-title-container">
        修改用户
      </div>
      <el-form :model="userForm" label-width="60px" size="medium">
        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname" style="width:220px"/>
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="roleIdList">
            <el-checkbox
                v-for="item of userRoleList"
                :key="item.id"
                :label="item.id"
            >
              {{ item.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="isEdit = false">取 消</el-button>
        <el-button type="primary" @click="editUserRole">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listUsers();
    this.listRoles();
  },
  data: function () {
    return {
      loading: true,
      isEdit: false,
      userForm: {
        userInfoId: null,
        nickname: ""
      },
      loginType: null,
      userRoleList: [],
      roleIdList: [],
      userList: [],
      typeList: [
        {
          type: 1,
          desc: "邮箱"
        },
        {
          type: 2,
          desc: "QQ"
        },
        {
          type: 3,
          desc: "微博"
        }
      ],
      keywords: null,
      current: 1,
      size: 10,
      count: 0
    };
  },
  methods: {
    searchUsers() {
      this.current = 1;
      this.listUsers();
    },
    sizeChange(size) {
      this.size = size;
      this.listUsers();
    },
    currentChange(current) {
      this.current = current;
      this.listUsers();
    },
    changeDisable(user) {
      this.axios.put("/api/admin/users/disable", {
        id: user.userInfoId,
        isDisable: user.isDisable
      }).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          // 操作成功，页面内容正确，不需要刷新
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
          // 操作失败，只需刷新用户
          this.listUsers();
        }
        this.isEdit = false;
      });
    },
    openEditModel(user) {
      this.roleIdList = [];
      this.userForm = JSON.parse(JSON.stringify(user));
      this.userForm.roleList.forEach(item => {
        this.roleIdList.push(item.id);
      });
      this.isEdit = true;
    },
    editUserRole() {
      this.userForm.roleIdList = this.roleIdList;
      this.axios
          .put("/api/admin/users/role", this.userForm)
          .then(({data}) => {
            if (data.flag) {
              this.$notify.success({
                title: "成功",
                message: data.message
              });
              this.listUsers();
            } else {
              this.$notify.error({
                title: "失败",
                message: data.message
              });
            }
            this.isEdit = false;
          });
    },
    listUsers() {
      this.axios
          .get("/api/admin/users", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords,
              loginType: this.loginType
            }
          })
          .then(({data}) => {
            this.userList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
    },
    listRoles() {
      this.axios.get("/api/admin/users/role").then(({data}) => {
        this.userRoleList = data.data;
      });
    }
  },
  watch: {
    loginType() {
      this.current = 1;
      this.listUsers();
    }
  }
};
</script>
