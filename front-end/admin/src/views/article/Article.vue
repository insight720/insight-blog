<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 文章标题 -->
    <div class="article-title-container">
      <el-input
          v-model="article.articleTitle"
          placeholder="输入文章标题"
          size="medium"
      />
      <el-button
          v-if="article.id == null || article.status == 3"
          class="save-btn"
          size="medium"
          type="danger"
          @click="saveArticleDraft"
      >
        保存草稿
      </el-button>
      <el-button
          size="medium"
          style="margin-left:10px"
          type="danger"
          @click="openModel"
      >
        发布文章
      </el-button>
    </div>
    <!-- 文章内容 -->
    <mavon-editor
        ref="md"
        v-model="article.articleContent"
        style="height:calc(100vh - 260px)"
        @imgAdd="uploadImg"
    />
    <!-- 添加文章对话框 -->
    <el-dialog :visible.sync="addOrEdit" top="3vh" width="40%">
      <div slot="title" class="dialog-title-container">
        发布文章
      </div>
      <!-- 文章数据 -->
      <el-form :model="article" label-width="80px" size="medium">
        <!-- 文章分类 -->
        <el-form-item label="文章分类">
          <el-tag
              v-show="article.categoryName"
              :closable="true"
              style="margin:0 1rem 0 0"
              type="success"
              @close="removeCategory"
          >
            {{ article.categoryName }}
          </el-tag>
          <!-- 分类选项 -->
          <el-popover
              v-if="!article.categoryName"
              placement="bottom-start"
              trigger="click"
              width="460"
          >
            <div class="popover-title">分类</div>
            <!-- 搜索框 -->
            <el-autocomplete
                v-model="categoryName"
                :fetch-suggestions="searchCategories"
                :trigger-on-focus="false"
                placeholder="请输入分类名搜索，enter可添加自定义分类"
                style="width:100%"
                @select="handleSelectCategories"
                @keyup.enter.native="saveCategory"
            >
              <template slot-scope="{ item }">
                <div>{{ item.categoryName }}</div>
              </template>
            </el-autocomplete>
            <!-- 分类 -->
            <div class="popover-container">
              <div
                  v-for="item of categoryList"
                  :key="item.id"
                  class="category-item"
                  @click="addCategory(item)"
              >
                {{ item.categoryName }}
              </div>
            </div>
            <el-button slot="reference" plain size="small" type="success">
              添加分类
            </el-button>
          </el-popover>
        </el-form-item>
        <!-- 文章标签 -->
        <el-form-item label="文章标签">
          <el-tag
              v-for="(item, index) of article.tagNameList"
              :key="index"
              :closable="true"
              style="margin:0 1rem 0 0"
              @close="removeTag(item)"
          >
            {{ item }}
          </el-tag>
          <!-- 标签选项 -->
          <el-popover
              v-if="article.tagNameList.length < 3"
              placement="bottom-start"
              trigger="click"
              width="460"
          >
            <div class="popover-title">标签</div>
            <!-- 搜索框 -->
            <el-autocomplete
                v-model="tagName"
                :fetch-suggestions="searchTags"
                :trigger-on-focus="false"
                placeholder="请输入标签名搜索，enter可添加自定义标签"
                style="width:100%"
                @select="handleSelectTag"
                @keyup.enter.native="saveTag"
            >
              <template slot-scope="{ item }">
                <div>{{ item.tagName }}</div>
              </template>
            </el-autocomplete>
            <!-- 标签 -->
            <div class="popover-container">
              <div style="margin-bottom:1rem">添加标签</div>
              <el-tag
                  v-for="(item, index) of tagList"
                  :key="index"
                  :class="tagClass(item)"
                  @click="addTag(item)"
              >
                {{ item.tagName }}
              </el-tag>
            </div>
            <el-button slot="reference" plain size="small" type="primary">
              添加标签
            </el-button>
          </el-popover>
        </el-form-item>
        <el-form-item label="文章类型">
          <el-select v-model="article.type" placeholder="请选择类型">
            <el-option
                v-for="item in typeList"
                :key="item.type"
                :label="item.desc"
                :value="item.type"
            />
          </el-select>
        </el-form-item>
        <!-- 文章类型 -->
        <el-form-item v-if="article.type != 1" label="原文地址">
          <el-input
              v-model="article.originalUrl"
              placeholder="请填写原文链接"
          />
        </el-form-item>
        <el-form-item label="上传封面">
          <el-upload
              :before-upload="beforeUpload"
              :on-success="uploadCover"
              action="/api/admin/articles/images"
              class="upload-cover"
              drag
              multiple
          >
            <i v-if="article.articleCover == ''" class="el-icon-upload"/>
            <div v-if="article.articleCover == ''" class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
                v-else
                :src="article.articleCover"
                height="180px"
                width="360px"
            />
          </el-upload>
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch
              v-model="article.isTop"
              :active-value="1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#F4F4F5"
          />
        </el-form-item>
        <el-form-item label="发布形式">
          <el-radio-group v-model="article.status">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="2">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="danger" @click="saveOrUpdateArticle">
          发 表
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import * as imageConversion from "image-conversion";

