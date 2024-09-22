<script setup>

import {Lock, User} from "@element-plus/icons-vue";
</script>

<template>

  <div style="width: 100vw; height: 100vh; overflow: hidden; display: flex;">
    <div style="flex:1">
      <el-image style="width: 100%; height: 100%" fit="cover" src="https://cdn-4.motorsport.com/images/amp/YXRxGpd0/s1000/oscar-piastri-mclaren-mcl60-1.jpg"/>
    </div>
    <div class="welcome-title">
      <div style="font-weight: bold; font-size: 50px">欢迎来到Mclaren车队学习平台</div>
      <div style="margin-top: 5px">在这里你可以学习如何打开'mini-DRS', 如何让车队同时丢失车队年度总冠军，和车手年度总冠军。</div>
    </div>

      <div style="width: 700px; background-color: white; z-index: 1">
          <el-image style="width: fit-content; height: min-content" fit="cover" src="https://1000logos.net/wp-content/uploads/2020/03/McLaren-Logo.png"/>
        <transition name="el-fade-in-linear">
          <router-view/>
        </transition>
      </div>
    </div>


</template>

<style scoped>
.welcome-title {
  position: absolute;
  font-size: 30px;
  bottom: 100px;
   left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}

</style>