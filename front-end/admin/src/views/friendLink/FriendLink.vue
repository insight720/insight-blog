<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button
          icon="el-icon-plus"
          size="small"
          type="primary"
          @click="openModel(null)"
      >
        新增
      </el-button>
      <el-button
          :disabled="linkIdList.length == 0"
          icon="el-icon-delete"
          size="small"
          type="danger"
          @click="deleteFlag = true"
      >
        批量删除
      </el-button>
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <el-input
            v-model="keywords"
            placeholder="请输入友链名"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="searchLinks"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="searchLinks"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        v-loading="loading"
        :data="linkList"
        border
        @selection-change="selectionChange"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column
          align="center"
          label="链接头像"
          prop="linkAvatar"
          width="180"
      >
        <template slot-scope="scope">
          <img :src="scope.row.linkAvatar" height="40" width="40"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="链接名" prop="linkName"/>
      <el-table-column align="center" label="链接地址" prop="linkAddress"/>
      <el-table-column align="center" label="链接介绍" prop="linkIntro"/>
      <el-table-column
          align="center"
          label="创建时间"
          prop="createTime"
          width="140"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column align="center" label="操作" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="openModel(scope.row)">
            编辑
          </el-button>
          <el-popconfirm
              style="margin-left:1rem"
              title="确定删除吗？"
              @confirm="deleteLink(scope.row.id)"
          >
            <el-button slot="reference" size="mini" type="danger">
              删除
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
    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="deleteFlag" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="deleteFlag = false">取 消</el-button>
        <el-button type="primary" @click="deleteLink(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 添加对话框 -->
    <el-dialog :visible.sync="addOrEdit" width="30%">
      <div ref="linkTitle" slot="title" class="dialog-title-container"/>
      <el-form :model="linkForm" label-width="80px" size="medium">
        <el-form-item label="链接名">
          <el-input v-model="linkForm.linkName" style="width:250px"/>
        </el-form-item>
        <el-form-item label="链接头像">
          <el-input v-model="linkForm.linkAvatar" style="width:250px"/>
        </el-form-item>
        <el-form-item label="链接地址">
          <el-input v-model="linkForm.linkAddress" style="width:250px"/>
        </el-form-item>
        <el-form-item label="链接介绍">
          <el-input v-model="linkForm.linkIntro" style="width:250px"/>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditCategory">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listLinks();
  },
  data: function () {
    return {
      loading: true,
      deleteFlag: false,
      addOrEdit: false,
      linkIdList: [],
      linkList: [],
      linkForm: {
        id: null,
        linkName: "",
        linkAvatar: "",
        linkIntro: "",
        linkAddress: ""
      },
      keywords: null,
      current: 1,
      size: 10,
      count: 0
    };
  },
  methods: {
    selectionChange(linkList) {
      this.linkIdList = [];
      linkList.forEach(item => {
        this.linkIdList.push(item.id);
      });
    },
    searchLinks() {
      this.current = 1;
      this.listLinks();
    },
    sizeChange(size) {
      this.size = size;
      this.listLinks();
    },
    currentChange(current) {
      this.current = current;
      this.listLinks();
    },
    deleteLink(id) {
      var param = {};
      if (id == null) {
        param = {data: this.linkIdList};
      } else {
        param = {data: [id]};
      }
      this.axios.delete("/api/admin/links", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listLinks();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.deleteFlag = false;
      });
    },
    openModel(link) {
      if (link != null) {
        this.linkForm = JSON.parse(JSON.stringify(link));
        this.$refs.linkTitle.innerHTML = "修改友链";
      } else {
        this.linkForm.id = null;
        this.linkForm.linkName = "";
        this.linkForm.linkAvatar = "";
        this.linkForm.linkIntro = "";
        this.linkForm.linkAddress = "";
        this.$refs.linkTitle.innerHTML = "添加友链";
      }
      this.addOrEdit = true;
    },
    addOrEditCategory() {
      if (this.linkForm.linkName.trim() == "") {
        this.$message.error("友链名不能为空");
        return false;
      }
      if (this.linkForm.linkAvatar.trim() == "") {
        this.$message.error("友链头像不能为空");
        return false;
      }
      if (this.linkForm.linkIntro.trim() == "") {
        this.$message.error("友链介绍不能为空");
        return false;
      }
      if (this.linkForm.linkAddress.trim() == "") {
        this.$message.error("友链地址不能为空");
        return false;
      }
      this.axios.post("/api/admin/links", this.linkForm).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listLinks();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addOrEdit = false;
      });
    },
    listLinks() {
      this.axios
          .get("/api/admin/links", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords
            }
          })
          .then(({data}) => {
            this.linkList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
    }
  }
};
</script>