export default {
  created() {
    const path = this.$route.path;
    const arr = path.split("/");
    const articleId = arr[2];
    if (articleId) {
      this.axios.get("/api/admin/articles/" + articleId).then(({data}) => {
        this.article = data.data;
      });
    } else {
      const article = sessionStorage.getItem("article");
      if (article) {
        this.article = JSON.parse(article);
      }
    }
  },
  destroyed() {
    //文章自动保存功能
    this.autoSaveArticle();
  },
  data: function () {
    return {
      addOrEdit: false,
      autoSave: true,
      categoryName: "",
      tagName: "",
      categoryList: [],
      tagList: [],
      typeList: [
        {
          type: 1,
          desc: "原创"
        },
        {
          type: 2,
          desc: "转载"
        },
        {
          type: 3,
          desc: "翻译"
        }
      ],
      article: {
        id: null,
        articleTitle: this.$moment(new Date()).format("YYYY-MM-DD"),
        articleContent: "",
        articleCover: "",
        categoryName: null,
        tagNameList: [],
        originalUrl: "",
        isTop: 0,
        type: 1,
        status: 1
      }
    };
  },
  methods: {
    listCategories() {
      this.axios.get("/api/admin/categories/search").then(({data}) => {
        this.categoryList = data.data;
      });
    },
    listTags() {
      this.axios.get("/api/admin/tags/search").then(({data}) => {
        this.tagList = data.data;
      });
    },
    openModel() {
      if (this.article.articleTitle.trim() == "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() == "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      this.listCategories();
      this.listTags();
      this.addOrEdit = true;
    },
    uploadCover(response) {
      this.article.articleCover = response.data;
    },
    beforeUpload(file) {
      return new Promise(resolve => {
        if (file.size / 1024 < this.config.UPLOAD_SIZE) {
          resolve(file);
        }
        // 压缩到200KB,这里的200就是要压缩的大小,可自定义
        imageConversion
            .compressAccurately(file, this.config.UPLOAD_SIZE)
            .then(res => {
              resolve(res);
            });
      });
    },
    uploadImg(pos, file) {
      var formdata = new FormData();
      if (file.size / 1024 < this.config.UPLOAD_SIZE) {
        formdata.append("file", file);
        this.axios
            .post("/api/admin/articles/images", formdata)
            .then(({data}) => {
              this.$refs.md.$img2Url(pos, data.data);
            });
      } else {
        // 压缩到200KB,这里的200就是要压缩的大小,可自定义
        imageConversion
            .compressAccurately(file, this.config.UPLOAD_SIZE)
            .then(res => {
              formdata.append(
                  "file",
                  new window.File([res], file.name, {type: file.type})
              );
              this.axios
                  .post("/api/admin/articles/images", formdata)
                  .then(({data}) => {
                    this.$refs.md.$img2Url(pos, data.data);
                  });
            });
      }
    },
    saveArticleDraft() {
      if (this.article.articleTitle.trim() == "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() == "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      this.article.status = 3;
      this.axios.post("/api/admin/articles", this.article).then(({data}) => {
        if (data.flag) {
          if (this.article.id === null) {
            this.$store.commit("removeTab", "发布文章");
          } else {
            this.$store.commit("removeTab", "修改文章");
          }
          sessionStorage.removeItem("article");
          this.$router.push({path: "/article-list"});
          this.$notify.success({
            title: "成功",
            message: "保存草稿成功"
          });
        } else {
          this.$notify.error({
            title: "失败",
            message: "保存草稿失败"
          });
        }
      });

      //关闭自动保存功能
      this.autoSave = false;
    },
    saveOrUpdateArticle() {
      if (this.article.articleTitle.trim() == "") {
        this.$message.error("文章标题不能为空");
        return false;
      }
      if (this.article.articleContent.trim() == "") {
        this.$message.error("文章内容不能为空");
        return false;
      }
      if (this.article.categoryName == null) {
        this.$message.error("文章分类不能为空");
        return false;
      }
      if (this.article.tagNameList.length == 0) {
        this.$message.error("文章标签不能为空");
        return false;
      }
      if (this.article.articleCover.trim() == "") {
        this.$message.error("文章封面不能为空");
        return false;
      }
      this.axios.post("/api/admin/articles", this.article).then(({data}) => {
        if (data.flag) {
          if (this.article.id === null) {
            this.$store.commit("removeTab", "发布文章");
          } else {
            this.$store.commit("removeTab", "修改文章");
          }
          sessionStorage.removeItem("article");
          this.$router.push({path: "/article-list"});
          this.$notify.success({
            title: "成功",
            message: data.message
          });
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.addOrEdit = false;
      });
      //关闭自动保存功能
      this.autoSave = false;
    },
    autoSaveArticle() {
      // 自动上传文章
      if (
          this.autoSave &&
          this.article.articleTitle.trim() != "" &&
          this.article.articleContent.trim() != "" &&
          this.article.id != null
      ) {
        this.axios
            .post("/api/admin/articles", this.article)
            .then(({data}) => {
              if (data.flag) {
                this.$notify.success({
                  title: "成功",
                  message: "自动保存成功"
                });
              } else {
                this.$notify.error({
                  title: "失败",
                  message: "自动保存失败"
                });
              }
            });
      }
      // 保存本地文章记录
      if (this.autoSave && this.article.id == null) {
        sessionStorage.setItem("article", JSON.stringify(this.article));
      }
    },
    searchCategories(keywords, cb) {
      this.axios
          .get("/api/admin/categories/search", {
            params: {
              keywords: keywords
            }
          })
          .then(({data}) => {
            cb(data.data);
          });
    },
    handleSelectCategories(item) {
      this.addCategory({
        categoryName: item.categoryName
      });
    },
    saveCategory() {
      if (this.categoryName.trim() != "") {
        this.addCategory({
          categoryName: this.categoryName
        });
        this.categoryName = "";
      }
    },
    addCategory(item) {
      this.article.categoryName = item.categoryName;
    },
    removeCategory() {
      this.article.categoryName = null;
    },
    searchTags(keywords, cb) {
      this.axios
          .get("/api/admin/tags/search", {
            params: {
              keywords: keywords
            }
          })
          .then(({data}) => {
            cb(data.data);
          });
    },
    handleSelectTag(item) {
      this.addTag({
        tagName: item.tagName
      });
    },
    saveTag() {
      if (this.tagName.trim() != "") {
        this.addTag({
          tagName: this.tagName
        });
        this.tagName = "";
      }
    },
    addTag(item) {
      if (this.article.tagNameList.indexOf(item.tagName) == -1) {
        this.article.tagNameList.push(item.tagName);
      }
    },
    removeTag(item) {
      const index = this.article.tagNameList.indexOf(item);
      this.article.tagNameList.splice(index, 1);
    }
  },
  computed: {
    tagClass() {
      return function (item) {
        const index = this.article.tagNameList.indexOf(item.tagName);
        return index != -1 ? "tag-item-select" : "tag-item";
      };
    }
  }
};
</script>

<style scoped>
.article-title-container {
  display: flex;
  align-items: center;
  margin-bottom: 1.25rem;
  margin-top: 2.25rem;
}

.save-btn {
  margin-left: 0.75rem;
  background: #fff;
  color: #f56c6c;
}

.tag-item {
  margin-right: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
}

.tag-item-select {
  margin-right: 1rem;
  margin-bottom: 1rem;
  cursor: not-allowed;
  color: #ccccd8 !important;
}

.category-item {
  cursor: pointer;
  padding: 0.6rem 0.5rem;
}

.category-item:hover {
  background-color: #f0f9eb;
  color: #67c23a;
}

.popover-title {
  margin-bottom: 1rem;
  text-align: center;
}

.popover-container {
  margin-top: 1rem;
  height: 260px;
  overflow-y: auto;
}
</style>
