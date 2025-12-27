<template>
  <div>
    <!-- <h1>{{message}}</h1>
    <el-row>
      <el-button>默认按钮</el-button>
      <el-button type="primary">主要按钮</el-button>
      <el-button type="success">成功按钮</el-button>
      <el-button type="info">信息按钮</el-button>
      <el-button type="warning">警告按钮</el-button>
      <el-button type="danger">危险按钮</el-button>
    </el-row> -->
    <dailymate-view></dailymate-view>
  </div>
</template>

<script>
import DailymateView from './views/elment/DailymateView.vue'


    export default{
      components:{ DailymateView },
      data(){
        return{
          message:"hello vue!"
        }
      },
      methods:{
            
      }
    }
</script>

<style>
    h1{
        color:red;
    }
</style>