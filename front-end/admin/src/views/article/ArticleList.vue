<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 文章状态 -->
    <div class="article-status-menu">
      <span>状态</span>
      <span :class="isActive('all')" @click="changeStatus('all')">全部</span>
      <span :class="isActive('public')" @click="changeStatus('public')">
        公开
      </span>
      <span :class="isActive('secret')" @click="changeStatus('secret')">
        私密
      </span>
      <span :class="isActive('draft')" @click="changeStatus('draft')">
        草稿箱
      </span>
      <span :class="isActive('delete')" @click="changeStatus('delete')">
        回收站
      </span>
    </div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button
          v-if="isDelete == 0"
          :disabled="articleIdList.length == 0"
          icon="el-icon-delete"
          size="small"
          type="danger"
          @click="updateIsDelete = true"
      >
        批量删除
      </el-button>
      <el-button
          v-else
          :disabled="articleIdList.length == 0"
          icon="el-icon-delete"
          size="small"
          type="danger"
          @click="remove = true"
      >
        批量删除
      </el-button>
      <el-button
          :disabled="articleIdList.length == 0"
          icon="el-icon-download"
          size="small"
          style="margin-right:1rem"
          type="success"
          @click="isExport = true"
      >
        批量导出
      </el-button>
      <el-dropdown>
        <el-button
            icon="el-icon-upload"
            size="small"
            style="margin-right:1rem"
            type="primary"
        >
          批量导入
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>
            <el-upload
                :limit="9"
                :on-success="uploadArticle"
                :show-file-list="false"
                action="/api/admin/articles/import"
                multiple
            >
              普通文章
            </el-upload>
          </el-dropdown-item>
          <el-dropdown-item
          >
            <el-upload
                :limit="9"
                :on-success="uploadArticle"
                :show-file-list="false"
                action="/api/admin/articles/import?type=hexo"
                multiple
            >
              Hexo文章
            </el-upload>
          </el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <!-- 文章类型 -->
        <el-select
            v-model="type"
            clearable
            placeholder="请选择文章类型"
            size="small"
            style="margin-right:1rem;width: 180px;"
        >
          <el-option
              v-for="item in typeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <!-- 分类 -->
        <el-select
            v-model="categoryId"
            clearable
            filterable
            placeholder="请选择分类"
            size="small"
            style="margin-right:1rem;width: 180px;"
        >
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
          />
        </el-select>
        <!-- 标签 -->
        <el-select
            v-model="tagId"
            clearable
            filterable
            placeholder="请选择标签"
            size="small"
            style="margin-right:1rem;width: 180px;"
        >
          <el-option
              v-for="item in tagList"
              :key="item.id"
              :label="item.tagName"
              :value="item.id"
          />
        </el-select>
        <!-- 文章名 -->
        <el-input
            v-model="keywords"
            clearable
            placeholder="请输入文章名"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="searchArticles"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="searchArticles"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        v-loading="loading"
        :data="articleList"
        border
        @selection-change="selectionChange"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <!-- 文章修改时间 -->
      <el-table-column
          align="center"
          label="文章封面"
          prop="articleCover"
          width="180"
      >
        <template slot-scope="scope">
          <el-image
              :src="
              scope.row.articleCover
            "
              class="article-cover"
          />
          <i
              v-if="scope.row.status == 1"
              class="iconfont el-icon-mygongkai article-status-icon"
          />
          <i
              v-if="scope.row.status == 2"
              class="iconfont el-icon-mymima article-status-icon"
          />
          <i
              v-if="scope.row.status == 3"
              class="iconfont el-icon-mycaogaoxiang article-status-icon"
          />
        </template>
      </el-table-column>
      <!-- 文章标题 -->
      <el-table-column align="center" label="标题" prop="articleTitle"/>
      <!-- 文章分类 -->
      <el-table-column
          align="center"
          label="分类"
          prop="categoryName"
          width="110"
      />
      <!-- 文章标签 -->
      <el-table-column
          align="center"
          label="标签"
          prop="tagDTOList"
          width="170"
      >
        <template slot-scope="scope">
          <el-tag
              v-for="item of scope.row.tagDTOList"
              :key="item.tagId"
              style="margin-right:0.2rem;margin-top:0.2rem"
          >
            {{ item.tagName }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章浏览量 -->
      <el-table-column
          align="center"
          label="浏览量"
          prop="viewsCount"
          width="70"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.viewsCount">
            {{ scope.row.viewsCount }}
          </span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <!-- 文章点赞量 -->
      <el-table-column
          align="center"
          label="点赞量"
          prop="likeCount"
          width="70"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.likeCount">
            {{ scope.row.likeCount }}
          </span>
          <span v-else>0</span>
        </template>
      </el-table-column>
      <!-- 文章类型 -->
      <el-table-column align="center" label="类型" prop="type" width="80">
        <template slot-scope="scope">
          <el-tag :type="articleType(scope.row.type).tagType">
            {{ articleType(scope.row.type).name }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 文章发表时间 -->
      <el-table-column
          align="center"
          label="发表时间"
          prop="createTime"
          width="130"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 文章置顶 -->
      <el-table-column align="center" label="置顶" prop="isTop" width="80">
        <template slot-scope="scope">
          <el-switch
              v-model="scope.row.isTop"
              :active-value="1"
              :disabled="scope.row.isDelete == 1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
              @change="changeTop(scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column align="center" label="操作" width="150">
        <template slot-scope="scope">
          <el-button
              v-if="scope.row.isDelete == 0"
              size="mini"
              type="primary"
              @click="editArticle(scope.row.id)"
          >
            编辑
          </el-button>
          <el-popconfirm
              v-if="scope.row.isDelete == 0"
              style="margin-left:10px"
              title="确定删除吗？"
              @confirm="updateArticleDelete(scope.row.id)"
          >
            <el-button slot="reference" size="mini" type="danger">
              删除
            </el-button>
          </el-popconfirm>
          <el-popconfirm
              v-if="scope.row.isDelete == 1"
              title="确定恢复吗？"
              @confirm="updateArticleDelete(scope.row.id)"
          >
            <el-button slot="reference" size="mini" type="success">
              恢复
            </el-button>
          </el-popconfirm>
          <el-popconfirm
              v-if="scope.row.isDelete == 1"
              style="margin-left:10px"
              title="确定彻底删除吗？"
              @confirm="deleteArticles(scope.row.id)"
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
    <!-- 批量逻辑删除对话框 -->
    <el-dialog :visible.sync="updateIsDelete" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="updateIsDelete = false">取 消</el-button>
        <el-button type="primary" @click="updateArticleDelete(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 批量彻底删除对话框 -->
    <el-dialog :visible.sync="remove" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否彻底删除选中项？</div>
      <div slot="footer">
        <el-button @click="remove = false">取 消</el-button>
        <el-button type="primary" @click="deleteArticles(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 批量导出对话框 -->
    <el-dialog :visible.sync="isExport" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否导出选中文章？</div>
      <div slot="footer">
        <el-button @click="isExport = false">取 消</el-button>
        <el-button type="primary" @click="exportArticles(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listArticles();
    this.listCategories();
    this.listTags();
  },
  data: function () {
    return {
      loading: true,
      updateIsDelete: false,
      remove: false,
      typeList: [
        {
          value: 1,
          label: "原创"
        },
        {
          value: 2,
          label: "转载"
        },
        {
          value: 3,
          label: "翻译"
        }
      ],
      activeStatus: "all",
      articleList: [],
      articleIdList: [],
      categoryList: [],
      tagList: [],
      keywords: null,
      type: null,
      categoryId: null,
      tagId: null,
      isDelete: 0,
      isExport: false,
      status: null,
      current: 1,
      size: 10,
      count: 0
    };
  },
  methods: {
    selectionChange(articleList) {
      this.articleIdList = [];
      articleList.forEach(item => {
        this.articleIdList.push(item.id);
      });
    },
    searchArticles() {
      this.current = 1;
      this.listArticles();
    },
    editArticle(id) {
      this.$router.push({path: "/articles/" + id});
    },
    updateArticleDelete(id) {
      let param = {};
      if (id != null) {
        param.idList = [id];
      } else {
        param.idList = this.articleIdList;
      }
      param.isDelete = this.isDelete == 0 ? 1 : 0;
      this.axios.put("/api/admin/articles", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listArticles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.updateIsDelete = false;
      });
    },
    deleteArticles(id) {
      var param = {};
      if (id == null) {
        param = {data: this.articleIdList};
      } else {
        param = {data: [id]};
      }
      this.axios.delete("/api/admin/articles", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listArticles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.remove = false;
      });
    },
    exportArticles(id) {
      var param = {};
      if (id == null) {
        param = this.articleIdList;
      } else {
        param = [id];
      }
      console.log(param);
      this.axios.post("/api/admin/articles/export", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          data.data.forEach(item => {
            this.downloadFile(item);
          });
          this.listArticles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isExport = false;
      });
    },
    downloadFile(url) {
      const iframe = document.createElement("iframe");
      iframe.style.display = "none"; // 防止影响页面
      iframe.style.height = 0; // 防止影响页面
      iframe.src = url;
      document.body.appendChild(iframe);
      setTimeout(() => {
        iframe.remove();
      }, 5 * 60 * 1000);
    },
    uploadArticle(data) {
      if (data.flag) {
        this.$notify.success({
          title: "成功",
          message: "导入成功"
        });
        this.listArticles();
      } else {
        this.$notify.error({
          title: "失败",
          message: data.message
        });
      }
    },
    sizeChange(size) {
      this.size = size;
      this.listArticles();
    },
    currentChange(current) {
      this.current = current;
      this.listArticles();
    },
    changeStatus(status) {
      switch (status) {
        case "all":
          this.isDelete = 0;
          this.status = null;
          break;
        case "public":
          this.isDelete = 0;
          this.status = 1;
          break;
        case "secret":
          this.isDelete = 0;
          this.status = 2;
          break;
        case "draft":
          this.isDelete = 0;
          this.status = 3;
          break;
        case "delete":
          this.isDelete = 1;
          this.status = null;
          break;
      }
      this.current = 1;
      this.activeStatus = status;
    },
    changeTop(article) {
      this.axios
          .put("/api/admin/articles/top", {
            id: article.id,
            isTop: article.isTop
          })
          .then(({data}) => {
            if (data.flag) {
              this.$notify.success({
                title: "成功",
                message: "置顶成功"
              });
            } else {
              this.$notify.error({
                title: "失败",
                message: data.message
              });
            }
            this.remove = false;
          });
    },
    listArticles() {
      this.axios
          .get("/api/admin/articles", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords,
              categoryId: this.categoryId,
              status: this.status,
              tagId: this.tagId,
              type: this.type,
              isDelete: this.isDelete
            }
          })
          .then(({data}) => {
            this.articleList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
    },
    listCategories() {
      this.axios.get("/api/admin/categories/search").then(({data}) => {
        this.categoryList = data.data;
      });
    },
    listTags() {
      this.axios.get("/api/admin/tags/search").then(({data}) => {
        this.tagList = data.data;
      });
    }
  },
  watch: {
    type() {
      this.current = 1;
      this.listArticles();
    },
    categoryId() {
      this.current = 1;
      this.listArticles();
    },
    tagId() {
      this.current = 1;
      this.listArticles();
    },
    status() {
      this.current = 1;
      this.listArticles();
    },
    isDelete() {
      this.current = 1;
      this.listArticles();
    }
  },
  computed: {
    articleType() {
      return function (type) {
        var tagType = "";
        var name = "";
        switch (type) {
          case 1:
            tagType = "danger";
            name = "原创";
            break;
          case 2:
            tagType = "success";
            name = "转载";
            break;
          case 3:
            tagType = "primary";
            name = "翻译";
            break;
        }
        return {
          tagType: tagType,
          name: name
        };
      };
    },
    isActive() {
      return function (status) {
        return this.activeStatus == status ? "active-status" : "status";
      };
    }
  }
};
</script>

<style scoped>
.operation-container {
  margin-top: 1.5rem;
}

.article-status-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}

.article-status-menu span {
  margin-right: 24px;
}

.status {
  cursor: pointer;
}

.active-status {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}

.article-cover {
  position: relative;
  width: 100%;
  height: 90px;
  border-radius: 4px;
}

.article-cover::after {
  content: "";
  background: rgba(0, 0, 0, 0.3);
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}

.article-status-icon {
  color: #fff;
  font-size: 1.5rem;
  position: absolute;
  right: 1rem;
  bottom: 1.4rem;
}
</style>
