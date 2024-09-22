<script setup>

import {ElMessage} from "element-plus";
import {get} from '@/net/index.js'
import {useStore} from '@/stores'
//
// const store = useStore()
//
//  if(store.auth.user == null) {
//    get('/api/user/me', (data) => {
//      store.auth.user = data
//    }, () => {
//      store.auth.user = null
//    })
// }
</script>
<template>
  <router-view/>
</template>

<style scoped>
</style>
