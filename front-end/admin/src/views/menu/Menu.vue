<template>
  <el-card class="main-card">
    <!-- 标题 -->
    <div class="title">{{ this.$route.name }}</div>
    <div class="operation-container">
      <el-button
          icon="el-icon-plus"
          size="small"
          type="primary"
          @click="openModel(null)"
      >
        新增菜单
      </el-button>
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-input
            v-model="keywords"
            placeholder="请输入菜单名"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="listMenus"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="listMenus"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 权限列表 -->
    <el-table
        v-loading="loading"
        :data="menuList"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        row-key="id"
    >
      <!-- 菜单名称 -->
      <el-table-column label="菜单名称" prop="name" width="140"/>
      <!-- 菜单icon -->
      <el-table-column align="center" label="图标" prop="icon" width="100">
        <template slot-scope="scope">
          <i :class="'iconfont ' + scope.row.icon"/>
        </template>
      </el-table-column>
      <!-- 菜单排序 -->
      <el-table-column
          align="center"
          label="排序"
          prop="orderNum"
          width="100"
      />
      <!-- 访问路径 -->
      <el-table-column label="访问路径" prop="path"/>
      <!-- 组件路径 -->
      <el-table-column label="组件路径" prop="component"/>
      <!-- 是否隐藏 -->
      <el-table-column align="center" label="隐藏" prop="isHidden" width="80">
        <template slot-scope="scope">
          <el-switch
              v-model="scope.row.isHidden"
              :active-value="1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              @change="changeDisable(scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 创建时间 -->
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
      <!-- 操作 -->
      <el-table-column align="center" label="操作" width="200">
        <template slot-scope="scope">
          <el-button
              v-if="scope.row.children"
              size="mini"
              type="text"
              @click="openModel(scope.row, 1)"
          >
            <i class="el-icon-plus"/> 新增
          </el-button>
          <el-button size="mini" type="text" @click="openModel(scope.row, 2)">
            <i class="el-icon-edit"/> 修改
          </el-button>
          <el-popconfirm
              style="margin-left:10px"
              title="确定删除吗？"
              @confirm="deleteMenu(scope.row.id)"
          >
            <el-button slot="reference" size="mini" type="text">
              <i class="el-icon-delete"/> 删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增模态框 -->
    <el-dialog :visible.sync="addMenu" top="12vh" width="30%">
      <div ref="menuTitle" slot="title" class="dialog-title-container"/>
      <el-form :model="menuForm" label-width="80px" size="medium">
        <!-- 菜单类型 -->
        <el-form-item v-if="show" label="菜单类型">
          <el-radio-group v-model="isCatalog">
            <el-radio :label="true">目录</el-radio>
            <el-radio :label="false">一级菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 菜单名称 -->
        <el-form-item label="菜单名称">
          <el-input v-model="menuForm.name" style="width:220px"/>
        </el-form-item>
        <!-- 菜单图标 -->
        <el-form-item label="菜单图标">
          <el-popover placement="bottom-start" trigger="click" width="300">
            <el-row>
              <el-col
                  v-for="(item, index) of iconList"
                  :key="index"
                  :gutter="10"
                  :md="12"
              >
                <div class="icon-item" @click="checkIcon(item)">
                  <i :class="'iconfont ' + item"/> {{ item }}
                </div>
              </el-col>
            </el-row>
            <el-input
                slot="reference"
                v-model="menuForm.icon"
                :prefix-icon="'iconfont ' + menuForm.icon"
                style="width:220px"
            />
          </el-popover>
        </el-form-item>
        <!-- 组件路径 -->
        <el-form-item v-show="!isCatalog" label="组件路径">
          <el-input v-model="menuForm.component" style="width:220px"/>
        </el-form-item>
        <!-- 路由地址 -->
        <el-form-item label="访问路径">
          <el-input v-model="menuForm.path" style="width:220px"/>
        </el-form-item>
        <!-- 显示排序 -->
        <el-form-item label="显示排序">
          <el-input-number
              v-model="menuForm.orderNum"
              :max="10"
              :min="1"
              controls-position="right"
          />
        </el-form-item>
        <!-- 显示状态 -->
        <el-form-item label="显示状态">
          <el-radio-group v-model="menuForm.isHidden">
            <el-radio :label="0">显示</el-radio>
            <el-radio :label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addMenu = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateMenu">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listMenus();
  },
  data() {
    return {
      keywords: "",
      loading: true,
      addMenu: false,
      isCatalog: true,
      show: true,
      menuList: [],
      menuForm: {
        id: null,
        name: "",
        icon: "",
        component: "",
        path: "",
        orderNum: 1,
        parentId: null,
        isHidden: 0
      },
      iconList: [
        "el-icon-myshouye",
        "el-icon-myfabiaowenzhang",
        "el-icon-myyonghuliebiao",
        "el-icon-myxiaoxi",
        "el-icon-myliuyan",
        "el-icon-myshouye",
        "el-icon-myfabiaowenzhang",
        "el-icon-myyonghuliebiao",
        "el-icon-myxiaoxi",
        "el-icon-myliuyan"
      ]
    };
  },
  methods: {
    listMenus() {
      this.axios
          .get("/api/admin/menus", {
            params: {
              keywords: this.keywords
            }
          })
          .then(({data}) => {
            this.menuList = data.data;
            this.loading = false;
          });
    },
    openModel(menu, type) {
      if (menu) {
        this.show = false;
        this.isCatalog = false;
        switch (type) {
          case 1:
            this.menuForm = {
              id: null,
              name: "",
              icon: "",
              component: "",
              path: "",
              orderNum: 1,
              parentId: null,
              isHidden: 0
            };
            this.$refs.menuTitle.innerHTML = "新增菜单";
            this.menuForm.parentId = JSON.parse(JSON.stringify(menu.id));
            break;
          case 2:
            this.$refs.menuTitle.innerHTML = "修改菜单";
            this.menuForm = JSON.parse(JSON.stringify(menu));
            break;
        }
      } else {
        this.$refs.menuTitle.innerHTML = "新增菜单";
        this.show = true;
        this.menuForm = {
          id: null,
          name: "",
          icon: "",
          component: "Layout",
          path: "",
          orderNum: 1,
          parentId: null,
          isHidden: 0
        };
      }
      this.addMenu = true;
    },
    checkIcon(icon) {
      this.menuForm.icon = icon;
    },
    saveOrUpdateMenu() {
      if (this.menuForm.name.trim() == "") {
        this.$message.error("菜单名不能为空");
        return false;
      }
      if (this.menuForm.icon.trim() == "") {
        this.$message.error("菜单icon不能为空");
        return false;
      }
      if (this.menuForm.component.trim() == "") {
        this.$message.error("菜单组件路径不能为空");
        return false;
      }
      if (this.menuForm.path.trim() == "") {
        this.$message.error("菜单访问路径不能为空");
        return false;
      }
      this.axios.post("/api/admin/menus", this.menuForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: "操作成功"
          });
          this.listMenus();
        } else {
          this.$notify.error({
            title: "失败",
            message: "操作失败"
          });
        }
        this.addMenu = false;
      });
    },
    deleteMenu(id) {
      this.axios.delete("/api/admin/menus/" + id).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: "删除成功"
          });
          this.listMenus();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.icon-item {
  cursor: pointer;
  padding: 0.5rem 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
