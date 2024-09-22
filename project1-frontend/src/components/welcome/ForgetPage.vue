<script setup>
import {Lock, Message, User} from "@element-plus/icons-vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import {reactive, ref} from "vue";
import router from "@/router/index.js";

const form = reactive({
  username: '',
  password: '',
  email_code: '',
  email: ''
})

const validateRePassword = (rules, value, callback) => {
  if(value !== form.password) {
    callback(new Error('两次密码输入不一致！'))
  } else if(value === '') {
    callback(new Error('确认密码不能为空！'))
  } else {
    callback()
  }
}
const validatePassword = (rules, value, callback) => {
  if(/^(?=.*[a-z])(?=.*\d)(?=.*[\W_])$/.test(value)) {
    callback(new Error('密码需要有写字母、数字、特殊字符组成'))
  }
  else if(value === '') {
    callback(new Error('密码不能为空！'))
  } else if(!/^(?=.{6,16}$)/.test(value)) {
    callback(new Error('密码长度需要在6-16个字符之间'))
  } else callback()
}


const validateEmail = (rules, value, callback) => {
  if(!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)) {
    callback(new Error('请输入正确的电子邮箱地址'))
  } else callback()
}

const validateName = (rules, value, callback) => {
  if(value === '') {
    callback(new Error('用户名不能为空'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) { // 不能包含特殊字符和数字
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}

const isEmailValid = ref(false) // 默认的是邮件格式不合法
const cold = ref(0) // 默认的冷却时间为〇

const onValidate = (prop, isValid) => {
  if(prop === 'email') {
    isEmailValid.value = isValid;
  }
}

const validateSendEmail = () => {
  post('api/auth/forget/validate-email', {
    username: form.username,
    email: form.email,
    email_code: form.email_code
  }, (message) => {
    ElMessage.success(message) // 根据后端发来的信息对用户进行展示
    cold.value = 60
    setInterval(() => cold.value--, 1000) // 每个一秒变化一次

  })
}

const rules =  {
  username: [
    { validator: validateName, trigger: ['blur', 'change']},
    { min: 2, max: 8, message: '用户名长度必须在2-8个字符之间', trigger: 'blur' },
  ],
  password:[
    {validator: validatePassword, trigger:['blur', 'change']}
  ],
  rePassword: [
    {validator: validateRePassword, trigger:['blur', 'change']}
  ],
  email: [

    {validator: validateEmail, trigger: ['blur', 'change']}
  ],
  email_code: [
    {required: true , message: '请输入验证码', trigger: ['blur', 'change']}
  ]
}

const forget = () => {
  post('api/auth/forget/reset-password', {
    username: form.username,
    email: form.email,
    password: form.password,
    email_code: form.email_code
  },  (message) => {
    ElMessage.success(message)
    ElMessage.success("重置密码成功，返回登录页面进行登录！")
    router.push('/')
  })
}
const active = ref(0)

</script>

<template>
  <el-steps :active="active" finish-status="success" align-center>
    <el-step title="验证电子邮件"/>
    <el-step title="重新设定密码"/>
  </el-steps>
  <div style="text-align: center; margin: 0 120px">
    <div style="text-align: center;">
      <div style="font-size: 40px; font-weight: bold">找回密码</div>
      <div style="font-size: 20px; margin-top: 10px; color: gray">
        输入详细用户信息找回密码
      </div>
    </div>
    <div style="margin-top: 40px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" style="height: 40px; width: 430px" type="text" placeholder="用户名">
            <template #prefix> <el-icon><User/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" style="width: 430px; height: 40px" type="password" placeholder="请输入新密码">
            <template #prefix>
              <el-icon ><lock/></el-icon>
            </template>
          </el-input>
        </el-form-item >
        <el-form-item prop="rePassword">
          <el-input v-model="form.rePassword" style="width: 430px; height: 40px" type="password" placeholder="确认新密码">
            <template #prefix>
              <el-icon ><lock/></el-icon>
            </template>
          </el-input></el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" style="height: 40px; width: 430px" type="email" placeholder="对应用户的电子邮箱地址">
            <template #prefix> <el-icon><<Message/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email_code">
          <el-row gutter="10" style="text-align: center">
            <el-col :span="17">
              <el-input v-model="form.email_code" style="height: 40px; text-align: right" type="text" placeholder="请输入验证码">
                <template #prefix> <el-icon><<Message/></el-icon></template>
              </el-input>
            </el-col>
            <el-col :span="7">
              <el-button size="default" @click="validateSendEmail" type="success" style="text-align: right; translate: 0 3px" :disabled="!isEmailValid || cold > 0" >{{cold > 0 ? '  请稍候' + cold + '秒  ' : '点击获取验证码' }}</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <el-button type="success" size="large" style="width: 200px; " @click="forget()">重置密码</el-button>
      <div style="margin-top: 10px">
        <el-divider>
          <el-link @click="router.push('/')">返回登录</el-link>
        </el-divider>
      </div>

    </div>
  </div>
</template>
<style scoped>

</style>