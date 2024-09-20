<script setup>

import {Lock, User} from "@element-plus/icons-vue";
</script>

<template>
  <el-image style="width: fit-content; height: min-content" fit="cover" src="https://1000logos.net/wp-content/uploads/2020/03/McLaren-Logo.png"/>
  <div style="text-align: center; margin: 0 120px">
    <div style="text-align: center;">
      <div style="font-size: 40px; font-weight: bold">登录</div>
      <div style="font-size: 20px; margin-top: 10px; color: gray">请在进入系统之前先输入用户名和密码再进行登录</div>
    </div>
    <div style="margin-top: 40px">
      <el-input style="height: 40px; width: 430px" type="text" placeholder="用户名或邮箱">
        <template #prefix> <el-icon><User/></el-icon></template>
      </el-input>
    </div>
    <div style="margin-top: 20px;">
      <el-input style="width: 430px; height: 40px" type="text" placeholder="请输入密码">
        <template #prefix>
          <el-icon ><lock/></el-icon>
        </template>
      </el-input>
      <div style="margin-top: 10px">
        <el-row>
          <el-col :span="12" style=" text-align: -webkit-left">
            <el-checkbox v-model="checked1" label="记住我" size="large"/>
          </el-col>
          <el-col :span="12" style="text-align: -webkit-right">
            <el-link underline="underline" style=" ">忘记密码？</el-link>
          </el-col>
        </el-row>
        <el-button size="default" type="success" style="width: 150px; " plain>登录</el-button>
      </div>
      <div>
        <el-divider>
          <span style="color: gray; font-size: 15px">没有账号？</span>
        </el-divider>
        <div style="">
          <el-button  type="info" style="width: 150px" plain>注册</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>