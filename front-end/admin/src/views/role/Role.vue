<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button
          icon="el-icon-plus"
          size="small"
          type="primary"
          @click="openMenuModel(null)"
      >
        新增
      </el-button>
      <el-button
          :disabled="this.roleIdList.length == 0"
          icon="el-icon-delete"
          size="small"
          type="danger"
          @click="isDelete = true"
      >
        批量删除
      </el-button>
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <el-input
            v-model="keywords"
            placeholder="请输入角色名"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="searchRoles"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="searchRoles"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        v-loading="loading"
        :data="roleList"
        border
        @selection-change="selectionChange"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column align="center" label="角色名" prop="roleName"/>
      <el-table-column align="center" label="权限标签" prop="roleLabel">
        <template slot-scope="scope">
          <el-tag>
            {{ scope.row.roleLabel }}
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
          label="创建时间"
          prop="createTime"
          width="150"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column align="center" label="操作" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="openMenuModel(scope.row)">
            <i class="el-icon-edit"/> 菜单权限
          </el-button>
          <el-button
              size="mini"
              type="text"
              @click="openResourceModel(scope.row)"
          >
            <i class="el-icon-folder-checked"/> 资源权限
          </el-button>
          <el-popconfirm
              style="margin-left:10px"
              title="确定删除吗？"
              @confirm="deleteRoles(scope.row.id)"
          >
            <el-button slot="reference" size="mini" type="text">
              <i class="el-icon-delete"/> 删除
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
    <!-- 菜单对话框 -->
    <el-dialog :visible.sync="roleMenu" width="30%">
      <div ref="roleTitle" slot="title" class="dialog-title-container"/>
      <el-form :model="roleForm" label-width="80px" size="medium">
        <el-form-item label="角色名">
          <el-input v-model="roleForm.roleName" style="width:250px"/>
        </el-form-item>
        <el-form-item label="权限标签">
          <el-input v-model="roleForm.roleLabel" style="width:250px"/>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree
              ref="menuTree"
              :data="menuList"
              :default-checked-keys="roleForm.menuIdList"
              check-strictly
              node-key="id"
              show-checkbox
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="roleMenu = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateRoleMenu">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 资源对话框 -->
    <el-dialog :visible.sync="roleResource" top="9vh" width="30%">
      <div slot="title" class="dialog-title-container">修改资源权限</div>
      <el-form :model="roleForm" label-width="80px" size="medium">
        <el-form-item label="角色名">
          <el-input v-model="roleForm.roleName" style="width:250px"/>
        </el-form-item>
        <el-form-item label="权限标签">
          <el-input v-model="roleForm.roleLabel" style="width:250px"/>
        </el-form-item>
        <el-form-item label="资源权限">
          <el-tree
              ref="resourceTree"
              :data="resourceList"
              :default-checked-keys="roleForm.resourceIdList"
              node-key="id"
              show-checkbox
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="roleResource = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateRoleResource">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="isDelete" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="deleteRoles(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listRoles();
  },
  data: function () {
    return {
      loading: true,
      isDelete: false,
      roleList: [],
      roleIdList: [],
      keywords: null,
      current: 1,
      size: 10,
      count: 0,
      roleMenu: false,
      roleResource: false,
      resourceList: [],
      menuList: [],
      roleForm: {
        roleName: "",
        roleLabel: "",
        resourceIdList: [],
        menuIdList: []
      }
    };
  },
  methods: {
    searchRoles() {
      this.current = 1;
      this.listRoles();
    },
    sizeChange(size) {
      this.size = size;
      this.listRoles();
    },
    currentChange(current) {
      this.current = current;
      this.listRoles();
    },
    selectionChange(roleList) {
      this.roleIdList = [];
      roleList.forEach(item => {
        this.roleIdList.push(item.id);
      });
    },
    listRoles() {
      this.axios
          .get("/api/admin/roles", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords
            }
          })
          .then(({data}) => {
            this.roleList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
      this.axios.get("/api/admin/role/resources").then(({data}) => {
        this.resourceList = data.data;
      });
      this.axios.get("/api/admin/role/menus").then(({data}) => {
        this.menuList = data.data;
      });
    },
    deleteRoles(id) {
      var param = {};
      if (id == null) {
        param = {data: this.roleIdList};
      } else {
        param = {data: [id]};
      }
      this.axios.delete("/api/admin/roles", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isDelete = false;
      });
    },
    openMenuModel(role) {
      this.$nextTick(function () {
        this.$refs.menuTree.setCheckedKeys([]);
      });
      this.$refs.roleTitle.innerHTML = role ? "修改角色" : "新增角色";
      if (role != null) {
        this.roleForm = JSON.parse(JSON.stringify(role));
      } else {
        this.roleForm = {
          roleName: "",
          roleLabel: "",
          resourceIdList: [],
          menuIdList: []
        };
      }
      this.roleMenu = true;
    },
    openResourceModel(role) {
      this.$nextTick(function () {
        this.$refs.resourceTree.setCheckedKeys([]);
      });
      this.roleForm = JSON.parse(JSON.stringify(role));
      this.roleResource = true;
    },
    saveOrUpdateRoleResource() {
      this.roleForm.menuIdList = null;
      this.roleForm.resourceIdList = this.$refs.resourceTree.getCheckedKeys();
      this.axios.post("/api/admin/role", this.roleForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.roleResource = false;
      });
    },
    saveOrUpdateRoleMenu() {
      if (this.roleForm.roleName.trim() == "") {
        this.$message.error("角色名不能为空");
        return false;
      }
      if (this.roleForm.roleLabel.trim() == "") {
        this.$message.error("权限标签不能为空");
        return false;
      }
      this.roleForm.resourceIdList = null;
      this.roleForm.menuIdList = this.$refs.menuTree
          .getCheckedKeys()
          .concat(this.$refs.menuTree.getHalfCheckedKeys());
      this.axios.post("/api/admin/role", this.roleForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.roleMenu = false;
      });
    }
  }
};
</script>
