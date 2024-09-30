<script setup>
import {reactive, ref, computed} from "vue";
import {ElMessage} from "element-plus";
import {post, get} from "@/net/index.js";



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

/**
 * 计算属性？
 */
function isValidateEmail() {
  return (/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email))
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
const coldTime = ref(0) // 作为点击发送邮箱地址的冷却时间
/**
 * 添加计时器
 */
function askCode() {
  if(isValidateEmail()) {

    coldTime.value = 60 // 发送之后初始化为60
    get(`/api/auth/ask-code?email=${form.email}&type=register`,
        () => {
          ElMessage.success("验证码发送成功请注意查收")
          const intervalId = setInterval(() => coldTime.value--, 1000) // 每一秒钟减去1
          setTimeout(() => {clearInterval(intervalId); coldTime.value = 0}, 60000)
        },
        (message) => {
          ElMessage.warning(message)
          coldTime.value = 0
        })
  }
}

/**
 * 注册之前通过函数进行判断
 */
const register = () => {
  console.log(formRef.value)
  formRef.value.validate((isValid) => {
    if(!isValid) {
      ElMessage.warning("信息填写错误,请完整正确填写注册信息")
    } else {
      post('api/auth/register', {...form}, (message) => {
        ElMessage.success("注册成功")
        router.push('/')
      },(message) => {
        ElMessage.warning(message)
      })
    }
  })
}

const reset = () => {


}

const active = ref(0)

</script>

<template>
  <div style="text-align: center;">
   <el-steps align-center finish-status="success" :active="active">
     <el-step title="验证电子邮件"></el-step>
     <el-step title="重新设置密码"></el-step>
   </el-steps>
  </div>
  <div v-if="active === 0" style="text-align: center">
    <div style="font-size: 25px; font-weight: bold">重置密码</div>
    <div style="margin-top: 10px; color: gray">请输入需要重置密码的电子邮件地址</div>
    <div style="margin-top: 25px; margin-left: 140px">
        <el-form :model="form">
          <el-form-item prop="email">
            <el-input v-model="form.email" style="height: 40px; width: 430px" type="email" placeholder="电子邮箱地址">
              <template #prefix> <el-icon><<Message /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email_code">
            <el-row gutter="10" style="">
              <el-col :span="17">
                <el-input v-model="form.email_code" style="height: 40px; translate:; text-align: right" type="text" placeholder="请输入验证码">
                  <template #prefix> <el-icon><<Message/></el-icon></template>
                </el-input>
              </el-col>
              <el-col :span="7">
                <el-button size="default" @click="askCode()" type="success" style="text-align: right; translate: 0 3px" :disabled="!isValidateEmail() || coldTime !== 0" >
                  {{ coldTime > 0 ? `请稍后${coldTime}秒` : '请获取验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      <div style="margin-top: 10px">
      </div>
      </div>
    <el-button @click="active++" size="default" type="success" style="width: 40%; " plain>注册</el-button>
 </div>
  <div v-if="active === 1" style="margin-left: 130px; margin-top: 20px">
    <el-form :model="form">
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
        </el-input>
      </el-form-item>
    </el-form>
    <div style="text-align: center">
    <el-button  @click="reset()"  type="success" style="width: 49%; margin-right: 100px" plain>重置密码</el-button>
  </div>
  </div>

</template>

<style scoped>

</style>