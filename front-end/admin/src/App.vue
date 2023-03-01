<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import {generaMenu} from "./assets/js/menu";

export default {
  created() {
    // 刷新页面查询用户菜单
    if (this.$store.state.userId != null) {
      generaMenu();
    }
    // 上传访客信息
    this.axios.post("/api/report");
  }
};
</script>

<style>
@import './assets/css/front/PingFang.css';
/* 全局字体 苹方 */
body {
  font-family: "PingFang", sans-serif;
}
</style>
