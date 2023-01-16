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
        新建相册
      </el-button>
      <div style="margin-left:auto">
        <el-button
            icon="el-icon-delete"
            size="small"
            style="margin-right:1rem"
            type="text"
            @click="checkDelete"
        >
          回收站
        </el-button>
        <el-input
            v-model="keywords"
            placeholder="请输入相册名"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="searchAlbums"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="searchAlbums"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 相册列表 -->
    <el-row v-loading="loading" :gutter="12" class="album-container">
      <!-- 空状态 -->
      <el-empty v-if="albumList == null" description="暂无相册"/>
      <el-col v-for="item of albumList" :key="item.id" :md="6">
        <div class="album-item" @click="checkPhoto(item)">
          <!-- 相册操作 -->
          <div class="album-opreation">
            <el-dropdown @command="handleCommand">
              <i class="el-icon-more" style="color:#fff"/>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="'update' + JSON.stringify(item)">
                  <i class="el-icon-edit"/>编辑
                </el-dropdown-item>
                <el-dropdown-item :command="'delete' + item.id">
                  <i class="el-icon-delete"/>删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="album-photo-count">
            <div>{{ item.photoCount }}</div>
            <i v-if="item.status == 2" class="iconfont el-icon-mymima"/>
          </div>
          <el-image :src="item.albumCover" class="album-cover" fit="cover"/>
          <div class="album-name">{{ item.albumName }}</div>
        </div>
      </el-col>
    </el-row>
    <!-- 分页 -->
    <el-pagination
        :current-page="current"
        :hide-on-single-page="true"
        :page-size="size"
        :total="count"
        class="pagination-container"
        layout="prev, pager, next"
        @size-change="sizeChange"
        @current-change="currentChange"
    />
    <!-- 新增模态框 -->
    <el-dialog :visible.sync="addOrEdit" top="10vh" width="35%">
      <div ref="albumTitle" slot="title" class="dialog-title-container"/>
      <el-form :model="albumForum" label-width="80px" size="medium">
        <el-form-item label="相册名称">
          <el-input v-model="albumForum.albumName" style="width:220px"/>
        </el-form-item>
        <el-form-item label="相册描述">
          <el-input v-model="albumForum.albumDesc" style="width:220px"/>
        </el-form-item>
        <el-form-item label="相册封面">
          <el-upload
              :before-upload="beforeUpload"
              :on-success="uploadCover"
              :show-file-list="false"
              action="/api/admin/photos/albums/cover"
              class="upload-cover"
              drag
              multiple
          >
            <i v-if="albumForum.albumCover == ''" class="el-icon-upload"/>
            <div v-if="albumForum.albumCover == ''" class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <img
                v-else
                :src="albumForum.albumCover"
                height="180px"
                width="360px"
            />
          </el-upload>
        </el-form-item>
        <el-form-item label="发布形式">
          <el-radio-group v-model="albumForum.status">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="2">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addOrEdit = false">取 消</el-button>
        <el-button type="primary" @click="addOrEditAlbum">
          确 定
        </el-button>
      </div>
    </el-dialog>
    <!-- 删除对话框 -->
    <el-dialog :visible.sync="isdelete" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否删除该相册？</div>
      <div slot="footer">
        <el-button @click="isdelete = false">取 消</el-button>
        <el-button type="primary" @click="deleteAlbum">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import * as imageConversion from "image-conversion";

export default {
  created() {
    this.listAlbums();
  },
  data: function () {
    return {
      keywords: "",
      loading: true,
      isdelete: false,
      addOrEdit: false,
      albumForum: {
        id: null,
        albumName: "",
        albumDesc: "",
        albumCover: "",
        status: 1
      },
      albumList: [],
      current: 1,
      size: 8,
      count: 0
    };
  },
  methods: {
    openModel(item) {
      if (item) {
        console.log(item);
        this.albumForum = JSON.parse(item);
        this.$refs.albumTitle.innerHTML = "修改相册";
      } else {
        this.albumForum = {
          id: null,
          albumName: "",
          albumLabel: "",
          albumCover: "",
          status: 1
        };
        this.$refs.albumTitle.innerHTML = "新建相册";
      }
      this.addOrEdit = true;
    },
    checkPhoto(item) {
      this.$router.push({path: "/albums/" + item.id});
    },
    checkDelete() {
      this.$router.push({path: "/photos/delete"});
    },
    listAlbums() {
      this.axios
          .get("/api/admin/photos/albums", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords
            }
          })
          .then(({data}) => {
            this.albumList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
    },
    addOrEditAlbum() {
      if (this.albumForum.albumName.trim() == "") {
        this.$message.error("相册名称不能为空");
        return false;
      }
      if (this.albumForum.albumDesc.trim() == "") {
        this.$message.error("相册描述不能为空");
        return false;
      }
      if (this.albumForum.albumCover == null) {
        this.$message.error("相册封面不能为空");
        return false;
      }
      this.axios
          .post("/api/admin/photos/albums", this.albumForum)
          .then(({data}) => {
            if (data.flag) {
              this.$notify.success({
                title: "成功",
                message: data.message
              });
              this.listAlbums();
            } else {
              this.$notify.error({
                title: "失败",
                message: data.message
              });
            }
          });
      this.addOrEdit = false;
    },
    uploadCover(response) {
      this.albumForum.albumCover = response.data;
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
    handleCommand(command) {
      const type = command.substring(0, 6);
      const data = command.substring(6);
      if (type == "delete") {
        this.albumForum.id = data;
        this.isdelete = true;
      } else {
        console.log(data);
        this.openModel(data);
      }
    },
    deleteAlbum() {
      this.axios
          .delete("/api/admin/photos/albums/" + this.albumForum.id)
          .then(({data}) => {
            if (data.flag) {
              this.$notify.success({
                title: "成功",
                message: data.message
              });
              this.listAlbums();
            } else {
              this.$notify.error({
                title: "失败",
                message: data.message
              });
            }
            this.isdelete = false;
          });
    },
    searchAlbums() {
      this.current = 1;
      this.listAlbums();
    },
    sizeChange(size) {
      this.size = size;
      this.listAlbums();
    },
    currentChange(current) {
      this.current = current;
      this.listAlbums();
    }
  }
};
</script>

<style scoped>
.album-cover {
  position: relative;
  border-radius: 4px;
  width: 100%;
  height: 170px;
}

.album-cover::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.album-photo-count {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1.5rem;
  z-index: 1000;
  position: absolute;
  left: 0;
  right: 0;
  padding: 0 0.5rem;
  bottom: 2.6rem;
  color: #fff;
}

.album-name {
  text-align: center;
  margin-top: 0.5rem;
}

.album-item {
  position: relative;
  cursor: pointer;
  margin-bottom: 1rem;
}

.album-opreation {
  position: absolute;
  z-index: 1000;
  top: 0.5rem;
  right: 0.8rem;
}
</style>
