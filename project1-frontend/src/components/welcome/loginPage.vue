<script setup>
import {post} from '@/net'
import {get} from '@/net'
import {ElMessage} from "element-plus";
import {reactive} from "vue";
// import {login} from "@/net";

const form = reactive({
  username: '',
  password: '',
  remember: false
});

const rule = {
  username: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ]
}

const login = () => {
  if(!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码~')
  } else {
    post('/api/auth/login', {
      username: form.username,
      password: form.password,
      remember: form.remember
    }, (message) => {
      ElMessage.success(message);
      router.push('index');
    })
  }
}

import {Lock, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
</script>

<template>
<!--  <el-image style="width: fit-content; height: min-content" fit="cover" src="https://1000logos.net/wp-content/uploads/2020/03/McLaren-Logo.png"/>-->
  <div style="text-align: center; margin: 0 120px">
    <div style="text-align: center;">
      <div style="font-size: 40px; font-weight: bold">登录</div>
      <div style="font-size: 20px; margin-top: 10px; color: gray">请在进入系统之前先输入用户名和密码再进行登录</div>
    </div>
    <div style="margin-top: 40px">
      <el-input v-model="form.username" style="height: 40px; width: 430px" type="text" placeholder="用户名或邮箱">
        <template #prefix> <el-icon><User/></el-icon></template>
      </el-input>
    </div>
    <div style="margin-top: 20px;">
      <el-input v-model="form.password" style="width: 430px; height: 40px" type="text" placeholder="请输入密码">
        <template #prefix>
          <el-icon ><lock/></el-icon>
        </template>
      </el-input>
      <div style="margin-top: 10px">
        <el-row>
          <el-col :span="12" style=" text-align: -webkit-left">
            <el-checkbox v-model="form.remember" label="记住我" size="large"/>
          </el-col>
          <el-col :span="12" style="text-align: -webkit-right">
            <el-link> 忘记密码？</el-link>
          </el-col>
        </el-row>
        <el-button @click="login()" size="default" type="success" style="width: 150px; " plain>登录</el-button>
      </div>
      <div>
        <el-divider>
          <span style="color: gray; font-size: 15px">没有账号？</span>
        </el-divider>
        <div style="">
          <el-button  @click="router.push('/register')"  type="info" style="width: 150px" plain>注册</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>