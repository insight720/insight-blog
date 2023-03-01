<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-input
            v-model="keywords"
            placeholder="请输入用户昵称"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="listOnlineUsers"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="listOnlineUsers"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table v-loading="loading" :data="userList">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="头像" prop="avatar" width="100">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" height="40" width="40"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="昵称" prop="nickname"/>
      <el-table-column align="center" label="ip地址" prop="ipAddress"/>
      <el-table-column
          align="center"
          label="登录地址"
          prop="ipSource"
          width="200"
      />
      <el-table-column
          align="center"
          label="浏览器"
          prop="browser"
          width="160"
      />
      <el-table-column align="center" label="操作系统" prop="os"/>
      <el-table-column
          align="center"
          label="登录时间"
          prop="lastLoginTime"
          width="200"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.lastLoginTime | dateTime }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="150">
        <template slot-scope="scope">
          <el-popconfirm
              style="margin-left:10px"
              title="确定下线吗？"
              @confirm="removeOnlineUser(scope.row)"
          >
            <el-button slot="reference" size="mini" type="text">
              <i class="el-icon-delete"/> 下线
            </el-button>
          </el-popconfirm>
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
  </el-card>
</template>

<script>
export default {
  created() {
    this.listOnlineUsers();
  },
  data() {
    return {
      loading: true,
      userList: [],
      keywords: null,
      current: 1,
      size: 10,
      count: 0,
      isCheck: false,
      operatingLog: {}
    };
  },
  methods: {
    listOnlineUsers() {
      this.axios
          .get("/api/admin/users/online", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords
            }
          })
          .then(({data}) => {
            this.userList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
    },
    sizeChange(size) {
      this.size = size;
      this.listOnlineUsers();
    },
    currentChange(current) {
      this.current = current;
      this.listOnlineUsers();
    },
    removeOnlineUser(user) {
      this.axios
          .delete("/api/admin/users/" + user.userInfoId + "/online")
          .then(({data}) => {
            if (data.flag) {
              this.$notify.success({
                title: "成功",
                message: data.message
              });
              this.listOnlineUsers();
            } else {
              this.$notify.error({
                title: "失败",
                message: data.message
              });
            }
          });
    }
  },
  computed: {
    tagType() {
      return function (type) {
        switch (type) {
          case "GET":
            return "";
          case "POST":
            return "success";
          case "PUT":
            return "warning";
          case "DELETE":
            return "danger";
        }
      };
    }
  }
};
</script>

<style scoped>
label {
  font-weight: bold !important;
}
</style>
