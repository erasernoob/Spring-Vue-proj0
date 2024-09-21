<script setup>
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {get} from "@/net"

const logout = () => {
  get('/api/auth/logout', (message) => {
    ElMessage.success(message);
    router.push('/');
  })
};

</script>

<template>
  <div>
    欢迎来到学习平台
  </div>
  <div>
    <el-button @click="logout()" type="danger" plain>退出登录</el-button>
  </div>

</template>

<style scoped>

</style>