<script setup>
import {ElMessage} from "element-plus";
import { reactive, ref } from 'vue'
// import { ComponentSize, FormInstance, FormRules } from 'element-plus'

import {Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
const form = reactive({
  username: '',
  password: '',
  rePassword: '',
  email_code: '',
  email: ''
})

const isEmailValid = ref(false)

const validateName = (rules, value, callback) => {
  if(value === '') {
    callback(new Error('用户名不能为空'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) { // 不能包含特殊字符和数字
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}
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

const onValidate = (prop, isValid) => { // 一个事件（element所提供的功能，会与form表单进行绑定之后）
  if(prop === 'email')
    isEmailValid.value = isValid
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

const formRef = ref()


/**
 * 注册之前通过函数进行判断
 */
const register = () => {
  formRef.value.validate((isValid) => {
    if(!isValid) {
      ElMessage.warning("信息填写错误,请完整正确填写注册信息")
    }
  })
    // post('api/auth/register', {
    //   username: form.username,
    //   password: form.password,
    //   email:  form.email,
    //   emailCode: form.email_code
    // }, (message) => {
    //   ElMessage.success(message)
    //   router.push('/index')
    // })
}
</script>

<template>
<!--  <el-image style="width: fit-content; height: min-content" fit="cover" src="https://1000logos.net/wp-content/uploads/2020/03/McLaren-Logo.png"/>-->
  <div style="text-align: center; margin: 0 120px">
    <div style="text-align: center;">
      <div style="font-size: 40px; font-weight: bold">注册新用户</div>
      <div style="font-size: 20px; margin-top: 10px; color: gray">
        请在下方输入详细信息注册进入平台
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
        <el-input v-model="form.password" style="width: 430px; height: 40px" type="password" placeholder="请输入密码">
        <template #prefix>
          <el-icon ><lock/></el-icon>
        </template>
          </el-input>
      </el-form-item >
      <el-form-item prop="rePassword">
        <el-input v-model="form.rePassword" style="width: 430px; height: 40px" type="password" placeholder="确认密码">
        <template #prefix>
          <el-icon ><lock/></el-icon>
        </template>
      </el-input></el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" style="height: 40px; width: 430px" type="email" placeholder="电子邮箱地址">
            <template #prefix> <el-icon><<Message/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email_code">
          <el-row gutter="10" style="text-align: center">
            <el-col :span="17">
              <el-input v-model="form.email_code" style="height: 40px; translate:; text-align: right" type="text" placeholder="请输入验证码">
                <template #prefix> <el-icon><<Message/></el-icon></template>
              </el-input>
            </el-col>
            <el-col :span="7">
              <el-button size="default" @click="" type="success" style="text-align: right; translate: 0 3px" :disabled="!isEmailValid" >点击发送验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <div style="margin-top: 10px">
        <el-button @click="register()" size="default" type="success" style="width: 150px; " plain>注册</el-button>
      </div>
      <div style="margin-top: 10px">
        <el-link type="primary" style="color: gray" @click="router.push('/')">已有账号？立即登录</el-link>
      </div>
    </div>
  </div>
</template>
<style scoped>

</style>