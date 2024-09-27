import axios from "axios";
import {isNavigationFailure} from "vue-router";
import {ElMessage} from "element-plus";

const defaultError = (err) => {
    console.error(err)
    ElMessage.error('发生了一些错误，请联系管理员')
}

const tokenItemName = 'access_token'


const defaultFailure = (message, code, url) => {
    console.warn(`请求地址: ${url}, 状态码: ${code}, 错误信息： ${message}`)
    ElMessage.warning(message)
}

function getAccessToken() {
    const str = localStorage.getItem(tokenItemName) || sessionStorage.getItem(tokenItemName)
    if(!str) {
        return null
    }
    const tokenObj = JSON.parse(str)
    if(tokenObj.expire <= new Date()) {
        deleteToken()
        ElMessage.warning("登录状态已经过期，请重新登录")
        return null
    }
    return tokenObj
}

function deleteToken() {
    localStorage.removeItem(tokenItemName)
    sessionStorage.removeItem(tokenItemName)
}

/**
 * 登录账号逻辑
 * @param username
 * @param password
 * @param remember
 * @param success
 * @param failure
 */
function login(username, password, remember, success, failure = defaultFailure) {
    internalPost("api/auth/login", {
        username: username,
        password: password
    }, (data) => {
        // 登录成功保存相应的token
        storeAccessToken(data.token, remember, data.expire)
        ElMessage.success(`登录成功！欢迎您！${data.username}`)
        success(data)
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, failure)

}

// 存储token逻辑
function storeAccessToken(token, remember, expire) {
    // 封装成一个token对象
    const tokenObj = {
        token: token, expire:expire
    }
    const str = JSON.stringify(tokenObj)
    // 如果勾选了记住我，则token存储在本地
    // 否则，token存储在sessionStorage
    if(remember) {
        localStorage.setItem(tokenItemName, str)
    } else {
        sessionStorage.setItem(tokenItemName, str)
    }
}

function internalPost(url, data, success, header, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data);
        } else {
            failure(data.message, data.code, url)
        }
    }).catch((err) => error(err))
}

function internalGet(url, data, success, header, failure = defaultFailure, error = defaultError) {
    axios.get(url,{
        headers: header
    }).then(({data}) => {
        if(data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch((err) => {
        error(err)
    })
}

export {login}






