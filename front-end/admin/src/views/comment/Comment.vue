<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <div class="review-menu">
      <span>状态</span>
      <span
          :class="isReview == null ? 'active-review' : 'review'"
          @click="changeReview(null)"
      >
        全部
      </span>
      <span
          :class="isReview == 1 ? 'active-review' : 'review'"
          @click="changeReview(1)"
      >
        正常
      </span>
      <span
          :class="isReview == 0 ? 'active-review' : 'review'"
          @click="changeReview(0)"
      >
        审核中
      </span>
    </div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button
          :disabled="commentIdList.length == 0"
          icon="el-icon-delete"
          size="small"
          type="danger"
          @click="remove = true"
      >
        批量删除
      </el-button>
      <el-button
          :disabled="commentIdList.length == 0"
          icon="el-icon-success"
          size="small"
          type="success"
          @click="updateCommentReview(null)"
      >
        批量通过
      </el-button>
      <!-- 数据筛选 -->
      <div style="margin-left:auto">
        <el-select
            v-model="type"
            clearable
            placeholder="请选择来源"
            size="small"
            style="margin-right:1rem"
        >
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <el-input
            v-model="keywords"
            placeholder="请输入用户昵称"
            prefix-icon="el-icon-search"
            size="small"
            style="width:200px"
            @keyup.enter.native="searchComments"
        />
        <el-button
            icon="el-icon-search"
            size="small"
            style="margin-left:1rem"
            type="primary"
            @click="searchComments"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        v-loading="loading"
        :data="commentList"
        border
        @selection-change="selectionChange"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55"/>
      <el-table-column align="center" label="头像" prop="avatar" width="120">
        <template slot-scope="scope">
          <img :src="scope.row.avatar" height="40" width="40"/>
        </template>
      </el-table-column>
      <!-- 评论人昵称 -->
      <el-table-column
          align="center"
          label="评论人"
          prop="nickname"
          width="120"
      />
      <!-- 回复人昵称 -->
      <el-table-column
          align="center"
          label="回复人"
          prop="replyNickname"
          width="120"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.replyNickname">
            {{ scope.row.replyNickname }}
          </span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <!-- 评论文章标题 -->
      <el-table-column align="center" label="文章标题" prop="articleTitle">
        <template slot-scope="scope">
          <span v-if="scope.row.articleTitle">
            {{ scope.row.articleTitle }}
          </span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <!-- 评论内容 -->
      <el-table-column align="center" label="评论内容" prop="commentContent">
        <template slot-scope="scope">
          <span class="comment-content" v-html="scope.row.commentContent"/>
        </template>
      </el-table-column>
      <!-- 评论时间 -->
      <el-table-column
          align="center"
          label="评论时间"
          prop="createTime"
          width="150"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px"/>
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 状态 -->
      <el-table-column align="center" label="状态" prop="isReview" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isReview == 0" type="warning">审核中</el-tag>
          <el-tag v-if="scope.row.isReview == 1" type="success">正常</el-tag>
        </template>
      </el-table-column>
      <!-- 来源 -->
      <el-table-column align="center" label="来源" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type == 1">文章</el-tag>
          <el-tag v-if="scope.row.type == 2" type="warning">友链</el-tag>
          <el-tag v-if="scope.row.type == 3" type="danger">说说</el-tag>
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column align="center" label="操作" width="160">
        <template slot-scope="scope">
          <el-button
              v-if="scope.row.isReview == 0"
              slot="reference"
              size="mini"
              type="success"
              @click="updateCommentReview(scope.row.id)"
          >
            通过
          </el-button>
          <el-popconfirm
              style="margin-left:10px"
              title="确定删除吗？"
              @confirm="deleteComments(scope.row.id)"
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
    <!-- 批量彻底删除对话框 -->
    <el-dialog :visible.sync="remove" width="30%">
      <div slot="title" class="dialog-title-container">
        <i class="el-icon-warning" style="color:#ff9900"/>提示
      </div>
      <div style="font-size:1rem">是否彻底删除选中项？</div>
      <div slot="footer">
        <el-button @click="remove = false">取 消</el-button>
        <el-button type="primary" @click="deleteComments(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listComments();
  },
  data: function () {
    return {
      loading: true,
      remove: false,
      options: [
        {
          value: 1,
          label: "文章"
        },
        {
          value: 2,
          label: "友链"
        },
        {
          value: 3,
          label: "说说"
        }
      ],
      commentList: [],
      commentIdList: [],
      type: null,
      keywords: null,
      isReview: null,
      current: 1,
      size: 10,
      count: 0
    };
  },
  methods: {
    selectionChange(commentList) {
      this.commentIdList = [];
      commentList.forEach(item => {
        this.commentIdList.push(item.id);
      });
    },
    searchComments() {
      this.current = 1;
      this.listComments();
    },
    sizeChange(size) {
      this.size = size;
      this.listComments();
    },
    currentChange(current) {
      this.current = current;
      this.listComments();
    },
    changeReview(review) {
      this.current = 1;
      this.isReview = review;
    },
    updateCommentReview(id) {
      let param = {};
      if (id != null) {
        param.idList = [id];
      } else {
        param.idList = this.commentIdList;
      }
      param.isReview = 1;
      this.axios.put("/api/admin/comments/review", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listComments();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
      });
    },
    deleteComments(id) {
      var param = {};
      if (id == null) {
        param = {data: this.commentIdList};
      } else {
        param = {data: [id]};
      }
      this.axios.delete("/api/admin/comments", param).then(({data}) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listComments();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.remove = false;
      });
    },
    listComments() {
      this.axios
          .get("/api/admin/comments", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords,
              type: this.type,
              isReview: this.isReview
            }
          })
          .then(({data}) => {
            this.commentList = data.data.recordList;
            this.count = data.data.count;
            this.loading = false;
          });
    }
  },
  watch: {
    isReview() {
      this.current = 1;
      this.listComments();
    },
    type() {
      this.current = 1;
      this.listComments();
    }
  }
};
</script>

<style scoped>
.comment-content {
  display: inline-block;
}

.operation-container {
  margin-top: 1.5rem;
}

.review-menu {
  font-size: 14px;
  margin-top: 40px;
  color: #999;
}

.review-menu span {
  margin-right: 24px;
}

.review {
  cursor: pointer;
}

.active-review {
  cursor: pointer;
  color: #333;
  font-weight: bold;
}
</style>
