<template>
    <div class="wrapper">
        <div style="margin: 50px 0; text-align: center; font-size: 24px"><b>小涵和小源的放映室</b></div>
        <div class="home">
            <el-form>
                <el-form-item prop="roomId">
                    <el-input size="medium" v-model="roomId" placeholder="请输入房间号"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" size="height" autocomplete="off" @click="create()" style="float: left;">创建房间</el-button>
                    <el-button type="primary" size="height" autocomplete="off" @click="join()" style="float: right;">加入房间</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>

import { nanoid } from "nanoid";
import request from '@/utils/request';


export default {
    name:'MyHome',
    data() {
        return{
            roomId: '',
            socket: {},
            uuid: localStorage.getItem("uuid")
        }
    },
    created() {
        this.onload()
    },
    methods:{
        onload(){
            if(this.uuid === null || this.uuid === ''){
                this.uuid = nanoid();
                localStorage.setItem('uuid', this.uuid)
            }
        },
        async create(){
            await request.get("/room/link/" + this.roomId).then(res => {
                if(res.code === '200'){
                    if(res.data === false){
                        this.$message.error("房间已存在，请勿重复注册")
                    } else {
                        this.$router.push( {path: "/room",query: {roomId: this.roomId}})
                    }
                }   
            })
        },
        join(){
            request.get("/room/link/" + this.roomId).then(res => {
                if(res.code === '200'){
                    if(res.data === true){
                        this.$message.error("房间不存在，请创建房间")
                    } else {
                        this.$router.push( {path: "/room",query: {roomId: this.roomId}})
                    }
                }
            })
        }
    }
}
</script>

<style scoped>
    .home {
        width:250px;
        margin:150px auto;
        background-color: #fff;
        padding: 15px;
        border-radius: 10px;
    }
    .wrapper {
        height: 100vh;
        background-image: linear-gradient(to bottom right, #ffa3e7, #95daff);
        overflow: hidden;
    }
</style>